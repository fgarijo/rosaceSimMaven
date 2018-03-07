/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.Rosace.tareasComunes;
import org.icaro.aplicaciones.Rosace.informacion.InfoEquipo;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Tarea;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class InicializarInfoWorkMemAlum extends TareaSincrona{
   @Override
   public void ejecutar(Object... params) {
	   try {
             String identRolAgte = (String)params[0];
             this.getItfConfigMotorDeReglas().setDepuracionActivationRulesDebugging(false);
             this.getItfConfigMotorDeReglas().setDepuracionHechosInsertados(false);
             this.getItfConfigMotorDeReglas().setDepuracionHechosModificados(false);
             this.getItfConfigMotorDeReglas().setFactHandlesMonitoring_beforeActivationFired_DEBUGGING(false);
             this.getItfConfigMotorDeReglas().setFactHandlesMonitoringRETRACT_DEBUGGING(false);
             this.getItfConfigMotorDeReglas().setfactHandlesMonitoring_afterActivationFired_DEBUGGING(false);
            String identEquipo = this.getItfUsoConfiguracion().getValorPropiedadGlobal(NombresPredefinidos.NOMBRE_PROPIEDAD_GLOBAL_IDENT_EQUIPO);
             Focus miFoco= new Focus();
             MisObjetivos misObjs= new MisObjetivos();
             this.getEnvioHechos().insertarHechoWithoutFireRules(miFoco);
             this.getEnvioHechos().insertarHechoWithoutFireRules(misObjs);
             RobotStatus1 miStatus = new RobotStatus1() ;
                    miStatus.setIdRobot(this.identAgente);
                    miStatus.setIdRobotRol(identRolAgte);
                    miStatus.setestadoMovimiento(EstadoMovimientoRobot.RobotParado.name());
             InfoEquipo miEquipo = new InfoEquipo(identAgente, identEquipo);
                    miEquipo.setTeamMemberStatus( miStatus); 
                    this.getEnvioHechos().insertarHechoWithoutFireRules(miStatus);
                    this.getEnvioHechos().insertarHecho(miEquipo);
                    this.trazas.aceptaNuevaTrazaEjecReglas(identAgente, this.getIdentTarea()+ "  Actualizo mi estatus. Mi Rol en equipo :  " + miStatus.getIdRobotRol());
       } catch (Exception e) {
	e.printStackTrace();
       }                
   }
     
}
