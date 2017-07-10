/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.informacion.InfoParaDecidirAQuienAsignarObjetivo;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import org.icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaAlMejorParaQueRealiceObjetivo  extends TareaSincrona {

	/**  */
    private InterfazUsoAgente agenteReceptor;
    private ArrayList agentesEquipo, respuestasAgentes,confirmacionesAgentes,nuevasEvaluacionesAgentes,empates;//resto de agentes que forman mi equipo
    private String nombreAgenteEmisor;
    private ItfUsoRecursoTrazas trazas;
    private InfoParaDecidirAQuienAsignarObjetivo infoDecision;
    private String identDeEstaTarea ;
    private String nombreAgenteReceptor ;

    //private TimeOutRespuestas tiempoSinRecibirRespuesta; //no usado

    
	@Override
	public void ejecutar(Object... params) {
		try {
              trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
              Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
              infoDecision = (InfoParaDecidirAQuienAsignarObjetivo)params[1];
              Victim victima = (Victim)params[2];
              nombreAgenteEmisor = this.getAgente().getIdentAgente();
              identDeEstaTarea = this.getIdentTarea();
                   trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se Ejecuta la Tarea :"+ identDeEstaTarea );
   //               if (! infoDecision.getheInformadoAlmejorParaQueAsumaElObjetivo() ){ // si ya se le ha informado no se hace nada
                   nombreAgenteReceptor = infoDecision.dameIdentMejor();
                        //     PropuestaAgente miPropuesta = new PropuestaAgente (nombreAgenteEmisor,"CreoQueDebesIrTu", infoDecision.getValorMiEvaluacion());
//                   this.getEnvioHechos().getStatefulKnowledgeSession().getAgenda().clear();
                   PropuestaAgente miPropuesta = new PropuestaAgente (nombreAgenteEmisor);
                   miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo);
                   miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
                   miPropuesta.setJustificacion(victima);
                   this.getComunicator().enviarInfoAotroAgente(miPropuesta,nombreAgenteReceptor );

                   this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionesRealizacionObjetivo1,objetivoEjecutantedeTarea, 
                           nombreAgenteEmisor,  infoDecision.getidElementoDecision());
                   
//                   this.generarInformeTemporizado(configConstantesSimulacion.TimeTimeoutRecibirEvaluaciones ,            		  
//                           VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionesRealizacionObjetivo1,objetivoEjecutantedeTarea, 
//                           nombreAgenteEmisor,  infoDecision.getidElementoDecision());
                   
                   infoDecision.setheInformadoAlmejorParaQueAsumaElObjetivo(Boolean.TRUE);
                   this.getEnvioHechos().actualizarHecho(infoDecision);
		   trazas.aceptaNuevaTrazaEjecReglas(identAgente, "IdentObjetoPropuesta: " +infoDecision.getidElementoDecision()+ "Enviamos la propuesta: " + VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo + "  Al agente " +nombreAgenteReceptor );
        }
        catch(Exception e) {
			e.printStackTrace();
        }
    }

}
