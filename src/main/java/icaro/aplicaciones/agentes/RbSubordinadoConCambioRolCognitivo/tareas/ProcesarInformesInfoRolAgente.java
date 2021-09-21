/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.RbSubordinadoConCambioRolCognitivo.tareas;

import icaro.aplicaciones.InfoSimulador.informacion.InfoRolAgente;
import icaro.aplicaciones.agentes.RbSubordinadoConCambioRolCognitivo.informacion.InfoCambioRolAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */


//NOTA JM: esta tarea se llama en la regla "Procesar propuestas de otro agente para ir yo" 

public class ProcesarInformesInfoRolAgente extends TareaSincrona {

    private String nombreAgenteEmisor;
    private String identDeEstaTarea ;

	@Override
	public void ejecutar(Object... params) {
            try {
                  InfoCambioRolAgente mInfocambioRol = (InfoCambioRolAgente)params[0];
                  InfoRolAgente estusRolrecibido = (InfoRolAgente)params[1];
                  nombreAgenteEmisor = this.getAgente().getIdentAgente();
                  identDeEstaTarea = this.getIdentTarea();
                  mInfocambioRol.procesarInfoCambioRolAgte(estusRolrecibido);
                  mInfocambioRol.verificarCondicionesCambioRolEquipo();
                  this.getEnvioHechos().eliminarHechoWithoutFireRules(estusRolrecibido);
                  this.getEnvioHechos().actualizarHecho(mInfocambioRol);
                  trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se procesa el informe de Rol:  "+ estusRolrecibido, InfoTraza.NivelTraza.debug));
               }         
            catch(Exception e) {
                  e.printStackTrace();
            }
    }
	
}
