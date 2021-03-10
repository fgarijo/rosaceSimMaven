/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.informacion;

import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import java.util.ArrayList;

/**
 *
 * @author FGarijo
 */
public class VictimsToRescue {

    private final ArrayList<Victim> victimasARescatar;
    private Victim victimaArescatar;
    private final ArrayList<Integer> misVictimasAsignadas;
    private final ArrayList<String> misVictimasArescatarIds;
    private String robotPropietario;
    private final int[][] matrizCostes;
    private boolean hayVictimasXrescatar;
    private int[] ultimoCaminoMinimoCalculado;
    private CalculoCoste funcionCoste;
    private int energiaDisponibleRobot = 10000;
    static final int maxDim = 30;
    static final int maximoCoste = 99999;
    static final int distanciaCercana = 5;
    private boolean trazarCalculo = false;

    public VictimsToRescue(String robtPropietario) {
        robotPropietario = robtPropietario;
        misVictimasAsignadas = new ArrayList<>();
        hayVictimasXrescatar = false;
        victimasARescatar = new ArrayList<>();
        matrizCostes = new int[maxDim][maxDim];
        hayVictimasXrescatar = false;
        misVictimasArescatarIds = new ArrayList<>();
        funcionCoste = new CalculoCoste();

    }

    public void setRobotPropietario(String robtId) {
        robotPropietario = robtId;
    }

    public void setEnergiaRobotPropietario(int energiaDisponible) {
        energiaDisponibleRobot = energiaDisponible;
    }

    public synchronized int addVictimARescatar(Victim victim) {
        String identVictima = victim.getName();
        int indiceVictima = misVictimasArescatarIds.indexOf(identVictima);
        System.out.println(" La victima con identificador " + identVictima + " Se intenta anyadir  a las victimas a rescatar y su indice es : " + indiceVictima);
        if (indiceVictima < 0) {
            misVictimasArescatarIds.add(identVictima);
            victimasARescatar.add(victim);
            indiceVictima = victimasARescatar.indexOf(victim);
            matrizCostes[indiceVictima][indiceVictima] = 0;
            costeDesdeVictimaRestoDeVictimaEnMatrizCostes(indiceVictima);
            System.out.println(" La victima con identificador " + victim.getName() + " Se anyade  a las victimas a rescatar y su indice es : " + indiceVictima);
            printMatrizCostesEntreVictimas();
        }
        return indiceVictima;
    }

    private void costeDesdeVictimaRestoDeVictimaEnMatrizCostes(int posicionVictima) {
        // El coste desde la posicion de la victima en la matriz de costes al resto de victimas no esta definido
        // La posicion de la victima estara en el extremo del array
        Victim victimaNueva = (Victim) this.victimasARescatar.get(posicionVictima);
        int valorCoste;
        System.out.println("Actualizo la matriz de costes para el agente " + robotPropietario + " y la victima : " + victimaNueva.getName() + "  en la posicion : " + posicionVictima);
        if (posicionVictima > 0) {
            Victim victimai;
            for (int i = 0; i < posicionVictima; i++) {
                victimai = (Victim) victimasARescatar.get(i);
                valorCoste = (int) funcionCoste.fdistanciaYenergia(victimaNueva.getCoordinateVictim(), victimai.getCoordinateVictim(), energiaDisponibleRobot);
                matrizCostes[i][posicionVictima] = valorCoste;
                matrizCostes[posicionVictima][i] = valorCoste;
//                        System.out.println("Actualizo la matriz de costes para el agente " + robotPropietario + "y la victima : "+victimai.getName()+ "  en la posicion : " + posicionVictima
//                                + "  en la posicion : " + i + " Valor distancia : " + valorCoste);          
            }
            printMatrizCostesEntreVictimas();
        }
    }

    public boolean costeRescateEnMatrizCostes(Victim victim) {
        int indiceVictima = victimasARescatar.indexOf(victim);
        return (indiceVictima < 0 || matrizCostes[indiceVictima][indiceVictima] < 0);

    }

