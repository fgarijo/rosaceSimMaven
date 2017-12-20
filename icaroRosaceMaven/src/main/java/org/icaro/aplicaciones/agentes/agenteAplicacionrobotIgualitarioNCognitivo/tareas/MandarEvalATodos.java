/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.EvaluacionAgente;
import org.icaro.aplicaciones.Rosace.informacion.InfoAgteAsignacionVictima;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import org.icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;


/**
 *
 * @author Francisco J Garijo
 */
   public class MandarEvalATodos  extends TareaSincrona {
    //public class MandarEvalATodos  extends TareaComunicacion {
	/**  */
   private InterfazUsoAgente agenteReceptor;
   private ArrayList <String> agentesEquipo;//resto de agentes que forman mi equipo                                
   private int mi_eval, mi_eval_nueva;
   private String nombreAgenteEmisor;
   private String idVictima;
   private InfoParaDecidirQuienVa infoDecision;

	@Override
	public void ejecutar(Object... params) {
		try {
              //El primer parametro es el objetivo DecidirQuienVa que tenia el foco en el momento de ejecutar la regla
              //"Ya tengo la evaluacion para realizar el objetivo.Se lo mando al resto". En su parte derecha llama a esta tarea
              Objetivo objetivoEjecutantedeTarea = (Objetivo) params[0];    
              infoDecision = (InfoParaDecidirQuienVa)params[1];
              EvaluacionAgente miEvaluacion = (EvaluacionAgente) params[2]; // redefino miEvaluacion 
              InfoEquipo miEquipo = (InfoEquipo) params[3]; 
              nombreAgenteEmisor = this.getIdentAgente();
              idVictima = infoDecision.getidElementoDecision();
              if(!infoDecision.miEvaluacionEnviadaAtodos){
              agentesEquipo = miEquipo.getIDsMiembrosActivos();
              trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Se Ejecuta la Tarea :"+ identTarea +"\n");
              if(agentesEquipo.size()>0){
              infoDecision.setAgentesEquipo(agentesEquipo);
              trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Agentes en el equipo : " + agentesEquipo.toString() + " Enviamos la evaluacion " + infoDecision.getMi_eval()+"\n");            
             this.getComunicator().informaraGrupoAgentes(miEvaluacion, agentesEquipo);
         //   this.getComunicator().informarConMomentoCreacionaGrupoAgentes(miEvaluacion, agentesEquipo);
              infoDecision.setRespuestasEsperadas(agentesEquipo.size());
              infoDecision.setMiEvaluacionEnviadaAtodos(true);
              infoDecision.sethanLlegadoTodasLasEvaluaciones(false);
             
              this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
       //       this.generarInformeOK(identTarea, objetivoEjecutantedeTarea, nombreAgenteEmisor, VocabularioRosace.ResEjTaskMiEvalucionEnviadaAlEquipo);
              trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Numero de agentes de los que espero respuesta:" + agentesEquipo.size()+"\n");
              this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirEvaluaciones1,  objetivoEjecutantedeTarea, 
                      nombreAgenteEmisor,  idVictima);
              }else{ // El robot es el unico disponible por ello considera que el objetivo esta conseguido
                  objetivoEjecutantedeTarea.setSolved();
                long tiempoActual = System.currentTimeMillis();
                InfoAgteAsignacionVictima infoVictimaAsignada = new InfoAgteAsignacionVictima (this.identAgente,idVictima,tiempoActual,infoDecision.getMi_eval());
                InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaAsignadaARobot",infoVictimaAsignada);
            this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
                  this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoEjecutantedeTarea);
                  trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Se Resuelve el objetivo porque no hay miembros activos en el equipo :"+ objetivoEjecutantedeTarea.getgoalId() +
                          " relativo a la victima : "+ objetivoEjecutantedeTarea.getobjectReferenceId()+" \n");
              }
               this.getEnvioHechos().eliminarHecho(miEvaluacion);
             } // en el caso de que ya la haya enviado la evaluacion no hago nada
		} catch (Exception e) {
			e.printStackTrace();
        }
    }
   
}