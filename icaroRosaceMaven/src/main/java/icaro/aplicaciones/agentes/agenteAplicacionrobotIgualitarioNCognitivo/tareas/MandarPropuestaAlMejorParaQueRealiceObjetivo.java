/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaAlMejorParaQueRealiceObjetivo extends TareaSincrona {

    /**
     *
     */
    private InfoParaDecidirQuienVa infoDecision;
    private String nombreAgenteReceptor;

    @Override
    public void ejecutar(Object... params) {
        try {
            InfoEquipo miEquipo = (InfoEquipo) params[0];
            infoDecision = (InfoParaDecidirQuienVa) params[1];
            try {
                nombreAgenteReceptor = infoDecision.dameIdentMejor();
                PropuestaAgente miPropuesta = new PropuestaAgente(this.identAgente);
                miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo);
                miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
                int miEvalRespuesta;
                if(miEquipo.getidentAgenteJefeEquipo().equals(this.getIdentAgente())){
                    miEvalRespuesta = infoDecision.getEvalucionRecibidaDelAgente(nombreAgenteReceptor);
                }
                else miEvalRespuesta = infoDecision.getMi_eval();
                miPropuesta.setJustificacion(miEvalRespuesta);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Se envia propuesta : "
                        + VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo + " Al Agente : " + nombreAgenteReceptor + " MiEvaluacion : " + miEvalRespuesta);
                this.getComunicator().enviarInfoAotroAgente(miPropuesta, nombreAgenteReceptor);
                infoDecision.setheInformadoAlmejorParaQueAsumaElObjetivo(true);
                this.getEnvioHechos().actualizarHecho(infoDecision);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Genera un timeout de :" + VocabularioRosace.TimeOutMiliSecConseguirObjetivo
                        + " Con mensaje  : " + VocabularioRosace.MsgTimeoutRecibirConfirmacionAsumirObjetivo);
                this.generarInformeTemporizado(VocabularioRosace.TimeOutMiliSecConseguirObjetivo, VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1, null, identAgente, infoDecision.getidElementoDecision());
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

}
