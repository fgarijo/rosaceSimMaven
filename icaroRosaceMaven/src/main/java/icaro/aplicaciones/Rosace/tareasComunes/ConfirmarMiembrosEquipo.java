/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;

import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;
import icaro.aplicaciones.Rosace.informacion.PeticionAgente;
import icaro.aplicaciones.Rosace.informacion.PeticionConfirmarEquipo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.InformeDeTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;

/**
 *
 * @author Francisco J Garijo
 */
public class ConfirmarMiembrosEquipo extends TareaSincrona {

    private ArrayList<String> agentesEquipo;//resto de agentes que forman mi equipo                                
    private String nombreAgenteEmisor;
    private final long valorTemporizador = 2000;

    @Override
    public void ejecutar(Object... params) {
        try {
            InformeDeTarea informe = (InformeDeTarea) params[0];
            InfoEquipo equipoInfo = (InfoEquipo) params[1];
            Focus focoActual = (Focus) params[2];
            nombreAgenteEmisor = this.identAgente;
            agentesEquipo = equipoInfo.getTeamMemberIDs();
            ArrayList<String> agtesConMiRol = equipoInfo.getTeamMemberIDsWithMyRol();
            if (agtesConMiRol.size() < agentesEquipo.size()) {
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se Ejecuta la Tarea : " + identTarea + " Los agtes con mi rol de mi equipo son : " + agtesConMiRol);
                PeticionAgente petConfirmarEquipo = new PeticionConfirmarEquipo(identAgente, VocabularioRosace.MsgePeticionConfirmarEquipo, VocabularioRosace.IdentMisionEquipo);
                trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Enviamos peticion confirmar equipo " + petConfirmarEquipo.toString());
                // Se envia la peticion a los agentes de los que no tengo el rol
                String identAgteAverificar;
                for (int i = 0; i < agentesEquipo.size(); i++) {
                    identAgteAverificar = agentesEquipo.get(i);
                    if (!agtesConMiRol.contains(identAgteAverificar)) {
                        this.getComunicator().enviarInfoAotroAgente(petConfirmarEquipo, identAgteAverificar);
                        trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Envio mensaje de peticion de rol al agente : " + identAgteAverificar);
                    }
                }
//               this.getEnvioHechos().insertarHecho(petConfirmarEquipo);
                this.getEnvioHechos().eliminarHecho(informe);
                this.generarInformeTemporizado(valorTemporizador, this.identTarea, nombreAgenteEmisor, null);
            } else { // tiene todos los miembros del equipo
                Objetivo objFocalizado = focoActual.getFoco();
                objFocalizado.setSolved();
                this.getEnvioHechos().actualizarHecho(objFocalizado);
                this.getEnvioHechos().actualizarHecho(focoActual);
                trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Tengo todos los miembros del equipo . el foco esta en  : " + objFocalizado);
            }
        } catch (Exception e) {
        }
    }

}
