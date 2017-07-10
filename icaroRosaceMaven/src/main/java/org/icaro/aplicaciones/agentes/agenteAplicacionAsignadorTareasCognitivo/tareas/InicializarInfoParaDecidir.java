/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.informacion.InfoParaDecidirAQuienAsignarObjetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class InicializarInfoParaDecidir extends TareaSincrona{

   @Override
   public void ejecutar(Object... params) {
	   try {
             Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
             String nombreAgenteEmisor = this.getIdentAgente();
             String idVictim = (String)params[1];
             InfoEquipo miEquipo = (InfoEquipo)params[2];
             InfoParaDecidirAQuienAsignarObjetivo infoDecisionAgente = new InfoParaDecidirAQuienAsignarObjetivo(nombreAgenteEmisor,miEquipo );
             infoDecisionAgente.setidElementoDecision(idVictim);
             this.getEnvioHechos().insertarHecho(infoDecisionAgente);
             // Activo un timeout para la decision. Cuando venza se decidira que hacer en funcion de la situacion del agente
             // Porque se supone que estoy esperando informaciones que no llegan. 
             
       } catch (Exception e) {
			 e.printStackTrace();
       }
   }

}
