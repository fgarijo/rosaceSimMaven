/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.Rosace.tareasComunes;
import java.util.ArrayList;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.InfoEstadoAgente;
import org.icaro.aplicaciones.Rosace.informacion.InfoRolAgente;
import org.icaro.aplicaciones.Rosace.informacion.OrdenParada;
import org.icaro.aplicaciones.Rosace.informacion.PeticionConfirmarEquipo;
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
public class ProcesarPeticionConfirmRolenEquipo extends TareaSincrona {
    private ItfUsoMovimientoCtrl itfcompMov;
    @Override
    public void ejecutar(Object... params) {
            try {
             RobotStatus1 miEstatus = (RobotStatus1) params[0];
             PeticionConfirmarEquipo peticion = (PeticionConfirmarEquipo) params[1];
             InfoEquipo miEquipo = (InfoEquipo)params[2];
//             InfoParaDecidirQuienVa infoDecision = (InfoParaDecidirQuienVa)params[4];
//             MisObjetivos misObjsDecision = (MisObjetivos)params[5];
//             trazas.aceptaNuevaTraza(new InfoTraza(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea , InfoTraza.NivelTraza.info));
             ArrayList<String> idsMiembroActivos = miEquipo.getIDsMiembrosActivos();  
             String idAgtePeticionario= peticion.getIdentAgente();
        trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Procesa una peticion para confirmar el rol en el  equipo   recibida del agente  " +idAgtePeticionario + "\n"
                + " Los miembros en mi equipo : "+ idsMiembroActivos+ "\n");
            System.out.println( "\n"+ " Ident Agente : " + identAgente + " Ident Tarea " + identTarea+" Miembros activos del equipo  "+idsMiembroActivos.toString() +" \n\n" ); 
           InfoRolAgente mirol = new InfoRolAgente(identAgente,miEquipo.getTeamId(),miEstatus.getIdRobotRol(),VocabularioRosace.IdentMisionEquipo);
                this.getComunicator().enviarInfoAotroAgente(mirol, identAgente);
                this.getEnvioHechos().eliminarHecho(peticion);
             if(!idsMiembroActivos.contains(idAgtePeticionario)){ 
              InfoRolAgente rolInferidoAgtePeticionario = new InfoRolAgente(idAgtePeticionario,miEquipo.getTeamId(),miEstatus.getIdRobotRol(),VocabularioRosace.IdentMisionEquipo);
                miEquipo.procesarInfoRolAgente(rolInferidoAgtePeticionario);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "El agente peticionario no esta entre los miembros activos. Lo añado  " +"\n"
                + " Los miembros en mi equipo : "+ idsMiembroActivos+ "\n");
                }  
               }         
            catch(Exception e) {
                  e.printStackTrace();
            }
    }
}
