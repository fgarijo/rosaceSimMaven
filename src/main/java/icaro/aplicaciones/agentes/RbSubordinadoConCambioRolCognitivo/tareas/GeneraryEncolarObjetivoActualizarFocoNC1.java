/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbSubordinadoConCambioRolCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.AceptacionPropuesta;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
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
public class GeneraryEncolarObjetivoActualizarFocoNC1 extends TareaSincrona {

    private enum EstadoMovimientoRobot {
        Indefinido, RobotParado, RobotEnMovimiento, RobotBloqueado, RobotavanceImposible, enDestino, error
    }
    private ItfUsoMovimientoCtrl itfcompMov;
    private final int velocidadCruceroPordefecto = 1;
    private Victim victima ;
    @Override
    public void ejecutar(Object... params) {
        try {
            MisObjetivos misObjsAccion = (MisObjetivos) params[0];
            Focus focoActual = (Focus) params[1];
            victima = (Victim) params[2];
            AceptacionPropuesta propuestaAceptada = (AceptacionPropuesta) params[3];
            RobotStatus1 estatusRobot = (RobotStatus1) params[4];
            VictimsToRescue victimas = (VictimsToRescue) params[5];
            Thread t = new Thread() {
                @Override
                public void run() {
                    itfcompMov.moverAdestino(victima.getName(), victima.getCoordinateVictim(), velocidadCruceroPordefecto);
                }
            };
            // Situaciones que pueden darse:
//     El robot no tiene ningun objetivo en curso         
            //  El robot tiene una victima pendiente de salvar: puede que este en destino pero no ha comunicado todavia
//      que ha llegado a destino           
//       El robot tiene focalizado un objetivo conseguido. Esta parado en destino esperando recibir nuevas tareas     
            AyudarVictima objetivoAsignado = new AyudarVictima(victima.getName());
            objetivoAsignado.setPriority(victima.getPriority());
            victimas.addVictimARescatar(victima);
            Objetivo objetivoActual = misObjsAccion.getobjetivoEnCurso();
            String estadoMovRobot = null;
            itfcompMov = estatusRobot.getInfoCompMovt();
            //     El robot no tiene ningun objetivo en curso o el que tiene esta conseguido           
            if (objetivoActual == null || objetivoActual.getState() == Objetivo.SOLVED) {// se pone el objetivo actual a solving y se da orden para que se empiece a mover
                misObjsAccion.addObjetivo(objetivoAsignado);
                objetivoAsignado.setSolving();
                focoActual.setFoco(objetivoAsignado);
                estatusRobot.setidentDestino(objetivoAsignado.getobjectReferenceId());
                itfcompMov.initContadorGastoEnergia();
                t.run();
                estadoMovRobot = EstadoMovimientoRobot.RobotEnMovimiento.name();
                estatusRobot.setestadoMovimiento(estadoMovRobot);
                misObjsAccion.setobjetivoEnCurso(objetivoAsignado);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAsignado);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, "No hay objetivos anteriores Objetivo considerado : " + objetivoAsignado.toString()
                        + "Se ejecuta la tarea : " + identTarea + " Se actualiza el  foco al objetivo:  " + objetivoAsignado
                        + "estado del robot : " + estadoMovRobot + "\n");
            } else {
                // hay un objetivo en curso ayudar victima comparamos prioridades
                if (objetivoAsignado.getPriority() <= objetivoActual.getPriority()) { // tiene menor prioridad  encolamos el objetivo
                    misObjsAccion.addObjetivo(objetivoAsignado);
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "El objetivo asignado : " + objetivoAsignado.toString()
                            + "Tiene menor o igual prioridad que el objetivo en curso  :  " + objetivoActual
                            + "estado del robot : " + estadoMovRobot + "\n");
                } else {// El objetivo actual tiene mayor prioridad
                    // se  orden al movimiento de que cambie el destino                      
                    victima = victimas.getVictimARescatar(objetivoActual.getobjectReferenceId());
                    focoActual.setFoco(objetivoActual);
                    objetivoActual.setPending();
                    estatusRobot.setidentDestino(objetivoAsignado.getobjectReferenceId());
                    t.run();
                    estadoMovRobot = EstadoMovimientoRobot.RobotEnMovimiento.name();
                    estatusRobot.setestadoMovimiento(estadoMovRobot);
                    this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAsignado);
                    this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsAccion);
                    this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "  El objetivo asignado : " + objetivoAsignado.toString()
                            + "Tiene mayor  prioridad que el objetivo en curso. Se cambia de objetivo. Se pone a solving Objetivo1 : " + objetivoAsignado.toString() + " Se actualiza el  foco al objetivo:  " + objetivoActual + "\n");
                }
            }
            System.out.println("\n" + identAgente + "Se ejecuta la tarea " + identTarea + " Se actualiza el  objetivo:  " + objetivoAsignado + "\n\n");
            this.getEnvioHechos().eliminarHechoWithoutFireRules(propuestaAceptada);
            this.getEnvioHechos().actualizarHecho(focoActual);
        } catch (Exception e) {
        }

    }

    
}
