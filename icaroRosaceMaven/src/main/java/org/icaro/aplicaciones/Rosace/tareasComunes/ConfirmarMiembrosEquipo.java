/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.Rosace.tareasComunes;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.Rosace.informacion.InfoRolAgente;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;
import org.icaro.aplicaciones.Rosace.informacion.PeticionAgente;
import org.icaro.aplicaciones.Rosace.informacion.PeticionConfirmarEquipo;
import org.icaro.aplicaciones.Rosace.objetivosComunes.DefinirMiEquipo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.InformeDeTarea;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;

/**
 *
 * @author Francisco J Garijo
 */
   public class ConfirmarMiembrosEquipo  extends TareaSincrona {
   private ArrayList <String> agentesEquipo;//resto de agentes que forman mi equipo                                
   private String nombreAgenteEmisor;
//   private long valorTemporizador = 5000;
   // private TimeOutRespuestas tiempoSinRecibirRespuesta;  //no usado
	@Override
	public void ejecutar(Object... params) {
		try {     
              InformeDeTarea informe = (InformeDeTarea)params[0];    
             InfoEquipo equipoInfo = (InfoEquipo)params[1]; 
             Focus focoActual = (Focus)params[2];
              nombreAgenteEmisor = this.identAgente;
              agentesEquipo = equipoInfo.getTeamMemberIDs();
              if(equipoInfo.getIDsMiembrosActivos().size()<agentesEquipo.size()){
              trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se Ejecuta la Tarea : "+ identTarea + " Los agtes de mi equipo son : " + agentesEquipo );
                        //            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Enviamos la evaluacion " + miEvaluacion, InfoTraza.NivelTraza.masterIA));          
              PeticionAgente petConfirmarEquipo = new PeticionConfirmarEquipo(identAgente,VocabularioRosace.MsgePeticionConfirmarEquipo,VocabularioRosace.IdentMisionEquipo);
              trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Enviamos peticion cnfirmar equipo " + petConfirmarEquipo.toString());  
              this.getComunicator().informaraGrupoAgentes(petConfirmarEquipo, agentesEquipo);
               this.getEnvioHechos().insertarHecho(petConfirmarEquipo);
               this.getEnvioHechos().eliminarHecho(informe);
//               this.getEnvioHechos().actualizarHecho(focoActual);
              trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Numero de agentes de los que espero respuesta: " + agentesEquipo.size() );     
              this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutContactarMiembrosEquipo,null,nombreAgenteEmisor, null);
                 }  else{ // tiene todos los miembros del equipo
                  Objetivo objFocalizado= focoActual.getFoco();
                  objFocalizado.setSolved();
                  this.getEnvioHechos().actualizarHecho(objFocalizado);
                  this.getEnvioHechos().actualizarHecho(focoActual);
              }
		} catch (Exception e) {
			e.printStackTrace();
        }
    }
   
}