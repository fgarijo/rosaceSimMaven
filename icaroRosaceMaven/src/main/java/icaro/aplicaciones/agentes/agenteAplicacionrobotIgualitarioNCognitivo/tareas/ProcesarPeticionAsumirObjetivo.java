/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.PeticionAsumirObjetivo;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.objetivos.DecidirQuienVa;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

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
                   infoDecision = (InfoParaDecidirQuienVa)params[5];
            // Se Verifica que el robot que hace la propuesta esta bloqueado
            String identAgteEnviaPeticion= peticionRecibida.getIdentAgente();
                  RobotStatus1 estatusRobot = (RobotStatus1)peticionRecibida.getJustificacion();
                    String idvictima = victimaImplicada.getName();
                  if(estatusRobot.getBloqueado()){
            // Actualizo el equipo
                      miEquipo.setTeamMemberStatus( estatusRobot);
                 if ( infoDecision!=null){
                     infoDecision.eliminarAgenteEquipo(identAgteEnviaPeticion);
                     this.getEnvioHechos().actualizarHecho(infoDecision);
                     this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida  del robot :  " + identAgteEnviaPeticion +"\n"+
                       " Durante el proceso de decision. idVictima implicada : "+idvictima +" Estado del robot proponente bloqueado? : "+estatusRobot.getBloqueado()+
                                "El foco esta en el objetivo : " + foco.getFoco()+ "\n" ); 
                 }  
                 AyudarVictima nuevoObjAyudarVictima= new AyudarVictima(idvictima);
                 nuevoObjAyudarVictima.setPriority(victimaImplicada.getPriority());
                 victimaImplicada.setrobotResponsableId(null);
                 victimaImplicada.setisCostEstimated(false);
          //      if((objetivoEjecutantedeTarea == null)) newObjetivo.setSolving(); // se comienza el proceso para intentar conseguirlo                                        
           //       Se genera un objetivo para decidir quien se hace cargo de la ayuda y lo ponemos a solving
                 DecidirQuienVa newDecision = new DecidirQuienVa(idvictima);
                 newDecision.setSolving();
                 misObjsDecision.addObjetivo(newDecision);
                 this.getEnvioHechos().actualizarHecho(victimaImplicada);
                 this.getEnvioHechos().actualizarHecho(nuevoObjAyudarVictima);
                 this.getEnvioHechos().actualizarHecho(newDecision);                
                 this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        " Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida del robot :  " + identAgteEnviaPeticion +"\n"+
                        " Cuando no esta en un  proceso de decision.  idVictima implicada : "+idvictima +" Estado del robot proponente bloqueado? : "+estatusRobot.getBloqueado()+"\n"+
                         "  Se generan los objetivos  : " +newDecision + " y : " + nuevoObjAyudarVictima  +"\n"+    
                                " El foco esta en el objetivo : " + foco.getFoco()+ "\n" +
                                "  Miembros en mi equipo : " + miEquipo.getIDsMiembrosActivos().toString()+ "\n"); 
//                  }
                  }else
                     this.trazas.aceptaNuevaTrazaEjecReglas(this.getIdentAgente(), 
                        "Se ejecuta la tarea : " + this.getIdentTarea() + " Peticion recibida del robot :  " + identAgteEnviaPeticion +"\n"+
                        " la victima implicada es null. Estado del robot proponente bloqueado? : "+estatusRobot.getBloqueado()+
                                "El foco esta en el objetivo : " + foco.getFoco()+ "\n" ); 
                this.getEnvioHechos().eliminarHecho(peticionRecibida);
                if(foco.getFoco()==null)foco.setFoco(misObjsDecision.getobjetivoMasPrioritario());
                this.getEnvioHechos().actualizarHecho(foco);                 
            }
            catch(Exception e) {
            }	 
}
}
