/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.AceptacionPropuesta;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarConfirmacionPropuestaParaIr extends TareaSincrona{

  @Override
  public void ejecutar(Object... params) {
		try {
             Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
             String nombreAgenteEmisor = this.getAgente().getIdentAgente();
             InfoParaDecidirQuienVa infoDecisionAgente = (InfoParaDecidirQuienVa) params[1];
             String nombreAgenteEmisorRespuesta = (String) params[2];
             AceptacionPropuesta confirmacionRecibida = (AceptacionPropuesta) params[3];
             trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :"+ identTarea +"\n");
             PropuestaAgente propuestaConfirmada = confirmacionRecibida.getpropuestaAceptada();
             String msgPropuestaOriginal = propuestaConfirmada.getMensajePropuesta();
             if ((!msgPropuestaOriginal.equals(VocabularioRosace.MsgPropuesta_Decision_Ir) )&
            	 (!msgPropuestaOriginal.equals(VocabularioRosace.MsgPropuesta_Oferta_Para_Ir) )){
                      this.generarInformeConCausaTerminacion(identTarea, objetivoEjecutantedeTarea, nombreAgenteEmisor, 
                    		                                 "LaPropuestaConfirmadaNoEsValida", CausaTerminacionTarea.ERROR);
                      trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "La propuesta Confirmada No es valida :" + propuestaConfirmada+"\n");
             } else {
                      infoDecisionAgente.addConfirmacionAcuerdoParaIr(nombreAgenteEmisorRespuesta,
                    		                                          confirmacionRecibida.getmsgAceptacionPropuesta());
                      if (!infoDecisionAgente.getTengoAcuerdoDeTodos()){ // si no tengo el acuerdo de todos no espero a tenerlo y lo doy por hecho
                           infoDecisionAgente.setTengoAcuerdoDeTodos(true);
                      }
             }             
              this.getEnvioHechos().eliminarHecho(confirmacionRecibida);   
             this.getEnvioHechos().actualizarHecho(infoDecisionAgente);
             //en la regla tambien se hace un retract
        } catch (Exception e) {
        }
  }

}