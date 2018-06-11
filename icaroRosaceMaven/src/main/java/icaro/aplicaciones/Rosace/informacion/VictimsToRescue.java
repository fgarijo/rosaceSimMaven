/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.informacion;

import java.util.ArrayList;

/**
 *
 * @author FGarijo
 */
public class VictimsToRescue {
    //   private Map<String, Victim> victims2Rescue;

    private ArrayList<Victim> victimasARescatar;
    private Victim victimaArescatar;
    private ArrayList<Integer> misVictimasAsignadas;
    private String robotPropietario;
    private int[][] matrizCostes;
    private boolean hayVictimasXrescatar;
    private int[] ultimoCaminoMinimoCalculado;
    static final int maxDim = 30;
    static final int maximoCoste = 99999;

    public VictimsToRescue() {
        //      victims2Rescue = new HashMap <>();
        misVictimasAsignadas = new ArrayList<>();
        hayVictimasXrescatar = false;
        victimasARescatar = new ArrayList<>();
        matrizCostes = new int[maxDim][maxDim];
    }

    public void inicializar() {
        //   victims2Rescue = new HashMap <>();
        victimasARescatar = new ArrayList<>();
        misVictimasAsignadas = new ArrayList<>();
        matrizCostes = new int[maxDim][maxDim];
        matrizCostes [0][0]=0;
        hayVictimasXrescatar = false;
    }
    public void setRobotPropietario(String robtId) {
        robotPropietario= robtId;
    }

    public synchronized int addVictimARescatar(Victim victim) {
        int indiceVictima = victimasARescatar.indexOf(victim);
        System.out.println(" La victima con identificador " + victim.getName() + " Se intenta anyadir  a las victimas a rescatar y su indice es : " + indiceVictima);
        if (indiceVictima < 0) {
            victimasARescatar.add(victim);
            indiceVictima = victimasARescatar.indexOf(victim);
            matrizCostes[indiceVictima][indiceVictima] = 0;
            // Se define el indice siguiente como indefinido y se usa para verificar si la fila y columna estan o no definidas
//            matrizCostes[indiceVictima ][indiceVictima + 1] = -1;
//             matrizCostes[indiceVictima+1 ][indiceVictima ] = -1;
            distanciasDeVictimaEnMatrizCostes( indiceVictima);
            System.out.println(" La victima con identificador " + victim.getName() + " Se anyade  a las victimas a rescatar y su indice es : " + indiceVictima);
//            printMatrizCostesEntreVictimas();
        }

        return indiceVictima;
    }
    private void distanciasDeVictimaEnMatrizCostes(int posicionVictima) {
        // El coste desde la posicion de la victima en la matriz de costes al resto de victimas no esta definido
        // La posicion de la victima estara en el extremo del array
//        int indiceVictima = victims2R.addVictimARescatar(victima);
        Victim victimaNueva = this.victimasARescatar.get(posicionVictima);
        int valorCoste;
//        System.out.println("Actualizo la matriz de costes para el agente " + robotPropietario + " y para  la victima en la posicion : " + indiceVictima);
        if (posicionVictima > 0) {
            Victim victimai = null;
            for (int i = 0; i < posicionVictima; i++) {
                        victimai = victimasARescatar.get(i);   
                        valorCoste = (int) distanciaC1toC2(victimaNueva.getCoordinateVictim(), victimai.getCoordinateVictim());
                        matrizCostes[i][posicionVictima]=valorCoste;
                        matrizCostes[posicionVictima][i]=valorCoste;
//                        System.out.println("Actualizo la matriz de costes para el agente " + robotPropietario + "  en la posicion : " + posicionVictima
//                                + "  en la posicion : " + i + " Valor distancia : " + valorCoste);
                    }
                }
    }
    public  boolean costeRescateEnMatrizCostes(Victim victim) {
        int indiceVictima = victimasARescatar.indexOf(victim);
        return (indiceVictima < 0|| matrizCostes[indiceVictima][indiceVictima]<0);
         
    }

