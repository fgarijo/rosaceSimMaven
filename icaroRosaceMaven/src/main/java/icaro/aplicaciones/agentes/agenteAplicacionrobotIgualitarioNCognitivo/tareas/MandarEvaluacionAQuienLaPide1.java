/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.Coordinate;
import icaro.aplicaciones.Rosace.informacion.*;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarEvaluacionAQuienLaPide1 extends TareaSincrona {

    /**
     *
     */
    private String nombreAgenteEmisor;
    private PeticionAgente peticionRecibida;
    private InfoParaDecidirQuienVa infoDecision;
    private MisObjetivos misObjtvsAccion;
    private String identObjEvaluacion;
    private String nombreAgenteQuePideLaEvaluacion;
    private Integer miEvalDeRespuesta;
    private VictimsToRescue victimasRecibidas;
    private Victim victimEnPeticion;
    private RobotStatus1 robot;

    @Override
    public void ejecutar(Object... params) {
        Victim victimaAdecidir = (Victim) params[0];
        infoDecision = (InfoParaDecidirQuienVa) params[1];
        peticionRecibida = (PeticionAgente) params[2];
        misObjtvsAccion = (MisObjetivos) params[3];
        robot = (RobotStatus1) params[4];
        victimasRecibidas = (VictimsToRescue) params[5];
        //      EvaluacionAgente miEvaluacion = (EvaluacionAgente) params[2];
        nombreAgenteEmisor = this.getIdentAgente();
        ArrayList agentesEquipo = infoDecision.getAgentesEquipo();
        identObjEvaluacion = peticionRecibida.getidentObjectRefPeticion();
        nombreAgenteQuePideLaEvaluacion = peticionRecibida.getIdentAgente();
        Coordinate robotLocation = robot.getRobotCoordinate();
        int valorDisuasorioParaElquePideAcepteQueSoyYoElResponsable= Integer.MAX_VALUE;
        try {
            // si el identificador esta entre mis objetivos es que esta resuelto , le mando un valor para que se desanime
            // en  otro caso puede ser que sea otro el que tenga el objetivo, en este caso el le mandara¡ un valor grande
            // si no tengo noticias del objetivo le mando un valor pequeno para que vaya el
            // si coincide con el que estoy trabajando le mando el valor 
            if (misObjtvsAccion.existeObjetivoConEsteIdentRef(identObjEvaluacion)) {
                miEvalDeRespuesta = valorDisuasorioParaElquePideAcepteQueSoyYoElResponsable;
            } else { // Miro en la tabla de victimas si tengo la victima, 
                victimEnPeticion = victimasRecibidas.getVictimARescatar(identObjEvaluacion);
                if (victimaAdecidir != null) { // tengo la victima miro si tengo el valor estimado
                    if (victimaAdecidir.getisCostEstimated()) {
                        miEvalDeRespuesta = victimEnPeticion.getEstimatedCost();
                    } else { // calculo el coste y lo guardo en la victima
                        int camino[] = victimasRecibidas.costeAyudarVictima(robot, victimEnPeticion);
                        miEvalDeRespuesta = camino[0];
                        victimaAdecidir.setEstimatedCost(miEvalDeRespuesta);
                        infoDecision.setMi_eval(miEvalDeRespuesta);
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(victimaAdecidir);
                        this.getEnvioHechos().actualizarHechoWithoutFireRules(infoDecision);
                    }
                }
            }
            this.getEnvioHechos().eliminarHecho(peticionRecibida);
            EvaluacionAgente miEvaluacion = new EvaluacionAgente(nombreAgenteEmisor, miEvalDeRespuesta);
            miEvaluacion.setObjectEvaluationId(identObjEvaluacion);
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Se Ejecuta la Tarea :" + identTarea + " Enviamos la evaluacion " + miEvaluacion + " de la victima : " + victimEnPeticion.getName() + " Al agente : " + nombreAgenteQuePideLaEvaluacion);
            this.getComunicator().enviarInfoAotroAgente(miEvaluacion, nombreAgenteQuePideLaEvaluacion);
        } catch (Exception e) {
        }
    }

}
