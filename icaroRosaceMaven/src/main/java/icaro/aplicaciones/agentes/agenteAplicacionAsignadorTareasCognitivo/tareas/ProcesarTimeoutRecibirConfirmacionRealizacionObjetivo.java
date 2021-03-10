/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.InformeDeTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarTimeoutRecibirConfirmacionRealizacionObjetivo extends TareaSincrona {
    int minimaPrioridadObjetivo = -1;
    @Override
    public void ejecutar(Object... params) {
        try { 
            InfoParaDecidirQuienVa infoDecisionAgente = (InfoParaDecidirQuienVa) params[0];
            InformeDeTarea informeTarea = (InformeDeTarea) params[1];
            Focus foco = (Focus) params[2];
            VictimsToRescue victimasArescatar = (VictimsToRescue)params[3];
            RobotStatus1 miStatus = (RobotStatus1) params[4];
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + identTarea + "\n");
            String idVictima = infoDecisionAgente.getidElementoDecision();
            String identTareaTimeout = informeTarea.getIdentTarea();
            String contInforme = (String) informeTarea.getContenidoInforme();
            if (!identTareaTimeout.equals(VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1)
                    || !contInforme.equals(idVictima)) {
                this.generarInformeConCausaTerminacion(identTarea, null, this.identAgente,
                        "LaPropuestaConfirmadaNoEsValida", CausaTerminacionTarea.ERROR);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Los datos del timeout recibido no son validos : " + identTareaTimeout + "  No es valida :" + idVictima + "\n");
            } else { // desactivamos temporalmente el robot al que se había mandado la propuesta y mandamos una nueva propuesta al mejor disponible
                String idRobotQnoRespondeLaPropuesta = infoDecisionAgente.dameIdentMejor();
                infoDecisionAgente.eliminarAgenteEquipo(idRobotQnoRespondeLaPropuesta);
                String idRobotMejor = infoDecisionAgente.dameIdentMejor();
                if (idRobotMejor != null) {
                    PropuestaAgente miPropuesta = new PropuestaAgente(this.identAgente);
                    miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo);
                    miPropuesta.setIdentObjectRefPropuesta(idVictima);
                    miPropuesta.setJustificacion(infoDecisionAgente.getEvalucionRecibidaDelAgente(idRobotMejor));
                    trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " Se envia propuesta : "
                            + VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo + " Al Agente : " + idRobotMejor + " MiEvaluacion : " + miPropuesta.getJustificacion());
//                infoDecision.setheInformadoAlmejorParaQueAsumaElObjetivo(true);
                     this.getComunicator().enviarInfoAotroAgente(miPropuesta, idRobotMejor);
                    this.getEnvioHechos().actualizarHecho(infoDecisionAgente);
                    trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Genera un timeout de :" + VocabularioRosace.TimeOutMiliSecConseguirObjetivo
                            + " Con mensaje  : " + VocabularioRosace.MsgTimeoutRecibirConfirmacionAsumirObjetivo);
                    this.generarInformeTemporizado(VocabularioRosace.TimeOutMiliSecConseguirObjetivo, VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionRealizacionObjetivo1, null, identAgente, idVictima);
                } else { // No hay robots disponibles para realizar la tarea   
                   hacermeCargoDeVictimasNoAsignadas(  victimasArescatar, miStatus );
                   String victimaId = victimasArescatar.getIdVictimaMasProxima();
                   AyudarVictima  objAyuda = new AyudarVictima(victimaId);
                   foco.setFoco(objAyuda);
                   this.getEnvioHechos().insertarHecho(objAyuda);
                   this.getEnvioHechos().eliminarHecho(infoDecisionAgente);
                   this.getEnvioHechos().actualizarHecho(foco);
                    trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + " No hay robots disponibles para realizar la tarea : "+
                     " Mis victimas asignadas : " + victimasArescatar.getIdtsVictimsAsignadas() + " Victima mas  proxima : " +victimaId  );                  
                }
            }
            this.getEnvioHechos().eliminarHecho(informeTarea);
            //en la regla tambien se hace un retract
        } catch (Exception e) {
        }
    }
    private void hacermeCargoDeVictimasNoAsignadas(VictimsToRescue victimasArescatar,RobotStatus1 miStatus){    
        Victim victima;
                for (int i=0; i<victimasArescatar.getVictimsARescatar().size();i++){
                    victima = victimasArescatar.getVictimaARescatar(i);                   
            if( !victimasArescatar.getVictimsAsignadas().contains(victima)){
                int camino[] = victimasArescatar.costeAyudarVictima(miStatus, victima);
                    victima.setEstimatedCost(camino[0]);
                    victima.setrobotResponsableId(this.getIdentAgente()); 
                    victimasArescatar.addVictimAsignada(victima);               
            }           
        }
    }

}
