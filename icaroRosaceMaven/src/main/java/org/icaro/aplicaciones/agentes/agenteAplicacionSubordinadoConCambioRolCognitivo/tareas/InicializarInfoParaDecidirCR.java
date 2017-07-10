/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionSubordinadoConCambioRolCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class InicializarInfoParaDecidirCR extends TareaSincrona{
    
   @Override
   public void ejecutar(Object... params) {
	   try {
             InfoEquipo miEquipo = (InfoEquipo)params[0];
             String idVictim = (String)params[1];
             InfoParaDecidirQuienVa infoDecisionAgente;
             if (miEquipo!=null){
                 infoDecisionAgente = new InfoParaDecidirQuienVa(this.identAgente,miEquipo.getIDsMiembrosActivos());
                  infoDecisionAgente.setidElementoDecision(idVictim);
                   this.getEnvioHechos().insertarHecho(infoDecisionAgente);
             }
             else{
                 this.trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ejecuta la tarea : "+this.identTarea + " No existe informacion del equipo : InfoEquipo es null");
                 this.trazas.trazar(identAgente, "Se ejecuta la tarea : "+this.identTarea + " No existe informacion del equipo : InfoEquipo es null", InfoTraza.NivelTraza.error);
             }
             
             // Activo un timeout para la decision. Cuando venza se decidira que hacer en funcion de la situacion del agente
             // Porque se supone que estoy esperando informaciones que no llegan. 
             
       } catch (Exception e) {
			 e.printStackTrace();
       }
   }

}
