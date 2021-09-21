/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.InfoAgteAsignacionVictima;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
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
public class ActualizarObjsAccionyDecision extends TareaSincrona {

    private enum EstadoMovimientoRobot {
        Indefinido, RobotParado, RobotEnMovimiento, RobotBloqueado, RobotavanceImposible, enDestino, error
    }
    private ItfUsoMovimientoCtrl itfcompMov;
    private Victim victima;
    private int miEvaluacion;
    private int velocidadCruceroPordefecto;

    @Override
    public void ejecutar(Object... params) {

        //    ItfUsoRecursoEstadistica itfUsoRecursoEstadistica=null;
        velocidadCruceroPordefecto = 1;// metros por segundo
        //Para recoger estadisticas del instante de envio de victimas desde el centro de control

        try {
//             trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
            MisObjetivos misObjsAcc = (MisObjetivos) params[0];
            MisObjetivos misObjsDec = (MisObjetivos) params[1];
            Objetivo objetivoAsignado = (Objetivo) params[2];// AyudarVictima .pending
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[3];
            Focus focoActual = (Focus) params[4];
            RobotStatus1 estatusRobot = (RobotStatus1) params[5];
            victima = (Victim) params[6];
            //Para anotar en el fichero cual es mi coste
            int coste = 0;   //El coste se define como el MAYOR ENTERO - VALOR DE LA FUNCION DE EVALUACION
            //El que menor coste tiene es el que se hace cargo de la victima 
            miEvaluacion = infoDecision.getMi_eval();
            String refVictima = objetivoAsignado.getobjectReferenceId();
            this.informarControladorAsignacionVictima(refVictima);
            victima.setrobotResponsableId(identAgente);
            Objetivo nuevoObjAcc = misObjsAcc.getobjetivoMasPrioritario();
            itfcompMov = estatusRobot.getInfoCompMovt();
            Thread t = new Thread() {
                @Override
                public void run() {
                    itfcompMov.initContadorGastoEnergia();
                    itfcompMov.moverAdestino(victima.getName(), victima.getCoordinateVictim(), velocidadCruceroPordefecto);
                }
            };
            if (nuevoObjAcc == null || nuevoObjAcc.getState() == Objetivo.SOLVED) {// se pone el objetivo actual a solving y se da orden para que se empiece a mover
                objetivoAsignado.setSolving();
                misObjsAcc.addObjetivo(objetivoAsignado);
                t.start();
                estatusRobot.setidentDestino(objetivoAsignado.getobjectReferenceId());
                estatusRobot.setestadoMovimiento(EstadoMovimientoRobot.RobotEnMovimiento.name());
                this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAsignado);
//                this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsAcc);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se ejecuta la tarea : " + identTarea + " ObjAcion  mas prioritario null o solved . Nuevo objetivo asignado : " + objetivoAsignado.toString()+ "\n"
                         + " Se pone a Solving el objetivo:  " + objetivoAsignado
                        + "estado del robot : " + EstadoMovimientoRobot.RobotEnMovimiento.name() + "\n");
            } else {
                misObjsAcc.addObjetivo(objetivoAsignado);
//                this.getEnvioHechos().actualizarHecho(misObjsAcc);
                // hay un objetivo en curso ayudar victima comparamos prioridades
                if (objetivoAsignado.getPriority() <= nuevoObjAcc.getPriority()) { // tiene menor prioridad  encolamos el objetivo
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "El objetivo asignado : " + objetivoAsignado.toString()
                            + "Tiene menor o igual prioridad que el objetivo en curso  :  " + nuevoObjAcc
                            + "estado del robot : " + estatusRobot.getestadoMovimiento() + "\n");
                } else {// El objetivo actual tiene mayor prioridad
                    // se  mira si el robot se esta moviendo a rescatar la victima                      
                    if (estatusRobot.getestadoMovimiento().equalsIgnoreCase(EstadoMovimientoRobot.RobotEnMovimiento.name())) {
                        t.start();
                        estatusRobot.setidentDestino(nuevoObjAcc.getobjectReferenceId());
                        objetivoAsignado.setSolving(); // interrumpimos la ejecucion y la sustituimos por el nuevo objetivo
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAsignado);
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(nuevoObjAcc);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "  El objetivo asignado : " + objetivoAsignado.toString() + "\n"
                                + "  Tiene mayor  prioridad que el objetivo en curso. Se cambia de objetivo. Se pone a solving Objetivo1 : " + objetivoAsignado.toString() + " Se actualiza el  foco al objetivo:  " + nuevoObjAcc + "\n");
                    } else {
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Robot parado. Se espera el tratamiento de la llegada a destino : " + objetivoAsignado.toString() +  " Objetivo en curso:  " + nuevoObjAcc + "\n");
                    }// verificar si esta parado en destino Se espera a que llegue y se trate la notificacion de llegada 
                }
            }
            System.out.println("\n" + identAgente + "Se ejecuta la tarea " + identTarea + " Se actualiza el  objetivo:  " + objetivoAsignado + "\n\n");
//          Actualizamos los objetivos decision
            
            if (infoDecision != null) {
                Objetivo objetivoDecision=focoActual.getFoco();
                
                this.getEnvioHechos().eliminarHechoWithoutFireRules(objetivoDecision);
                misObjsDec.eliminarObjetivo(objetivoDecision);
                objetivoDecision=misObjsDec.getobjetivoMasPrioritario();               
                if(objetivoDecision!=null)this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoDecision);
//                else focoActual.refocusUltimoObjetivoSolving(); 
                  focoActual.setFoco(objetivoDecision);
                  this.getEnvioHechos().eliminarHecho(infoDecision);
            }
            String estadoMovRobot = estatusRobot.getestadoMovimiento();
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Refocalizo en una nueva decision o null" + "\n");
            this.getEnvioHechos().actualizarHecho(victima);
            this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Posicion Robot : " + itfcompMov.getCoordenadasActuales()+ "\n"
                    + " estado del Movimiento del Robot: " + estadoMovRobot + " Victima objetivo  :  " + estatusRobot.getidentDestino()+ "\n"
                    + " Victima asignada  :  " + victima + " El objetivo decision esta en el foco.  El foco actual es : " + focoActual + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void informarControladorAsignacionVictima(String idVictimaAsignada) {
        long tiempoActual = System.currentTimeMillis();
        InfoAgteAsignacionVictima infoVictimaAsignada = new InfoAgteAsignacionVictima(this.identAgente, idVictimaAsignada, tiempoActual, miEvaluacion);
        InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaAsignadaARobot", infoVictimaAsignada);
        this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
    }
}
