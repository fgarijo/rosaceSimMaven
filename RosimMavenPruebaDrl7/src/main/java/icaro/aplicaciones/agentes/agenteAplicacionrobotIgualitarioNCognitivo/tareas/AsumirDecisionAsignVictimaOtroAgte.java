/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.DecisionAgente;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 * 
 * Corresponde a un decision de que otro agente se ha hecho cargo de la tarea
 */
public class AsumirDecisionAsignVictimaOtroAgte extends TareaSincrona{
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
//             this.getEnvioHechos().eliminarHecho(decidirquienVa);
//                this.getEnvioHechos().eliminarHecho(ayudarVictima);
//                this.getEnvioHechos().eliminarHecho(infoDecision);
//                misObjsDecision.eliminarObjetivo(decidirquienVa);
//                decidirquienVa = misObjsDecision.getobjetivoMasPrioritario();
                decidirquienVa.setSolved();
                victima.setrobotResponsableId(infoDecision.dameIdentMejor());
                 this.getEnvioHechos().actualizarHecho(victima);
                 this.getEnvioHechos().actualizarHecho(decidirquienVa);
//                if(decidirquienVa!=null)  this.getEnvioHechos().insertarHecho(decidirquienVa);
//                this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
//                focoActual.setFocusToObjetivoMasPrioritario(misObjsDecision);
//                focoActual.setFoco(decidirquienVa);
//                this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se ejecuta la tarea " + this.identTarea+"\n"+
                    "Foco anterior : "+focoActual.getFocoAnterior()+
                                    " Se actualiza el  foco a :  "+ focoActual+"\n"+
                    " La victima : "+ victima.getName() + " Se la queda el robot : "+ victima.getrobotResponsableId()+"\n"+
                     " Objetivos decision en la cola : " + misObjsDecision.getMisObjetivosPriorizados().toString()+"\n"+ 
                     " Objetivo mas prioritario : "+ misObjsDecision.getobjetivoMasPrioritario().toString()+ "\n");
            System.out.println("\n"+this.identAgente +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se actualiza el  objetivo:  "+ ayudarVictima+"\n\n" );
                          
             
       } catch (Exception e) {
			 e.printStackTrace();
       }
}
  
            
}