    public int[] costeAyudarVictima(RobotStatus1 robotStatus, Victim victimaRescatar) {
        int indiceVictimaRescatar = this.addVictimARescatar(victimaRescatar);
        System.out.println(" Victima a rescatar : " + victimaRescatar.getName() + " Se  calcula los costes del robot a las victimas asignadas  " + misVictimasAsignadas);
        //Calculo del coste del robot  a las victimas asignadas
        if (misVictimasAsignadas.isEmpty()) { // no hay victimas asignadas. Es el coste desde la posicion del robot a la victima a rescatar
            int[] camino = new int[2];
            camino[0] = (int) funcionCoste.fdistanciaYenergia(robotStatus.getRobotCoordinate(), victimaRescatar.getCoordinateVictim(), robotStatus.getAvailableEnergy());
            camino[1] = indiceVictimaRescatar;
            return camino;
        }
                // hay varias victimas asignadas
         Victim victimai;
         ArrayList costesRobtAvictsAsignadas = new ArrayList();    
        // Se calcula el coste desde la posición del robot a las victimas asignadas
        if (!misVictimasAsignadas.contains(indiceVictimaRescatar)) {
            misVictimasAsignadas.add(indiceVictimaRescatar);
        }
        if( distanciaC1toC2(robotStatus.getRobotCoordinate(), victimaRescatar.getCoordinateVictim())<=distanciaCercana){
            // Caso en que el robot este cerca de la victima a rescatar. La distancia del robot a la victima a rescatar es menor que distanciaCercana     
            // Dado que se ha calculado el coste desde la victima al resto de las victimas a rescatar. Se supone que el robot ya esta en la posicion
            // de la victima y el vector de costes del robot a las victimas asignadas se copiará de la matriz de costes
            int indiceVictima= victimasARescatar.indexOf(victimaRescatar);
            for (int i = 0; i < misVictimasAsignadas.size(); i++) {
            costesRobtAvictsAsignadas.add(i,matrizCostes[indiceVictima][i] );
        }
        }else{
        for (int i = 0; i < misVictimasAsignadas.size(); i++) {
            victimai = getVictimaARescatar(misVictimasAsignadas.get(i));
            costesRobtAvictsAsignadas.add(i, (int) funcionCoste.fdistanciaYenergia(robotStatus.getRobotCoordinate(), victimai.getCoordinateVictim(), robotStatus.getAvailableEnergy()));
        }
        }
        System.out.println(" Los costes desde la posicion del robot a las victimas asignadas son : " + costesRobtAvictsAsignadas);
        return minCaminoRobotVictsAsignadas(costesRobtAvictsAsignadas);
    }

    public void addCosteRescateAmatrizCostes(Victim victim1, Victim victim2, int valor) {
        int indiceVictima1 = victimasARescatar.indexOf(victim1);
        int indiceVictima2 = victimasARescatar.indexOf(victim2);
        if (indiceVictima1 < 0 || indiceVictima2 < 0) {
            System.out.println("Las victimas " + victim1.getName() + "o la victima" + victim2.getName() + " No han sido definidas " + "\n");
        } else {
            matrizCostes[indiceVictima1][indiceVictima2] = valor;
            matrizCostes[indiceVictima2][indiceVictima1] = valor;
        }
    }

    private void printMatrizCostesEntreVictimas() {
        System.out.println("\n Matriz de costes entre victimas recibidas del robot : " + robotPropietario);
        int dimMatriz = victimasARescatar.size();
        System.out.print("\n               ");
        for (int i = 0; i < dimMatriz; i++) {
            System.out.print(i + "(" + victimasARescatar.get(i).getName() + ") | ");
        }
        for (int i = 0; i < dimMatriz; i++) {
            System.out.print("\n  " + i + "(" + victimasARescatar.get(i).getName() + ") | ");
            for (int j = 0; j < dimMatriz; j++) {
                System.out.print("       " + matrizCostes[i][j] + " | ");
            }
            System.out.print(" \n               ---------------------------------------------------------");
        }
    }

    private void printMatrizCostesEntreVictimasAsignadasyRobot() {
        System.out.println("\n Matriz de costes entre victimas asignadas  el robot : " + robotPropietario);
        System.out.println(" Mis victimas asignadas  son  : " + misVictimasAsignadas + " Identificadores : " + getIdtsVictimsAsignadas());
        int dimMatriz = misVictimasAsignadas.size();
        int posRobotEnMatrizCostes = victimasARescatar.size() + 1;
        System.out.print("              ");
        for (int i = 0; i < dimMatriz; i++) {
            System.out.print(misVictimasAsignadas.get(i) + "(" + victimasARescatar.get(misVictimasAsignadas.get(i)).getName() + ")|");
        }
        System.out.print(" (Robot ) | \n ");
        for (int i = 0; i < dimMatriz; i++) {
            System.out.print("\n  " + misVictimasAsignadas.get(i) + "(" + victimasARescatar.get(misVictimasAsignadas.get(i)).getName() + ") |");
            for (int j = 0; j < dimMatriz; j++) {
                System.out.print("   " + matrizCostes[misVictimasAsignadas.get(i)][misVictimasAsignadas.get(j)] + "   |");
            }
            System.out.print("       " + matrizCostes[misVictimasAsignadas.get(i)][posRobotEnMatrizCostes] + "  | \n ");
            System.out.print("            ---------------------------------------------------------\n ");
        }
    }

