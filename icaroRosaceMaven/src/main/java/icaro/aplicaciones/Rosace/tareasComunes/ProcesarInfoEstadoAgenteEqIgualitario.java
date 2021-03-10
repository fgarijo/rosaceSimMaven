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
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;

/**
 *
 * @author FGarijo
 */
public class ProcesarInfoEstadoAgenteEqIgualitario extends TareaSincrona {

    private InfoParaDecidirQuienVa infoDecision;
    private String idVictimaEnDecision;

    @Override
    public void ejecutar(Object... params) {
        try {
            InfoEquipo miEquipo = (InfoEquipo) params[0];
            InfoEstadoAgente infoEstadoRobot = (InfoEstadoAgente) params[1];
            VictimsToRescue victims2R = (VictimsToRescue) params[2];
            Objetivo objetivoDecision = (Objetivo) params[3];
            infoDecision = (InfoParaDecidirQuienVa) params[4];
            RobotStatus1 estatusRobot = (RobotStatus1) params[5];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
//                  RobotStatus1 estatusRobot = (RobotStatus1)peticionRecibida.getJustificacion();
            String idAgteqEnviasuEstado = infoEstadoRobot.getidentAgte();
            String identMejor = null;
            // Actualizo el equipo
            miEquipo.setEstadoTeamMember(infoEstadoRobot);
            if (objetivoDecision!=null ){
                idVictimaEnDecision = objetivoDecision.getobjectReferenceId();       
                if (miEquipo.getIDsMiembrosActivos().isEmpty()) // el robot es el unico miembro del equipo, asume la victima y considera conseguido el objetio              
                conseguirObjetivoDecision(objetivoDecision);
            } else if (infoDecision != null && infoEstadoRobot.getBloqueado()) {
                Victim victimaImplicada = victims2R.getVictimARescatar(idVictimaEnDecision);
                victimaImplicada.setrobotResponsableId(null);
                // Actualizar el equipo en infoDecision
                // Al eliminar el robot hay que ver quien es el mejor y si es el propio robot quien es el mejor debe informar al resto de 
                // que sume  la victima y si es otro robot informarle de que es el otro quien debe asumirla               
                infoDecision.eliminarAgenteEquipo(idAgteqEnviasuEstado);
                if (!infoDecision.getTengoMiEvaluacion()) {// obtengo mi evaluacion y se la envio al  motor para que siga el proceso de consecucion del objetivo
                    int miEval = victims2R.costeAyudarVictima(estatusRobot, victimaImplicada)[0];
                    infoDecision.setMi_eval(miEval);
                    victimaImplicada.setEstimatedCost(miEval);
                    this.getEnvioHechos().insertarHecho(new EvaluacionAgente(this.identAgente, miEval));
                } else if (infoDecision.gethanLlegadoTodasLasEvaluaciones()) {
                    if (infoDecision.gettengoLaMejorEvaluacion())// Tengo la mejor evaluacion mando un mensaje al equipo para asumir el objetivo
                    {
                        mandarPropuestaAsumirVictimaEquipo();
                    } else {
                        mandarPropuestaAlmejorParaAsumirObjetivo(infoDecision.dameIdentMejor());
                    }
                    infoDecision.setobjetivoAsumidoPorOtroAgte(false); // en caso de que lo hubiera asumido                
                    this.getEnvioHechos().actualizarHecho(victimaImplicada);
                    this.getEnvioHechos().actualizarHecho(infoDecision);
                    this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                            " Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado + "\n"
                            + "  idVictima implicada : " + idVictimaEnDecision + " Estado del robot proponente bloqueado? : " + infoEstadoRobot.getBloqueado() + "\n"
                            + "  El robot que tiene la mejor evaluacion es : " + infoDecision.dameIdentMejor() + "\n"
                            + " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
                }
            } else {
                this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado + "\n"
                        + " infoDecision es  null. "
                        + " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
            }
            this.getEnvioHechos().eliminarHecho(infoEstadoRobot);
        } catch (Exception e) {
        }
    }

    private void mandarPropuestaAsumirVictimaEquipo() {
        PropuestaAgente miPropuesta = new PropuestaAgente(identAgente);
        miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Oferta_Para_Ir);
        miPropuesta.setJustificacion(infoDecision.getMi_eval());
        miPropuesta.setIdentObjectRefPropuesta(idVictimaEnDecision);
        ArrayList agentesEquipo = infoDecision.getAgentesEquipo();
        trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se Ejecuta la Tarea :" + identTarea + "Enviamos la propuesta: " + VocabularioRosace.MsgPropuesta_Oferta_Para_Ir
                + "  al equipo formado por : " + agentesEquipo);
        this.getComunicator().informaraGrupoAgentes(miPropuesta, agentesEquipo);
//                 this.getEnvioHechos().actualizarHecho(infoDecision);
        this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1, null,
                identAgente, infoDecision.getidElementoDecision());
    }

    private void mandarPropuestaAlmejorParaAsumirObjetivo(String identAgteReceptor) {
        PropuestaAgente miPropuesta = new PropuestaAgente(this.identAgente);
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
}
