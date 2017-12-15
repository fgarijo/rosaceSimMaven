/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.DecisionAgente;
import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 * 
 * Corresponde a un decision de que otro agente se ha hecho cargo de la tarea
 */
public class AsimilarDecisionAsignVictimaOtroAgte extends TareaSincrona{
    private int velocidadCruceroPordefecto = 5;// metros por segundo
    private ItfUsoMovimientoCtrl itfcompMov;
    private Victim victima;
    @Override
   public void ejecutar(Object... params) {
	   try {
             MisObjetivos misObjsDecision = (MisObjetivos) params[0];
             Objetivo ayudarVictima = (Objetivo)params[1]; // el estado es pending
             Objetivo decidirquienVa = (Objetivo)params[2];
             InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[3];
             Focus focoActual = (Focus)params[4]; // el foco actual es decidir quien va 
             Victim victima = (Victim)params[5];
             this.getEnvioHechos().eliminarHecho(decidirquienVa);
                this.getEnvioHechos().eliminarHecho(ayudarVictima);
                this.getEnvioHechos().eliminarHecho(infoDecision);
                misObjsDecision.eliminarObjetivo(decidirquienVa);
                decidirquienVa = misObjsDecision.getobjetivoMasPrioritario();
                victima.setrobotResponsableId(infoDecision.dameIdentMejor());
                 this.getEnvioHechos().actualizarHecho(victima);
                if(decidirquienVa!=null)  this.getEnvioHechos().insertarHecho(decidirquienVa);
//                this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
//                focoActual.setFocusToObjetivoMasPrioritario(misObjsDecision);
                focoActual.setFoco(decidirquienVa);
                this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se ejecuta la tarea " + this.identTarea+"\n"+
                    "Foco anterior : "+focoActual.getFocoAnterior()+
                                    " Se actualiza el  foco a :  "+ focoActual+"\n"+
                    " La victima : "+ victima.getName() + " Se la queda el robot : "+ victima.getrobotResponsableId());
            System.out.println("\n"+this.identAgente +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se actualiza el  objetivo:  "+ ayudarVictima+"\n\n" );
                          
             
       } catch (Exception e) {
			 e.printStackTrace();
       }
}
  
            
}


