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
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;

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
    private Victim victimaRescatable;
    private int miEvaluacion;
    private final int incrementoPrioridad = 10;
    private int velocidadCruceroPordefecto;
    private MisObjetivos misObjsAcc;
    private RobotStatus1 estatusRobot;
    private boolean actualizarEstatusRobot= false;
    private Focus focoActual;

    @Override
    public void ejecutar(Object... params) {
        velocidadCruceroPordefecto = 1;// metros por segundo
        try {
             misObjsAcc = (MisObjetivos) params[0];
            MisObjetivos misObjsDec = (MisObjetivos) params[1];
            Objetivo objetivoAccion = (Objetivo) params[2];// AyudarVictima .pending
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[3];
             focoActual = (Focus) params[4];
             estatusRobot = (RobotStatus1) params[5];
            Victim victimaAsignada = (Victim) params[6];
            VictimsToRescue victimas = (VictimsToRescue) params[7];
            boolean hayObjetivosDecisionPendientes= false;
            boolean hayObjetivosAccionPendientes= false;
            
            // Actualizar objetivos Decision
            String idVictimaAsignada = victimaAsignada.getName();
            Objetivo objAccionMasPrioritario=null;
            if (infoDecision != null) {
                // se elimina el objetivo decision del motor y de los objetivos decision
                Objetivo objetivoDecision = focoActual.getFoco(); // objetivoDecision.Solved
                this.getEnvioHechos().eliminarHechoWithoutFireRules(objetivoDecision);
                // se obtiene un uevo objetivo decision
//                objetivoDecision.setPriority(-1);
//                misObjsDec.addObjetivo(objetivoDecision);
//                objetivoDecision = misObjsDec.getobjetivoMasPrioritario();
                    misObjsDec.eliminarObjetivo(objetivoDecision);
                    objetivoDecision = misObjsDec.getobjetivoMasPrioritario();              
                if (objetivoDecision == null) {
                    hayObjetivosDecisionPendientes=false;
                }else {
//                     Si hay mas decisiones pendientes se continua con el proceso de decision
                    hayObjetivosDecisionPendientes=true;
//                    this.getEnvioHechos().actualizarHecho(objetivoDecision);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se elimina el objetivo decision : " + focoActual.getFoco() + "\n"
                                + " Objetivos decision pendientes : " + misObjsDec.getMisObjetivosPriorizados().toString() + "\n"
                                + " Objetivo mas prioritario : " + objetivoDecision.getobjectReferenceId() + "\n"
                                + " Victimas Asignadas : " + victimas.getIdtsVictimsAsignadas() + " Victimas mas proxima : " + victimas.getIdVictimaMasProxima() + "\n");
                }
              misObjsDec.setobjetivoEnCurso(objetivoDecision);
//              
//               
                String idvictimaRescatable;
                    ArrayList victsAsignadasIds;
                    Objetivo objAccionEnCurso = misObjsAcc.getobjetivoEnCurso();
                    victsAsignadasIds = victimas.getIdtsVictimsAsignadas();
                if (!infoDecision.gettengoLaMejorEvaluacion()) { 
                    // Se debe eliminar la victima asignada porque se ha anyadido para calcular el coste
                    // Si no se ha anyadido no se hace nada
                    victimas.elimVictimAsignada(idVictimaAsignada);
//                    System.out.println(" El objetivo accion en curso es : Null . se ejecuta el objetivo accion con el ident : " + victimaAsignada.getName() + "  \n\n");
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, " La victima ha sido asignada a otro agente. Se elimina la victima asignada : " +
                                idVictimaAsignada + "\n");               
                    // Si hay victimas asignadas se procede a salvar las victimas si el robot no esta haciendo nada
                  if (objAccionEnCurso == null || objAccionEnCurso.getState() == Objetivo.SOLVED) {
                        // No hay objetivos accion iniciados Se inicia el proceso de salvar a la victima mas proxima si no es null      
                        victimaRescatable = victimas.getVictimaMasProxima();             
                    if(victimaRescatable!=null ){
                        // se pone el objetivo accion actual a solving y se da orden para que se empiece a mover 
                        idvictimaRescatable=victimaRescatable.getName();
                         if (!idVictimaAsignada.equals(idvictimaRescatable)) {
                        objetivoAccion = new AyudarVictima(idvictimaRescatable);
                        objetivoAccion.setPriority(victimaRescatable.getPriority() + incrementoPrioridad);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, " El identificador de la victima mas proxima : " + idvictimaRescatable + "\n"
                                + " No coincide con el  identificador de la victima asignada :  " + idVictimaAsignada
                                + " Mis victimas asignadas : " + victimas.getIdtsVictimsAsignadas() + "  \n");
                    } // en caso de que sean iguales se toma como objetivo accion el recuperado por la regla
                         trazas.aceptaNuevaTrazaEjecReglas(identAgente, "  Se da la orden de salvar a la victima : " + idvictimaRescatable + "\n");
//                        objetivoAccion.setSolving();                      
//                        misObjsAcc.setobjetivoEnCurso(objetivoAccion);
                        ordenarSalvamentoVictima(objetivoAccion);
//                        estatusRobot.setidentDestino(idvictimaRescatable);
//                        estatusRobot.setestadoMovimiento(EstadoMovimientoRobot.RobotEnMovimiento.name());
//                        actualizarEstatusRobot=true;
////                        this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
////                        this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
//                        focoActual.setFoco(objetivoAccion);  
                  }else  trazas.aceptaNuevaTrazaEjecReglas(identAgente, " la victima ha sido asignada a otro robot pero no hay victimas asignadas \n"+
                          " Hay un objetivo accion en curso no Null ni Solving : " + objAccionEnCurso+ " Se espera a que el robot llegue a destino  \n");
                    }
                  else trazas.aceptaNuevaTrazaEjecReglas(identAgente, " la victima ha sido asignada a otro robot " + "  \n"+
                          " Hay un objetivo accion en curso  Solving : " + objAccionEnCurso+ " Se espera a que el robot llegue a destino  \n");
                  focoActual.setFoco(objetivoDecision);
                } else { // La victima ha sido asignada al robot
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, " la victima ha sido asignada al robot. Se informa al controlador " + "  \n");
                    miEvaluacion = infoDecision.getMi_eval();
