/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;
import org.icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.informacion.InfoParaDecidirAQuienAsignarObjetivo;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
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
             trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
             MisObjetivos misObjs = (MisObjetivos) params[0];
             Objetivo ayudarVictima = (Objetivo)params[1];
             InfoParaDecidirAQuienAsignarObjetivo infoDecision = (InfoParaDecidirAQuienAsignarObjetivo)params[2];
             Focus focoActual = (Focus)params[3];
             String identTarea = this.getIdentTarea();
             String nombreAgenteEmisor = this.getIdentAgente();
                this.getEnvioHechos().eliminarHechoWithoutFireRules(infoDecision);
                this.getEnvioHechos().eliminarHechoWithoutFireRules(ayudarVictima);
                focoActual.setFocusToObjetivoMasPrioritario(misObjs);
       //       this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjs);
                this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Se ejecuta la tarea " + this.getIdentTarea()+
                                              " Se actualiza el  objetivo:  "+ ayudarVictima);
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se actualiza el  objetivo:  "+ ayudarVictima+"\n\n" );
                          
             
       } catch (Exception e) {
			 e.printStackTrace();
       }
   }

}

