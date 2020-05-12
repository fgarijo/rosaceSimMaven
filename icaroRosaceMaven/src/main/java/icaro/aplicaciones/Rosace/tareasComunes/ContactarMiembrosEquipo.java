/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;

import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.Rosace.informacion.InfoRolAgente;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import java.util.ArrayList;
import icaro.aplicaciones.Rosace.objetivosComunes.DefinirMiEquipo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;

/**
 *
 * @author Francisco J Garijo
 */
public class ContactarMiembrosEquipo extends TareaSincrona {

    private ArrayList<String> agentesEquipo;//resto de agentes que forman mi equipo                                
    private String nombreAgenteEmisor;
    private final long valorTemporizador = 2000;

    @Override
    public void ejecutar(Object... params) {
        try {
            RobotStatus1 miStatus = (RobotStatus1) params[0];
            InfoEquipo equipoInfo = (InfoEquipo) params[1];
            Focus focoActual = (Focus) params[2];
            nombreAgenteEmisor = this.identAgente;
            agentesEquipo = equipoInfo.getTeamMemberIDs();
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se Ejecuta la Tarea : " + identTarea + " Los agtes de mi equipo son : " + agentesEquipo);  
            InfoRolAgente mirol = new InfoRolAgente(identAgente, equipoInfo.getTeamId(), miStatus.getIdRobotRol(), VocabularioRosace.IdentMisionEquipo);
            trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Enviamos informacion de Rol " + mirol.getidentRolAgte());
            this.getComunicator().informaraGrupoAgentes(mirol, agentesEquipo);
            equipoInfo.setinicioContactoConEquipo();
            equipoInfo.setidentMiRolEnEsteEquipo(miStatus.getIdRobotRol());
            DefinirMiEquipo definirMiequipoObj = new DefinirMiEquipo(VocabularioRosace.IdentMisionEquipo);
            this.generarInformeTemporizado(valorTemporizador, this.identTarea, nombreAgenteEmisor, null);
            definirMiequipoObj.setSolving();
            focoActual.setFoco(definirMiequipoObj);
            this.getEnvioHechos().insertarHecho(definirMiequipoObj);
            this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(nombreAgenteEmisor, " Numero de agentes de los que espero respuesta: " + agentesEquipo.size());
        } catch (Exception e) {
        }
    }

}
