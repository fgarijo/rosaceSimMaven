/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaAlMejorParaQueRealiceObjetivo extends TareaSincrona {

    /**
     *     */
    private InfoParaDecidirQuienVa infoDecision;
    private String nombreAgenteReceptor;
    @Override
    public void ejecutar(Object... params) {
        try {
            Objetivo objetivoEjecutantedeTarea = (Objetivo) params[0];
            infoDecision = (InfoParaDecidirQuienVa) params[1];
            //             nombreAgenteReceptor = (String)params[2];
            try {
                //               if (! infoDecision.getheInformadoAlmejorParaQueAsumaElObjetivo() ){ // si ya se le ha informado no se hace nada

                nombreAgenteReceptor = infoDecision.dameIdentMejor();
                PropuestaAgente miPropuesta = new PropuestaAgente(this.identAgente);
                miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Q_vayaOtro);
                miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
                miPropuesta.setJustificacion(infoDecision.getMi_eval());

                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Se envia propuesta : "
                        + VocabularioRosace.MsgPropuesta_Para_Q_vayaOtro + " Al Agente : " + nombreAgenteReceptor + " MiEvaluacion : " + infoDecision.getMi_eval());
                        
                this.getComunicator().enviarInfoAotroAgente(miPropuesta, nombreAgenteReceptor);
                infoDecision.setheInformadoAlmejorParaQueAsumaElObjetivo(true);
                this.getEnvioHechos().actualizarHecho(infoDecision);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Genera un timeout de :" + VocabularioRosace.TimeOutMiliSecConseguirObjetivo + 
                         " Con mensaje  : " + VocabularioRosace.MsgTimeoutRecibirConfirmacionAsumirObjetivo);
                this.generarInformeTemporizado(VocabularioRosace.TimeOutMiliSecConseguirObjetivo, identTarea, objetivoEjecutantedeTarea, identAgente, VocabularioRosace.MsgTimeoutRecibirConfirmacionAsumirObjetivo);
                
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

}
