/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.InfoAgteAsignacionVictima;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author FGarijo rule "Encolar el objetivo Ayudar nueva victima despues de
 * obtener el acuerdo de todos" **when victimaCC:Victim(idVict:name) misObjs :
 * MisObjetivos() infoComMov :InfoCompMovimiento ()
 * victims2R:VictimsToRescue(victims2Rescue!=null) obj1 :AyudarVictima(state ==
 * Objetivo.PENDING, victimId == idVict) obj2: DecidirQuienVa (state ==
 * Objetivo.SOLVED,objectDecisionId == idVict )
 * obj:ConfirmacionParaIrYo(state==Objetivo.SOLVED,objectConfirmationId==idVict)
 * infoDecision: InfoParaDecidirQuienVa(idElementoDecision == idVict)
 * focoActual:Focus(foco == obj) then
 */
public class EncolarObjetivoActualizarFocoIGN1 extends TareaSincrona {

    private enum EstadoMovimientoRobot {
        Indefinido, RobotParado, RobotEnMovimiento, RobotBloqueado, RobotavanceImposible, enDestino, error
    }
    private ItfUsoMovimientoCtrl itfcompMov;
    private Victim victima;
    private int miEvaluacion;
    private int velocidadCruceroPordefecto;

    @Override
    public void ejecutar(Object... params) {
        velocidadCruceroPordefecto = 1;// metros por segundo
        //Para recoger estadisticas del instante de envio de victimas desde el centro de control
        try {
            MisObjetivos misObjs = (MisObjetivos) params[0];
            Objetivo objetivoAsignado = (Objetivo) params[1];// AyudarVictima .pending
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[2];
            Focus focoActual = (Focus) params[3];
            RobotStatus1 estatusRobot = (RobotStatus1) params[4];
            victima = (Victim) params[5];
            VictimsToRescue victimas = (VictimsToRescue) params[6];
            //Para anotar en el fichero cual es mi coste
            miEvaluacion = infoDecision.getMi_eval();
            String refVictima = objetivoAsignado.getobjectReferenceId();
            this.informarControladorAsignacionVictima(refVictima);
            Objetivo nuevoObj = misObjs.getobjetivoMasPrioritario();
            itfcompMov = estatusRobot.getInfoCompMovt();
            Thread t = new Thread() {
                @Override
                public void run() {
                    itfcompMov.initContadorGastoEnergia();
                    itfcompMov.moverAdestino(victima.getName(), victima.getCoordinateVictim(), velocidadCruceroPordefecto);
                }
            };
            if (nuevoObj == null || nuevoObj.getState() == Objetivo.SOLVED) {// se pone el objetivo actual a solving y se da orden para que se empiece a mover
                objetivoAsignado.setSolving();
                misObjs.addObjetivo(objetivoAsignado);
                t.start();
                estatusRobot.setidentDestino(objetivoAsignado.getobjectReferenceId());
                estatusRobot.setestadoMovimiento(EstadoMovimientoRobot.RobotEnMovimiento.name());
                this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAsignado);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjs);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, "No hay objetivos anteriores Objetivo considerado : " + objetivoAsignado.toString()
                        + "Se ejecuta la tarea : " + identTarea + " Se pone a Solving el objetivo:  " + objetivoAsignado
                        + "estado del robot : " + EstadoMovimientoRobot.RobotEnMovimiento.name() + "\n");
            } else {
                misObjs.addObjetivo(objetivoAsignado);
                this.getEnvioHechos().actualizarHecho(misObjs);
                // hay un objetivo en curso ayudar victima comparamos prioridades
                if (objetivoAsignado.getPriority() <= nuevoObj.getPriority()) { // tiene menor prioridad  encolamos el objetivo
//                                    misObjs.addObjetivo(objetivoAsignado);
//                                    this.getEnvioHechos().actualizarHecho(misObjs);
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "El objetivo asignado : " + objetivoAsignado.toString()
                            + "Tiene menor o igual prioridad que el objetivo en curso  :  " + nuevoObj
                            + "estado del robot : " + estatusRobot.getestadoMovimiento() + "\n");
                } else {// El objetivo actual tiene mayor prioridad
                    // se  mira si el robot se esta moviendo a rescatar la victima                      
//                    victima = victimas.getVictimToRescue(nuevoObj.getobjectReferenceId());
                    if (estatusRobot.getestadoMovimiento().equalsIgnoreCase(EstadoMovimientoRobot.RobotEnMovimiento.name())) {
//                       itfcompMov.cambiaDestino(nuevoObj.getobjectReferenceId(), victima.getCoordinateVictim());
                        t.start();
                        estatusRobot.setidentDestino(nuevoObj.getobjectReferenceId());
                        objetivoAsignado.setSolving(); // interrumpimos la ejecucion y la sustituimos por el nuevo objetivo
//                       nuevoObj.setPending();
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAsignado);
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(nuevoObj);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "  El objetivo asignado : " + objetivoAsignado.toString()
                                + "Tiene mayor  prioridad que el objetivo en curso. Se cambia de objetivo. Se pone a solving Objetivo1 : " + objetivoAsignado.toString() + " Se actualiza el  foco al objetivo:  " + nuevoObj + "\n");
                    } else {
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Robot parado. Se espera el tratamiento de la llegada a destino : " + objetivoAsignado.toString() + "Se ejecuta la tarea : " + identTarea + " Objetivo en curso:  " + nuevoObj + "\n");
                    }// verificar si esta parado en destino Se espera a que llegue y se trate la notificacion de llegada 
                }
            }
            System.out.println("\n" + identAgente + "Se ejecuta la tarea " + identTarea + " Se actualiza el  objetivo:  " + objetivoAsignado + "\n\n");
//            }
            if (infoDecision != null) {
                this.getEnvioHechos().eliminarHechoWithoutFireRules(infoDecision);
            }
            String estadoMovRobot = estatusRobot.getestadoMovimiento();
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Refocalizo en el ultimo objetivo solving" + "\n");
            focoActual.refocusUltimoObjetivoSolving();
            this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Posicion Robot : " + itfcompMov.getCoordenadasActuales()
                    + "estado del Movimiento del Robot: " + estadoMovRobot + " Victima objetivo  :  " + estatusRobot.getidentDestino()
                    + " Victima asignada  :  " + victima + " El foco actual es : " + focoActual + "\n");
        } catch (Exception e) {
        }
    }

    private void informarControladorAsignacionVictima(String idVictimaAsignada) {
        long tiempoActual = System.currentTimeMillis();
        InfoAgteAsignacionVictima infoVictimaAsignada = new InfoAgteAsignacionVictima(this.identAgente, idVictimaAsignada, tiempoActual, miEvaluacion);
        InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaAsignadaARobot", infoVictimaAsignada);
        this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
    }
}
