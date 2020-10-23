package icaro.aplicaciones.Rosace.informacion;

import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import java.util.ArrayList;

public class Coste1 {

    private Coordinate robotLocation; //Localizacion del robot
    private double funcionEvaluacion; //Variable para almacenar el resultado de calcular la funcion de evaluacion utilizada
    private Integer cotaMaxima;
    // Argumentos para calcular el coste 
    private double distanciaPosRobotVictima;
    private int tiempoAtencionVictimas; // para tener en cuenta  el  numero de victimas asignadas
    private int numeroVictimasAsignadas; // numero de victimas a rescatar cuando se hace la evaluacion
    private int energiaDisponibleRobot;
    // Pesos asociados a los parametros
    private int pesoArgumentoDistancia;
    private int pesoArgumentoEnergia;
    private int pesoArgumentoTiempoAtencion;
    private RobotStatus1 robot;
    private VictimsToRescue victims2R;
    private MisObjetivos misObjs;
    private boolean trazar = true;
    private String trazaCalculoCoste = "";
    private String identAgenteQusaCoste;
    private final double factorMultiplicativo = 3.0;

    public void Coste1() {
        trazaCalculoCoste = "";
        cotaMaxima = Integer.MAX_VALUE;
    }

    //Funcion de evaluacion que solo considera distancia entre la nueva victima y la posicion del robot. NO SE CONSIDERA LA ENERGIA NI LAS VICTIMAS QUE TIENE ASIGNADAS PREVIAMENTE.
    //En este caso, el tercer parametro, robot, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
    //El cuarto parametro solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
    public double FuncionEvaluacion1(double par1DistanciaEntreDosPuntos, double pesoPar1, RobotStatus1 robot, Victim nuevaVictima) {
//	    trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "Coste: FuncionEvaluacion1 sobre Victima(" + nuevaVictima.getName() + ")"  + 
//	    		  ": robot " + robot.getIdRobot() + "-> " + (pesoPar1 * par1DistanciaEntreDosPuntos)  	    		  
//	    		   , InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           			
        return pesoPar1 * par1DistanciaEntreDosPuntos;
    }
    //Funcion de evaluacion que considera la energia disponible en el robot para intentar poder atender a las victimas que tenia y la nueva victima. 
    //El recorrido se haria de acuerdo a la prioridad de las victimas.  
    //El cuarto parametro, nuevaVictima, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo	

    public double FuncionEvaluacion2(double par1DistanciaCamino, double pesoPar1, RobotStatus1 robot, Victim nuevaVictima) {
        if (par1DistanciaCamino > robot.getAvailableEnergy()) {
//	       trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "Coste: FuncionEvaluacion2 sobre Victima(" + nuevaVictima.getName() + ")"  +
//		          ": robot " + robot.getIdRobot() + "-> -1.0"	    		   
//	    		   , InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           			
            return cotaMaxima;
        } else {
//		   trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "Coste: FuncionEvaluacion2 sobre Victima(" + nuevaVictima.getName() + ")"  +
//		           ": robot " + robot.getIdRobot() + "-> " + par1DistanciaCamino * pesoPar1 
//		   		   , InfoTraza.NivelTraza.info));
            return par1DistanciaCamino * pesoPar1;
        }
    }

    //Funcion de evaluacion que considera la ENERGIA disponible en el robot para intentar poder atender a las victimas que tenia y la nueva victima.
    //                   ademas considera el TIEMPO invertido en curar a cada victima
    //El recorrido se haria de acuerdo a la prioridad de las victimas.  
    //El cuarto parametro, nuevaVictima, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
    //En esta funcion FuncionEvaluacion3 SE ESTA SUPONIENDO QUE MIENTRAS EL ROBOT CURA A LA VICTIMA, EL ROBOT NO CONSUME ENERGIA
    public double FuncionEvaluacion3(double par1DistanciaCamino, double pesoPar1, double par2TiempoTotalAtencionVictimas, double pesoPar2, RobotStatus1 robot, Victim nuevaVictima) {
        double resultado;
        //Si no tiene energia devuelve un -1 para indicar que no tiene recursos para ir
        if (par1DistanciaCamino > robot.getAvailableEnergy()) {
            resultado = cotaMaxima;
        } else {
            resultado = (par1DistanciaCamino * pesoPar1) + (par2TiempoTotalAtencionVictimas * pesoPar2);

        }
        if (trazar) {
            this.addTraza("Coste con FuncionEvaluacion3", resultado);
            this.addTraza("DistanciaCamino", par1DistanciaCamino);
            this.addTraza("pesoDistanciaCamino", pesoPar1);
            this.addTraza("par2TiempoTotalAtencionVictimas", par2TiempoTotalAtencionVictimas);
            this.addTraza("pesoTiempoTotalAtencionVictimas", pesoPar2);
        }
        return resultado;
    }
    //Funcion para desempatar. La nueva evaluacion es la evaluacion que tenia anteriormente + el identificador numerico del robot
    //                         Es decir, tendra una nueva evaluacion mayor, aquel robot que tenga mayor indice de entre los robots empatados.
    //ESTA FUNCION PRETENDE HACER LO MISMO QUE EL METODO calcularEvalucionParaDesempate DEFINIDO EN LA CLASE InfoParaDecidirQuienVa
    //SERIA MEJOR TENER TODOS LOS METODOS DE EVALUACIONES EN ESTA MISMA CLASE DE Coste
    //Actualmente FuncionEvaluacionDesempate no se usa en ningun otro sitio