    public int[][] getMatrizCostesVictimasAsign(ArrayList costesRobot, boolean trazar) {
        // costesRobot contiene los costes del robot a cada una de las victimas
        // costesRobot[0] ocupara la posicion [0][0]valor maximo
        // costesRobot[1]  ocupara la posicion [0][1]y [1][0] coste de salvar a la victima 1,
        // costesRobot[2]  ocupara la posicion [0][2]y [2][0]coste de salvar a la victima 2

        int dimMatriz = costesRobot.size() + 1;

        System.out.println("Calculo de la matriz de costes  Costes robot a victimas asignadas" + costesRobot + " Dimension matriz costes Robot : " + dimMatriz);
        System.out.println("Mis victimas asignadas" + misVictimasAsignadas);
//        printMatrizCostesEntreVictimas();
        int valorCoste;
        int indMatCostesVicti;
        int indMatCostesVictj = 0;
        if (dimMatriz > 2) {
            int[][] matrizVictimaAsign = new int[dimMatriz][dimMatriz];
            matrizVictimaAsign[0][0] = maximoCoste;
            for (int indiceFila = 1; indiceFila < dimMatriz; indiceFila++) {
                valorCoste = (int) costesRobot.get(indiceFila - 1);
                indMatCostesVicti = (int) misVictimasAsignadas.get(indiceFila - 1);
                matrizVictimaAsign[0][indiceFila] = valorCoste;
                matrizVictimaAsign[indiceFila][0] = valorCoste;
                if (trazar) {
                    System.out.println("Coste  desde la posicion del robot a la victima en la posicion : " + indMatCostesVicti
                            + " El valor de la matriz   ( " + " 0, " + indiceFila + " ) = " + valorCoste + " \n"
                            + "El valor de la matriz   ( " + indiceFila + " 0, " + " ) = " + valorCoste + " \n");
                }
                for (int j = 1; j < dimMatriz - 1; j++) {
                    // se obtiene el indice en la matriz de la victima
//                     indMatCostesVicti= (int)misVictimasAsignadas.get(j);
                    if (indiceFila == j) {
                        matrizVictimaAsign[indiceFila][j] = maximoCoste;
                    } else {
                        indMatCostesVictj = (int) misVictimasAsignadas.get(j - 1);
                        valorCoste = (int) matrizCostes[indMatCostesVicti][indMatCostesVictj];

                        matrizVictimaAsign[indiceFila][j] = valorCoste;
                        matrizVictimaAsign[j][indiceFila] = valorCoste;
                    }
                    matrizVictimaAsign[indiceFila][indiceFila] = maximoCoste;
                    if (trazar) {
                        System.out.println("Costes  copiados de la matriz de costes entre victimas \n"
                                + "El valor de la matriz de costes  ( " + indMatCostesVicti + ", " + indMatCostesVictj + "  ) = " + valorCoste + " \n"
                                + "Se copia a la matriz   ( " + indiceFila + ", " + j + "  ) = " + valorCoste + " \n"
                                + "Se copia a la matriz  ( " + j + ", " + indiceFila + "  ) = " + valorCoste + " \n");
                    }
                }
            }
            return matrizVictimaAsign;
        }
        return null;
    }

    public synchronized Victim getVictimARescatar(String victimId) {
        Victim victima;
        for (int i = 0; i < victimasARescatar.size(); i++) {
            victima = (Victim) victimasARescatar.get(i);
            if (victima.getName().equals(victimId)) {
                return victima;
            }
        }
        return null;
    }

    private int getIndiceVictimARescatar(String victimId) {
        if(victimasARescatar.isEmpty())return -1;
        Victim victima;
        for (int i = 0; i < victimasARescatar.size(); i++) {
            victima = (Victim) victimasARescatar.get(i);
            if (victima.getName().equals(victimId)) {
                return i;
            }
        }
        return -1;
    }

