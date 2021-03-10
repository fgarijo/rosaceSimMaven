/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.*;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class ObtenerEvaluacionRealizarObjetivo extends TareaSincrona {
	
    private int mi_eval;
    private Coordinate robotLocation; //Localizacion del robot
    private boolean trazarCalculoCoste;   
// Introduzco como parametro  la InfoParaDecidrQuienVa y le definimos el valor de la evaluacion. 
    @Override
    public void ejecutar(Object... params) {
	   try {
            Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
            Victim victim = (Victim)params[1];               
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[2];
            RobotStatus1 robot = (RobotStatus1)params[3];                        
            VictimsToRescue victims2R =(VictimsToRescue)params[4];
            MisObjetivos misObjsAccion = (MisObjetivos)params[5];
            trazarCalculoCoste=true;
            robotLocation = robot.getInfoCompMovt().getCoordenadasActuales();
            robot.setRobotCoordinate(robotLocation);
            System.out.println("Realizando  la evaluacion para el Robot ->" + this.identAgente);
            System.out.println("robotLocation->"+robotLocation);
            System.out.println("para la victima ->"+victim.toString());

            if (misObjsAccion.getobjetivoMasPrioritario()!=null)System.out.println("misObjs->"+misObjsAccion.getobjetivoMasPrioritario().toString());
            if( robot.getBloqueado()){ // se envia una evaluacion maxima 
                mi_eval= Integer.MAX_VALUE;
            }else{
              int camino[]=victims2R.costeAyudarVictima(robot, victim);
             mi_eval = camino[0];
            if(trazarCalculoCoste) {
                this.trazas.aceptaNuevaTrazaEjecReglas(identAgente," Se ejecuta la tarea :" + identTarea + 
                        " Mis victimas asignadas : " +victims2R.getIdtsVictimsAsignadas() + " Victima mas proxima : " + victims2R.getIdVictimaMasProxima()+"\n");
            }                                        
            EvaluacionAgente eval = new  EvaluacionAgente (identAgente, mi_eval);
            eval.setObjectEvaluationId(victim.getName());// Referenciamos la evaluacion con el ident de la victima
            infoDecision.setMi_eval(mi_eval);
            victim.setEstimatedCost(mi_eval);
      this.trazas.aceptaNuevaTrazaEjecReglas(identAgente,
                        " el coste de realizar el objetivo almacenado en Victimas2R es : " +victims2R.getVictimARescatar(victim.getName()).getEstimatedCost() + " El coste calculado ha sido  : " + victim.getEstimatedCost()+"\n");
            infoDecision.setTengoMiEvaluacion(Boolean.TRUE);
            this.getEnvioHechos().insertarHechoWithoutFireRules(eval);
            this.getEnvioHechos().insertarHechoWithoutFireRules(robot);
            this.getEnvioHechos().actualizarHecho(infoDecision);
            }
           } catch (Exception e) {
       }
    }
}