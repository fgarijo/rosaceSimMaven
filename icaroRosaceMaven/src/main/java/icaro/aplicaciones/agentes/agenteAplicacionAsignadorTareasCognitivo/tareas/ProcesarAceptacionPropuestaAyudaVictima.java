/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.AceptacionPropuesta;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarAceptacionPropuestaAyudaVictima extends TareaSincrona {
int minimaPrioridadObjetivo=-1;
    @Override
    public void ejecutar(Object... params) {
        try {
            Objetivo objDecision = (DecidirQuienVa) params[0];
            AceptacionPropuesta aceptacionRecibida = (AceptacionPropuesta) params[1];
            InfoParaDecidirQuienVa infoDecisionAgente = (InfoParaDecidirQuienVa) params[2];
            MisObjetivos misObjetivosDecision = (MisObjetivos) params[3];
            VictimsToRescue victimas = (VictimsToRescue) params[4];
            Focus foco = (Focus) params[5];
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + identTarea + "\n");
            PropuestaAgente propuestaConfirmada = aceptacionRecibida.getpropuestaAceptada();
            String msgPropuestaOriginal = propuestaConfirmada.getMensajePropuesta();
            String idVictima = aceptacionRecibida.getidentObjectRefAcetPropuesta();
            String idAgteAceptaPropuesta = aceptacionRecibida.getIdentAgente();
            Objetivo nuevoObjDecision = null;
            if (!(propuestaConfirmada.getIdentAgente().equals(this.getIdentAgente()))
                    || (!msgPropuestaOriginal.equals(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo))) {
                this.generarInformeConCausaTerminacion(identTarea, null, aceptacionRecibida.getIdentAgente(),
                        "LaPropuestaConfirmadaNoEsValida", CausaTerminacionTarea.ERROR);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "La propuesta Confirmada por el agente : " + idAgteAceptaPropuesta + "  No es valida :" + propuestaConfirmada + "\n");
            } else {
            Victim victimaImplicada = victimas.getVictimARescatar(idVictima);
                victimaImplicada.setrobotResponsableId(idAgteAceptaPropuesta);
                victimas.addVictimAsignada(victimaImplicada);
                objDecision.setSolved();
                misObjetivosDecision.eliminarObjetivo(objDecision);
                nuevoObjDecision = misObjetivosDecision.getobjetivoMasPrioritario();
                foco.setFoco(nuevoObjDecision);
                this.getEnvioHechos().eliminarHecho(aceptacionRecibida);
                this.getEnvioHechos().eliminarHecho(infoDecisionAgente);
                if (nuevoObjDecision != null) {
                    this.getEnvioHechos().actualizarHecho(nuevoObjDecision);
                }
                this.getEnvioHechos().actualizarHecho(objDecision);
                this.getEnvioHechos().actualizarHecho(foco);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "La propuesta ha sido aceptada por el agente : " + idAgteAceptaPropuesta + "  Para hacerse cargo de la victima :" + idVictima
                        + " Se elimina Infodecision y se focaliza en el objetivo " + nuevoObjDecision + " El foco esta en el objetivo "
                        + foco.getFoco() + "\n");
            }
            //en la regla tambien se hace un retract
        } catch (Exception e) {
        }
    }

}
