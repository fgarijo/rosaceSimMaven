/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.Rosace.tareasComunes;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.InfoRolAgente;
import org.icaro.aplicaciones.Rosace.informacion.OrdenCentroControl;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Informe;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 *
 * @author FGarijo
 */
public class ProcesarOrdenFinCasoSimulacion extends TareaSincrona {
    
    @Override
    public void ejecutar(Object... params) {
            try {
             InfoEquipo miEquipo = (InfoEquipo) params[0];
             InfoCompMovimiento infoCompMov = (InfoCompMovimiento) params[1];
//             trazas.aceptaNuevaTraza(new InfoTraza(this.identAgente, "Se Ejecuta la Tarea :"+ this.identTarea , InfoTraza.NivelTraza.info));
             trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Procesa la orden  Fin de simulacion   recibida por el agente  "+"\n");
            // se inicializa objetivos, foco y victimas, y se vacia la memoria de trabajo y se me
             this.getItfMotorDeReglas().reinicializarSesion();
//             misObjs.inicializar();focoActual.inicializar();victims.inicializar();
//             this.getItfMotorDeReglas().assertFactWithoutFireRules(misObjs);
//             this.getItfMotorDeReglas().assertFactWithoutFireRules(focoActual);
//             this.getItfMotorDeReglas().assertFactWithoutFireRules(victims);
//             this.getEnvioHechos().actualizarHechoWithoutFireRules(miEquipo);
             this.getEnvioHechos().actualizarHecho(infoCompMov);
               }         
            catch(Exception e) {
                  e.printStackTrace();
            }
    }
}
