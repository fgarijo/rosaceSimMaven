package icaro.aplicaciones.Rosace.informacion;

import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Coste {

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

    public void Coste() {
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
            int index = getNumberStartIndex(identificadorRobot);
            String number = getNumber(identificadorRobot, index);
            int identficadorNumericoRobot = Integer.parseInt(number);
            infoDecision.tengoLaMejorEvaluacion = true;
            evaluacionActual = evaluacionActual + identficadorNumericoRobot;
            infoDecision.mi_eval = evaluacionActual;
            return evaluacionActual;
        } else {
            return evaluacionActual;
        }
    }
    
//    public int calculoCosteAyudarVictimaConRLocation (String nombreAgenteEmisor, RobotStatus1 robot,Victim victima, VictimsToRescue victims2R, MisObjetivos misObjs, String identFuncEval){
//            
//        try{    		   
//                ItfUsoRepositorioInterfaces itfUsoRepositorioInterfaces = NombresPredefinidos.REPOSITORIO_INTERFACES_OBJ;
//                ItfUsoRecursoMorse morseResourceRef;
//       		 morseResourceRef = (ItfUsoRecursoMorse) itfUsoRepositorioInterfaces.obtenerInterfaz(NombresPredefinidos.ITF_USO + 
//       				                      "RecursoMorse1");
//       		  robotLocation = morseResourceRef.getGPSInfo(nombreAgenteEmisor);
//       		           
//       	          }
//   	              catch (Exception ex){
//       		              ex.printStackTrace();
//       	          }
//         double distanciaCamino = this.CalculaDistanciaCamino(nombreAgenteEmisor, robotLocation, victima, victims2R, misObjs);
//         double tiempoAtencionVictimas = this.CalculaTiempoAtencion(3.0, victima, victims2R, misObjs); 
//        if (identFuncEval.equalsIgnoreCase("FuncionEvaluacion1"))
//                funcionEvaluacion = this.FuncionEvaluacion1(distanciaCamino, 10.0,  robot, victima);
//            else if(identFuncEval.equalsIgnoreCase("FuncionEvaluacion2"))
//                funcionEvaluacion = this.FuncionEvaluacion2(distanciaCamino, 10.0, robot, victima);
//            else if(identFuncEval.equalsIgnoreCase("FuncionEvaluacion3"))
//                funcionEvaluacion = this.FuncionEvaluacion3(distanciaCamino, 10.0, tiempoAtencionVictimas, 3.0, robot, victima);
//            else {
////                trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "FuncionEvaluacion Especificada no existe sobre Victima(" + victima.getName() + ")"  +
////		          ": robot " + robot.getIdRobot() + "-> -1.0"	    		   
////	    		   , InfoTraza.NivelTraza.error)); 
//            }
//	        
//           int mi_eval = (int)funcionEvaluacion;   //convierto de double a int porque la implementación inicial de Paco usaba int                                  
//            
//            if (mi_eval>=0){            
//              int  mi_eval_nueva = Integer.MAX_VALUE; 
////              mi_eval_nueva = cotaMaxima; 
//                //como va el que menor rango tiene, lo inicializamos a la peor                        
//            	//Para que gane el que mayor valor tiene de evaluación le resto el valor de la distancia obtenida al valor máximo de Integer
//            	//El que este más cercano hará decrecer menos ese valor y por tanto es el MEJOR
//            	mi_eval = mi_eval_nueva - mi_eval;
//            }
//            return mi_eval;
//        }
public int costeAyudarVictimaIndividual(String nombreAgenteEmisor, Coordinate robotLocation, RobotStatus1 robot, Victim victima, VictimsToRescue victims2R, MisObjetivos misObjs, String identFuncEval) {
    // El coste es funcion de la distancia. Si no tiene energia suficiente para salvar a la victima el coste es maximo
    int valorCoste; 
    double distanciaCamino = this.distanciaC1toC2( robotLocation, victima.getCoordinateVictim());
     if(robot.hayEnergiaSuficiente(distanciaCamino))valorCoste= (int)distanciaCamino;
     else valorCoste= cotaMaxima;
     if (trazar) {
            this.addTraza("costeAyudarVictimaIndividual", valorCoste);
            this.addTraza("DistanciaCamino", distanciaCamino);
     }
     return valorCoste;
}
public int costeAyudarVictimaConVictmsAsignadas(String nombreAgenteEmisor, Coordinate robotLocation, RobotStatus1 robot, Victim victima, VictimsToRescue victims2R, MisObjetivos misObjs, String identFuncEval) {
    // el robot tiene asignada mas de una victima. Puede estar en camino a salvar una victima y puede tener mas victimas asignadas
    // se actualiza la matriz de costes entre victimas
    if (trazar) {
      System.out.println("costeAyudarVictimaConVictmsAsignadas para el agente " + nombreAgenteEmisor);  
    }
    victims2R.addVictimAsignada(victima);
    ArrayList idtsVictimasAsignadas = victims2R.getIdtsVictimsAsignadas();
    // Se anyade la victima al conj de victimas asignadas
    int indiceVictima= idtsVictimasAsignadas.size()-1;
//    idtsVictimasAsignadas.add(victima.getName()); // la victima anyadida ocupara la posicion del tamanyo antes de anyadir
     Victim victimai;
    if (victims2R.costeRescateEnMatrizCostes(victima)){ // si la victima no tiene coste asignado en la matriz de costes
    // Si la victima no esta definida actualizamos el coste de las distancias entre la nueva victima y el resto
//    if( victims2R.victimaDefinida(victima)){
if (trazar) {
      System.out.println("La victima  " + victima.getName() + " no tiene coste asignado " + "el indice en VictimasAsignadas es : " +indiceVictima );  
    }
    for(int i = 0; i< idtsVictimasAsignadas.size(); i++){
         victimai= victims2R.getVictimARescatar((String) idtsVictimasAsignadas.get(i));
        victims2R.addCosteRescateAmatrizCostes(victima,victimai , (int)distanciaC1toC2(victima.getCoordinateVictim(), victimai.getCoordinateVictim()));
    }
    }
     System.out.println(" Se  calcula los costes del robot a las victimas asignadas  " + idtsVictimasAsignadas);  
    //Calculo del coste del robot  a las victimas asignadas
    ArrayList costesRobtAvictsAsignadas = new ArrayList();
    
    for(int i = 0; i<= indiceVictima; i++){
        victimai= victims2R.getVictimARescatar((String)idtsVictimasAsignadas.get(i));
        costesRobtAvictsAsignadas.add(i, (int)distanciaC1toC2(robot.getRobotCoordinate(), victimai.getCoordinateVictim()));
//        victims2R.addCosteRescateAmatrizCostes(victima,victimai , (int)distanciaC1toC2(victima.getCoordinateVictim(), victimai.getCoordinateVictim()));
    }
    System.out.println(" Los costes desde la posicion del robot a las victimas asignadas son : " + costesRobtAvictsAsignadas);  
    // obtencion de la matriz de costes
   int[][] matrizCostes = victims2R.getMatrizCostesVictimasAsign(costesRobtAvictsAsignadas, trazar);
   if (trazar) {
      System.out.println(" Se ha calculado la matriz de coste  " ); 
      imprimirMatriz(matrizCostes);
    }
   int dimMatrizObtenida=matrizCostes.length;
    int camino[] = dijkstra(matrizCostes,0);
//   CaminoMasCorto obtenerCamino = new CaminoMasCorto();
//  int[]camino= obtenerCamino.dijkstra(matrizCostes, 0);
//   obtenerCamino.printSolution(camino, 20);
  // la evaluacion sera el valor del camino hasta  la posicion del indice 
  printSolution(camino,dimMatrizObtenida);
  return camino[dimMatrizObtenida-1];
}
private void imprimirMatriz(int[][] matriz){
        for (int x=0; x < matriz.length; x++){
        for (int y=0; y < matriz[x].length; y++)
              System.out.print(" | " + matriz[x][y]+ " | ");   
        System.out.println("\n----------------------------------------");

}
}
    //Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas actualmente, mas el tiempo que tardara en atender a la nueva victima
    //El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo
    public int CalculoCosteAyudarVictima(String nombreAgenteEmisor, Coordinate robotLocation, RobotStatus1 robot, Victim victima, VictimsToRescue victims2R, MisObjetivos misObjs, String identFuncEval) {
        this.identAgenteQusaCoste = nombreAgenteEmisor;
    //    Caso la victima de la que se pide calcular el coste 
    // caso 1 tiene mayor prioridad que las victimas actualmente asignadas al robot
    // se calcula la distancia desde la posici�n del robot a la nueva victima
    
    // caso2 tiene igual prioridad que las de mayor prioridad
    // se extraen las victimas que tienen prioridad igual y se calcula el camino minimo que contiene a todas
    // caso 3 tiene menor prioridad
    // coste del camino minimo que contine a todas las de mayor prioridad + coste del camino minimo que contiene a todas
    // las menor prioridad que las precedentes pero de igual prioridad
        
        double distanciaCamino = this.CalculaDistanciaCamino(nombreAgenteEmisor, robotLocation, victima, victims2R, misObjs);
        double tiempoAtencionVictimas = this.CalculaTiempoAtencion(3.0, victima, victims2R, misObjs);

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
    public double CalculaTiempoAtencion(double factorMultiplicativo, Victim nuevaVictima, VictimsToRescue victims2R, MisObjetivos misObjs) {

        double tiempo = 0;     //Variable para calcular el tiempo

        //Obtener la prioridad de la victima
        int prioridadNuevaVictima = nuevaVictima.getPriority();
        // si la victima no esta entre las vicitimas a rescatar o en los objetivos

        PriorityBlockingQueue<Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();

        Iterator<Objetivo> it = colaobjetivos.iterator();
        boolean hayVictimasArescatar = victims2R.hayVictimasArescatar();

        while (it.hasNext() && hayVictimasArescatar) {
            //Hay al menos un objetivo
            Objetivo ob = it.next();
            String referenciaIdObjetivo = ob.getobjectReferenceId();
            //Obtener la victima de la cola
            if (referenciaIdObjetivo != null) {
                Victim victimaActualCola = victims2R.getVictimARescatar(referenciaIdObjetivo);
                if (victimaActualCola != null) {
                    int prioridadVictimaActualCola = victimaActualCola.getPriority();
                    tiempo = tiempo + (factorMultiplicativo * prioridadVictimaActualCola);
                }
            }
        }
        tiempo = tiempo + (factorMultiplicativo * prioridadNuevaVictima);
        return tiempo;
    }

    //Calcula la distancia del camino que pasa por las victimas actualmente asignadas, incluyendo la nueva victima actual que ha llegado.
    //El orden de visita de victimas esta determinado por las prioridades de las victimas, es decir, las de mayor prioridad se visitan primero.
    //Resaltar que cuandno hay varias victimas con la misma prioridad, se visitara primero aquella que lleva mas tiempo en la cola (mis objetivos).
//    public double costeCaminoMinimoRoVictima(String nombreAgenteEmisor, Coordinate posicionActualRobot, Victim nuevaVictima, VictimsToRescue victims2R, MisObjetivos misObjs) {
//        
//    }
    public double CalculaDistanciaCamino(String nombreAgenteEmisor, Coordinate posicionActualRobot, Victim nuevaVictima, VictimsToRescue victims2R, MisObjetivos misObjs) {
        boolean flag = true;
        double distancia = 0;     //Variable para calcular la distancia para recorrer el camino de las victimas actualmente asignadas, incluyendo la nueva victima actual que ha llegado 
        Coordinate coordinateNuevaVictima = nuevaVictima.getCoordinateVictim();
        int prioridadNuevaVictima = nuevaVictima.getPriority();
        Objetivo ob;
        PriorityBlockingQueue<Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();
        int tamaniocola = colaobjetivos.size();

        Victim victimaAnteriorCola = null;
        Coordinate coordinateVictimaAnteriorCola = null;
        distancia = distanciaC1toC2(posicionActualRobot, coordinateNuevaVictima);
        if (tamaniocola > 1) {

            Iterator<Objetivo> it = colaobjetivos.iterator();

            //Mostrar informacion del calculo de la evaluacion en ventana de trazas Evaluacion
            //trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "CALCULANDO DISTANCIA .......... Agente " + nombreAgenteEmisor, InfoTraza.NivelTraza.info)); 
            int i = 1;
            while (it.hasNext()) {
                //Hay al menos un objetivo
                ob = it.next();
                String referenciaIdObjetivo = ob.getobjectReferenceId();
                Victim victimaActualCola = victims2R.getVictimARescatar(referenciaIdObjetivo);
                if (ob.getState() != Objetivo.SOLVED || victimaActualCola != null) {
                    //Obtener la victima de la cola
                    System.out.println(" Objetivo : " + ob.getgoalId() + "  Calculo la prioridad de la victima ->" + referenciaIdObjetivo);
                    int prioridadVictimaActualCola = victimaActualCola.getPriority();
                    Coordinate coordinateVictimaActualCola = victimaActualCola.getCoordinateVictim();
                    if (flag == false) {
                        //terminar el recorrido por el resto de victimas de la cola
                        distancia = distancia + distanciaC1toC2(coordinateVictimaAnteriorCola, coordinateVictimaActualCola);
                    } else {
                        if (prioridadVictimaActualCola >= prioridadNuevaVictima) {
                            if (i == 1) {
                                //distancia del robot a la nueva victima
                                distancia = distanciaC1toC2(posicionActualRobot, coordinateVictimaActualCola);
                            } else {
                                distancia = distancia + distanciaC1toC2(coordinateVictimaAnteriorCola, coordinateVictimaActualCola);
                            }
                            if (i == tamaniocola) {
                                distancia = distancia + distanciaC1toC2(coordinateVictimaActualCola, coordinateNuevaVictima);
                            }
                        } else { //prioridadVictimaActualCola < prioridadNuevaVictima
                            if (i == 1) {
                                distancia = distanciaC1toC2(posicionActualRobot, coordinateNuevaVictima);
                            } else {
                                distancia = distancia + distanciaC1toC2(coordinateVictimaAnteriorCola, coordinateNuevaVictima);
                            }
                            distancia = distancia + distanciaC1toC2(coordinateNuevaVictima, coordinateVictimaActualCola);
                            flag = false;
                        }
                    }
                    i = i + 1;
                    victimaAnteriorCola = victimaActualCola;
                    coordinateVictimaAnteriorCola = victimaAnteriorCola.getCoordinateVictim();
                }
            } //fin del while
        }
        //trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", 
        //		"CalculaDistancia: Agente " + nombreAgenteEmisor + ": Distancia->" + distancia 
        //		, InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           

        return distancia;
    }

    //Calcula la distancia entre dos puntos
    public double distanciaC1toC2(Coordinate c1, Coordinate c2) {

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
    public double calculaCosteRecorrerCaminoVictimas(String nombreAgenteEmisor, Coordinate posicionActualRobot, VictimsToRescue victims2R, MisObjetivos misObjs) {
        double tiempoCamino = 0;     //Variable para calcular el tiempo para recorrer el camino de las victimas asignadas        
        double tiempoAux = 0;

        PriorityBlockingQueue<Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();
        int tamaniocola = colaobjetivos.size();

        Iterator<Objetivo> it = colaobjetivos.iterator();

        if (tamaniocola == 0) {
            return 0;
        }

        int i = 1;
        Coordinate coordinateaux = new Coordinate(0.0, 0.0, 0.0);
        coordinateaux = posicionActualRobot;

        while (it.hasNext()) {
            //Hay al menos un objetivo    		
            Objetivo ob = it.next();
            String referenciaIdObjetivo = ob.getobjectReferenceId();

            //Obtener la victima de la cola
            Victim victimaActualCola = victims2R.getVictimARescatar(referenciaIdObjetivo);
            Coordinate coordinateVictimaActualCola = victimaActualCola.getCoordinateVictim();
            if (i == 1) {
                //distancia del robot a la nueva victima
                tiempoAux = tiempoAux + distanciaC1toC2(coordinateaux, coordinateVictimaActualCola);
                coordinateaux = coordinateVictimaActualCola;
                i = 2;
            } else {
                //distancia de una victima a la siguiente victima a visitar
                tiempoAux = tiempoAux + distanciaC1toC2(coordinateaux, coordinateVictimaActualCola);
                coordinateaux = coordinateVictimaActualCola;
                i++;
            }
        }
        System.out.println("Numero de victimas asignadas al robot " + nombreAgenteEmisor + " -> " + (i - 1));
        tiempoCamino = tiempoAux;
        return tiempoCamino;
    }

    //Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas
    //El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo
    public double calculaCosteAtencionVictimasFinalesAsignadas(double factorMultiplicativo, VictimsToRescue victims2R, MisObjetivos misObjs) {

        double tiempo = 0;     //Variable para calcular el tiempo

        PriorityBlockingQueue<Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();
        int tamaniocola = colaobjetivos.size();

        Iterator<Objetivo> it = colaobjetivos.iterator();

        if (tamaniocola == 0) {
            return 0;
        }

        while (it.hasNext()) {
            //Hay al menos un objetivo
            Objetivo ob = it.next();
            String referenciaIdObjetivo = ob.getobjectReferenceId();

            //Obtener la victima de la cola
            Victim victimaActualCola = victims2R.getVictimARescatar(referenciaIdObjetivo);
            int prioridadVictimaActualCola = victimaActualCola.getPriority();

            tiempo = tiempo + (factorMultiplicativo * prioridadVictimaActualCola);
        }
        return tiempo;
    }

    public double calculaCosteTotalCompletarMisionAtenderVictimasFinalesAsignadas(double par1TiempoRecorrerCaminoVictimasAsignadas, double pesoPar1,
            double par2TiempoAtencionVictimasAsignadas, double pesoPar2) {
        double resultado;

        resultado = (par1TiempoRecorrerCaminoVictimasAsignadas * pesoPar1)
                + (par2TiempoAtencionVictimasAsignadas * pesoPar2);

        return resultado;
    }

    //----------------------------------------------------
    //   METODOS UTILIDADES (NO EVALUAN COSTE)
    //----------------------------------------------------
    //El string finaliza en un numero.
    //Este metodo devuelve la posicion en el que empieza el numero.
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

    private int getNumberStartIndex(String s) {
        int index = 0;

        for (int x = s.length() - 1; x >= 0; x--) {
            char ch = s.charAt(x);
            String sch = "" + ch;
            int chint = (int) ch;
            int numberchint = chint - 48; //48 es el valor ascii del 0
            if ((numberchint < 0) || (numberchint >= 10)) //no es un numero
            {
                return x + 1;
            }
        }
        return index;
    }

    //El string finaliza en un numero.
    //Este metodo devuelve el substring que contiene el numero    
    private String getNumber(String s, int index) {
        String stringNumber;
        stringNumber = s.substring(index);
        return stringNumber;
    }
    private int minDistance(int[] dist, boolean[] verticeYaProcesado) {
            // Initialize min value
            int min = Integer.MAX_VALUE;
            int min_index = 0;

            for (int v = 0; v < dist.length; v++) {
                if (verticeYaProcesado[v] == false && dist[v] <= min) {
                    min = dist[v];
                    min_index = v;
                }
            }

            return min_index;
        }
    private int[] dijkstra(int[][] grafo, int src) {
            int dimMatriz=grafo.length;
            int[] dist = new int[dimMatriz];
            // dist[i] guarda la distancia mas corta desde src hasta el vertice i

            boolean[] verticeYaProcesado = new boolean[dimMatriz];
            //Este arreglo tiene true si el vertice i ya fue procesado

            // Initialize all distances as INFINITE and stpSet[] as false
            for (int i = 0; i < dimMatriz; i++) {
                dist[i] = Integer.MAX_VALUE;
                verticeYaProcesado[i] = false;
            }
            // La distancia del vertice origen hacia el mismo es siempre 0
            dist[src] = 0;

            //Encuentra el camino mas corto para todos los vertices
            for (int count = 0; count < dimMatriz- 1; count++) {

                //Toma el vertice con la distancia minima del cojunto de vertices aun no procesados
                //En la primera iteracion siempre se devuelve src
                int u = minDistance(dist, verticeYaProcesado);

                // Se marca como ya procesado
                verticeYaProcesado[u] = true;

                // Update dist value of the adjacent vertices of the picked vertex.
                for (int v = 0; v < dimMatriz; v++) //Se actualiza la dist[v] solo si no esta en verticeYaProcesado, hay un
                //arco desde u a v y el peso total del camino desde src hasta v a traves de u es 
                // mas pequeno que el valor actual de dist[v]
                {
                    if (!verticeYaProcesado[v] && grafo[u][v] > 0 && dist[u] != Integer.MAX_VALUE
                            && dist[u] + grafo[u][v] < dist[v]) {
                        dist[v] = dist[u] + grafo[u][v];
                    System.out.println(" Se actualiza la distacia dist( " + v + " ) = " + dist[v] + " grafo ( "+ u + " , " + v+ " ) 0 "+grafo[u][v]+  "\n");
                        
                    }
                }
            }

            // se imprime el arreglo con las distancias
            printSolution(dist, dimMatriz);
            return dist;
        }
    
private void printSolution(int[] dist, int n) {
            System.out.println("Distancia del vertice desde el origen\n");
            for (int i = 0; i < n; i++) {
                System.out.println(i + " \t\t " + dist[i]);
            }
        }

    private class CaminoMasCorto {

        /**
         * **************************************************
         ***Algoritmo: Dijkstra (One Source Shortest Path) **Tipo: Grafos
         * **Autor: Mauricio Rojas
         */

        static final int V = 50;

// Funcion utilitaria para encontrar el vertice con la distancia minima, 
// a partir del conjunto de los vertices todavia no incluidos en el 
// camino mas corto
        private int minDistance(int[] dist, boolean[] verticeYaProcesado) {
            // Initialize min value
            int min = Integer.MAX_VALUE;
            int min_index = 0;

            for (int v = 0; v < dist.length; v++) {
                if (verticeYaProcesado[v] == false && dist[v] <= min) {
                    min = dist[v];
                    min_index = v;
                }
            }

            return min_index;
        }

// Funcion utilitaria para imprimir el arreglo de distancias calculadas
        private void printSolution(int[] dist, int n) {
            System.out.println("Distancia del vertice desde el origen\n");
            for (int i = 0; i < n; i++) {
                System.out.println(i + " \t\t " + dist[i]);
            }
        }

        public int[] dijkstra(int[][] grafo, int src) {
            int dimMatriz=grafo.length;
            int[] dist = new int[dimMatriz];
            // dist[i] guarda la distancia mas corta desde src hasta el vertice i

            boolean[] verticeYaProcesado = new boolean[dimMatriz];
            //Este arreglo tiene true si el vertice i ya fue procesado

            // Initialize all distances as INFINITE and stpSet[] as false
            for (int i = 0; i < dimMatriz; i++) {
                dist[i] = Integer.MAX_VALUE;
                verticeYaProcesado[i] = false;
            }
            // La distancia del vertice origen hacia el mismo es siempre 0
            dist[src] = 0;

            //Encuentra el camino mas corto para todos los vertices
            for (int count = 0; count < dimMatriz- 1; count++) {

                //Toma el vertice con la distancia minima del cojunto de vertices aun no procesados
                //En la primera iteracion siempre se devuelve src
                int u = minDistance(dist, verticeYaProcesado);

                // Se marca como ya procesado
                verticeYaProcesado[u] = true;

                // Update dist value of the adjacent vertices of the picked vertex.
                for (int v = 0; v < dimMatriz; v++) //Se actualiza la dist[v] solo si no esta en verticeYaProcesado, hay un
                //arco desde u a v y el peso total del camino desde src hasta v a traves de u es 
                // mas pequeno que el valor actual de dist[v]
                {
                    if (!verticeYaProcesado[v] && grafo[u][v] > 0 && dist[u] != Integer.MAX_VALUE
                            && dist[u] + grafo[u][v] < dist[v]) {
                        dist[v] = dist[u] + grafo[u][v];
                    }
                }
            }

            // se imprime el arreglo con las distancias
            printSolution(dist, dimMatriz);
            return dist;
        }
    }
}
