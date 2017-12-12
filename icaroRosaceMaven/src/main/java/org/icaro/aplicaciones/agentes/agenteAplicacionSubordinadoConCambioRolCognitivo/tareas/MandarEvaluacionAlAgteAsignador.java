/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionSubordinadoConCambioRolCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.Coordinate;
import org.icaro.aplicaciones.Rosace.informacion.*;
import org.icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarEvaluacionAlAgteAsignador  extends TareaSincrona {

	/**  */
    private InterfazUsoAgente agenteReceptor;
    private ArrayList agentesEquipo, respuestasAgentes,confirmacionesAgentes,nuevasEvaluacionesAgentes,empates;//resto de agentes que forman mi equipo
    private int mi_eval, mi_eval_nueva;
    private String nombreAgenteEmisor;
    private PeticionAgente  peticionRecibida;
//    private ItfUsoRecursoTrazas trazas;
    private Coordinate robotLocation; //Localizacion del robot
    private double funcionEvaluacion; //Variable para almacenar el resultado de calcular la funcion de evaluacion utilizada
    private Integer divisor = 10000;
    private boolean trazarCalculoCoste;
    private MisObjetivos misObjtvs;
    private String identObjEvaluacion;
    private String nombreAgenteQuePideLaEvaluacion ;
    private Integer miEvalDeRespuesta;
    private VictimsToRescue victimasRecibidas ;
    private Victim victimEnPeticion ;
    private RobotStatus1 robot;
    //private TimeOutRespuestas tiempoSinRecibirRespuesta; //no usado
    
	@Override
	public void ejecutar(Object... params) {
		
          Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
   //       infoDecision = (InfoParaDecidirQuienVa)params[1];
          peticionRecibida = (PeticionAgente) params[1]; 
          misObjtvs = (MisObjetivos) params[2];
          robot = (RobotStatus1)params[3];
          victimasRecibidas = (VictimsToRescue) params[4];
                       //      EvaluacionAgente miEvaluacion = (EvaluacionAgente) params[2];
          nombreAgenteEmisor = this.getIdentAgente();
                       //             String identTareaLong = getClass().getName();
          String identTarea = this.getIdentTarea();
                       //             agentesEquipo = infoDecision.ObtenerNombreAgentesDelEquipo("robotMasterIA", nombreAgenteEmisor);
//        agentesEquipo = infoDecision.getNombreAgentesEquipoDefinidos(nombreAgenteEmisor, VocabularioRosace.IdentComunAgtesSubordinados);
          identObjEvaluacion = peticionRecibida.getidentObjectRefPeticion();
          nombreAgenteQuePideLaEvaluacion= peticionRecibida.getIdentAgente();
          try {
              trazas.aceptaNuevaTrazaEjecReglas(identAgente,"Se Ejecuta la Tarea :"+ identTarea );
          //     if (identObjEvaluacion.equals(infoDecision.idElementoDecision)) miEvalDeRespuesta = infoDecision.getMi_eval();
          //      else 
                     // Miro en la tabla de victimas si tengo la victima, 
                        victimEnPeticion = victimasRecibidas.getVictimToRescue(identObjEvaluacion);
                        if(victimEnPeticion != null){ // tengo la victima miro si tengo el valor estimado
                            if (victimEnPeticion.getisCostEstimated()) miEvalDeRespuesta = victimEnPeticion.getEstimatedCost();
                            else{ // calculo el coste y lo guardo en la victima
                               miEvalDeRespuesta= calcularCosteEstimadoVictima();
                               victimEnPeticion.setEstimatedCost(miEvalDeRespuesta);
                               this.getEnvioHechos().actualizarHechoWithoutFireRules(victimEnPeticion);
                            }
                        }
                        else {// la victima no existe -> no se ha recibido el mensaje del CC. Calculo el coste y  meto los objetivos y la victima
                            victimEnPeticion = (Victim) peticionRecibida.getJustificacion();
                            miEvalDeRespuesta= calcularCosteEstimadoVictima();
                            victimEnPeticion.setEstimatedCost(miEvalDeRespuesta);
                      //      AyudarVictima newAyudarVictima = new AyudarVictima (identObjEvaluacion);
                       //     newAyudarVictima.setPriority(victimEnPeticion.getPriority());
                            victimasRecibidas.addVictimToRescue(victimEnPeticion);
                      //      DecidirQuienVa newDecision = new DecidirQuienVa(identObjEvaluacion);
                       //     newDecision.setSolving();   
                            this.getEnvioHechos().actualizarHechoWithoutFireRules(victimasRecibidas);
                            this.getEnvioHechos().insertarHechoWithoutFireRules(victimEnPeticion);
                      //      this.getEnvioHechos().insertarHechoWithoutFireRules(newAyudarVictima);
                      //      this.getEnvioHechos().insertarHechoWithoutFireRules(newDecision);
                        }
                
              this.getEnvioHechos().eliminarHecho(peticionRecibida);
              EvaluacionAgente miEvaluacion = new EvaluacionAgente (nombreAgenteEmisor, miEvalDeRespuesta );
                               miEvaluacion.setObjectEvaluationId(identObjEvaluacion);
              trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Enviamos la evaluacion " + miEvaluacion  + "  Victima En Peticion : "+victimEnPeticion +"  Al agente : "+nombreAgenteQuePideLaEvaluacion);             
              this.getComunicator().enviarInfoAotroAgente(miEvaluacion, nombreAgenteQuePideLaEvaluacion);
              this.generarInformeOK(identTarea, objetivoEjecutantedeTarea, nombreAgenteEmisor, VocabularioRosace.ResEjTaskMiEvaluacionEnviadaAlAgtesQLaPide+ nombreAgenteQuePideLaEvaluacion);
              
//              trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "EvaluacionEnviadaAlAgente : "+ nombreAgenteQuePideLaEvaluacion, InfoTraza.NivelTraza.info));
              trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Estatus Robot. Estado" +robot.getestadoMovimiento() + " Coordenadas  : "+robotLocation + "  Destino:  " +robot.getidentDestino());
//            tiempoSinRecibirRespuesta.start();

		  } catch (Exception e) {
			  e.printStackTrace();
          }
   }
        private int calcularCosteEstimadoVictima(){
           Coste coste = new Coste();
           coste.setTrazar(true);
            robotLocation = robot.getInfoCompMovt().getCoordenadasActuales();
            robot.setRobotCoordinate(robotLocation);

//            
            //Las dos sentencias siguientes permiten utilizar la funcion de evaluacion 1 que solo considera la distancia entre el robot y la nueva victima
            //COMENATAR las dos lineas siguientes si se quiere utilizar la funcion de evaluacion 2
	        //double distanciaC1toC2 = coste.distanciaC1toC2(robotLocation, victimLocation);
	        //funcionEvaluacion = coste.FuncionEvaluacion2(distanciaC1toC2, 1.0, robot, victim);
	        //Las dos sentencias siguientes permiten utilizar la funcion de evaluacion 2 que considera el recorrido que tendria que hacer y la engergia
	        //QUITAR EL COMENTARIO de las dos lineas siguientes si se quiere utilizar la funcion de evaluacion 2
	        //SI SE UTILIZA LA funcion de evaluacion 1 entonces las dos lineas siguientes deben estar comentadas
//	        double distanciaCamino = coste.CalculaDistanciaCamino(nombreAgenteEmisor, robotLocation, victim, victims2R, misObjs);
//	        funcionEvaluacion = coste.FuncionEvaluacion2(distanciaCamino, 1.0, robot, victim);
//
            System.out.println("Realizando  la evaluacion para el Robot ->" + this.identAgente);
            System.out.println("robotLocation->"+robotLocation);
            System.out.println("para la victima ->"+victimEnPeticion.toString());
//            System.out.println("victims2R->"+victimasRecibidas.getlastVictimToRescue().toString());
//            System.out.println("misObjs->"+misObjs.getobjetivoMasPrioritario().toString());
            if (misObjtvs.getobjetivoMasPrioritario()!=null)System.out.println("misObjs->"+misObjtvs.getobjetivoMasPrioritario().toString());
	        //Las sentencias siguientes permiten utilizar la funcion de evaluacion 3 que considera el recorrido que tendria que hacer y la engergia y el tiempo
//            double distanciaCamino = coste.CalculaDistanciaCamino(this.identAgente, robotLocation, victim, victims2R, misObjs);
//            double tiempoAtencionVictimas = coste.CalculaTiempoAtencion(3.0, victim, victims2R, misObjs);
//            funcionEvaluacion = coste.FuncionEvaluacion3(distanciaCamino, 5.0, tiempoAtencionVictimas, 9.0, robot, victim);
	     funcionEvaluacion= coste.CalculoCosteAyudarVictima(identAgente, robotLocation, robot, victimEnPeticion, victimasRecibidas, misObjtvs, "FuncionEvaluacion3");
            if(trazarCalculoCoste) {
                this.trazas.aceptaNuevaTrazaEjecReglas(identAgente, coste.getTrazaCalculoCoste());
            }         
          return  mi_eval = (int)funcionEvaluacion;   //convierto de double a int porque la implementacion inicial de Paco usaba int                                  
                              
        }
 	
}
