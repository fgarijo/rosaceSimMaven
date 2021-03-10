/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;
import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.OrdenCentroControl;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author fgarijo
 */
public class InterpretarOdenDelCC2 extends TareaSincrona  {

    @Override
    public void ejecutar(Object... params) {
        try {
            OrdenCentroControl ccOrdenAyudarVictima = (OrdenCentroControl) params[0];
            MisObjetivos misObjsDecision = (MisObjetivos) params[1];
            RobotStatus1 estatusRobot = (RobotStatus1) params[2];
            InfoEquipo miEquipo = (InfoEquipo)params[3];
            VictimsToRescue victims2R = (VictimsToRescue) params[4];
            Focus foco = (Focus) params[5];
            Victim victim = (Victim) ccOrdenAyudarVictima.getJustificacion();
            victim = (Victim) victim.clone();
            String idVictim = victim.getName();
            String idRobot=this.getIdentAgente();
            AyudarVictima newAyudarVictima = new AyudarVictima(idVictim);
            newAyudarVictima.setPriority(victim.getPriority());
            victims2R.addVictimARescatar(victim);
            DecidirQuienVa newDecision = new DecidirQuienVa(idVictim);
            victims2R.setRobotPropietario(idRobot);
            // caso en que el robot sea el unico miembro del equipo disponible para realizar  la tarea
            if (miEquipo.getIDsMiembrosActivos().isEmpty() ){
                int miEval = victims2R.costeAyudarVictima(estatusRobot, victim)[0];
                victim.setrobotResponsableId(idRobot);
                victim.setEstimatedCost(miEval);
                victims2R.addRobotResponsableVictim2Rescue(idVictim, idRobot);
                victims2R.addVictimAsignada(victim);
                newDecision.setSolved();
                foco.setFoco(newDecision);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " No hay miembros activos en el equipo.  Se crea el  objetivo:  " + newAyudarVictima + "  y el objetivo : " + newDecision
                    + " Se pone a Solved.  Se actualiza el foco : " + foco.getFoco() + "\n" + " Victimas asignadas : " + victims2R.getVictimsAsignadas()+ "\n");
            }else {
            newDecision.setSolving();
            misObjsDecision.addObjetivo(newDecision);
            Objetivo objetivoFocalizado = foco.getFoco();
//            Objetivo objetivoFocalizado = misObjsDecision.getobjetivoMasPrioritario();
            if (objetivoFocalizado == null || objetivoFocalizado.getState() == Objetivo.SOLVED
                    || (objetivoFocalizado.getgoalId().equals(newAyudarVictima.getgoalId())
                    && objetivoFocalizado.getState() == Objetivo.SOLVING)) {
                DecidirQuienVa decisionPendiente = (DecidirQuienVa) misObjsDecision.getobjetivoMasPrioritario();
                foco.setFoco(decisionPendiente);
                this.getEnvioHechos().actualizarHecho(decisionPendiente);
//                this.getEnvioHechos().actualizarHecho(foco);
            }
            }
            this.getEnvioHechos().eliminarHecho(ccOrdenAyudarVictima);
            this.getEnvioHechos().insertarHecho(victim);
            this.getEnvioHechos().insertarHecho(newDecision);
            this.getEnvioHechos().insertarHecho(newAyudarVictima);
            this.getEnvioHechos().actualizarHecho(foco);
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Se crea el  objetivo:  " + newAyudarVictima + "  y el objetivo : " + newDecision
                    + " Se actualiza el foco : " + foco.getFoco() + "\n" + " Objetivos decision en la cola : " + misObjsDecision.getMisObjetivosPriorizados().toString()
                    + "\n" + " Objetivo mas prioritario : " + misObjsDecision.getobjetivoMasPrioritario().toString() + "\n");
            System.out.println("\n" + this.getIdentAgente() + "Se ejecuta la tarea " + this.getIdentTarea() + " Se crea el  objetivo:  " + newAyudarVictima + "\n\n");
        } catch (Exception e) {
        }
    }
}
