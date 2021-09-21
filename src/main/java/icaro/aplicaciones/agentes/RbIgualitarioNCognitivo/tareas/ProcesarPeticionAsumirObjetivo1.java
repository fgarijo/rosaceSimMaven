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
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author FGarijo
 */
public class ProcesarPeticionAsumirObjetivo1 extends TareaSincrona {

    @Override
    public void ejecutar(Object... params) {
        try {
            Victim victimaImplicada = (Victim) params[0];
            PeticionAsumirObjetivo peticionRecibida = (PeticionAsumirObjetivo) params[1];
            MisObjetivos misObjsDecision = (MisObjetivos) params[2];
            InfoEquipo miEquipo = (InfoEquipo) params[3];
            VictimsToRescue victims2R = (VictimsToRescue) params[4];
            RobotStatus1 miStatus = (RobotStatus1) params[5];
            Focus foco = (Focus) params[6];
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[7];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
            String identAgteEnviaPeticion = peticionRecibida.getIdentAgente();
            RobotStatus1 estatusRobot = (RobotStatus1) peticionRecibida.getJustificacion();
            String idvictimaImplicada = victimaImplicada.getName();
            if (estatusRobot.getBloqueado()) {
                // Actualizo el equipo
                miEquipo.setTeamMemberStatus(estatusRobot);
                if (miEquipo.getIDsMiembrosActivos().isEmpty()) { // El robot es el unico miembro del equipo
                    hacermeCargoVictimasPendientesDecision(miStatus, victims2R);
                    String victimaId = victims2R.getIdVictimaMasProxima();
                    AyudarVictima objAyuda = new AyudarVictima(victimaId);
                    this.getEnvioHechos().insertarHecho(objAyuda);
                    if (infoDecision != null) {
                        Objetivo objetivoDecision = misObjsDecision.getobjetivoEnCurso();
                        objetivoDecision.setSolved();
                        foco.setFoco(objAyuda);
                        this.getEnvioHechos().actualizarHecho(objetivoDecision);
                        this.getEnvioHechos().eliminarHecho(infoDecision);
                    }
                    misObjsDecision.inicializar();
                    trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " No hay robots disponibles para realizar la tarea : "
                            + " Mis victimas asignadas : " + victims2R.getIdtsVictimsAsignadas() + " Victima mas  proxima : " + victimaId + "  Se actualiza el   objetivo decision   \n");
                } else {
                    if (infoDecision != null) {
                        infoDecision.eliminarAgenteEquipo(identAgteEnviaPeticion);
                        // La victima objeto de decision es distinta de la victima del objetivo que se delega
                        this.getEnvioHechos().actualizarHecho(infoDecision);
                            // la victima implicada es la misma que esta en el proceso de decision
                            // se continua con el proceso dedecision una vez que se ha actualizado InfoDecision
                            this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                                    "Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida  del robot :  " + identAgteEnviaPeticion + "\n"
                                    + " Durante el proceso de decision. idVictima implicada : " + idvictimaImplicada + " Estado del robot proponente bloqueado? : " + estatusRobot.getBloqueado()
                                    + "El foco esta en el objetivo : " + foco.getFoco() + "\n");
                        } else {
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
                        this.getEnvioHechos().actualizarHecho(newDecision);
                        this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(),
                                " Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida del robot :  " + identAgteEnviaPeticion + "\n"
                                + " idVictima implicada : " + idvictimaImplicada + " Estado del robot proponente bloqueado? : " + estatusRobot.getBloqueado() + "\n"
                                + "  Se generan los objetivos  : " + newDecision + " y : " + nuevoObjAyudarVictima + "\n"
                                + " El foco esta en el objetivo : " + foco.getFoco() + "\n"
                                + "  Miembros en mi equipo : " + miEquipo.getIDsMiembrosActivos().toString() + "\n");
                    }
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

    private void hacermeCargoVictimasPendientesDecision(RobotStatus1 miStatus, VictimsToRescue victims2R) {
        Victim victima;
        for (int i = 0; i < victims2R.getVictimsARescatar().size(); i++) {
            victima = victims2R.getVictimaARescatar(i);
            if (victima.getrobotResponsableId() == null) {
                int camino[] = victims2R.costeAyudarVictima(miStatus, victima);
                victima.setEstimatedCost(camino[0]);
                victima.setrobotResponsableId(this.getIdentAgente());
                victims2R.addVictimAsignada(victima);
            }
        }
        trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " No hay robots disponibles para realizar la tarea : "
                + " Mis victimas asignadas : " + victims2R.getIdtsVictimsAsignadas() + " Victima mas  proxima : " + victims2R.getVictimaMasProxima().getName() + "\n");
    }

}
