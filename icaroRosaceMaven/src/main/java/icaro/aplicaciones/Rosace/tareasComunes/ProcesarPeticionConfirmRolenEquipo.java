/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;

import java.util.ArrayList;
import icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import icaro.aplicaciones.Rosace.informacion.InfoRolAgente;
import icaro.aplicaciones.Rosace.informacion.PeticionConfirmarEquipo;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;

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
            InfoEquipo miEquipo = (InfoEquipo) params[2];
            ArrayList<String> idsMiembroActivos = miEquipo.getIDsMiembrosActivos();
            String idAgtePeticionario = peticion.getIdentAgente();
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Procesa una peticion para confirmar el rol en el  equipo   recibida del agente  " + idAgtePeticionario + "\n"
                    + " Los miembros en mi equipo : " + idsMiembroActivos + "\n");
            System.out.println("\n" + " Ident Agente : " + identAgente + " Ident Tarea " + identTarea + " Miembros activos del equipo  " + idsMiembroActivos.toString() + " \n\n");
            InfoRolAgente mirol = new InfoRolAgente(identAgente, miEquipo.getTeamId(), miEstatus.getIdRobotRol(), VocabularioRosace.IdentMisionEquipo);
            this.getComunicator().enviarInfoAotroAgente(mirol, identAgente);
            this.getEnvioHechos().eliminarHecho(peticion);
            if (!idsMiembroActivos.contains(idAgtePeticionario)) {
                InfoRolAgente rolInferidoAgtePeticionario = new InfoRolAgente(idAgtePeticionario, miEquipo.getTeamId(), miEstatus.getIdRobotRol(), VocabularioRosace.IdentMisionEquipo);
                miEquipo.procesarInfoRolAgente(rolInferidoAgtePeticionario);
                trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "El agente peticionario no esta entre los miembros activos. Lo añado  " + "\n"
                        + " Los miembros en mi equipo : " + idsMiembroActivos + "\n");
            }
        } catch (Exception e) {
        }
    }
}