//                    String refVictima = victimaAsignada.getName();
                    this.informarControladorAsignacionVictima(idVictimaAsignada);
                    victimaAsignada.setrobotResponsableId(identAgente);
                    victimaAsignada.setEstimatedCost(miEvaluacion);
                    this.getEnvioHechos().actualizarHecho(victimaAsignada);
                    victimaRescatable = victimas.getVictimaMasProxima();
                    idvictimaRescatable=victimaRescatable.getName();
                    if (!idVictimaAsignada.equals(idvictimaRescatable)) {
                        objetivoAccion = new AyudarVictima(idvictimaRescatable);
                        objetivoAccion.setPriority(victimaRescatable.getPriority() + incrementoPrioridad);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, " El identificador de la victima mas proxima : " + idvictimaRescatable + "\n"
                                + " No coincide con el  identificador de la victima asignada :  " + idVictimaAsignada
                                + " Mis victimas asignadas : " + victsAsignadasIds + "\n");
                    }
//                         objetivoAccion.setSolving();
//                    this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
//                    misObjsAcc.addObjetivo(objetivoAccion);
                    //        En el caso de que misObjAccion fuera null ya hemos anyadido un objetivo, luego no puede ser null
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se ejecuta la tarea : " + identTarea + " Objetivo Decision Solved para la victima : " + idVictimaAsignada + "\n"
                            + " Se ha tomado una decision sobre la victima :  " + victimaAsignada.getName()
                            + " Mis victimas asignadas : " + victimas.getVictimsAsignadas() + " Victima mas proxima: " + idvictimaRescatable + "\n");
                    itfcompMov = estatusRobot.getInfoCompMovt();
//                     objAccionMasPrioritario = misObjsAcc.getobjetivoMasPrioritario(); // Da problemas y lo quito
                    // El objetivo mas prioritario no puede ser null pq se ha anyadido un objetivo, pero puede que sea diferente del anyadido
//                    this.getEnvioHechos().actualizarHechoWithoutFireRules(objAccionMasPrioritario);
                    System.out.println("\n" + identAgente + "Se ejecuta la tarea " + identTarea + " La victima mas proxima es :  " + idvictimaRescatable
                            + " El ident de la  victima asignada es : " + idVictimaAsignada + " El ident del objetivo mas prioritario es :  "
                            + objetivoAccion.getobjectReferenceId() + " Mis victimas asignadas :  " + victsAsignadasIds + " \n\n");
                    if (objAccionEnCurso == null || objAccionEnCurso.getState() == Objetivo.SOLVED) {
                        // No hay objetivos accion iniciados Se inicia el proceso de salvar a la victima asignada
                        // se pone el objetivo accion actual a solving y se da orden para que se empiece a mover     
//                        System.out.println(" El objetivo accion en curso es : Null . se ejecuta el objetivo accion con el ident : " + victimaAsignada.getName() + "  \n\n");
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, " El objetivo accion en curso es : Null . se ejecuta el objetivo accion con el ident : " + objetivoAccion.getobjectReferenceId() + "\n");
//                        misObjsAcc.setobjetivoEnCurso(objetivoAccion);
                        ordenarSalvamentoVictima(objetivoAccion);
