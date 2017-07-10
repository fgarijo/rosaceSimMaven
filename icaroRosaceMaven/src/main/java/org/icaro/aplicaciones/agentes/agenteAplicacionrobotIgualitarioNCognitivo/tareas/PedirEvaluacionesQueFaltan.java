/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import org.icaro.aplicaciones.Rosace.informacion.PeticionAgente;
import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.ComunicacionAgentes;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.MensajeSimple;
import org.icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                   
                   ComunicacionAgentes comunicacion = new ComunicacionAgentes(identAgente );            
                   for(int i = 0; i< infoDecisionAgente.getAgentesEquipo().size(); i++){
                       Integer evaluacionAgente = (Integer)infoDecisionAgente.getEvaluacionesRecibidas().get(i);
                       if(evaluacionAgente == 0){//si aun no tenemos la evaluacion , queremos que nos la vuelva a mandar
                          String agenteReceptor = (String)infoDecisionAgente.getAgentesEquipo().get(i);
                          
                          trazas.aceptaNuevaTrazaEjecReglas(identAgente, "!!!!!!!!Pidiendo evaluacion al agente "+ agenteReceptor);
                                  PeticionAgente peticionEval = new PeticionAgente(this.getIdentAgente());
                                  peticionEval.setidentObjectRefPeticion(objetivoEjecutantedeTarea.getobjectReferenceId());
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
