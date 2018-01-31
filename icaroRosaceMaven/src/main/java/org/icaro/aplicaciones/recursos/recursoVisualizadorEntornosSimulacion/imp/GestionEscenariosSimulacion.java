/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author FGarijo
 */
public class GestionEscenariosSimulacion {
       private HashMap tablaEscenariosDefinidos;
	//arraylist que contiene los paneles visualizados
    private EscenarioSimulacionRobtsVictms infoEscenario;
//    private ItfUsoRecursoPersistenciaEntornosSimulacion itfPersistencia;
//   private LinkedList<String> listaElementosTrazables  ;
    private HashSet identsEscenarios;
    private String numRobots = "Robts_";
    private String numVictims = "Victs_";
    private String orgModelo = "Org_";
    private String  orgModeloInicial = "SinDefinir";
    private String tipoFichero=".xml";

     public GestionEscenariosSimulacion (){
        tablaEscenariosDefinidos = new HashMap();      
        identsEscenarios = new HashSet();
     }
//     public void setItfUsoPersistencia (ItfUsoRecursoPersistenciaEntornosSimulacion persitenciaItf){
//         itfPersistencia = persitenciaItf;
//     }
//     public void obtenerEscenariosGuardados (){
//           try {
//               identsEscenarios = itfPersistencia.obtenerIdentsEscenarioSimulacion();
//           } catch (Exception ex) {
//               Exceptions.printStackTrace(ex);
//           }
//}
     public synchronized String getIdentEscenario (String orgTipo,int numRobts, int numVictm){
         String identEscenarioBase = orgModelo+orgTipo+numRobots+numRobts+numVictims+numVictm;
         int indiceEscenarioRepetido = 0;
         String identEscenario=identEscenarioBase+"_"+indiceEscenarioRepetido;
         if (identsEscenarios.isEmpty())return identEscenario;       
         while (identsEscenarios.contains(identEscenario+tipoFichero)){
             indiceEscenarioRepetido ++;
             identEscenario =identEscenarioBase+"_"+indiceEscenarioRepetido;
         }
         return identEscenario;
     }
     public synchronized String getNewVersionEscenario (String idEscenario){
         String idEscenarioBase= idEscenario.substring(0,idEscenario.lastIndexOf("_"));
         int indiceEscenarioRepetido = Integer.parseInt(idEscenario.substring(idEscenario.lastIndexOf("_")+1));
        System.out.println(" Nueva version Escenario. Escenario Base : "+ idEscenarioBase + " Subindice recibido : "+indiceEscenarioRepetido ); 
         identsEscenarios.add(idEscenario+tipoFichero);
         indiceEscenarioRepetido++;
         String identEscenarioNuevo=idEscenarioBase+"_"+indiceEscenarioRepetido;
         identsEscenarios.add(identEscenarioNuevo+tipoFichero);
          System.out.println(" Nueva version Escenario. Ident escenario  :" +identEscenarioNuevo);
          return identEscenarioNuevo;
         
     }
     
     public void  setIdentsEscenariosSimulacion ( HashSet setIdentsEscenarios){
         identsEscenarios = setIdentsEscenarios;
     }
      public  HashSet getIdentsEscenariosSimulacion ( ){
          return identsEscenarios;
      }
     public EscenarioSimulacionRobtsVictms crearEscenarioSimulacion(){
         EscenarioSimulacionRobtsVictms escenarioSim = new EscenarioSimulacionRobtsVictms();
         escenarioSim.setGestorEscenarios(this);
         String identEscenario = getIdentEscenario (orgModeloInicial,0, 0);
//         escenarioSim.setIdentEscenario(getIdentEscenario (orgModeloInicial,0, 0));
         escenarioSim.setIdentEscenario (identEscenario);
         return escenarioSim;
    }
    public void addEscenario(EscenarioSimulacionRobtsVictms escenario){
        tablaEscenariosDefinidos.put(escenario.getIdentEscenario(), escenario);
        identsEscenarios.add(escenario.getIdentEscenario());
    }
    public boolean existeEscenario(String identEscenario){
        return identsEscenarios.contains(identEscenario);
    }
     public void eliminarEscenario(String identEscenario){
        tablaEscenariosDefinidos.remove(identEscenario);
        identsEscenarios.remove(identEscenario);
    }

    boolean hayEscenariosCreados() {
        if (identsEscenarios!=null)return ! identsEscenarios.isEmpty();
        return false;
    }
}
