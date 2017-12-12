/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.Rosace.tareasComunes;
import java.util.ArrayList;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.InfoEstadoAgente;
import org.icaro.aplicaciones.Rosace.informacion.OrdenParada;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot;
import org.icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
/**
 *
 * @author FGarijo
 */
public class ProcesarOrdenParada extends TareaSincrona {
    private ItfUsoMovimientoCtrl itfcompMov;
    @Override
    public void ejecutar(Object... params) {
            try {
             RobotStatus1 miEstatus = (RobotStatus1) params[0];
             InfoCompMovimiento infoCompMov = (InfoCompMovimiento) params[1];
             OrdenParada ordenCC = (OrdenParada)params[2];
             InfoEquipo miEquipo = (InfoEquipo)params[3];
             InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[4];
             MisObjetivos misObjsDecision = (MisObjetivos)params[5];
//             trazas.aceptaNuevaTraza(new InfoTraza(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea , InfoTraza.NivelTraza.info));
             trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Procesa la orden  Parada   recibida por el agente  "+"\n");
            // se inicializa objetivos, foco y victimas, y se vacia la memoria de trabajo y se me
            itfcompMov = (ItfUsoMovimientoCtrl)infoCompMov.getitfAccesoComponente();
//             this.informarControladorRescateVictima(victimaRescatadaId); // informamos al agente controlador
             // Se actualizan los objetivos, se da por conseguido el objetivo salvar a la victima
             // se supone que este objetivo era el mas prioritario, si no lo era hay un problema
//              Objetivo objetivoConseguido = misObjs.getobjetivoMasPrioritario();
              Thread accesoCompMovimiento = new Thread(){
                                @Override
				public void run(){
//                                    itfcompMov.initContadorGastoEnergia();
					itfcompMov.parar();
				}
			};
            accesoCompMovimiento.start();
//             this.getEnvioHechos().insertarHechoWithoutFireRules(miEquipo);
            miEstatus.setBloqueado(true);
            miEstatus.setRobotCoordinate(itfcompMov.getCoordenadasActuales());
            miEstatus.setestadoMovimiento(EstadoMovimientoRobot.RobotBloqueado.name());
            miEstatus.setcausaCambioEstado(VocabularioRosace.CausaCambioMovtoOrdenCC);
            ArrayList<String> idsMiembroActivos = miEquipo.getIDsMiembrosActivos();
            if (idsMiembroActivos.size()>0)
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, 
                        "Se ejecuta la tarea : " + identTarea + " El robot esta en la posicion " + itfcompMov.getCoordenadasActuales() +
                        "estado del robot : "+EstadoMovimientoRobot.RobotBloqueado.name()+"\n" +
                        "esta bloqueado : "+ miEstatus.getBloqueado()+"\n" +
                         " Informo al equipo : "+ miEquipo.getIDsMiembrosActivos().toString()+ "  de mi estado " +"\n");
            
            // Todos los objetivos decision pendientes los pongo a solved
//            Iterator<Objetivo> it = misObjsDecision.getMisObjetivosPriorizados().iterator();    	    	
//    	while (it.hasNext()){
//  	  	  //Hay al menos un objetivo    		
//  	      Objetivo obj = it.next();
//              obj.setSolved();
//              }
            // Se informa al agente controlador de la ejecucion de la orden de parada y los companyeros del bloqueo
            InfoEstadoAgente infoMiEstado = new InfoEstadoAgente(this.identAgente,EstadoMovimientoRobot.RobotBloqueado.name(),VocabularioRosace.CausaCambioMovtoOrdenCC);
            infoMiEstado.setBloqueado(true);
            InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo(VocabularioRosace.MsgeInfoEstadoAgente,infoMiEstado);
            this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
           
            if (idsMiembroActivos.size()>0)
            System.out.println( "\n"+ " Ident Agente : " + identAgente + " Ident Tarea " + identTarea+" Miembros activos del equipo  "+idsMiembroActivos.toString() +" \n\n" ); 
            System.out.println( "\n"+identTarea+" Miestado  "+ infoMiEstado.toString() +" \n\n" ); 
                this.getComunicator().informaraGrupoAgentes(infoMiEstado, idsMiembroActivos);
                this.getEnvioHechos().eliminarHecho(ordenCC);
             this.getEnvioHechos().eliminarHechoWithoutFireRules(miEstatus);
             this.getEnvioHechos().insertarHecho(miEstatus.clone());
             if(infoDecision!=null){
//                EvaluacionAgente miEval = new EvaluacionAgente(identAgente, Integer.MAX_VALUE);
                infoDecision.setMi_eval(Integer.MAX_VALUE);
                infoDecision.hanLlegadoTodasLasEvaluaciones=true;
                infoDecision.heInformadoAlmejorParaQueAsumaElObjetivo=false;
                infoDecision.noSoyElMejor=true;
                this.getEnvioHechos().actualizarHecho(infoDecision);
                Objetivo objDecision = misObjsDecision.getobjetivoMasPrioritario();
                if(objDecision!=null){
                    objDecision.setSolved();
                    this.getEnvioHechos().actualizarHecho(objDecision);
                }  
            }
               }         
            catch(Exception e) {
                  e.printStackTrace();
            }
    }
}
