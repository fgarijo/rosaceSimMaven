/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
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
public class ProcesarPeticionAsumirObjetivo extends TareaSincrona {

    private InfoParaDecidirQuienVa infoDecision;
	@Override
	public void ejecutar(Object... params) {
            try {
                Victim victimaImplicada = (Victim)  params[0];
                PeticionAsumirObjetivo peticionRecibida = (PeticionAsumirObjetivo)params[1];
                  MisObjetivos misObjsDecision= (MisObjetivos)params[2];
                  InfoEquipo miEquipo =  (InfoEquipo)params[3];
                  Focus foco = (Focus)params[4];
//                  VictimsToRescue victims = (VictimsToRescue)params[4];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
            String identAgteEnviaPeticion= peticionRecibida.getIdentAgente();
                  RobotStatus1 estatusRobot = (RobotStatus1)peticionRecibida.getJustificacion();
                  if(estatusRobot.getBloqueado()){
            // Actualizo el equipo
                      miEquipo.setTeamMemberStatus( estatusRobot);
                      miEquipo.eliminarIdentAgteDeMiEquipo(identAgteEnviaPeticion);
            // Se asume que el objetivo es "SalvarVictima" y se trabaja con la referencia a  la victima     
            // Se asume la peticion generando un objetivo para ver quien se hace cargo de salvar a la victima
//                 Victim victimaRescate= (Victim) peticionRecibida.getinfoComplementaria();
//                 if(victimaRescate!=null){
                 String idvictima = victimaImplicada.getName();
                 AyudarVictima nuevoObjAyudarVictima= new AyudarVictima(idvictima);
                 nuevoObjAyudarVictima.setPriority(victimaImplicada.getPriority());
                 victimaImplicada.setrobotResponsableId(null);
                 victimaImplicada.setisCostEstimated(false);
          //      if((objetivoEjecutantedeTarea == null)) newObjetivo.setSolving(); // se comienza el proceso para intentar conseguirlo                                        
           //       Se genera un objetivo para decidir quien se hace cargo de la ayuda y lo ponemos a solving
                 DecidirQuienVa newDecision = new DecidirQuienVa(idvictima);
                 newDecision.setSolving();
                 misObjsDecision.addObjetivo(newDecision);
//                 if (foco.getFoco()==null)foco.setFoco(newDecision);               
//                 this.getEnvioHechos().actualizarHecho(miEquipo);
//                 this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsDecision);
                 peticionRecibida.setpeticionAsumida(true);
                 this.getEnvioHechos().actualizarHecho(victimaImplicada);
                 this.getEnvioHechos().actualizarHecho(nuevoObjAyudarVictima);
//                 this.getEnvioHechos().actualizarHecho(foco);
                 this.getEnvioHechos().actualizarHecho(peticionRecibida);
                 this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        " Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida del robot :  " + identAgteEnviaPeticion +"\n"+
                        "  idVictima implicada : "+idvictima +" Estado del robot proponente bloqueado? : "+estatusRobot.getBloqueado()+"\n"+
                         "  Se generan los objetivos  : " +newDecision + " y : " + nuevoObjAyudarVictima  +"\n"+    
                                " El foco esta en el objetivo : " + foco.getFoco()+ "\n" +
                                "  Miembros en mi equipo : " + miEquipo.getIDsMiembrosActivos().toString()+ "\n"); 
                  }else
                     this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " Preticion recibida del robot :  " + identAgteEnviaPeticion +"\n"+
                        " la victima implicada es null. Estado del robot proponente bloqueado? : "+estatusRobot.getBloqueado()+
                                "El foco esta en el objetivo : " + foco.getFoco()+ "\n" );  
            
                  
            }
            catch(Exception e) {
                  e.printStackTrace();
            }
	 
}
}
