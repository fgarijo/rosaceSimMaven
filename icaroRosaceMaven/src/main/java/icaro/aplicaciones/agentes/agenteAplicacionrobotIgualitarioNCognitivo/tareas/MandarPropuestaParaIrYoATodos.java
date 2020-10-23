/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.*;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaParaIrYoATodos extends TareaSincrona {

    /**
     *     */
    private ArrayList agentesEquipo, respuestasAgentes, confirmacionesAgentes, nuevasEvaluacionesAgentes, empates;//resto de agentes que forman mi equipo
    private String nombreAgenteEmisor;
    private InfoParaDecidirQuienVa infoDecision;
    private String identDeEstaTarea;
    @Override
    public void ejecutar(Object... params) {
        Objetivo objetivoEjecutantedeTarea = (Objetivo) params[0];
        infoDecision = (InfoParaDecidirQuienVa) params[1];
        nombreAgenteEmisor = this.getAgente().getIdentAgente();
        identDeEstaTarea = this.getIdentTarea();

        try {
            PropuestaAgente miPropuesta = new PropuestaAgente(nombreAgenteEmisor);
            miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Oferta_Para_Ir);
            miPropuesta.setJustificacion(infoDecision.getMi_eval());
            miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
            if (!infoDecision.miPropuestaParaAsumirElObjetivoEnviadaAtodos) { // si ya lo he enviado no hago nada asi evito problemas de invocacion en  la regla
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :" + identDeEstaTarea, InfoTraza.NivelTraza.debug));
                agentesEquipo = infoDecision.getAgentesEquipo();
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la propuesta: " + VocabularioRosace.MsgPropuesta_Oferta_Para_Ir, InfoTraza.NivelTraza.debug));
                this.getComunicator().informaraGrupoAgentes(miPropuesta, agentesEquipo);
                infoDecision.setRespuestasEsperadas(infoDecision.getAgentesEquipo().size() - 1);
                infoDecision.setMiPropuestaParaAsumirElObjetivoEnviadaAtodos(Boolean.TRUE);
                this.getEnvioHechos().actualizarHecho(infoDecision);
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Numero de agentes de los que espero respuesta:" + agentesEquipo.size(), InfoTraza.NivelTraza.info));
                this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionesRealizacionObjetivo1, objetivoEjecutantedeTarea,
                        nombreAgenteEmisor, infoDecision.getidElementoDecision());
            }
        } catch (Exception e) {
        }
    }

}