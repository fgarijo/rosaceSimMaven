/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
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
    private ArrayList agentesEquipo;//resto de agentes que forman mi equipo
    private InfoParaDecidirQuienVa infoDecision;
    @Override
    public void ejecutar(Object... params) {
        VictimsToRescue victimas = (VictimsToRescue) params[0];
        infoDecision = (InfoParaDecidirQuienVa) params[1];
        try {
            PropuestaAgente miPropuesta = new PropuestaAgente(this.identAgente);
            miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Oferta_Para_Ir);
            miPropuesta.setJustificacion(infoDecision.getMi_eval());
            miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
            if (!infoDecision.miPropuestaParaAsumirElObjetivoEnviadaAtodos) { // si ya lo he enviado no hago nada asi evito problemas de invocacion en  la regla
//                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Agentes en el equipo : " +agentesEquipo );
                victimas.addVictimAsignada(victimas.getVictimARescatar(infoDecision.idElementoDecision));
            agentesEquipo = infoDecision.getAgentesEquipo();
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea +" se envia  la propuesta: " + VocabularioRosace.MsgPropuesta_Oferta_Para_Ir + " Agentes en el equipo : " +agentesEquipo);
                this.getComunicator().informaraGrupoAgentes(miPropuesta, agentesEquipo);
                infoDecision.setRespuestasEsperadas(infoDecision.getAgentesEquipo().size() - 1);
                infoDecision.setMiPropuestaParaAsumirElObjetivoEnviadaAtodos(Boolean.TRUE);
                this.getEnvioHechos().actualizarHecho(infoDecision);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente,  " El numero de agentes de los que espero respuesta es : " + agentesEquipo.size() + " Me asigno  la victima  : " + infoDecision.idElementoDecision +"/n" );
//                this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1, objetivoEjecutantedeTarea,
//                        nombreAgenteEmisor, infoDecision.getidElementoDecision());
                 this.generarInformeTemporizado(VocabularioRosace.TimeOutMiliSecConseguirObjetivo, VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1, null, identAgente, infoDecision.getidElementoDecision());
            }
        } catch (Exception e) {
        }
    }

}