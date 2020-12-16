/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;

import icaro.aplicaciones.Rosace.informacion.EvaluacionAgente;
import icaro.aplicaciones.Rosace.informacion.InfoAgteAsignacionVictima;
import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.InfoEstadoAgente;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;

/**
 *
 * @author FGarijo
 */
public class ProcesarInfoEstadoAgenteEqJerarquico extends TareaSincrona {

    private InfoParaDecidirQuienVa infoDecision;
    private String idVictimaEnDecision;
    private VictimsToRescue victims2R;

    @Override
    public void ejecutar(Object... params) {
        try {
            InfoEquipo miEquipo = (InfoEquipo) params[0];
            InfoEstadoAgente infoEstadoRobot = (InfoEstadoAgente) params[1];
            victims2R = (VictimsToRescue) params[2];
            Objetivo objetivoDecision = (Objetivo) params[3];
            infoDecision = (InfoParaDecidirQuienVa) params[4];
            MisObjetivos objsDecision = (MisObjetivos) params[5];
            RobotStatus1 estatusRobot = (RobotStatus1) params[6];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
//                  RobotStatus1 estatusRobot = (RobotStatus1)peticionRecibida.getJustificacion();
            String idAgteqEnviasuEstado = infoEstadoRobot.getidentAgte();
            String identMejor = null;
            ArrayList<String> idsVictimasAsignadas;
            // Actualizo el equipo
            miEquipo.setEstadoTeamMember(infoEstadoRobot);
            this.getEnvioHechos().eliminarHecho(infoEstadoRobot);
            // activar los objetivos decision de las victimas asignadas al robot
            if (victims2R.getIdtsVictimsAsignadas() != null) {
                idsVictimasAsignadas = victims2R.getIdtsVictimsAsignadas();
                this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                        " Se ejecuta la tarea : " + this.getIdentTarea() + " Estado del robot proponente bloqueado : " + idAgteqEnviasuEstado + "\n"
                        + " Se actualiza el equipo.  Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n"
                        + "  Las victimas asignadas por el  robot jefe de equipo son   : " + idsVictimasAsignadas + "\n");

                if (!idsVictimasAsignadas.isEmpty()) {
                    for (String idVictima : victims2R.getIdtsVictimsAsignadas()) {
                        Victim victimaImplicada = victims2R.getVictimARescatar(idVictima);
                        if (victimaImplicada.getrobotResponsableId().equalsIgnoreCase(idAgteqEnviasuEstado)) {
                            victims2R.elimVictimAsignada(idVictima);
                            victimaImplicada.setrobotResponsableId(null);
                            victimaImplicada.setisCostEstimated(false);
                            DecidirQuienVa newDecision = new DecidirQuienVa(idVictima);
                            newDecision.setSolving();
                            objsDecision.addObjetivo(newDecision);
                            this.getEnvioHechos().actualizarHecho(victimaImplicada);
//                 this.getEnvioHechos().actualizarHecho(nuevoObjAyudarVictima);
                            this.getEnvioHechos().actualizarHecho(newDecision);
//                 this.getEnvioHechos().actualizarHecho(foco);            
                            this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                                    " Se ejecuta la tarea : " + this.getIdentTarea() + " Estado del robot proponente bloqueado : " + idAgteqEnviasuEstado + "\n"
                                    + "  Se generan   objetivos decision  : " + newDecision + "\n");
                        }
                    }
                }
            }
            if (objetivoDecision != null) {
                idVictimaEnDecision = objetivoDecision.getobjectReferenceId();
                if (miEquipo.getIDsMiembrosActivos().isEmpty()) // el robot es el unico miembro del equipo, asume la victima y considera conseguido el objetio              
                //                conseguirObjetivoDecision(objetivoDecision);
                // Debería informar al controador que no tiene robots para hacer la tares
                // tambien  pordria quedarse con la victima e  intentar salvarla el 
                {
                    this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                            "Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado + "\n"
                            + " infoDecision es  null.  y no hay miembros del equipo disponibles "
                            + " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
                }
            }
            if (infoDecision != null && infoEstadoRobot.getBloqueado()) {
                Victim victimaEnDecision = victims2R.getVictimARescatar(idVictimaEnDecision);
                victimaEnDecision.setrobotResponsableId(null);
                // Actualizar el equipo en infoDecision
                // Al eliminar el robot hay que ver quien es el mejor y si es el propio robot quien es el mejor debe informar al resto de 
                // que sume  la victima y si es otro robot informarle de que es el otro quien debe asumirla               
                infoDecision.eliminarAgenteEquipo(idAgteqEnviasuEstado);
                if (infoDecision.gethanLlegadoTodasLasEvaluaciones()) {
                    mandarPropuestaAlmejorParaAsumirObjetivo(infoDecision.dameIdentMejor());  // en caso de que lo hubiera asumido                
                    this.getEnvioHechos().actualizarHecho(victimaEnDecision);
                    this.getEnvioHechos().actualizarHecho(infoDecision);
                    this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                            " Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado + "\n"
                            + "  idVictima implicada : " + idVictimaEnDecision + " Estado del robot proponente bloqueado? : " + infoEstadoRobot.getBloqueado() + "\n"
                            + "  El robot que tiene la mejor evaluacion es : " + infoDecision.dameIdentMejor() + "\n"
                            + " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
                } else // Se debe continuar con el proceso de decision esperando que lleguen las evaluaciones que faltan
                {
                    this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                            "Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado + "\n"
                            + " Faltan evaluaciones para asignar la tarea  "
                            + " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
                }
            } else {
                this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado + "\n"
                        + " infoDecision es  null. "
                        + " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
            }
            // activar los objetivos decision de las victimas asignadas al robot
//            reactivarObjetivosAsignados( idAgteqEnviasuEstado);
//            this.getEnvioHechos().eliminarHecho(infoEstadoRobot);
        } catch (Exception e) {
        }
    }

    private void mandarPropuestaAlmejorParaAsumirObjetivo(String identAgteReceptor) {
        PropuestaAgente miPropuesta = new PropuestaAgente(this.identAgente, identAgteReceptor);
        miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo);
        miPropuesta.setIdentObjectRefPropuesta(idVictimaEnDecision);
        miPropuesta.setJustificacion(infoDecision.getMi_eval());
        trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Se envia propuesta : "
                + VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo + " Al Agente : " + identAgteReceptor + " MiEvaluacion : " + infoDecision.getMi_eval());
        this.getComunicator().enviarInfoAotroAgente(miPropuesta, identAgteReceptor);
        infoDecision.setheInformadoAlmejorParaQueAsumaElObjetivo(true);
        trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Genera un timeout de :" + VocabularioRosace.TimeOutMiliSecConseguirObjetivo
                + " Con mensaje  : " + VocabularioRosace.MsgTimeoutRecibirConfirmacionAsumirObjetivo);
        this.generarInformeTemporizado(VocabularioRosace.TimeOutMiliSecConseguirObjetivo, identTarea, null, identAgente, VocabularioRosace.MsgTimeoutRecibirConfirmacionAsumirObjetivo);
    }

    private void conseguirObjetivoDecision(Objetivo objetivoDecision) {
        objetivoDecision.setSolved();
        infoDecision.sethanLlegadoTodasLasEvaluaciones(true);
        infoDecision.settengoLaMejorEvaluacion(true);
        long tiempoActual = System.currentTimeMillis();
        InfoAgteAsignacionVictima infoVictimaAsignada = new InfoAgteAsignacionVictima(this.identAgente, idVictimaEnDecision, tiempoActual, infoDecision.getMi_eval());
        InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaAsignadaARobot", infoVictimaAsignada);
        this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
        this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoDecision);
        this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
        trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Resuelve el objetivo porque no hay miembros activos en el equipo : " + objetivoDecision.getgoalId()
                + " relativo a la victima : " + idVictimaEnDecision + " \n");

    }

