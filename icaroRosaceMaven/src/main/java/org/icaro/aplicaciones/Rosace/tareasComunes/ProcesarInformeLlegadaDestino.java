/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.Rosace.tareasComunes;
import org.icaro.aplicaciones.Rosace.informacion.InfoAgteRescateVictima;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.aplicaciones.Rosace.informacion.Victim;
import org.icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Informe;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaAsincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarInformeLlegadaDestino extends TareaAsincrona{
 private int velocidadCruceroPordefecto = 1;// metros por segundo 
 private int tiempoMedioRescate = 15; // minutos
 private int gastoEnergiaMinuto = 5; //unidades de energia
 private int costeEstimadoRescate = 0;
 
  private ItfUsoMovimientoCtrl itfcompMov;
  private Victim victimaRescatar;
  private String estadoComponente;
  @Override
  public void ejecutar(Object... params) {
		try {
//Suponemos que cuando llega al destino se salva la victima
 // habra que actualizar las victimas, los objetivos, el estado del movimiento  y cambiar el foco  
 // El robot esta parado, por tanto no hace falta en thread para dar la orden
             MisObjetivos misObjsAccion = (MisObjetivos)params[0];
             VictimsToRescue victims = (VictimsToRescue) params[1];
             Focus focoActual = (Focus) params[2];
             RobotStatus1 estatusRobot = (RobotStatus1) params[3];
             InfoCompMovimiento infoCompMov = (InfoCompMovimiento) params[4];
             Informe informeRecibido = (Informe) params[5];
//             trazas.aceptaNuevaTraza(new InfoTraza(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea , InfoTraza.NivelTraza.info));
             trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se Procesa el informe   recibido por el agente :"+ "\n"+ informeRecibido.referenciaContexto +" Cuyo contenido:"+informeRecibido.contenidoInforme + "\n");
            trazas.aceptaNuevaTraza(new InfoTraza("OrdenRescate",
                                                     " El robot " + this.identAgente + " ha salvado a la victima " + informeRecibido.referenciaContexto+"\n",
                                                     InfoTraza.NivelTraza.debug));
    // se actualiza el coste de la  vicitima salvada
             itfcompMov = (ItfUsoMovimientoCtrl)infoCompMov.getitfAccesoComponente();
             String victimaRescatadaId = informeRecibido.getReferenciaContexto();
             
//             victims.addEstimatedCostVictim2Rescue(victimaRescatadaId, 0);
            estadoComponente=estatusRobot.getestadoMovimiento();
             victims.eliminarVictima(victimaRescatadaId);
             this.getEnvioHechos().eliminarHecho(informeRecibido);
             this.informarControladorRescateVictima(victimaRescatadaId); // informamos al agente controlador
             // Se actualizan los objetivos, se da por conseguido el objetivo salvar a la victima
             // se supone que este objetivo era el mas prioritario, si no lo era hay un problema
              Objetivo objetivoConseguido = misObjsAccion.getobjetivoMasPrioritario();
              Thread accesoCompMovimiento = new Thread(){
                                @Override
				public void run(){
                                    itfcompMov.initContadorGastoEnergia();
					itfcompMov.moverAdestino(victimaRescatar.getName(), victimaRescatar.getCoordinateVictim(), velocidadCruceroPordefecto);
				}
			};
              if (victimaRescatadaId.equals(objetivoConseguido.getobjectReferenceId())){
                  objetivoConseguido.setSolved();
                  objetivoConseguido.setPriority(-1);
                  misObjsAccion.cambiarPrioridad(objetivoConseguido);
                  // Se actualiza el componente movimiento
             // Verificamos el foco si tiene un objetivo solved cambiamos el foco al objetivo actual
             // si tiene uno solving lo dejamos
                  Objetivo nuevoObjetivo = misObjsAccion.getobjetivoMasPrioritario();
                  if( nuevoObjetivo.getState()!= Objetivo.SOLVED){
             // tiene  objetivos pendientes , se da la orden de que vaya a salvar a la victima
                      String idVictimaRescatar=nuevoObjetivo.getobjectReferenceId();
                      victimaRescatar = victims.getVictimToRescue(idVictimaRescatar);
                     estatusRobot.setidentDestino(idVictimaRescatar);
//                     itfcompMov.moverAdestino(nuevoObjetivo.getobjectReferenceId(), victimaRescatada.getCoordinateVictim(), velocidadCruceroPordefecto);
                     nuevoObjetivo.setSolving();
                     this.getEnvioHechos().actualizarHechoWithoutFireRules(nuevoObjetivo);
                     itfcompMov.moverAdestino(victimaRescatar.getName(), victimaRescatar.getCoordinateVictim(), velocidadCruceroPordefecto);
//                     accesoCompMovimiento.start();
                       estadoComponente=EstadoMovimientoRobot.RobotEnMovimiento.name();
                    estatusRobot.setestadoMovimiento(estadoComponente);
                  }else {
                      nuevoObjetivo=null;
                  }
                 
//                  if(focoActual.getFoco().getState()==Objetivo.SOLVED) focoActual.setFoco(objetivoConseguido);
                  focoActual.setFoco(nuevoObjetivo);
//                   focoActual.refocusUltimoObjetivoSolving();
              // Se envian los cambios al motor   
                  
                  this.getEnvioHechos().actualizarHechoWithoutFireRules(victims);
//                  this.getEnvioHechos().actualizarHechoWithoutFireRules(misObjsAccion);
                  this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
                  this.getEnvioHechos().actualizarHechoWithoutFireRules(objetivoConseguido);
                  this.getEnvioHechos().actualizarHecho(focoActual);
                  trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea +"\n"+
                           " El identificador de la victima  :"+ victimaRescatadaId + " y el del ultimo objetivo : "+objetivoConseguido.getobjectReferenceId()+" coinciden " +"\n"+
                           "EstadoComponente : "+estadoComponente+ " Objetivo mas prioritario en curso "+misObjsAccion.getobjetivoMasPrioritario().toString()+ "\n"+ "  El foco esta en el ojetivo :  "+focoActual + "\n");
              }else
                   trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea +"\n"+
                           "El identificador de la victima  :"+ victimaRescatadaId + " y el del ultimo objetivo : "+objetivoConseguido.getobjectReferenceId()+" NO coinciden " + "\n");
        } catch (Exception e) {
			   e.printStackTrace();
        }
                
}
private void informarControladorRescateVictima(String idVictimaAsignada){
    //ACTUALIZAR ESTADISTICAS
            //Inicializar y recuperar la referencia al recurso de estadisticas 
    // Supongo como prueba que la evaluacion es una constante, pero deberia obtenerse del RobotStatus
            long tiempoActual = System.currentTimeMillis();
             int gastoEnergia = itfcompMov.getContadorGastoEnergia(); // En terminos de energia consumida  para el rescate
            InfoAgteRescateVictima infoVictimaRescatada = new InfoAgteRescateVictima (this.identAgente,idVictimaAsignada,tiempoActual,gastoEnergia);
            InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaRescatada",infoVictimaRescatada);
            this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
}

}