//                        estatusRobot.setidentDestino(idvictimaRescatable);
//                        estatusRobot.setestadoMovimiento(EstadoMovimientoRobot.RobotEnMovimiento.name());
////                        this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
//                        actualizarEstatusRobot=true;
//                        focoActual.setFoco(objetivoAccion);
                        
                    } else // if (objAccionEnCurso.getState() == Objetivo.SOLVING) Tiene forzosamente estado Solving
                        if (!objAccionEnCurso.getobjectReferenceId().equals(idvictimaRescatable)) {
                    // hay un objetivo en curso ayudar victima que no coincide con la victima rescatable comparamos prioridades
                    // El objetivo accion es el proporcionado por la regla o el nuevo generado por la victima mas proxima
                    if (objetivoAccion.getPriority() <= objAccionEnCurso.getPriority()) {
                        // tiene menor prioridad  no se hace nada y se espera a que llegue a destino
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, " El objetivo mas prioritario : " + objetivoAccion.getobjectReferenceId()
                                + " Tiene menor o igual prioridad que el objetivo en curso  :  " + objAccionEnCurso.getobjectReferenceId()
                                + " estado del robot : " + estatusRobot.getestadoMovimiento() + "\n");
                    } else {// El objetivo mas prioritario obtenido tiene mayor prioridad
                        // los valores del start estan en victima asignada
//                        misObjsAcc.setobjetivoEnCurso(objetivoAccion);
//                        t.start();
                        objAccionEnCurso.setPending();                       
                        ordenarSalvamentoVictima(objetivoAccion);
//                        estatusRobot.setidentDestino(objetivoAccion.getobjectReferenceId());
//                         focoActual.setFoco(objetivoAccion);
                         this.getEnvioHechos().actualizarHechoWithoutFireRules(objAccionEnCurso);
//                            this.getEnvioHechos().actualizarHechoWithoutFireRules(objMasPrioritario);
                        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "  El objetivo asignado : " + objetivoAccion.getobjectReferenceId() + "\n"
                                + "  Tiene mayor  prioridad que el objetivo en curso. Se ejecuta este objetivo : " + objAccionEnCurso.getobjectReferenceId() + "\n");
                    }
                    // El robt esta en marcha para salvar a una victima que coincide con la victima mas cercana
                    System.out.println("\n" + identAgente + "Se ejecuta la tarea " + identTarea + " Se actualiza el  objetivo:  " + objetivoAccion.getobjectReferenceId() + "\n\n");

                    this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);  
                } 
                }    
              
                 // Si hay mas decisiones pendientes se continua con ellas
   // Se termina la tarea actualizando la informacion del motor de acuerdo con las acciones realizadas
                this.getEnvioHechos().actualizarHecho(victimaAsignada);
                this.getEnvioHechos().eliminarHecho(infoDecision);
                if (hayObjetivosDecisionPendientes){
                    focoActual.setFoco(objetivoDecision);
                    this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoDecision);
                } 
//                this.getEnvioHechos().eliminarHechoWithoutFireRules(objetivoDecision);
//                if(hayObjetivosDecisionPendientes)
//                this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
//                if ( actualizarEstatusRobot) this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                this.getEnvioHechos().actualizarHecho(focoActual);
                String estadoMovRobot = estatusRobot.getestadoMovimiento(); 
//                this.getEnvioHechos().actualizarHecho(victimaAsignada);
//            
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Posicion Robot : " + estatusRobot.getRobotCoordinate() + "\n"
                        + " estado del Movimiento del Robot: " + estadoMovRobot + " Victima en objetivo accion recibido :  " + objetivoAccion.getobjectReferenceId() + "\n"
                        + " Victima implicada en la decision  :  " + victimaAsignada.getName() + "   El foco actual es : " + focoActual.getFoco() + "\n");

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
    private void ordenarSalvamentoVictima(Objetivo objetivoAccion){
        Thread t;
        t = new Thread() {
            @Override
            public void run() {
                itfcompMov.initContadorGastoEnergia();
                itfcompMov.moverAdestino(victimaRescatable.getName(), victimaRescatable.getCoordinateVictim(), velocidadCruceroPordefecto);
            }
        };
         objetivoAccion.setSolving(); 
         misObjsAcc.setobjetivoEnCurso(objetivoAccion);
         misObjsAcc.addObjetivo(objetivoAccion);
                        estatusRobot.setidentDestino(victimaRescatable.getName());
                        estatusRobot.setestadoMovimiento(EstadoMovimientoRobot.RobotEnMovimiento.name());
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
//                        actualizarEstatusRobot=true;
                        focoActual.setFoco(objetivoAccion);
         t.run();
    }
//    private void actualizarInfoMotor(){
//        this.getEnvioHechos().actualizarHecho(victimaAsignada);
//        this.getEnvioHechos().eliminarHecho(infoDecision);
//         this.getEnvioHechos().eliminarHechoWithoutFireRules(objetivoDecision);
//         this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoAccion);
//         this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
//         this.getEnvioHechos().actualizarHecho(focoActual);
//    }
}
