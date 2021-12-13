/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.InfoSimulador.tareasComunes;

import icaro.aplicaciones.InfoSimulador.informacion.PeticionAgente;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.comunicacion.ComunicacionAgentes;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
/**
 *
 * @author Francisco J Garijo
 */
public class PedirEvaluacionesQueFaltan extends TareaSincrona{
  @Override
  public void ejecutar(Object... params) {
              Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
              InfoParaDecidirQuienVa infoDecisionAgente = (InfoParaDecidirQuienVa) params[1];
              Victim victima = (Victim) params[2];
              try {
                   trazas.aceptaNuevaTrazaEjecReglas( identAgente,"Se Ejecuta la Tarea : "+ identTarea );
                  if (!infoDecisionAgente.hanLlegadoTodasLasEvaluaciones){ // si no han llegado todas las evaluaciones                  
                   ComunicacionAgentes comunicacion = this.getComunicator();            
                   for(int i = 0; i< infoDecisionAgente.getAgentesEquipo().size(); i++){
                       Integer evaluacionAgente = (Integer)infoDecisionAgente.getEvaluacionesRecibidas().get(i);
                       if(evaluacionAgente == 0){//si aun no tenemos la evaluacion , queremos que nos la vuelva a mandar
                          String agenteReceptor = (String)infoDecisionAgente.getAgentesEquipo().get(i);
                          
                          trazas.aceptaNuevaTrazaEjecReglas(identAgente, "!!!!!!!!Pidiendo evaluacion al agente "+ agenteReceptor);
                                  PeticionAgente peticionEval = new PeticionAgente(this.getIdentAgente());
                                  peticionEval.setidentObjectRefPeticion(victima.getName());
                                  peticionEval.setMensajePeticion(VocabularioRosace.MsgPeticionEnvioEvaluaciones);
                                  peticionEval.setJustificacion(victima); // para que se sepa que evaluacion le pedimos
                                  comunicacion.enviarInfoAotroAgente(peticionEval, agenteReceptor);
                            //      comunicacion.enviarInfoConMomentoCreacionAotroAgente(peticionEval, agenteReceptor);
                       }
                   }
                   this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirEvaluaciones2,  objetivoEjecutantedeTarea, 
                      identAgente,  infoDecisionAgente.getidElementoDecision());
                   }
                       //            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Pedimos que nos reenvien las evaluaciones al agente: " +agenteReceptor, InfoTraza.NivelTraza.debug));
              } catch (Exception ex) {
                 trazas.aceptaNuevaTraza(new InfoTraza(identAgente, "Error al enviar peticionRecibirRespuestasRestantes "+ ex, InfoTraza.NivelTraza.error));
              }
        }
}