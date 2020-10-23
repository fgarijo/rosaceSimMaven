/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.Rosace.tareasComunes;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class GenerarDecisionyFocalizar extends TareaSincrona{

   @Override
   public void ejecutar(Object... params) {
	   try {
             MisObjetivos misObjetivosDecision = (MisObjetivos)params[0];
             Focus miFocoActual = (Focus) params[1];
             Victim victimaSinDecision = (Victim)params[2];
             misObjetivosDecision.deleteObjetivosSolved();
             Objetivo objDecision = misObjetivosDecision.getobjetivoMasPrioritario();
             Objetivo objFocalizado= miFocoActual.getFoco();
             if(objDecision != null)
                 if( !objDecision.equals(objFocalizado)){
                    this.getEnvioHechos().insertarHechoWithoutFireRules(objDecision);
                    miFocoActual.setFoco(objDecision);
                    this.getEnvioHechos().actualizarHecho(miFocoActual);
             }
             this.trazas.aceptaNuevaTrazaEjecReglas(this.identAgente," Ejecucion de la tarea : " + this.getIdentTarea()+ "  Se genera el objetivo : " +objDecision );      	        	      
       } catch (Exception e) {
	e.printStackTrace();
       }
   }

}