//    private void reactivarObjetivosAsignados(String identAgteParado) {
//        ArrayList<String> idsVictimasAsignadasRobot = victims2R.idsVictimasAsignadasArobot(identAgteParado);
//        if (!idsVictimasAsignadasRobot.isEmpty()) {
//            for (String idVictima : idsVictimasAsignadasRobot) {
////                     AyudarVictima nuevoObjAyudarVictima= new AyudarVictima(idVictima);
//                Victim victimaImplicada = victims2R.getVictimARescatar(idVictima);
////                 nuevoObjAyudarVictima.setPriority(victimaImplicada.getPriority());
//                victimaImplicada.setrobotResponsableId(null);
//                victimaImplicada.setisCostEstimated(false);
//                //      if((objetivoEjecutantedeTarea == null)) newObjetivo.setSolving(); // se comienza el proceso para intentar conseguirlo                                        
//                //       Se genera un objetivo para decidir quien se hace cargo de la ayuda y lo ponemos a solving
//                DecidirQuienVa newDecision = new DecidirQuienVa(idVictima);
//                newDecision.setSolving();
////                 misObjsDecision.addObjetivo(newDecision);
////                 if (foco.getFoco()==null)foco.setFoco(newDecision);               
////                 this.getEnvioHechos().actualizarHecho(miEquipo);
////                 this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsDecision);
////                 peticionRecibida.setpeticionAsumida(true);
//                this.getEnvioHechos().actualizarHecho(victimaImplicada);
////                 this.getEnvioHechos().actualizarHecho(nuevoObjAyudarVictima);
//                this.getEnvioHechos().actualizarHecho(newDecision);
////                 this.getEnvioHechos().actualizarHecho(foco);            
//                this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
//                        " Se ejecuta la tarea : " + this.getIdentTarea() + " Estado del robot proponente bloqueado : " + identAgteParado + "\n"
//                        + "  Se generan   objetivos decision  : " + newDecision + "\n");
//            }
//        }
//    }
}
