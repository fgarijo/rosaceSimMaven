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
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class ObtenerEvaluacionRealizarObjetivo extends TareaSincrona {
	
    private int mi_eval, mi_eval_nueva;
        
//    private ItfUsoRecursoTrazas trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
    
    //private TimeOutRespuestas tiempoSinRecibirRespuesta;  //no usado
    
    private Coordinate robotLocation; //Localizacion del robot
    private double funcionEvaluacion; //Variable para almacenar el resultado de calcular la funcion de evaluacion utilizada
    private Integer divisor = 10000;
    private boolean trazarCalculoCoste;
    
// Introduzco como parametro  la InfoParaDecidrQuienVa y le definimos el valor de la evaluacion. 
    @Override
    public void ejecutar(Object... params) {
	   try {
            Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
            Victim victim = (Victim)params[1];            
            Coordinate victimLocation = victim.getCoordinateVictim();            
            InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[2];
            RobotStatus1 robot = (RobotStatus1)params[3];                        
            VictimsToRescue victims2R =(VictimsToRescue)params[4];
            MisObjetivos misObjsAccion = (MisObjetivos)params[5];
            trazarCalculoCoste=true;
            Coste1 coste = new Coste1();
            coste.setTrazar(true);
            robotLocation = robot.getInfoCompMovt().getCoordenadasActuales();
            robot.setRobotCoordinate(robotLocation);

//            
            //Las dos sentencias siguientes permiten utilizar la funcion de evaluacion 1 que solo considera la distancia entre el robot y la nueva victima
            //COMENATAR las dos lineas siguientes si se quiere utilizar la funcion de evaluacion 2
	        //double distanciaC1toC2 = coste.distanciaC1toC2(robotLocation, victimLocation);
	        //funcionEvaluacion = coste.FuncionEvaluacion2(distanciaC1toC2, 1.0, robot, victim);
	        //Las dos sentencias siguientes permiten utilizar la funcion de evaluacion 2 que considera el recorrido que tendria que hacer y la engergia
	        //QUITAR EL COMENTARIO de las dos lineas siguientes si se quiere utilizar la funcion de evaluacion 2
	        //SI SE UTILIZA LA funcion de evaluacion 1 entonces las dos lineas siguientes deben estar comentadas
//	        double distanciaCamino = coste.CalculaDistanciaCamino(nombreAgenteEmisor, robotLocation, victim, victims2R, misObjs);
//	        funcionEvaluacion = coste.FuncionEvaluacion2(distanciaCamino, 1.0, robot, victim);
//
            System.out.println("Realizando  la evaluacion para el Robot ->" + this.identAgente);
            System.out.println("robotLocation->"+robotLocation);
            System.out.println("para la victima ->"+victim.toString());
//            ArrayList victimasAsignadas = victims2R.getIdtsVictimsAsignadas();
//            int numVictimasAsignadas=0;
//            if (victimasAsignadas!=null){
//                numVictimasAsignadas= victimasAsignadas.size();
//            System.out.println("victims2R->"+victimasAsignadas.toString());
//            }
//            System.out.println("misObjs->"+misObjs.getobjetivoMasPrioritario().toString());
            if (misObjsAccion.getobjetivoMasPrioritario()!=null)System.out.println("misObjs->"+misObjsAccion.getobjetivoMasPrioritario().toString());
	        //Las sentencias siguientes permiten utilizar la funcion de evaluacion 3 que considera el recorrido que tendria que hacer y la engergia y el tiempo
//            double distanciaCamino = coste.CalculaDistanciaCamino(this.identAgente, robotLocation, victim, victims2R, misObjs);
//            double tiempoAtencionVictimas = coste.CalculaTiempoAtencion(3.0, victim, victims2R, misObjs);
//            funcionEvaluacion = coste.FuncionEvaluacion3(distanciaCamino, 5.0, tiempoAtencionVictimas, 9.0, robot, victim);

            if( robot.getBloqueado()){ // se envia una evaluacion maxima 
                mi_eval= Integer.MAX_VALUE;
            }else{
               
//                funcionEvaluacion= coste.CalculoCosteAyudarVictima(identAgente, robotLocation, robot, victim, victims2R, misObjsAccion, "FuncionEvaluacion3");
//             mi_eval = (int)funcionEvaluacion;   //convierto de double a int porque la implementacion inicial de Paco usaba int 
//            ArrayList victimasArescatar= victims2R.getVictimsAsignadas();
//             if (victims2R.getVictimsAsignadas().isEmpty()){
//                 int[] camino = new int[2];
//                 mi_eval =coste.costeAyudarVictimaIndividual(identAgente, robotLocation, robot, victim, victims2R, misObjsAccion, identTarea);
//                 camino[0]=mi_eval;
//                 camino[1]=
//             }
//             else{
                
                 int camino[] = coste.costeAyudarVictimas(identAgente, robotLocation, robot, victim, victims2R, misObjsAccion, identTarea);
             mi_eval = camino[0];
             victims2R.setCaminoMinimo(camino);
//            }
	     
            if(trazarCalculoCoste) {
                this.trazas.aceptaNuevaTrazaEjecReglas(identAgente," Se ejecuta la tarea :" + identTarea + 
                        " Mis victimas asignadas : " +victims2R.getIdtsVictimsAsignadas() + " Victima mas proxima : " + victims2R.getIdVictimaMasProxima()+"\n");
            }         
                                         
            EvaluacionAgente eval = new  EvaluacionAgente (identAgente, mi_eval);
            eval.setObjectEvaluationId(victim.getName());// Referenciamos la evaluacion con el ident de la victima
            infoDecision.setMi_eval(mi_eval);
            victim.setEstimatedCost(mi_eval);
      //      victims2R.actualizarVictimARescatar(victim);
      this.trazas.aceptaNuevaTrazaEjecReglas(identAgente,
                        " el coste de realizar el objetivo almacenado en Victimas2R es : " +victims2R.getVictimARescatar(victim.getName()).getEstimatedCost() + " El coste calculado ha sido  : " + victim.getEstimatedCost()+"\n");
            infoDecision.setTengoMiEvaluacion(Boolean.TRUE);
            this.getEnvioHechos().insertarHechoWithoutFireRules(eval);
            this.getEnvioHechos().insertarHechoWithoutFireRules(robot);
            this.getEnvioHechos().actualizarHecho(infoDecision);
       
            }
           } catch (Exception e) {
		   e.printStackTrace();
       }
    }
}