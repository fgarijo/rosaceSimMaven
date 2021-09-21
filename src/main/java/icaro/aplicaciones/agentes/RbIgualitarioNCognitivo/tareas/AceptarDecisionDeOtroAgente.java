/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.DecisionAgente;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author FGarijo
 */
public class AceptarDecisionDeOtroAgente extends TareaSincrona {

    @Override
    public void ejecutar(Object... params) {
        try {
            Victim victimaImplicada = (Victim) params[0];
            Objetivo decidirQuienVa = (Objetivo) params[1];
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa) params[2];
            Focus focoActual = (Focus) params[3];
            DecisionAgente decisionOtroAgente = (DecisionAgente) params[4];
            VictimsToRescue victimas = (VictimsToRescue) params[5];
            String nombreAgenteEmisor = this.getIdentAgente();
            String identAgtEnviaDecision = decisionOtroAgente.identAgente;
            if (infoDecision.getAgentesEquipo().contains(identAgtEnviaDecision)) {
                decidirQuienVa.setSolved();
                infoDecision.setobjetivoAsumidoPorOtroAgte(true);
                victimas.elimVictimAsignada(victimaImplicada.getName());
                victimaImplicada.setrobotResponsableId(identAgtEnviaDecision);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(decidirQuienVa);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(victimaImplicada);
                this.getEnvioHechos().actualizarHecho(focoActual);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ha ejecutado la tarea " + identTarea
                        + " Se ha tomado una decision para salvar a la victima :  " + victimaImplicada.getName() + "\n"
                        + " Agente a cargo de la tarea : " + identAgtEnviaDecision
                        + "Objetivo conseguido :  " + decidirQuienVa + "El foco esta en el objetivo  :  " + focoActual + "\n");
            } else {
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se ha ejecutado la tarea " + identTarea
                        + " Se ha tomado una decision para salvar a la victima :  " + decisionOtroAgente.getidentObjectRefDecision() + "\n"
                        + " Agente a cargo de la tarea : " + identAgtEnviaDecision + " Pero el agente no esta en mi equipo porque puede estar bloqeado" + "\n");
            }
            this.getEnvioHechos().eliminarHecho(decisionOtroAgente);

            System.out.println("\n" + nombreAgenteEmisor + "Se ejecuta la tarea " + this.getIdentTarea() + " Se actualiza el  objetivo:  " + decidirQuienVa + "\n\n");

        } catch (Exception e) {
        }
    }

}
