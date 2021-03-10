/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.InformeDeTarea;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaComunicacion;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaParaIrYoALosqNoHanConfirmado  extends TareaComunicacion {

	/**  */
    private ArrayList agentesEquipo ;//resto de agentes que forman mi equipo
        
    private String nombreAgenteEmisor;
//    private ItfUsoRecursoTrazas trazas;
    private InfoParaDecidirQuienVa infoDecision;
    private String identDeEstaTarea ;

	@Override
	public void ejecutar(Object... params) {
           Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
           infoDecision = (InfoParaDecidirQuienVa)params[1];
           nombreAgenteEmisor = this.getIdentAgente();     
           identDeEstaTarea = this.getIdentTarea();
           try {
                 PropuestaAgente miPropuesta = new PropuestaAgente (nombreAgenteEmisor);
                 miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Oferta_Para_Ir);
                 miPropuesta.setJustificacion(infoDecision.getMi_eval());
                 trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :"+ identDeEstaTarea , InfoTraza.NivelTraza.debug));
                 agentesEquipo = infoDecision.getConfirmacionesRecibidas();
                 for(int i = 0; i< infoDecision.getConfirmacionesRecibidas().size(); i++){  
                     String respuestaAgente = (String)infoDecision.getConfirmacionesRecibidas().get(i);
                     if(respuestaAgente == ""){//si aun no tenemos confirmacion, queremos que nos vuelva a mandar las cosas
                        String agenteReceptor = (String)infoDecision.getAgentesEquipo().get(i);
                        this.informaraOtroAgente(miPropuesta, agenteReceptor);
                     }
                 }
                 trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la propuesta: "+ VocabularioRosace.MsgPropuesta_Oferta_Para_Ir , InfoTraza.NivelTraza.debug));           
                 this.generarInformeTemporizado(VocabularioRosace.TimeOutMiliSecConseguirObjetivo, VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1, objetivoEjecutantedeTarea, this.getIdentAgente(), infoDecision.getidElementoDecision());
                 infoDecision.setRespuestasEsperadas(infoDecision.getAgentesEquipo().size());
                 trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Numero de agentes de los que espero respuesta:" + agentesEquipo.size(), InfoTraza.NivelTraza.info));	
           } catch(Exception e) {
           }
    }
    
 }
