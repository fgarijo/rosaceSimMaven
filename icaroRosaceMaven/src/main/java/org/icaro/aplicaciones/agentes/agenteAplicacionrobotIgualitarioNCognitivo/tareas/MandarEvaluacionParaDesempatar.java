/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import org.icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.Rosace.utils.AccesoPropiedadesGlobalesRosace;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.ComunicacionAgentes;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.MensajeSimple;
import org.icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarEvaluacionParaDesempatar extends TareaSincrona {
//public class MandarEvaluacionParaDesempatar  extends Tarea {

    /**
     *     */
    private InterfazUsoAgente agenteReceptor;
    private ArrayList<String> identsAgentesEmpatados;//resto de agentes que forman mi equipo

    private String nombreAgenteEmisor;
    private InfoParaDecidirQuienVa infoDecision;
    private String identDeEstaTarea;
    private Objetivo objetivoEjecutantedeTarea;
    @Override
    public void ejecutar(Object... params) {
        try {
            objetivoEjecutantedeTarea = (Objetivo) params[0];
            infoDecision = (InfoParaDecidirQuienVa) params[1];
            nombreAgenteEmisor = this.getAgente().getIdentAgente();
            identDeEstaTarea = this.getIdentTarea();
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :" + identDeEstaTarea, InfoTraza.NivelTraza.info));
            identsAgentesEmpatados = infoDecision.getAgentesEmpatados();
            infoDecision.propuestasDesempateRecibidas = 0;
            infoDecision.propuestasDesempateEsperadas = identsAgentesEmpatados.size();
            Integer nuevaEvalucion = infoDecision.calcularEvalucionParaDesempate();
            //       PropuestaAgente miPropuesta = new PropuestaAgente (nombreAgenteEmisor,"MiEvaluacionParaDesempatar", nuevaEvalucion);
            PropuestaAgente miPropuesta = new PropuestaAgente(nombreAgenteEmisor);
            miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Desempatar);
            miPropuesta.setJustificacion(nuevaEvalucion);
            miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());

            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la propuesta: MiEvaluacionParaDesempatar " + nuevaEvalucion, InfoTraza.NivelTraza.debug));

            this.mandaMensajeAEmpatados(miPropuesta);
            infoDecision.setMiPropuestaDeDesempateEnviadaAtodos(Boolean.TRUE);
            this.getEnvioHechos().actualizarHecho(infoDecision);
            Random r = new Random();
//                  long time = (long)AccesoPropiedadesGlobalesRosace.getTimeTimeoutMilisegundosRecibirPropuestaDesempate();
            long time = (long) Integer.parseInt(this.itfConfig.getValorPropiedadGlobal(VocabularioRosace.timeTimeoutMilisegundosRecibirPropuestaDesempate));
            long timeoutRespuestas = time + r.nextInt(1000);   //OBSERVO QUE timeoutRespuestas no se usa
            this.generarInformeTemporizado(time,
                    VocabularioRosace.IdentTareaTimeOutRecibirPropuestasDesempate, objetivoEjecutantedeTarea,
                    nombreAgenteEmisor, infoDecision.getidElementoDecision());

//                  long timeoutRespuestas = configConstantesSimulacion.TimeTimeoutRecibirPropuestaDesempate + r.nextInt(1000);
//                  this.generarInformeTemporizado(configConstantesSimulacion.TimeTimeoutRecibirPropuestaDesempate, 
//                		                         VocabularioRosace.IdentTareaTimeOutRecibirPropuestasDesempate, objetivoEjecutantedeTarea, 
//                		                         nombreAgenteEmisor, infoDecision.getidElementoDecision());
            //     infoDecision.setRespuestasEsperadas(IdentsAgentesEmpatados.size());
            //      this.generarInformeOK(identDeEstaTarea, objetivoEjecutantedeTarea, nombreAgenteEmisor, VocabularioRosace.ResEjTaskMiPropuestaParaDesempatarEnviada);
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Numero de agentes con los que estoy empatado:" + identsAgentesEmpatados.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mandaMensajeAEmpatados(Object contenidoMsg) {
        try {
            Integer numAgentesEquipo = identsAgentesEmpatados.size();
            for (int i = 0; i < identsAgentesEmpatados.size(); i++) {
                String agenteReceptor = (String) identsAgentesEmpatados.get(i);
                this.comunicator.enviarInfoAotroAgente(contenidoMsg, agenteReceptor);
            }
        } catch (Exception ex) {
            Logger.getLogger(MandarEvalATodos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
