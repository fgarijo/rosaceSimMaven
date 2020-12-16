/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author FGarijo
 */
public class EliminarObjetivoyDecisionInfoActualizarFoco extends TareaSincrona{
    @Override
   public void ejecutar(Object... params) {
	   try {
             MisObjetivos misObjsDecision = (MisObjetivos) params[0];
             Objetivo objetivoDecision = (Objetivo)params[1];
             InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[2];
             Focus focoActual = (Focus)params[3];
             String nombreAgenteEmisor = this.getIdentAgente();
              misObjsDecision.eliminarObjetivo(objetivoDecision);
                this.getEnvioHechos().eliminarHecho(infoDecision);
                this.getEnvioHechos().eliminarHecho(objetivoDecision);
                focoActual.setFoco(misObjsDecision.getobjetivoMasPrioritario());
                this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Se ejecuta la tarea " + this.getIdentTarea()+
                                              " Se elimina el  objetivo:  "+ objetivoDecision + " El foco esta en el objetivo : " +
                    focoActual.getFoco()+ "\n");
            System.out.println("\n"+nombreAgenteEmisor +"Se ejecuta la tarea " + this.getIdentTarea()+ " Se actualiza el  objetivo:  "+ objetivoDecision+"\n\n" );             
       } catch (Exception e) {
       }
   }

}