    public int FuncionEvaluacionDesempate(InfoParaDecidirQuienVa infoDecision, RobotStatus robot) {
        int evaluacionActual = infoDecision.mi_eval;  //recupero el valor de evaluacion que provoco el empate
        //ArrayList<String> agentesEmpatados = infoDecision.getAgentesEmpatados();
        if (evaluacionActual >= 0) {
            String identificadorRobot = robot.getIdRobot();
            //Obtener el identificador numerico del robot. Por ejemplo, para robotMasterIA3 la variable identficadorNumericoRobot tomara el valor 3
            int numeroAleatorio = (int) (Math.random() * 100) + 1;
//            String number = getNumber(identificadorRobot, index);
//            int identficadorNumericoRobot = Integer.parseInt(number);
            infoDecision.tengoLaMejorEvaluacion = true;
            evaluacionActual = evaluacionActual + numeroAleatorio;
            infoDecision.mi_eval = evaluacionActual;
            return evaluacionActual;
        } else {
            return evaluacionActual;
        }
    }

    public int costeAyudarVictimaIndividual(String nombreAgenteEmisor, Coordinate robotLocation, RobotStatus1 robot, Victim victima, VictimsToRescue victimas, MisObjetivos misObjs, String identFuncEval) {
        // El coste es funcion de la distancia. Si no tiene energia suficiente para salvar a la victima el coste es maximo
        identAgenteQusaCoste = nombreAgenteEmisor;
        int valorCoste;
        this.victims2R = victimas;
//        if (!victims2R.costeRescateEnMatrizCostes(victima)) {
//            costeEstimadoDeVictimaEnMatrizCostes(victima);
//        }
        double distanciaCamino = this.distanciaC1toC2(robotLocation, victima.getCoordinateVictim());
        if (robot.hayEnergiaSuficiente(distanciaCamino)) {
            valorCoste = (int) distanciaCamino;
        } else {
            valorCoste = cotaMaxima;
        }
        if (trazar) {
            this.addTraza("costeAyudarVictimaIndividual", valorCoste);
            this.addTraza("DistanciaCamino", distanciaCamino);
        }
        return valorCoste;
    }

    public int[] costeAyudarVictimas(String nombreAgenteEmisor, Coordinate robotLocation, RobotStatus1 robot, Victim victima, VictimsToRescue victimas, MisObjetivos misObjs, String identFuncEval) {
        identAgenteQusaCoste = nombreAgenteEmisor;
        int valorCoste;
        this.victims2R = victimas;
        ArrayList misVictimasAsignadas = victims2R.getVictimsAsignadas();
        System.out.println(" Victima a rescatar : " + victima.getName() + " Se  calcula los costes del robot a las victimas asignadas  " + misVictimasAsignadas);
        //Calculo del coste del robot  a las victimas asignadas
        if (misVictimasAsignadas.isEmpty()) { // no hay victimas asignadas 
            int[] camino = new int[2];
            double distanciaCamino = this.distanciaC1toC2(robotLocation, victima.getCoordinateVictim());
            if (robot.hayEnergiaSuficiente(distanciaCamino)) {
                camino[0] = (int) distanciaCamino;
            } else {
                valorCoste = cotaMaxima;
            }
            camino[1] = victimas.addVictimARescatar(victima);
            return camino;
        }
        // hay varias victimas asignadas
        misVictimasAsignadas.add(victims2R.addVictimARescatar(victima));
        ArrayList costesRobtAvictsAsignadas = new ArrayList();
        int dimArray = misVictimasAsignadas.size();
        Victim victimai;
        for (int i = 0; i < misVictimasAsignadas.size(); i++) {
            victimai = victims2R.getVictimaARescatar((Integer) misVictimasAsignadas.get(i));
            costesRobtAvictsAsignadas.add(i, (int) distanciaC1toC2(robot.getRobotCoordinate(), victimai.getCoordinateVictim()));
        }
        System.out.println(" Los costes desde la posicion del robot a las victimas asignadas son : " + costesRobtAvictsAsignadas);
        // obtencion de la matriz de costes
        return victims2R.minCaminoRobotVictsAsignadas(costesRobtAvictsAsignadas);
    }

