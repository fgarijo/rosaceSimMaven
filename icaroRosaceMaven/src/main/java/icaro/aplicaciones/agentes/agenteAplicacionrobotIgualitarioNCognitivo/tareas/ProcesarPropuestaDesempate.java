package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarPropuestaDesempate  extends TareaSincrona {

	/**  */
    private ArrayList<String> identsAgentesEmpatados;
    private String nombreAgenteEmisor;
    private InfoParaDecidirQuienVa infoDecision;
    private String identDeEstaTarea;
    private PropuestaAgente miPropuesta;
    private String mensajePropuesta ;   //se utiliza para en el generarInformeOK y para mostrar información en las trazas
	@Override
	public void ejecutar(Object... params) {
         try {
               Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
               infoDecision = (InfoParaDecidirQuienVa)params[1];
               PropuestaAgente propuestaRecibida =  (PropuestaAgente)params[2];     
               nombreAgenteEmisor = this.getAgente().getIdentAgente();
               identDeEstaTarea = this.getIdentTarea();
               trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :"+ identDeEstaTarea , InfoTraza.NivelTraza.info));        
               infoDecision.procesarValorPropuestaDesempate(propuestaRecibida);        
               if  (infoDecision.hayEmpates && (infoDecision.propuestasDesempateRecibidas ==infoDecision.propuestasDesempateEsperadas  )){
                // Se ha termiando la primera ronda de desempates con empates
                // Se intenta desempatar con los empatados
                    identsAgentesEmpatados = infoDecision.getAgentesEmpatados();
                    infoDecision.propuestasDesempateRecibidas = 0;
                    infoDecision.propuestasDesempateEsperadas = identsAgentesEmpatados.size();
                    Integer nuevaEvalucion = infoDecision.calcularEvalucionParaDesempate();   
                    mensajePropuesta = VocabularioRosace.MsgPropuesta_Para_Desempatar;
                    miPropuesta = new PropuestaAgente (nombreAgenteEmisor);
                    miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Desempatar );
                    miPropuesta.setJustificacion(nuevaEvalucion);
                    miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
                 //   ComunicacionAgentes comunicacion = new ComunicacionAgentes(nombreAgenteEmisor);
                    this.getComunicator().informaraGrupoAgentes(miPropuesta, identsAgentesEmpatados);
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la propuesta: " + mensajePropuesta , InfoTraza.NivelTraza.info)); 
               }else if (!infoDecision.hayEmpates && infoDecision.tengoLaMejorEvaluacion) {
                       identsAgentesEmpatados = infoDecision.getAgentesEmpatados();
                   if(!identsAgentesEmpatados.isEmpty()){ // la propuesta hace que se desempate
                   // Se manda una propuesta a los empatados para que vayan ellos
                    miPropuesta = new PropuestaAgente (nombreAgenteEmisor);
                    miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo );
                    miPropuesta.setJustificacion(infoDecision.getMi_eval());
                    miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
                    this.getComunicator().informaraGrupoAgentes(miPropuesta, identsAgentesEmpatados);
                    }                      
               }      this.getEnvioHechos().eliminarHechoWithoutFireRules(propuestaRecibida);
                     this.getEnvioHechos().actualizarHecho(infoDecision);
	 } catch (Exception e) {
               }
              
   }


}
