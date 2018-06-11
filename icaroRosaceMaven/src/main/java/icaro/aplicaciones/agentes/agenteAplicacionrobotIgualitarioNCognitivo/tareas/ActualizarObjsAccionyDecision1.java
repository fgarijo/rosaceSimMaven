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
public class ActualizarObjsAccionyDecision1 extends TareaSincrona {

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
        try {
            MisObjetivos misObjsAcc = (MisObjetivos) params[0];
            MisObjetivos misObjsDec = (MisObjetivos) params[1];
            Objetivo objetivoAccion = (Objetivo) params[2];// AyudarVictima .pending
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[3];
            Focus focoActual = (Focus) params[4];
            RobotStatus1 estatusRobot = (RobotStatus1) params[5];
            victima = (Victim) params[6];
            //Para anotar en el fichero cual es mi coste
            int coste = 0;   //El coste se define como el MAYOR ENTERO - VALOR DE LA FUNCION DE EVALUACION
            //El que menor coste tiene es el que se hace cargo de la victima 
            
            // Actualizar objetivos Decision
            if (infoDecision != null) {
                Objetivo objetivoDecision=focoActual.getFoco();
                
                this.getEnvioHechos().eliminarHechoWithoutFireRules(objetivoDecision);
                misObjsDec.eliminarObjetivo(objetivoDecision);
                objetivoDecision=misObjsDec.getobjetivoMasPrioritario(); 
                if(objetivoDecision!=null){
                 this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoDecision);
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se elimina el objetivo decision : " + objetivoDecision + "\n"+
                " Objetivos decision pendientes : " + misObjsDec.getMisObjetivosPriorizados().toString()+ "\n" +
                    " Objetivo mas prioritario : "+ misObjsDec.getobjetivoMasPrioritario().toString()+ "\n");
                }
                  focoActual.setFoco(objetivoDecision);
                  this.getEnvioHechos().eliminarHecho(infoDecision);     
//            }
            // Si el robot es responsable del objetivo se deben actualizar los objetivos Accion
            if ( infoDecision.gettengoLaMejorEvaluacion()){
            miEvaluacion = infoDecision.getMi_eval();
            String refVictima = objetivoAccion.getobjectReferenceId(); 
            this.informarControladorAsignacionVictima(refVictima);
            victima.setrobotResponsableId(identAgente);
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se ejecuta la tarea : " + identTarea + " Objetivo Decision Solved para la victima : " + objetivoAccion.toString()+ "\n"
                         + " Se pone a Solving el objetivo:  " + objetivoAccion 
                    + "  La victima ha sido asignada al robot : " + victima.getrobotResponsableId()
                        + "  Estado del robot : " + EstadoMovimientoRobot.RobotEnMovimiento.name() + "\n" );          
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
                objetivoAccion.setSolving();
                misObjsAcc.addObjetivo(objetivoAccion);
                t.start();
                estatusRobot.setidentDestino(objetivoAccion.getobjectReferenceId());
                estatusRobot.setestadoMovimiento(EstadoMovimientoRobot.RobotEnMovimiento.name());
                this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se actualiza el objetivo a solving y se anyade a los objetivos accion : " + objetivoAccion + "\n"+
                " Objetivos Accion pendientes : " + misObjsAcc.getMisObjetivosPriorizados().toString()+ "\n" +
                    " Objetivo mas prioritario : "+ misObjsAcc.getobjetivoMasPrioritario().toString()+ "\n");
//                this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsAcc);
                
            } else {
                misObjsAcc.addObjetivo(objetivoAccion);
//                this.getEnvioHechos().actualizarHecho(misObjsAcc);
                // hay un objetivo en curso ayudar victima comparamos prioridades
                if (objetivoAccion.getPriority() <= nuevoObjAcc.getPriority()) { // tiene menor prioridad  encolamos el objetivo
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "El objetivo asignado : " + objetivoAccion.toString()
                            + "Tiene menor o igual prioridad que el objetivo en curso  :  " + nuevoObjAcc
                            + "estado del robot : " + estatusRobot.getestadoMovimiento() + "\n");
                } else {// El objetivo actual tiene mayor prioridad
                    // se  mira si el robot se esta moviendo a rescatar la victima                      
                    if (estatusRobot.getestadoMovimiento().equalsIgnoreCase(EstadoMovimientoRobot.RobotEnMovimiento.name())) {
                        t.start();
                        estatusRobot.setidentDestino(nuevoObjAcc.getobjectReferenceId());
                        objetivoAccion.setSolving(); // interrumpimos la ejecucion y la sustituimos por el nuevo objetivo
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(nuevoObjAcc);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : " + identTarea + "  El objetivo asignado : " + objetivoAccion.toString() + "\n"
                                + "  Tiene mayor  prioridad que el objetivo en curso. Se cambia de objetivo. Se pone a solving Objetivo1 : " + objetivoAccion.toString() + " Se actualiza el  foco al objetivo:  " + nuevoObjAcc + "\n");
                    } else {
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Robot parado. Se espera el tratamiento de la llegada a destino : " + objetivoAccion.toString() +  " Objetivo en curso:  " + nuevoObjAcc + "\n");
                    }// verificar si esta parado en destino Se espera a que llegue y se trate la notificacion de llegada 
                }
            }
            System.out.println("\n" + identAgente + "Se ejecuta la tarea " + identTarea + " Se actualiza el  objetivo:  " + objetivoAccion + "\n\n");
//          Actualizamos los objetivos decision
            }
            
            String estadoMovRobot = estatusRobot.getestadoMovimiento();
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Refocalizo en una nueva decision o null" + "\n");
            this.getEnvioHechos().actualizarHecho(victima);
            this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Posicion Robot : " +  estatusRobot.getRobotCoordinate()+ "\n"
                    + " estado del Movimiento del Robot: " + estadoMovRobot + " Victima objetivo  :  " + estatusRobot.getidentDestino()+ "\n"
                    + " Victima asignada  :  " + victima + " El objetivo decision esta en el foco.  El foco actual es : " + focoActual + "\n");
            }
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
