/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;

import icaro.aplicaciones.Rosace.informacion.InfoAgteVictimaRescatada;
import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaAsincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarInfoVictimaRescatada extends TareaAsincrona {

    @Override
    public void ejecutar(Object... params) {
        try {
//Suponemos que cuando llega al destino se salva la victima
            // habra que actualizar las victimas, los objetivos, el estado del movimiento  y cambiar el foco  
            // El robot esta parado, por tanto no hace falta en thread para dar la orden
            InfoAgteVictimaRescatada infoVictimaRescatada = (InfoAgteVictimaRescatada) params[0];
            InfoEquipo miEquipo = (InfoEquipo) params[1];
            VictimsToRescue victims = (VictimsToRescue) params[2];
            Focus focoActual = (Focus) params[3];     
            Objetivo objetivoFocalizado = focoActual.getFoco();           
            String victimaRescatadaId = infoVictimaRescatada.getVictimId();
            String robotRescatador = infoVictimaRescatada.getRobotId();
            Victim victimaRescatada = victims.getVictimARescatar(victimaRescatadaId);
            victimaRescatada.setRescued();
            victimaRescatada.setrobotResponsableId(robotRescatador);
            victims.elimVictimAsignada(victimaRescatadaId);
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se Procesa la  informacion   enviada  por el agente :" + robotRescatador +  " El Foco estaba en :" +objetivoFocalizado + "\n"
                    + " Cuyo tiempo de rescate ha sido de : " + infoVictimaRescatada.getTiempoRescate() + " Se informa al Agte controlador del rescate de la victima" + victimaRescatadaId + "\n"
                    + " Elimino  la victima : " + victimaRescatadaId + " del conj de victimas asignadas. Las victimas asignadas restantes son: " + victims.getIdtsVictimsAsignadas() + "\n");   
            InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaRescatada", infoVictimaRescatada);
            this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
            this.getEnvioHechos().actualizarHecho(victimaRescatada);
            this.getEnvioHechos().eliminarHecho(infoVictimaRescatada);                  
        } catch (Exception e) {
        }
    }

}