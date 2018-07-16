/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.OrdenCentroControl;
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
 * @author FGarijo
 */
public class InterpretarOrdenDelCC extends TareaSincrona {
    @Override
    public void ejecutar(Object... params) {
        try {
            MisObjetivos misObjsDecision = (MisObjetivos) params[0];
            OrdenCentroControl ccOrdenAyudarVictima = (OrdenCentroControl) params[1];
            VictimsToRescue victims2R = (VictimsToRescue) params[2];
            Focus foco = (Focus) params[3];
            Victim victim = (Victim) ccOrdenAyudarVictima.getJustificacion();
            // String identTarea = this.getIdentTarea();
            victim = (Victim) victim.clone();
            String nombreAgenteEmisor = this.getIdentAgente();
            String idVictim = victim.getName();
            AyudarVictima newAyudarVictima = new AyudarVictima(idVictim);
            newAyudarVictima.setPriority(victim.getPriority());
            victims2R.addVictimARescatar(victim);
            victims2R.setRobotPropietario(this.identAgente);
            DecidirQuienVa newDecision = new DecidirQuienVa(idVictim);
            newDecision.setSolving();
            misObjsDecision.addObjetivo(newDecision);      
//            Objetivo objetivoFocalizado = foco.getFoco();
            Objetivo objetivoFocalizado = misObjsDecision.getobjetivoMasPrioritario();
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Se crea el  objetivo:  " + newAyudarVictima + "  y el objetivo : " + newDecision
                    + " Se actualiza el foco : " + foco.getFoco() + "\n" + " Objetivos decision en la cola : " + misObjsDecision.getMisObjetivosPriorizados().toString()
                    + "\n" + " Objetivo mas prioritario : " + misObjsDecision.getobjetivoMasPrioritario().toString() + "\n");
            if (objetivoFocalizado == null || objetivoFocalizado.getState() == Objetivo.SOLVED) {
                DecidirQuienVa decisionPendiente = (DecidirQuienVa) misObjsDecision.getobjetivoMasPrioritario();
                foco.setFoco(decisionPendiente);
                this.getEnvioHechos().actualizarHecho(decisionPendiente);
                this.getEnvioHechos().actualizarHecho(foco);
            }
            this.getEnvioHechos().eliminarHecho(ccOrdenAyudarVictima);
            this.getEnvioHechos().insertarHecho(victim);
            this.getEnvioHechos().actualizarHecho(newAyudarVictima);
            
            System.out.println("\n" + nombreAgenteEmisor + "Se ejecuta la tarea " + this.getIdentTarea() + " Se crea el  objetivo:  " + newAyudarVictima + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