    public  void addCosteRescateAmatrizCostes(Victim victim1, Victim victim2, int valor) {
        int indiceVictima1 = addVictimARescatar(victim1);
        int indiceVictima2 = addVictimARescatar(victim2);

        matrizCostes[indiceVictima1][indiceVictima2] = valor;
        matrizCostes[indiceVictima2][indiceVictima1] = valor;
//        System.out.println("Costes  calculados de la matriz de costes entre victimas " + victim1.getName() + "y la victima" + victim2.getName() + "\n"
//                + "El valor de la matriz   ( " + indiceVictima1 + ", " + indiceVictima2 + "  ) = " + valor + " \n"
//                + "El valor de la matriz   ( " + indiceVictima2 + ", " + indiceVictima1 + "  ) = " + valor + " \n");
//        printMatrizCostesEntreVictimas();
    }

//    public synchronized void addCosteRescateAmatrizCostes(int indVictim1, int indVictim2, int valor) {
//
//        matrizCostes[indVictim1][indVictim2] = valor;
//        matrizCostes[indVictim1][indVictim2+1] = -1;
//        matrizCostes[indVictim2][indVictim1] = valor;
//        matrizCostes[indVictim2+1][indVictim1] = -1;
//        System.out.println("Costes  calculados de la matriz de costes entre victimas en la posicion: " + indVictim1 + " y la victima en la posicion: " + indVictim2 + "\n"
//                + "El valor de la matriz   ( " + indVictim1 + ", " + indVictim2 + "  ) = " + valor + " \n"
//                + "El valor de la matriz   ( " + indVictim2 + ", " + indVictim1 + "  ) = " + valor + " \n");
//    }
    private void printMatrizCostesEntreVictimas(){
        System.out.println("Matriz de costes entre victimas recibidas del robot : " + robotPropietario);
        int dimMatriz= victimasARescatar.size();
        for (int i=0; i<dimMatriz;i++){
            for (int j=0; j<matrizCostes[i].length;j++){
                System.out.print(" | " + matrizCostes[i][j]+ " | ");
            }
        System.out.println("\n--------------------------------------------");
        }
    }

