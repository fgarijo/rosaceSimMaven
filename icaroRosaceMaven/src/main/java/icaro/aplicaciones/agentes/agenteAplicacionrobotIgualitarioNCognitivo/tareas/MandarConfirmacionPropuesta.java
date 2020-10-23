
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.AceptacionPropuesta;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarConfirmacionPropuesta  extends TareaSincrona {

    private String nombreAgenteEmisor;
    private String identDeEstaTarea ;
    private String nombreAgenteReceptor ;    
	@Override
	public void ejecutar(Object... params) {
           Victim victima = (Victim)params[0];
           PropuestaAgente propuestaRecibida = (PropuestaAgente)params[1];
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[2];
            VictimsToRescue victimas = (VictimsToRescue)params[3];
           nombreAgenteEmisor = this.getAgente().getIdentAgente();
           identDeEstaTarea = this.getIdentTarea();
           try {
                 trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Se Ejecuta la Tarea :"+ identDeEstaTarea + " Victima implicada : " + victima.getName()+
                         " Se intenta eliminar la victima del conj de victimas asignadas : " + victimas.getIdtsVictimsAsignadas() );
                 nombreAgenteReceptor = propuestaRecibida.getIdentAgente();
                 victima.setrobotResponsableId(nombreAgenteReceptor);
                 AceptacionPropuesta propuestaAceptada = new AceptacionPropuesta (nombreAgenteEmisor,VocabularioRosace.MsgAceptacionPropuesta,propuestaRecibida);
                 propuestaAceptada.setidentObjectRefAcetPropuesta(propuestaRecibida.getIdentObjectRefPropuesta());
                 trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor," Se Ejecuta la Tarea :"+ identDeEstaTarea + " Enviamos la confirmacion de la Propuesta: "+ propuestaRecibida + " Al agente " +nombreAgenteReceptor );
                 this.getComunicator().enviarInfoAotroAgente(propuestaAceptada, nombreAgenteReceptor);
                 // La victima fue  asignada para calcular el coste de salvarla
                 victimas.elimVictimAsignada(victima.getName());
                 // si estoy esperando evaluaciones dejo de esperarlas poniendo el atributo hanLlegadoTodasLasEvaluaciones=true
                 infoDecision.sethanLlegadoTodasLasEvaluaciones(true);
                 this.getEnvioHechos().insertarHechoWithoutFireRules(propuestaAceptada);
                 this.getEnvioHechos().insertarHechoWithoutFireRules(victima);
                 this.getEnvioHechos().insertarHecho(infoDecision );
		   } catch (Exception e) {
           }            
    }

}