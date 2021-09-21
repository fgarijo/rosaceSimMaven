/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.InfoSimulador.tareasComunes;

import icaro.aplicaciones.InfoSimulador.informacion.AceptacionPropuesta;
import icaro.aplicaciones.InfoSimulador.informacion.InfoAgteAsignacionVictima;
import icaro.aplicaciones.InfoSimulador.informacion.PropuestaAgente;
import icaro.aplicaciones.InfoSimulador.informacion.RechazarPropuesta;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarPropuestaParaAsumirObjetivo extends TareaSincrona {

    int costeAyudaVictima;

    @Override
    public void ejecutar(Object... params) {
        try {
            RobotStatus1 miStatus = (RobotStatus1) params[0];
            MisObjetivos misObjtvsAccion = (MisObjetivos) params[1];
            VictimsToRescue victimasRecibidas = (VictimsToRescue) params[2];
            PropuestaAgente propuestaRecibida = (PropuestaAgente) params[3];
            String idVictima = (String) propuestaRecibida.getIdentObjectRefPropuesta();
            Victim victima = victimasRecibidas.getVictimARescatar(idVictima);
            if (!(propuestaRecibida.getMensajePropuesta().equals(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo))) {
                this.generarInformeConCausaTerminacion(this.identTarea, null, this.identAgente, VocabularioRosace.MsgContenidoPropuestaNoValida, CausaTerminacionTarea.ERROR);
                trazas.aceptaNuevaTraza(new InfoTraza(this.identAgente, "El mensaje de la propuesta Recibida No es valido :  " + propuestaRecibida, InfoTraza.NivelTraza.error));
            } else {
                // Se supone que el coste ya ha sido calculado por el robot

                if (miStatus.getBloqueado()) {
                    costeAyudaVictima = Integer.MAX_VALUE;
                } else {// recalculo el coste             
                    int camino[] = victimasRecibidas.costeAyudarVictima(miStatus, victima);
                    costeAyudaVictima = camino[0];
                    victima.setEstimatedCost(costeAyudaVictima);
                }
                if (costeAyudaVictima > -1 && !miStatus.getBloqueado()) {
                    AceptacionPropuesta aceptacionAsignacion = new AceptacionPropuesta(this.identAgente, VocabularioRosace.MsgAceptacionPropuesta, propuestaRecibida);
                    aceptacionAsignacion.setidentObjectRefAcetPropuesta(propuestaRecibida.getIdentObjectRefPropuesta());
                    victimasRecibidas.addVictimAsignada(victima);
                    this.getComunicator().enviarInfoAotroAgente(aceptacionAsignacion, propuestaRecibida.getIdentAgente());
                    this.getEnvioHechos().eliminarHechoWithoutFireRules(propuestaRecibida);
                    this.getEnvioHechos().actualizarHechoWithoutFireRules(victima);
                    this.getEnvioHechos().insertarHecho(aceptacionAsignacion);
                    trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se acepta la propuesta:  " + propuestaRecibida);
//                    long tiempoActual = System.currentTimeMillis();
                    //ENVIAR INFORMACION AL AGENTE CONTROLADOR DEL SIMULADOR           
//                    InfoAgteAsignacionVictima infoVictimaAsignada = new InfoAgteAsignacionVictima(this.identAgente, victima.getName(), tiempoActual, costeAyudaVictima);
//                    InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaAsignadaARobot", infoVictimaAsignada);
//                    this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
                    informarControladorAsignacionVictima(idVictima);
                } else { // no se acepta la propuesta por no disponer de recursos - como lo indica la evaluacion
                    RechazarPropuesta rechazoAsignacion = new RechazarPropuesta(this.identAgente, VocabularioRosace.MsgAceptacionPropuesta, propuestaRecibida);
                    rechazoAsignacion.setJustificacion(costeAyudaVictima);
                    this.getEnvioHechos().insertarHecho(rechazoAsignacion);
                    trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se Rechaza la propuesta:  " + propuestaRecibida + "Justificacion mi evaluacion : " + costeAyudaVictima);
                }
            }
        } catch (Exception e) {
        }

    }

    private void informarControladorAsignacionVictima(String idVictimaAsignada) {
        long tiempoActual = System.currentTimeMillis();
        InfoAgteAsignacionVictima infoVictimaAsignada = new InfoAgteAsignacionVictima(this.identAgente, idVictimaAsignada, tiempoActual, costeAyudaVictima);
        InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaAsignadaARobot", infoVictimaAsignada);
        this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
    }
}