    public synchronized ArrayList<String> getIdtsVictimsAsignadas() {
        if (misVictimasAsignadas.isEmpty()) {
            return null;
        }
        ArrayList identVictimasAsignadas = new ArrayList();
        Victim victima;
        for (int i = 0; i < misVictimasAsignadas.size(); i++) {
            identVictimasAsignadas.add(i, misVictimasArescatarIds.get(misVictimasAsignadas.get(i)));
        }
        System.out.println("Los identificadores de las victimas asignadas son : " + identVictimasAsignadas);
        return identVictimasAsignadas;
    }

    public synchronized ArrayList getVictimsAsignadas() {
        return misVictimasAsignadas;
    }
public synchronized ArrayList getVictimsARescatar() {
    return victimasARescatar;
}
    public synchronized void addRobotResponsableVictim2Rescue(String victimId, String robotId) {
        Victim victima = getVictimARescatar(victimId);
        if (victima != null) {
            victima.setrobotResponsableId(robotId);
        }
    }

    public synchronized int[] minCaminoRobotVictsAsignadas(ArrayList costesRobot) {
        // Se utiliza la matriz de costes entre victimas y las victimas asignadas
        // Se ponen los valores de las distancias del robot a las victimas asignadas en la fila indiceCol = victimasARescatar.size()+1;
        // costesRobot[0] contiene el coste a la victima asignada en la posicion 0
        // costesRobot[1] contiene el coste a la victima asignada en la posicion 1
        // costesRobot[2] contiene el coste a la victima asignada en la posicion 2
        int indiceCol = victimasARescatar.size() + 1;
        matrizCostes[indiceCol][indiceCol] = maximoCoste;
        int indiceFila;
        int valorCoste;
        for (int i = 0; i < costesRobot.size(); i++) {
            indiceFila = misVictimasAsignadas.get(i);
            valorCoste = (Integer) costesRobot.get(i);
            matrizCostes[indiceFila][indiceCol] = valorCoste;
            matrizCostes[indiceCol][indiceFila] = valorCoste;
            if (trazarCalculo) {
                System.out.println("Coste  recibido del array de costes en la posicion : " + i
                        + " El valor de la matriz   ( " + indiceFila + " , " + indiceCol + " ) = " + valorCoste + " \n");
            }
        }
        printMatrizCostesEntreVictimasAsignadasyRobot();
        int i, j, k, x, y;
        int NumNodos = misVictimasAsignadas.size() + 1;
        int costeCaminoMinimo = 0;
        boolean[] nodoAlcanzado = new boolean[NumNodos];
        ultimoCaminoMinimoCalculado = new int[NumNodos];
        int[] elemImplicados = new int[NumNodos];
        nodoAlcanzado[0] = true;
        elemImplicados[0] = indiceCol; // Se han anyadido los valores en esa columna
        // copia los elementos de la matriz implicados . En la posicion 0 ponemos la columna donde se han definido las
        // distancias del robot al resto de victimas
        for (k = 1; k < NumNodos; k++) {
            nodoAlcanzado[k] = false;
            elemImplicados[k] = misVictimasAsignadas.get(k - 1);
            if (trazarCalculo) {
                System.out.println("Los elementos implicados para el calculo del camino son  : " + elemImplicados[k]
                        + " Se le asigna el valor definido en la posicion :  " + (k - 1) + " del array de victimas asignadas \n");
            }
        }
        ultimoCaminoMinimoCalculado[0] = 0;
        int nuevoCoste, costeMinimo;
        for (k = 1; k < NumNodos; k++) {
            x = y = 0;
            costeMinimo = maximoCoste;
            for (i = 0; i < NumNodos; i++) {
                for (j = 0; j < NumNodos; j++) {
                    indiceFila = elemImplicados[j];
                    indiceCol = elemImplicados[i];
                    nuevoCoste = matrizCostes[indiceFila][indiceCol];
//                    System.out.println("Se explora el nodo de la matriz de coste   :( " + indiceFila + " , " + indiceCol + " ) = " + nuevoCoste + " \n"
//                            + " El coste minimo es : " + costeMinimo + " \n");
                    if (nodoAlcanzado[i] && !nodoAlcanzado[j] && i != j
                            && nuevoCoste < costeMinimo) {
                        costeMinimo = nuevoCoste;
                        x = i;
                        y = j;
//                        System.out.println("Se cambia el valor del coste minimo ( " + indiceFila + " , " + indiceCol + " ) = " + nuevoCoste + " \n"
//                                + " El coste minimo es : " + costeMinimo + " \n"
//                                + " El valor de la matriz   ( " + indiceFila + " , " + indiceCol + " ) = " + costeMinimo + " \n");
                    }
                }
            }
            costeCaminoMinimo = costeCaminoMinimo + matrizCostes[elemImplicados[x]][elemImplicados[y]];
//            System.out.println("Arco de  coste  minimo: ("
//                    + +x + ","
//                    + +y + ")"
//                    + "coste = " + costeMinimo + " Coste del camino recorrido : " + costeCaminoMinimo);

            ultimoCaminoMinimoCalculado[k] = elemImplicados[y];
            nodoAlcanzado[y] = true;
//            System.out.println();
        }
        ultimoCaminoMinimoCalculado[0] = costeCaminoMinimo;
        for (i = 0; i < NumNodos; i++) {
            System.out.println(ultimoCaminoMinimoCalculado[i] + " --> " + i);
        }
        ordenarVictimasAsignadas();
        return ultimoCaminoMinimoCalculado;
    }

