/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import org.icaro.aplicaciones.Rosace.informacion.OrdenCentroControl;
import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import org.icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 */
public class InterpretarOrdenDelCC extends TareaSincrona{
    @Override
   public void ejecutar(Object... params) {
	   try {
      //       trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
             MisObjetivos misObjsDecision = (MisObjetivos) params[0];
             OrdenCentroControl ccOrdenAyudarVictima = (OrdenCentroControl)params[1];
             VictimsToRescue victims2R = (VictimsToRescue)params[2];
             Focus foco = (Focus)params[3];
             Victim victim = (Victim)ccOrdenAyudarVictima.getJustificacion();
            // String identTarea = this.getIdentTarea();
            victim = (Victim) victim.clone();
             String nombreAgenteEmisor = this.getIdentAgente();
             String idVictim = victim.getName(); 
                 AyudarVictima newAyudarVictima = new AyudarVictima (idVictim);
                 newAyudarVictima.setPriority(victim.getPriority());
                 victims2R.addVictimToRescue(victim);
                 DecidirQuienVa newDecision = new DecidirQuienVa(idVictim);
                 newDecision.setSolving();
                 misObjsDecision.addObjetivo(newDecision);
                 this.getEnvioHechos().insertarHecho(victim);
//                 this.getEnvioHechos().actualizarHechoWithoutFireRules(victims2R);
//                 this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsDecision);
                 this.getEnvioHechos().actualizarHecho(newAyudarVictima);
                 Objetivo objetivoFocalizado= foco.getFoco();
                 if (objetivoFocalizado==null||objetivoFocalizado.getState()==Objetivo.SOLVED){
                     DecidirQuienVa decisionPendiente = (DecidirQuienVa) misObjsDecision.getobjetivoMasPrioritario();
                        foco.setFoco(decisionPendiente);
                        this.getEnvioHechos().actualizarHecho(decisionPendiente);
                        this.getEnvioHechos().actualizarHecho(foco);
                        
                 }
//                 this.getEnvioHechos().insertarHecho(newDecision);
                 this.getEnvioHechos().eliminarHecho(ccOrdenAyudarVictima);
                 trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea +" Se crea el  objetivo:  "+ newAyudarVictima+ "  y el objetivo : "+ newDecision+
                         " Se actualiza el foco : " + foco.getFoco()+ "\n");
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se crea el  objetivo:  "+ newAyudarVictima+"\n\n" );
//             }
        //         Si el foco esta en un objetivo solved
                
       } catch (Exception e) {
			 e.printStackTrace();
       }
   }

}
