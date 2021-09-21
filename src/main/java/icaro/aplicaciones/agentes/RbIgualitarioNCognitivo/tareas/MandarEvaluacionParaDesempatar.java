/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.PropuestaAgente;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarEvaluacionParaDesempatar extends TareaSincrona {

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
            PropuestaAgente miPropuesta = new PropuestaAgente(nombreAgenteEmisor);
            miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Desempatar);
            miPropuesta.setJustificacion(nuevaEvalucion);
            miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la propuesta: MiEvaluacionParaDesempatar " + nuevaEvalucion, InfoTraza.NivelTraza.debug));
            this.mandaMensajeAEmpatados(miPropuesta);
            infoDecision.setMiPropuestaDeDesempateEnviadaAtodos(Boolean.TRUE);
            this.getEnvioHechos().actualizarHecho(infoDecision);
            Random r = new Random();
            long time = (long) Integer.parseInt(this.itfConfig.getValorPropiedadGlobal(VocabularioRosace.timeTimeoutMilisegundosRecibirPropuestaDesempate));
            this.generarInformeTemporizado(time,
                    VocabularioRosace.IdentTareaTimeOutRecibirPropuestasDesempate, objetivoEjecutantedeTarea,
                    nombreAgenteEmisor, infoDecision.getidElementoDecision());
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Numero de agentes con los que estoy empatado:" + identsAgentesEmpatados.size());
        } catch (Exception e) {
        }
    }

    private void mandaMensajeAEmpatados(Object contenidoMsg) {
        try {
            for (int i = 0; i < identsAgentesEmpatados.size(); i++) {
                String agenteReceptor = (String) identsAgentesEmpatados.get(i);
                this.comunicator.enviarInfoAotroAgente(contenidoMsg, agenteReceptor);
            }
        } catch (Exception ex) {
            Logger.getLogger(MandarEvalATodos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