    public synchronized void elimVictimAsignada(String idVict) {
        int indiceVictima = getIndiceVictimARescatar(idVict);
        if (indiceVictima < 0) {
            return;
        }
        System.out.println(" Mis victimas asignadas antes de eliminar la victima : " + idVict + " son :  " + misVictimasAsignadas);
        for (int i = 0; i < misVictimasAsignadas.size(); i++) {
            if (misVictimasAsignadas.get(i) == indiceVictima) {
                misVictimasAsignadas.remove(i);
                System.out.println(" Se elimina la victima con identificador :  " + idVict + " que ocupa la  posicion : " + i + " en el vector de victimas asignadas"
                        + " Mis victimas asignadas quedan   " + misVictimasAsignadas);
            }
        }
    }

    public synchronized void addVictimAsignada(Victim victima) {
        int indiceVictima = victimasARescatar.indexOf(victima);
        System.out.println(" Orden addVictimAsignada. Mis victimas asignadas son:   " + misVictimasAsignadas);
        if (indiceVictima >= 0 && !misVictimasAsignadas.contains(indiceVictima)) {
            misVictimasAsignadas.add(indiceVictima);
            hayVictimasXrescatar = true;
            System.out.println(" Se anyade el indice de  la victima " + indiceVictima + " con identificador " + victima.getName() + " a las victimas asignadas"
                    + " Mis victimas asignadas quedan   " + misVictimasAsignadas);
        }
    }

    public void setCaminoMinimo(int[] camino) {
     ultimoCaminoMinimoCalculado=camino;
        this.ordenarVictimasAsignadas();
    }
    public synchronized void setRescued(String victimId) {
        Victim victima = getVictimARescatar(victimId);
        if (victima != null) {
            victima.setRescued();
        }
        if (victimId.equals(victimaArescatar.getName())) {
            victimaArescatar = null;
        }
    }

    public synchronized void setVictimaARescatar(Victim victim) {
        victimaArescatar = victim;
    }

    public synchronized Victim getVictimaARescatar(Integer indiceVictima) {
        if(indiceVictima<0||indiceVictima>victimasARescatar.size())return null;
        return (Victim)victimasARescatar.get(indiceVictima);
        }

    public synchronized void eliminarVictima(String victimId) {
        if (misVictimasAsignadas.isEmpty()) {
            return;
    }
        Victim victima = getVictimARescatar(victimId);
        Integer indiceVictima;

        if (victima != null) {
            indiceVictima = victimasARescatar.indexOf(victima);
//        victimasARescatar.remove(victima);

            boolean resultado = misVictimasAsignadas.remove(indiceVictima);
            System.out.println(" Se intenta eliminar  la victima con indice " + indiceVictima
                    + " Las victimas asignadas son :  " + misVictimasAsignadas + "Se ha eliminado el indice : " + resultado);

        }

    }
public synchronized ArrayList idsVictimasAsignadasArobot( String robotId){
    ArrayList<String> victimasAsigndasRobot = new ArrayList();
    for ( Victim victim : victimasARescatar){
        if( victim.getrobotResponsableId().equalsIgnoreCase(robotId))victimasAsigndasRobot.add(victim.getName());
    }
    return victimasAsigndasRobot;
}
    public synchronized boolean hayVictimasArescatar() {
        return !victimasARescatar.isEmpty();
    }

