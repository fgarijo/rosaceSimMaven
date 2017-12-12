/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;
import org.icaro.aplicaciones.Rosace.informacion.EvaluacionAgente;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.InfoEstadoAgente;
import org.icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import org.icaro.aplicaciones.Rosace.informacion.PeticionAsumirObjetivo;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

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
                  if(infoEstadoRobot.getBloqueado())
            // Actualizo el equipo
                      miEquipo.setEstadoTeamMember(infoEstadoRobot);
                  if(infoDecision !=null){
                 String idVictimaEnDecision = infoDecision.getidElementoDecision();
                     Victim victimaImplicada = victims2R.getVictimToRescue(idVictimaEnDecision);
                             victimaImplicada.setrobotResponsableId(null);
            // Se crea una evaluacion disuasoria con maximo coste y se procesa como si el agte la hubiera enviado
                      EvaluacionAgente evalDisuasoria = new EvaluacionAgente (idAgteqEnviasuEstado,Integer.MAX_VALUE );
                      infoDecision.addNuevaEvaluacion(evalDisuasoria);
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