    public  int[][] getMatrizCostesVictimasAsign(ArrayList costesRobot, boolean trazar) {
        // costesRobot contiene los costes del robot a cada una de las victimas
        // costesRobot[0] ocupara la posicion [0][0]valor maximo
        // costesRobot[1]  ocupara la posicion [0][1]y [1][0] coste de salvar a la victima 1,
        // costesRobot[2]  ocupara la posicion [0][2]y [2][0]coste de salvar a la victima 2,

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
                for (int j = 1; j < dimMatriz-1; j++) {
                    // se obtiene el indice en la matriz de la victima
//                     indMatCostesVicti= (int)misVictimasAsignadas.get(j);
                    if (indiceFila == j) {
                        matrizVictimaAsign[indiceFila][j] = maximoCoste;
                    } else {
                        indMatCostesVictj = (int) misVictimasAsignadas.get(j-1);
                        valorCoste = (int) matrizCostes[indMatCostesVicti][indMatCostesVictj];

                        matrizVictimaAsign[indiceFila][j] = valorCoste;
                        matrizVictimaAsign[j][indiceFila] = valorCoste;
                    }
                    matrizVictimaAsign[indiceFila][indiceFila]=maximoCoste;
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
//public synchronized Victim getVictimToRescue (String victimId){
//   return  victims2Rescue.get(victimId);
//}

    public synchronized Victim getVictimARescatar(String victimId) {
        Victim victima;
        for (int i = 0; i < victimasARescatar.size(); i++) {
            victima = victimasARescatar.get(i);
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
            victima = victimasARescatar.get(i);
            if (victima.getName().equals(victimId)) return i;
        }
        return -1;
    }
    public synchronized ArrayList getIdtsVictimsAsignadas() {
        if (misVictimasAsignadas.isEmpty()) {
            return null;
        }
        ArrayList identVictimasAsignadas = new ArrayList();
        for (int i = 0; i < misVictimasAsignadas.size(); i++) {
            String victimaId = victimasARescatar.get(misVictimasAsignadas.get(i)).getName();
            identVictimasAsignadas.add(i, victimaId);
        }
        return identVictimasAsignadas;
    }
public synchronized ArrayList getVictimsAsignadas() {
            return misVictimasAsignadas;
        }
    public synchronized void addRobotResponsableVictim2Rescue(String victimId, String robotId) {
        Victim victima = getVictimARescatar(victimId);
        if (victima != null) {
            victima.setrobotResponsableId(robotId);
        }

    }

//    public synchronized void elimVictimAsignada(String idVict) {
//        if (misVictimasAsignadas.isEmpty()) {
//            System.out.println(" El conjunto de victimas asignadas al robot es nulo ");
//        } else {
//            System.out.println(" Se procesa una peticion para eliminar la victima:  " + idVict);
//            int i = 0;
//            boolean encontrado = false;
//            int indiceVictima;
//            while (i < misVictimasAsignadas.size() && !encontrado) {
//                indiceVictima = misVictimasAsignadas.get(i);
//                if (victimasARescatar.get(indiceVictima).getName().equals(idVict)) {
//                    misVictimasAsignadas.remove(i);
//                    encontrado = true;
//                    System.out.println(" Se elimina la victima con identificador :  " + idVict + " que ocupa la  posicion : " + i + " en el vector de victimas asignadas");
//                    if (misVictimasAsignadas.isEmpty()) hayVictimasXrescatar = false;
//                }
//            }
//        } 
//    }
    public synchronized void elimVictimAsignada(String idVict) {
        int indiceVictima=getIndiceVictimARescatar(idVict);
       if( indiceVictima<0)return;
       for (int i = 0; i < misVictimasAsignadas.size(); i++) {
           if(misVictimasAsignadas.get(i)==indiceVictima){
               misVictimasAsignadas.remove(i);
               System.out.println(" Se elimina la victima con identificador :  " + idVict + " que ocupa la  posicion : " + i + " en el vector de victimas asignadas");
           }
       }
    }

    public synchronized void addVictimAsignada(Victim victima) {
        int indiceVictima = victimasARescatar.indexOf(victima);
        if (indiceVictima >= 0 && !misVictimasAsignadas.contains(indiceVictima)) {
            misVictimasAsignadas.add(indiceVictima);
        }
        hayVictimasXrescatar = true;
        System.out.println(" Se anyade el indice de  la victima " + indiceVictima + " con identificador " + victima.getName() + " a las victimas asignadas");

    }
 public void setCaminoMinimo(int[] camino) {
     ultimoCaminoMinimoCalculado=camino;
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

    public synchronized Victim getVictimaARescatar() {
        return victimaArescatar;
    }
    public synchronized Victim getVictimaARescatar(int indiceVictima) {
        if(indiceVictima<0||indiceVictima>=victimasARescatar.size())return null;
        return victimasARescatar.get(indiceVictima);
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

//    public synchronized boolean costeRescateDesdeVict(int i) {
//        // devuleve true si el coste de rescate desde la victima que esta en la posicion i al resto de las Victimas a Rescatar 
//        // al resto
//        System.out.println(" Se verifica si el coste de la victima con indice " + i + " con identificador " + victimasARescatar.get(i).getName()
//                + " tiene el valor : " + matrizCostes[i][i+1]);
//        return matrizCostes[i][i+1] >= 0;
//    }
    
//public boolean costeRescateDesdeVict(int i, int j) {
//        return matrizCostes[i][j] >= 0;
//    }
    public synchronized boolean hayVictimasArescatar() {
        return !victimasARescatar.isEmpty();
    }
//    public  int numVictimasArescatar() {
//        return victimasARescatar.size();
//    }

    public synchronized boolean victimaDefinida(Victim victima) {
        return victimasARescatar.indexOf(victima) >= 0;
    }

     private double distanciaC1toC2(Coordinate c1, Coordinate c2) {

        double distancia = Math.sqrt(Math.pow(c1.x - c2.x, 2)
                + Math.pow(c1.y - c2.y, 2)
                + Math.pow(c1.z - c2.z, 2)
        );
//            System.out.println("Coord calculo Coste c1->" + c1);
//            System.out.println("Coord calculo Coste c2->" + c2);
           
        return distancia;
    }

    private void ordenarVictimasAsignadas() {
        // Se ha almacenado  un camino minimo en ultimoCaminoMinimoCalculado donde la posición 0 tiene el coste del camino y el resto las victimas implicadas
        for ( int i=1;i<=ultimoCaminoMinimoCalculado.length; i++){
            misVictimasAsignadas.set(i,ultimoCaminoMinimoCalculado[i]);
           System.out.println(" Se ordenan la victimas asignadas. Se sustituye  la victima asignada con indice " + i+ " por  la victima en camino i con valor : " + ultimoCaminoMinimoCalculado[i]); 
        }
    }
}
