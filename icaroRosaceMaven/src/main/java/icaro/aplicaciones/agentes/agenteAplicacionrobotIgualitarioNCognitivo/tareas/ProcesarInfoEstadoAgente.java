/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.EvaluacionAgente;
import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.InfoEstadoAgente;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.PeticionAsumirObjetivo;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 */
public class ProcesarInfoEstadoAgente extends TareaSincrona {

    private InfoParaDecidirQuienVa infoDecision;
	@Override
	public void ejecutar(Object... params) {
            try {
                InfoEquipo miEquipo = (InfoEquipo)  params[0];
                InfoEstadoAgente infoEstadoRobot = (InfoEstadoAgente)params[1];
                  VictimsToRescue victims2R= (VictimsToRescue)params[2];
                  InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[3];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
//                  RobotStatus1 estatusRobot = (RobotStatus1)peticionRecibida.getJustificacion();
            String idAgteqEnviasuEstado = infoEstadoRobot.getidentAgte();
//                  if(infoEstadoRobot.getBloqueado())
            // Actualizo el equipo
                      miEquipo.setEstadoTeamMember(infoEstadoRobot);
                  if(infoDecision !=null&&infoEstadoRobot.getBloqueado() ){
                 String idVictimaEnDecision = infoDecision.getidElementoDecision();
                     Victim victimaImplicada = victims2R.getVictimToRescue(idVictimaEnDecision);
                             victimaImplicada.setrobotResponsableId(null);
            // Se crea una evaluacion disuasoria con maximo coste y se procesa como si el agte la hubiera enviado
            // Actualizar el equipo en infoDecision
//                      EvaluacionAgente evalDisuasoria = new EvaluacionAgente (idAgteqEnviasuEstado,Integer.MAX_VALUE );
//                      infoDecision.addNuevaEvaluacion(evalDisuasoria);
                      infoDecision.eliminarAgenteEquipo(idAgteqEnviasuEstado);
                      infoDecision.setobjetivoAsumidoPorOtroAgte(false); // en caso de que lo hubiera asumido
                
                 this.getEnvioHechos().actualizarHecho(victimaImplicada);
                 this.getEnvioHechos().actualizarHecho(infoDecision);
                 
                 this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        " Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado +"\n"+
                        "  idVictima implicada : "+idVictimaEnDecision +" Estado del robot proponente bloqueado? : "+infoEstadoRobot.getBloqueado()+"\n"+
                        "  Se generan  una Evaluacion Disuasoria y se anyade InfoParaDecidir al motor: " + "\n" +
                                 " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString()+ "\n" ); 
                  }else
                     this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " InfoEstado recibido del robot :  " + idAgteqEnviasuEstado +"\n"+
                        " infoDecision es  null. " +
                                " Se actualiza el equipo y se elimina InfoEstadoAgte. Robots en el equipo :  " + miEquipo.getIDsMiembrosActivos().toString()+ "\n" );  
            
//                 this.getEnvioHechos().actualizarHecho(miEquipo);
                 this.getEnvioHechos().eliminarHecho(infoEstadoRobot);
            }
            catch(Exception e) {
                  e.printStackTrace();
            }
	 
}
}
