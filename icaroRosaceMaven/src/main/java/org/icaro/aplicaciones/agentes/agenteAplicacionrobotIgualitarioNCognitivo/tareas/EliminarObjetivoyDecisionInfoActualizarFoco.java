/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 */
public class EliminarObjetivoyDecisionInfoActualizarFoco extends TareaSincrona{
    @Override
   public void ejecutar(Object... params) {
	   try {
             MisObjetivos misObjs = (MisObjetivos) params[0];
             Objetivo ayudarVictima = (Objetivo)params[1];
             InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[2];
             Focus focoActual = (Focus)params[3];
             String nombreAgenteEmisor = this.getIdentAgente();
                this.getEnvioHechos().eliminarHechoWithoutFireRules(infoDecision);
                this.getEnvioHechos().eliminarHechoWithoutFireRules(ayudarVictima);
                focoActual.setFocusToObjetivoMasPrioritario(misObjs);
                this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se ejecuta la tarea " + this.identTarea+
                                    " Se actualiza el  foco a :  "+ focoActual +"\n" );
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se actualiza el  objetivo:  "+ ayudarVictima+"\n\n" );
                          
             
       } catch (Exception e) {
			 e.printStackTrace();
       }
   }

}

