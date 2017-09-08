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
             MisObjetivos misObjs = (MisObjetivos) params[0];
             OrdenCentroControl ccOrdenAyudarVictima = (OrdenCentroControl)params[1];
             VictimsToRescue victims2R = (VictimsToRescue)params[2];
             Victim victim = (Victim)ccOrdenAyudarVictima.getJustificacion();
            // String identTarea = this.getIdentTarea();
             String nombreAgenteEmisor = this.getIdentAgente();
             String idVictim = victim.getName(); 
                 AyudarVictima newAyudarVictima = new AyudarVictima (idVictim);
           //      newObjetivo.setvictimId(idVictim);
                 newAyudarVictima.setPriority(victim.getPriority());
                 victims2R.addVictimToRescue(victim);
          //      if((objetivoEjecutantedeTarea == null)) newObjetivo.setSolving(); // se comienza el proceso para intentar conseguirlo                                        
           //       Se genera un objetivo para decidir quien se hace cargo de la ayuda y lo ponemos a solving
                 DecidirQuienVa newDecision = new DecidirQuienVa(idVictim);
                 newDecision.setSolving();                
                 this.getEnvioHechos().insertarHechoWithoutFireRules(victim);
                 this.getEnvioHechos().actualizarHechoWithoutFireRules(victims2R);
                 this.getEnvioHechos().insertarHecho(newAyudarVictima);
                 this.getEnvioHechos().insertarHecho(newDecision);
                 this.getEnvioHechos().eliminarHecho(ccOrdenAyudarVictima);
                 trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea +" Se crea el  objetivo:  "+ newAyudarVictima+ "  y el objetivo : "+ newDecision+"\n");
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se crea el  objetivo:  "+ newAyudarVictima+"\n\n" );
//             }
        //         Si el foco esta en un objetivo solved
                
       } catch (Exception e) {
			 e.printStackTrace();
       }
   }

}
