/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionSubordinadoConCambioRolCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.Coordinate;
import icaro.aplicaciones.Rosace.informacion.*;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarEvaluacionAlAgteAsignador extends TareaSincrona {

    /**
     *     */
    private boolean trazarCalculoCoste;
    private String nombreAgenteQuePideLaEvaluacion;

    @Override
    public void ejecutar(Object... params) {
        PeticionAgente peticionRecibida = (PeticionAgente) params[0];
        RobotStatus1 robot = (RobotStatus1) params[1];
        VictimsToRescue victims2R = (VictimsToRescue) params[2];
        String nombreAgenteEmisor = this.getIdentAgente();
        String identTarea = this.getIdentTarea();
        int mi_eval;
        trazarCalculoCoste = true;
        Coordinate robotLocation = robot.getInfoCompMovt().getCoordenadasActuales();
        robot.setRobotCoordinate(robotLocation);
        Victim victimEnPeticion = (Victim)peticionRecibida.getJustificacion();
        nombreAgenteQuePideLaEvaluacion = peticionRecibida.getIdentAgente();
        String identVictima = victimEnPeticion.getName();
        try {
            if (victimEnPeticion != null) { 
                if (robot.getBloqueado()) { // se envia una evaluacion maxima 
                    mi_eval = Integer.MAX_VALUE;
                } else {
                    victims2R.addVictimARescatar(victimEnPeticion);
                    int camino[] = victims2R.costeAyudarVictima(robot, victimEnPeticion);
                    mi_eval = camino[0];
                    victims2R.elimVictimAsignada(identVictima);
                }
 
                if (trazarCalculoCoste) {
                    this.trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se ejecuta la tarea :" + identTarea + " Ident Victima : " +identVictima + " Mi evaluacion :" + mi_eval
                            + " Mis victimas asignadas : " + victims2R.getIdtsVictimsAsignadas() + " Victima mas proxima : " + victims2R.getIdVictimaMasProxima() + "\n");
                }
                EvaluacionAgente miEvalDeRespuesta = new EvaluacionAgente(identAgente, mi_eval);
                miEvalDeRespuesta.setObjectEvaluationId(identVictima);
                this.getEnvioHechos().eliminarHecho(peticionRecibida);
                this.getEnvioHechos().insertarHecho(victimEnPeticion);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Enviamos la evaluacion " + mi_eval + "  Victima En Peticion : " + victimEnPeticion + "  Al agente : " + nombreAgenteQuePideLaEvaluacion);
                this.getComunicator().enviarInfoAotroAgente(miEvalDeRespuesta, nombreAgenteQuePideLaEvaluacion);
                trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, "Estatus Robot. Estado" + robot.getestadoMovimiento() + " Coordenadas  : " + robotLocation + "  Destino:  " + robot.getidentDestino());
            } else {
                this.trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se ejecuta la tarea :" + identTarea + " La victima es null ");
            }
        } catch (Exception e) {
        }
    }

}
