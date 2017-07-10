/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import org.icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 */
public class GeneracionObjetivosAsignacionVictima extends TareaSincrona{
    @Override
   public void ejecutar(Object... params) {
	   try {
    //         MisObjetivos misObjs = (MisObjetivos) params[0];
             Victim victim = (Victim) params[0];
             VictimsToRescue victims2R = (VictimsToRescue)params[1];
             MisObjetivos misObjs = (MisObjetivos) params[2];
             Focus focoActual = (Focus) params[3];
             String identTarea = this.getIdentTarea();
             String nombreAgenteEmisor = this.getIdentAgente();
             String idVictim = victim.getName();
   // Se eliminan  los objetivos conseguidos
             misObjs.deleteObjetivosSolved();
           // se crea el objetivo y se inserta en el motor  
                 AyudarVictima newAyudarVictima = new AyudarVictima (idVictim);
           //      newObjetivo.setvictimId(idVictim);
                 newAyudarVictima.setPriority(victim.getPriority());
                 victims2R.addVictimToRescue(victim);
                 DecidirQuienVa newDecision = new DecidirQuienVa(idVictim);
                 newDecision.setSolving();
                 this.getEnvioHechos().actualizarHechoWithoutFireRules(victims2R);
                 this.getEnvioHechos().insertarHechoWithoutFireRules(newAyudarVictima);
                 this.getEnvioHechos().insertarHechoWithoutFireRules(newDecision);
                 focoActual.setFoco(newDecision);
                 this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se ejecuta la tarea " + identTarea+
                                    " Se crea el  objetivo:  "+ newAyudarVictima, InfoTraza.NivelTraza.debug));
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + identTarea + " Se crea el  objetivo:  "+ newAyudarVictima+"\n\n" );
             
             
      //       if ((ccOrdenAyudarVictima.mensajeOrden.equals(VocabularioRosace.MsgOrdenCCAyudarVictima)) &&
      //               (!idVictim.equals(objetivoEjecutantedeTarea.getobjectReferenceId()))&& (!misObjs.existeObjetivoConEsteIdentRef(idVictim))  ){
                 
            
             }
               catch (Exception e) {
			 e.printStackTrace();
       }
   }

}