    public synchronized boolean victimaDefinida(Victim victima) {
        return victimasARescatar.indexOf(victima) >= 0;
    }

    private double distanciaC1toC2(Coordinate c1, Coordinate c2) {

        double distancia = Math.sqrt(Math.pow(c1.x - c2.x, 2)
                + Math.pow(c1.y - c2.y, 2)
                + Math.pow(c1.z - c2.z, 2)
        );

        return distancia;
    }

    private void ordenarVictimasAsignadas() {
        // Se ha almacenado  un camino minimo en ultimoCaminoMinimoCalculado donde la posición 0 tiene el coste del camino y el resto las victimas implicadas
        // decrementamos tambien los valores porque se incrementan  las posiciones cuando se calcula el camino minimo
         System.out.println(" Mis victimas asignadas. " + misVictimasAsignadas );
         if(!misVictimasAsignadas.isEmpty()){
        for ( int i=1;i<ultimoCaminoMinimoCalculado.length; i++){
            misVictimasAsignadas.set(i-1,ultimoCaminoMinimoCalculado[i]);
           System.out.println(" Se ordenan la victimas asignadas. Se sustituye  la victima asignada con indice " + (i-1)+ " por  valor del  camino i  : " + ultimoCaminoMinimoCalculado[i]+
                   " Valor sustituido en la ordenacion . : " + (ultimoCaminoMinimoCalculado[i])); 
            }
        }
    }

    public synchronized String getIdVictimaMasProxima() {
        // Estara en la posicion 0 del vector de victimas asignadas
        if(misVictimasAsignadas.isEmpty())return null;
        return getVictimaARescatar(misVictimasAsignadas.get(0)).getName();
    }
    public synchronized Victim getVictimaMasProxima() {
        // Estara en la posicion 0 del vector de victimas asignadas
        // Despues de calcular un camino se ordenan las victimas asignadas de acuerdo con el calculo de la ruta
        if(misVictimasAsignadas.isEmpty())return null;
        Victim victimaMasProx = getVictimaARescatar(misVictimasAsignadas.get(0));
//         System.out.println(" Se obtiene la victima mas proxima . El ultimo camino calculado es : " + ultimoCaminoMinimoCalculado[1]
//                    + " Las victimas asignadas son :  " + misVictimasAsignadas + "La victima mas proxima es  : " + victimaMasProx.getName());
        return victimaMasProx;
    }

    public class CalculoCoste {

        private Integer cotaMaxima;
        // Argumentos para calcular el coste 
        private int tiempoAtencionVictimas; // para tener en cuenta  el  numero de victimas asignadas
        private int numeroVictimasAsignadas; // numero de victimas a rescatar cuando se hace la evaluacion
        private int energiaDisponibleRobot;
        // Pesos asociados a los parametros
        private int pesoArgumentoDistancia;
        private int pesoArgumentoEnergia;
        private int pesoArgumentoTiempoAtencion;
        private boolean trazar = true;
        private String trazaCalculoCoste = "";
        private final double factorMultiplicativo = 3.0;
        private final double gastoMedioEnergiaPorUnidad = 0.005;

        public void CalculoCoste() {
            trazaCalculoCoste = "";
            cotaMaxima = Integer.MAX_VALUE;
        }

        //Funcion de evaluacion que solo considera distancia entre la nueva victima y la posicion del robot. NO SE CONSIDERA LA ENERGIA NI LAS VICTIMAS QUE TIENE ASIGNADAS PREVIAMENTE.
        //En este caso, el tercer parametro, robot, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
        //El cuarto parametro solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
        public double fdistanciaYenergia(Coordinate coordEnt1, Coordinate coordEnt2, int energiaDisponible) {
            double distancia = distanciaC1toC2(coordEnt1, coordEnt2);
            if (distancia >= 0 && gastoEnergetico(distancia) < energiaDisponible) {
                return distancia;
            } else {
                return maximoCoste;
            }
        }

        public int gastoEnergetico(double distancia) {
            return (int) (distancia * gastoMedioEnergiaPorUnidad);
        }

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
}
