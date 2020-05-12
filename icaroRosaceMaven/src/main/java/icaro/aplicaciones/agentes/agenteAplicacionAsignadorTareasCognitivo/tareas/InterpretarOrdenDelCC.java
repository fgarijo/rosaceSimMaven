/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.OrdenCentroControl;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.objetivos.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author FGarijo
 */
public class InterpretarOrdenDelCC extends TareaSincrona{
    @Override
   public void ejecutar(Object... params) {
	   try {
             trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
             MisObjetivos misObjs = (MisObjetivos) params[0];
             Objetivo objetivoEjecutantedeTarea = (Objetivo)params[1];
             OrdenCentroControl ccOrdenAyudarVictima = (OrdenCentroControl)params[2];
             VictimsToRescue victims2R = (VictimsToRescue)params[3];
             Victim victim = (Victim)ccOrdenAyudarVictima.getJustificacion();
             String identTarea = this.getIdentTarea();
             String nombreAgenteEmisor = this.getIdentAgente();
             String idVictim = victim.getName();
             this.getEnvioHechos().eliminarHechoWithoutFireRules(ccOrdenAyudarVictima);
                 if (!victims2R.hayVictimasArescatar() || !victims2R.victimaDefinida(victim))  {   
                 AyudarVictima newAyudarVictima = new AyudarVictima (idVictim);
                 newAyudarVictima.setPriority(victim.getPriority());
                 victims2R.addVictimARescatar(victim);
                 DecidirQuienVa newDecision = new DecidirQuienVa(idVictim);
                 newDecision.setSolving();                 
                 this.getEnvioHechos().insertarHechoWithoutFireRules(victim);
                 this.getEnvioHechos().insertarHechoWithoutFireRules(newAyudarVictima);
                 this.getEnvioHechos().insertarHecho(newDecision);
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Se ejecuta la tarea " + this.getIdentTarea()+
                                    " Se crea el  objetivo:  "+ newAyudarVictima );
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se crea el  objetivo:  "+ newAyudarVictima+"\n\n" );
             }else{
              trazas.aceptaNuevaTrazaEjecReglas("\n" +nombreAgenteEmisor, "Se ejecuta la tarea " + this.getIdentTarea()+
                                    " Pero NO Se crea ningun   objetivo:  ");
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Pero NO Se crea ningun   objetivo:  "+"\n\n" ); 
             }      
       } catch (Exception e) {
       }
   }

}
