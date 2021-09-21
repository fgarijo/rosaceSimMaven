/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.InfoSimulador.tareasComunes;

import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEstadoAgente;
import icaro.aplicaciones.InfoSimulador.informacion.PropuestaAgente;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.InfoSimulador.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
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
            Focus focoActual = (Focus) params[7];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
//                  RobotStatus1 estatusRobot = (RobotStatus1)peticionRecibida.getJustificacion();
            String idAgteqEnviasuEstado = infoEstadoRobot.getidentAgte();
            String identMejor = null;
            ArrayList<String> idsVictimasAsignadas;
            // Actualizo el equipo
            miEquipo.setEstadoTeamMember(infoEstadoRobot);
            this.getEnvioHechos().eliminarHecho(infoEstadoRobot);
            if (miEquipo.getIDsMiembrosActivos().isEmpty()) {
                hacermeCargoVictimasNoRescatadas(estatusRobot, focoActual);
                if (objetivoDecision!=null){
                    objetivoDecision.setSolved();
                    this.getEnvioHechos().actualizarHecho(objetivoDecision);
                }
            } else { // quedam miembros activos en el equipo 
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
                                this.getEnvioHechos().actualizarHecho(newDecision);       
                                this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                                        " Se ejecuta la tarea : " + this.getIdentTarea() + " Estado del robot proponente bloqueado : " + idAgteqEnviasuEstado + "\n"
                                        + "  Se generan   objetivos decision  : " + newDecision + "\n");
                            }
                        }
                    }
                }
                if (objetivoDecision != null) {
                    idVictimaEnDecision = objetivoDecision.getobjectReferenceId();
                    // el robot es el unico miembro del equipo, asume la victima y considera conseguido el objetio              
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
            }
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

    private void hacermeCargoVictimasNoRescatadas(RobotStatus1 miStatus, Focus foco) {
        Victim victima;
        for (int i = 0; i < victims2R.getVictimsARescatar().size(); i++) {
            victima = victims2R.getVictimaARescatar(i);
            if (!victima.getRescued()) {
                int camino[] = victims2R.costeAyudarVictima(miStatus, victima);
                victima.setEstimatedCost(camino[0]);
                victima.setrobotResponsableId(this.getIdentAgente());
                victims2R.addVictimAsignada(victima);
            }
        }
        String victimaId = victims2R.getIdVictimaMasProxima();
        AyudarVictima objAyuda = new AyudarVictima(victimaId);
        this.getEnvioHechos().insertarHecho(objAyuda);
        foco.setFoco(objAyuda);   
        if (infoDecision != null) {
            this.getEnvioHechos().eliminarHecho(infoDecision);
        }  
        this.getEnvioHechos().actualizarHecho(foco);
        trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " No hay robots disponibles para realizar la tarea : "
                + " Mis victimas asignadas : " + victims2R.getIdtsVictimsAsignadas() + " Victima mas  proxima : " + victimaId + "  Se actualiza el  foco  : " + objAyuda + "\n");
    }

}
