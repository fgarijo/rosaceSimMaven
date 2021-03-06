/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.PeticionAsumirObjetivo;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoTransimisionObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;
import java.util.Iterator;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author FGarijo
 */
public class EnviarEquipoPeticionesAsumirMisObjetivos extends TareaSincrona{
    
   @Override
   public void ejecutar(Object... params) {
	   try {
             InfoEquipo miEquipo = (InfoEquipo)params[0];
             MisObjetivos misObjsAccion = (MisObjetivos)params[1];
             RobotStatus1 miEstatus = (RobotStatus1) params[2];
             VictimsToRescue victimas = (VictimsToRescue)params[3];
             InfoTransimisionObjetivos infoTransmisionObjs;
             if (victimas.hayVictimasArescatar()){
             infoTransmisionObjs = new InfoTransimisionObjetivos(identAgente,miEquipo,miEstatus.getcausaCambioEstado());
             // Enviamos las propuestas a los miembros del equipo
             Iterator<Objetivo>  iterObj = misObjsAccion.getMisObjetivosPriorizados().iterator();
            ArrayList<String> idsAgtesMiequipo = miEquipo.getIDsMiembrosActivos();
                ArrayList idsVictimasAsignadas = victimas.getIdtsVictimsAsignadas();
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, "  Se ejecuta la tarea : " + identTarea 
                      + " Idents de las victimas a rescatar : "+ idsVictimasAsignadas +"\n"+
                      "Agentes en mi equipo: " + idsAgtesMiequipo ); 
             for(int i=0; i<idsVictimasAsignadas.size();i++){
  	  	  //Hay al menos un objetivo 
                  String idVictimaAsignada= (String)idsVictimasAsignadas.get(i);
                Objetivo obj = new AyudarVictima(idVictimaAsignada);
                PeticionAsumirObjetivo petAsumirObj = new PeticionAsumirObjetivo(this.identAgente, obj, (RobotStatus1)miEstatus); 
                this.getComunicator().informaraGrupoAgentes(petAsumirObj, idsAgtesMiequipo);
                infoTransmisionObjs.addInfoPropuestaEnviada(idVictimaAsignada);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Se envia una peticion al equipo para salvar a la victima : "+idVictimaAsignada+ " Se aniade la victima a InfoTransimisionObjetivos . Contenido :  " + infoTransmisionObjs +"\n");
                }
             this.getEnvioHechos().insertarHecho(infoTransmisionObjs);
             }else trazas.aceptaNuevaTrazaEjecReglas(identAgente, "  Se ejecuta la tarea : " + identTarea 
                      + " No hay victimas asignadas  para rescatar \n");
       } catch (Exception e) {
       }
   }

}
