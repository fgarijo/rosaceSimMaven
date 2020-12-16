/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.PeticionAgente;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class PedirEvalAtodos extends TareaSincrona {
   private ArrayList <String> agentesEquipo;//resto de agentes que forman mi equipo                                
   private String nombreAgenteEmisor;
   private InfoParaDecidirQuienVa infoDecision;
	@Override
	public void ejecutar(Object... params) {
		try {       
              //"Ya tengo la evaluacion para realizar el objetivo.Se lo mando al resto". En su parte derecha llama a esta tarea
              Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];    
              infoDecision = (InfoParaDecidirQuienVa)params[1]; 
              Victim victima = (Victim)params[2]; 
              nombreAgenteEmisor = this.getAgente().getIdentAgente();
              trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :"+ identTarea , InfoTraza.NivelTraza.debug));
              if(!infoDecision.miEvaluacionEnviadaAtodos){
              agentesEquipo = infoDecision.getAgentesEquipo();
              int numAgtesEnEquipo = agentesEquipo.size();
              if(numAgtesEnEquipo ==0) trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Se ejecuta la tarea " + this.identTarea+
					 " No se puede enviar la peticion porque el grupo de agentes a los que hay que enviar la informacion esta vacio "+"\n");
              else{
                PeticionAgente peticionEval = new PeticionAgente(nombreAgenteEmisor);
                peticionEval.setidentObjectRefPeticion(objetivoEjecutantedeTarea.getobjectReferenceId());
                peticionEval.setMensajePeticion(VocabularioRosace.MsgPeticionEnvioEvaluaciones);
                peticionEval.setJustificacion(victima); // para que se sepa que evaluacion le pedimos
                this.getComunicator().informaraGrupoAgentes(peticionEval, agentesEquipo);
                infoDecision.setRespuestasEsperadas(agentesEquipo.size());
                infoDecision.setMiEvaluacionEnviadaAtodos(true);
                this.getEnvioHechos().actualizarHecho(infoDecision);
                trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Se ejecuta la tarea " + this.identTarea+ " Robots en el equipo : " + agentesEquipo +
                        " Numero de agentes de los que espero respuesta:" + agentesEquipo.size()+"\n");
                this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirEvaluaciones1,objetivoEjecutantedeTarea, 
                        nombreAgenteEmisor,  infoDecision.getidElementoDecision());
             }
              }// en el caso de que ya la haya enviado la evaluacion no hago nada
		} catch (Exception e) {
        }
    }
}
