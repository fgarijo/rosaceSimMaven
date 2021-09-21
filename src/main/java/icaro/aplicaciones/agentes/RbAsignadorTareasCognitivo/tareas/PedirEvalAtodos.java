/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbAsignadorTareasCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.PeticionAgente;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class PedirEvalAtodos extends TareaSincrona {

    private ArrayList<String> agentesEquipo;//resto de agentes que forman mi equipo                                
    private String nombreAgenteEmisor;
    

    @Override
    public void ejecutar(Object... params) {
        try {
            //"Ya tengo la evaluacion para realizar el objetivo.Se lo mando al resto". En su parte derecha llama a esta tarea
           InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[0];
            Victim victima = (Victim) params[1];
            VictimsToRescue victimas = (VictimsToRescue) params[2];
            RobotStatus1 miStatus = (RobotStatus1) params[3];
            Focus focoActual = (Focus) params[4];

            nombreAgenteEmisor = this.getIdentAgente();
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :" + identTarea, InfoTraza.NivelTraza.debug));
            if (!infoDecision.miEvaluacionEnviadaAtodos) {
                agentesEquipo = infoDecision.getAgentesEquipo();
                if (agentesEquipo.isEmpty()) {
                    trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Se ejecuta la tarea " + this.identTarea
                            + " El equipo de agentes a los que hay que enviar la informacion esta vacio. El jefe se hace cargo de la victima " + "\n");
                    int camino[] = victimas.costeAyudarVictima(miStatus, victima);
                    victima.setEstimatedCost(camino[0]);
                    victima.setrobotResponsableId(this.getIdentAgente());
                    victimas.addVictimAsignada(victima);
                    Objetivo objetivoDecision = focoActual.getFoco();
                    objetivoDecision.setSolved();
                    this.getEnvioHechos().actualizarHecho(objetivoDecision);
                    if (infoDecision != null) {
                        this.getEnvioHechos().eliminarHecho(infoDecision);
                    }
                    this.getEnvioHechos().actualizarHecho(focoActual);
                } else {
                    PeticionAgente peticionEval = new PeticionAgente(nombreAgenteEmisor);
                    peticionEval.setidentObjectRefPeticion(victima.getName());
                    peticionEval.setMensajePeticion(VocabularioRosace.MsgPeticionEnvioEvaluaciones);
                    peticionEval.setJustificacion(victima); // para que se sepa que evaluacion le pedimos
                    this.getComunicator().informaraGrupoAgentes(peticionEval, agentesEquipo);
                    infoDecision.setRespuestasEsperadas(agentesEquipo.size());
                    infoDecision.setMiEvaluacionEnviadaAtodos(true);
                    this.getEnvioHechos().actualizarHecho(infoDecision);
                    trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Se ejecuta la tarea " + this.identTarea + " Robots en el equipo : " + agentesEquipo
                            + " Numero de agentes de los que espero respuesta:" + agentesEquipo.size() + "\n");
                    this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirEvaluaciones1, focoActual.getFoco(),
                            nombreAgenteEmisor, infoDecision.getidElementoDecision());
                }
            }// en el caso de que ya la haya enviado la evaluacion no hago nada
        } catch (Exception e) {
        }
    }
}
