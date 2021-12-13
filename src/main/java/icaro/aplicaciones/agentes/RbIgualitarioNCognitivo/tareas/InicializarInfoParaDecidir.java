/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class InicializarInfoParaDecidir extends TareaSincrona{
    
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
             
       } catch (Exception e) {
       }
   }

}