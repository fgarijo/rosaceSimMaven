/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.InfoSimulador.tareasComunes;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import icaro.aplicaciones.InfoSimulador.informacion.InfoRolAgente;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
/**
 *
 * @author FGarijo
 */
public class ProcesarInfoRolAgente extends TareaSincrona {
    
    @Override
    public void ejecutar(Object... params) {
            try {
                InfoEquipo  miEquipo = (InfoEquipo)params[0];
                InfoRolAgente infoRolRecibido = (InfoRolAgente)params[1];
                miEquipo.procesarInfoRolAgente(infoRolRecibido);
                // Si el equipo es jerarquico y el Rol es de agente asignador de tareas entonces
                // se obtiene su identificador y se le pone como  jefe en el equipo
                String identAgteEmisor= infoRolRecibido.getAgteIniciadorId();
                  if (infoRolRecibido.getidentRolAgte().equals(VocabularioRosace.IdentRolAgteDistribuidorTareas))
                                                miEquipo.setidentAgenteJefeEquipo(identAgteEmisor);
                  this.getEnvioHechos().eliminarHecho(infoRolRecibido);
//                  this.getEnvioHechos().actualizarHecho(miEquipo);
                  trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se procesa el InfoRol recibido :  "+ infoRolRecibido.toString()+
                         " Enviado por el agente  " + identAgteEmisor + "\n");
               }         
            catch(Exception e) {
            }
    }
}