    private void imprimirMatriz(int[][] matriz) {
        for (int x = 0; x < matriz.length; x++) {
            for (int y = 0; y < matriz[x].length; y++) {
                System.out.print(" | " + matriz[x][y] + " | ");
            }
            System.out.println("\n----------------------------------------");

        }
    }

    //Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas actualmente, mas el tiempo que tardara en atender a la nueva victima
    //El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo
    public int CalculoCosteAyudarVictima(String nombreAgenteEmisor, Coordinate robotLocation, RobotStatus1 robot, Victim victima, VictimsToRescue victims2R, MisObjetivos misObjs, String identFuncEval) {
        this.identAgenteQusaCoste = nombreAgenteEmisor;
        //    Caso la victima de la que se pide calcular el coste 
        // caso 1 tiene mayor prioridad que las victimas actualmente asignadas al robot
        // se calcula la distancia desde la posición del robot a la nueva victima

        // caso2 tiene igual prioridad que las de mayor prioridad
        // se extraen las victimas que tienen prioridad igual y se calcula el camino minimo que contiene a todas
        // caso 3 tiene menor prioridad
        // coste del camino minimo que contine a todas las de mayor prioridad + coste del camino minimo que contiene a todas
        // las menor prioridad que las precedentes pero de igual prioridad
        double distanciaCamino = this.distanciaC1toC2(robotLocation, victima.getCoordinateVictim());
        double tiempoAtencionVictimas = (factorMultiplicativo * victima.getPriority());;

        if (identFuncEval.equalsIgnoreCase("FuncionEvaluacion1")) {
            funcionEvaluacion = this.FuncionEvaluacion1(distanciaCamino, 10.0, robot, victima);
        } else if (identFuncEval.equalsIgnoreCase("FuncionEvaluacion2")) {
            funcionEvaluacion = this.FuncionEvaluacion2(distanciaCamino, 10.0, robot, victima);
        } else if (identFuncEval.equalsIgnoreCase("FuncionEvaluacion3")) {
            funcionEvaluacion = this.FuncionEvaluacion3(distanciaCamino, 10.0, tiempoAtencionVictimas, 3.0, robot, victima);
        } else {
//                trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "FuncionEvaluacion Especificada no existe sobre Victima(" + victima.getName() + ")"  +
//		          ": robot " + robot.getIdRobot() + "-> -1.0"	    		   
//	    		   , InfoTraza.NivelTraza.error)); 
        }
        if (trazar) {
            this.addTraza("Posicion Robot : ", robotLocation.toString());
            this.addTraza("Victima Destino : ", victima.getName() + " Coordenadas Victima : " + victima.getCoordinateVictim().toString());

        }
        return (int) funcionEvaluacion;
    }

    //Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas actualmente, mas el tiempo que tardara en atender a la nueva victima
    //El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo
    //Calcula la distancia entre dos puntos
    private double distanciaC1toC2(Coordinate c1, Coordinate c2) {

        double distancia = Math.sqrt(Math.pow(c1.x - c2.x, 2)
                + Math.pow(c1.y - c2.y, 2)
                + Math.pow(c1.z - c2.z, 2)
        );
        if (trazar) {
            System.out.println("Coord calculo Coste c1->" + c1);
            System.out.println("Coord calculo Coste c2->" + c2);
            this.addTraza("Calculo Distancia desde ", c1.toString() + " hasta :" + c2.toString() + "distancia = " + distancia);
        }
        return distancia;
    }

    //Calcula la tiempo que tarda en recorrer el camino de todas las victimas de las que se ha hecho responsable.
    //El orden de visita de victimas esta determinado por las prioridades de las victimas, es decir, las de mayor prioridad se visitan primero.
    //Resaltar que cuando hay varias victimas con la misma prioridad, se visitara primero aquella que lleva mas tiempo en la cola (mis objetivos).
    public double calculaCosteTotalCompletarMisionAtenderVictimasFinalesAsignadas(double par1TiempoRecorrerCaminoVictimasAsignadas, double pesoPar1,
            double par2TiempoAtencionVictimasAsignadas, double pesoPar2) {
        double resultado;

        resultado = (par1TiempoRecorrerCaminoVictimasAsignadas * pesoPar1)
                + (par2TiempoAtencionVictimasAsignadas * pesoPar2);

        return resultado;
    }

    public void setTrazar(boolean trazas) {
        this.trazar = trazas;
    }

    public String getTrazaCalculoCoste() {
        return trazaCalculoCoste;
    }

    private void addTraza(String nombreParametro, String valorParametro) {
        trazaCalculoCoste = trazaCalculoCoste + nombreParametro + " : " + valorParametro + " ; \n ";
    }

    private void addTraza(String nombreParametro, double valorParametro) {
        trazaCalculoCoste = trazaCalculoCoste
                + String.format(nombreParametro + " :  %.2f", valorParametro) + " ; \n ";
    }

}
