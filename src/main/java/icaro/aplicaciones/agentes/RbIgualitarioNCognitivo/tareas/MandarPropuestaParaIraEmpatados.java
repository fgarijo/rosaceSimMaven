/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.PropuestaAgente;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.InfoSimulador.utils.AccesoPropiedadesGlobalesRosace;
import icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaParaIraEmpatados extends TareaSincrona {

    /**
     *     */
    private ArrayList<String> IdentsAgentesEmpatados;//resto de agentes que forman mi equipo
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
            IdentsAgentesEmpatados = infoDecision.getAgentesEmpatados();
            Integer nuevaEvalucion = infoDecision.calcularEvalucionParaDesempate();
            PropuestaAgente miPropuesta = new PropuestaAgente(nombreAgenteEmisor);
            miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Oferta_Para_Ir);
            miPropuesta.setJustificacion(nuevaEvalucion);
            miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la propuesta: Mi Propuesta Para Ir " + nuevaEvalucion, InfoTraza.NivelTraza.debug));
            this.getComunicator().informaraGrupoAgentes(miPropuesta, IdentsAgentesEmpatados);
            infoDecision.setMiPropuestaDeDesempateEnviadaAtodos(Boolean.TRUE);
            this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
            long time = (long) AccesoPropiedadesGlobalesRosace.getTimeTimeoutMilisegundosRecibirPropuestaDesempate();
            this.generarInformeTemporizado(time,
                    VocabularioRosace.IdentTareaTimeOutRecibirPropuestasDesempate, objetivoEjecutantedeTarea,
                    nombreAgenteEmisor, infoDecision.getidElementoDecision());
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Numero de agentes con los que estoy empatado:" + IdentsAgentesEmpatados.size(), InfoTraza.NivelTraza.info));
        } catch (Exception e) {
        }
    }

}
