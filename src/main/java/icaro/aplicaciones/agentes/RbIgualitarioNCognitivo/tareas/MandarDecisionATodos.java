/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.DecisionAgente;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarDecisionATodos extends TareaSincrona {
       
	@Override
	public void ejecutar(Object... params) {
            try {
  //               trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
                 Objetivo objetivoDecision = (Objetivo)params[0];
                 InfoParaDecidirQuienVa  infoDecision = (InfoParaDecidirQuienVa)params[1];
                 Victim victima = (Victim) params[2];
                 VictimsToRescue victimasArescatar = (VictimsToRescue) params[3];
                 DecisionAgente decision = new DecisionAgente (this.identAgente,VocabularioRosace.MsgPropuesta_Decision_Ir);
                             decision.setidentObjectRefDecision(victima.getName()); // En este caso el identificador se refiere a la victima
                             decision.setJustificacion(infoDecision.getMi_eval());
                 
                 trazas.aceptaNuevaTraza(new InfoTraza("OrdenAsignacion",
                                                     " El robot " + this.identAgente + " se hace cargo de la victima " + victima.getName()+"\n",
                                                     InfoTraza.NivelTraza.debug));
                 this.getComunicator().informaraGrupoAgentes(decision, infoDecision.getAgentesEquipo());
                 victimasArescatar.addVictimAsignada(victima);
                 trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se ejecuta la tarea : "+ this.getIdentTarea()+ " Enviamos la decision al Equipo : " + decision +
                         " La  victima asignada es : " + victima.getName() +
                          " Mis victimas asignadas son : " + victimasArescatar.getIdtsVictimsAsignadas()+ " La victima mas proxima es : "+ victimasArescatar.getIdVictimaMasProxima()); 
                 objetivoDecision.setSolved();
                 infoDecision.setMiDecisionParaAsumirElObjetivoEnviadaAtodos(true);
                 this.getEnvioHechos().actualizarHechoWithoutFireRules(victima);
                this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
                this.getEnvioHechos().actualizarHecho(objetivoDecision);
		    } catch (Exception e) {
            }
    }
	
}