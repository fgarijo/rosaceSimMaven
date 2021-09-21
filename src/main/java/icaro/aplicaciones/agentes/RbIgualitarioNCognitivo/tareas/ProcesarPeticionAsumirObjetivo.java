/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import icaro.aplicaciones.InfoSimulador.informacion.PeticionAsumirObjetivo;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author FGarijo
 */
public class ProcesarPeticionAsumirObjetivo extends TareaSincrona {

    @Override
    public void ejecutar(Object... params) {
        try {
                Victim victimaImplicada = (Victim)  params[0];
                PeticionAsumirObjetivo peticionRecibida = (PeticionAsumirObjetivo)params[1];
                  MisObjetivos misObjsDecision= (MisObjetivos)params[2];
                  InfoEquipo miEquipo =  (InfoEquipo)params[3];
                  Focus foco = (Focus)params[4];
                  InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[5];
//                  VictimsToRescue victims = (VictimsToRescue)params[4];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
            String identAgteEnviaPeticion = peticionRecibida.getIdentAgente();
            String idVictimaEnDecision=null;
            RobotStatus1 estatusRobot = (RobotStatus1) peticionRecibida.getJustificacion();
            String idvictimaImplicada = victimaImplicada.getName();
            if (estatusRobot.getBloqueado()) {
                // Actualizo el equipo
                miEquipo.setTeamMemberStatus(estatusRobot);
                    if (infoDecision != null|| victimaImplicada.getRescued()) {
                        infoDecision.eliminarAgenteEquipo(identAgteEnviaPeticion);
                        // La victima objeto de decision es distinta de la victima del objetivo que se delega
                         idVictimaEnDecision = infoDecision.getidElementoDecision();
                        this.getEnvioHechos().actualizarHecho(infoDecision);
                            // la victima implicada es la misma que esta en el proceso de decision
                            // se continua con el proceso dedecision una vez que se ha actualizado InfoDecision
                            this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                                    "Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida  del robot :  " + identAgteEnviaPeticion + "\n"
                                    + " Durante el proceso de decision. idVictima implicada : " + idvictimaImplicada + " Estado del robot proponente bloqueado? : " + estatusRobot.getBloqueado()
                                    + "El foco esta en el objetivo : " + foco.getFoco() + "\n");
                        } 
                        if (!idvictimaImplicada.equals(idVictimaEnDecision)|| infoDecision == null) {
                        // Se acepta la victima implicada que se supone ya ha sido asignada y se pone de nuevo el objetivo decision
                        AyudarVictima nuevoObjAyudarVictima = new AyudarVictima(idvictimaImplicada);
                        nuevoObjAyudarVictima.setPriority(victimaImplicada.getPriority());
                        victimaImplicada.setrobotResponsableId(null);
                        victimaImplicada.setisCostEstimated(false);
                        DecidirQuienVa newDecision = new DecidirQuienVa(idvictimaImplicada);
                        newDecision.setSolving();
                        misObjsDecision.addObjetivo(newDecision);
                        this.getEnvioHechos().actualizarHecho(victimaImplicada);
                        this.getEnvioHechos().actualizarHecho(nuevoObjAyudarVictima);
                        this.getEnvioHechos().insertarHecho(newDecision);
                        this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                                " Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida del robot :  " + identAgteEnviaPeticion + "\n"
                                + " idVictima implicada : " + idvictimaImplicada + " Estado del robot proponente bloqueado? : " + estatusRobot.getBloqueado() + "\n"
                                + "  Se generan los objetivos  : " + newDecision + " y : " + nuevoObjAyudarVictima + "\n"
                                + " El foco esta en el objetivo : " + foco.getFoco() + "\n"
                                + "  Miembros en mi equipo : " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
                    }
                if (foco.getFoco() == null) {
                    foco.setFoco(misObjsDecision.getobjetivoMasPrioritario());
                }
                this.getEnvioHechos().actualizarHecho(foco);
            } else {
                this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida del robot :  " + identAgteEnviaPeticion + "\n"
                        + " El robot no esta bloqueado. Estado del robot proponente bloqueado? : " + estatusRobot.getBloqueado()
                        + "El foco esta en el objetivo : " + foco.getFoco() + "\n");
            }
            this.getEnvioHechos().eliminarHecho(peticionRecibida);
        } catch (Exception e) {
        }
    }

}
