/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.InfoSimulador.tareasComunes;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.VictimsToRescue;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot;
import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */
public class InicializarInfoWorkMemCRN1 extends TareaSincrona{
            String miIdentAgte ;
            String identEquipo;
            int velocidadCruceroPorDefecto = 4; // metros por segundo
   @Override
   public void ejecutar(Object... params) {
	   try {
             String identRolAgte = (String)params[0];
             InfoCompMovimiento  infoCompmov = (InfoCompMovimiento) params[1];
             RobotStatus1  miStatus = (RobotStatus1) params[2];
             miIdentAgte= this.getIdentAgente();
             this.getItfConfigMotorDeReglas().setDepuracionActivationRulesDebugging(false);
             this.getItfConfigMotorDeReglas().setDepuracionHechosInsertados(false);
             this.getItfConfigMotorDeReglas().setDepuracionHechosModificados(false);
             this.getItfConfigMotorDeReglas().setFactHandlesMonitoring_beforeActivationFired_DEBUGGING(false);
             this.getItfConfigMotorDeReglas().setFactHandlesMonitoringRETRACT_DEBUGGING(false);
             this.getItfConfigMotorDeReglas().setfactHandlesMonitoring_afterActivationFired_DEBUGGING(false);
             identEquipo = this.getItfUsoConfiguracion().getValorPropiedadGlobal(NombresPredefinidos.NOMBRE_PROPIEDAD_GLOBAL_IDENT_EQUIPO);
             this.getEnvioHechos().insertarHechoWithoutFireRules(new Focus());
             // Crear dos categorias de objetivos : decision y accion           
             MisObjetivos objetivosDecision= new MisObjetivos();
             objetivosDecision.setcategoria(VocabularioRosace.IdentCategoriaObjetivosDecision);
              this.getItfMotorDeReglas().addGlobalVariable(VocabularioRosace.IdentCategoriaObjetivosDecision, objetivosDecision);
             MisObjetivos objetivosAccion= new MisObjetivos();
             objetivosAccion.setcategoria(VocabularioRosace.IdentCategoriaObjetivosAccion);
             this.getItfMotorDeReglas().addGlobalVariable(VocabularioRosace.IdentCategoriaObjetivosAccion, objetivosAccion);
                VictimsToRescue victimsArescatar = new VictimsToRescue(miIdentAgte);
                this.getItfMotorDeReglas().addGlobalVariable(VocabularioRosace.IdentVictimsArescatar, victimsArescatar);     
                if (  miStatus != null){
                    miStatus.setIdRobotRol(identRolAgte);
                    ItfUsoMovimientoCtrl itfCompMov = (ItfUsoMovimientoCtrl) infoCompmov.getitfAccesoComponente();
                    miStatus.setItfCompMovimiento(itfCompMov);
                    miStatus.setestadoMovimiento(EstadoMovimientoRobot.RobotParado.name());
                    itfCompMov.inicializarInfoMovimiento(miStatus.getAvailableEnergy(),miStatus.getRobotCoordinate(), velocidadCruceroPorDefecto);
                    InfoEquipo miEquipo = new InfoEquipo(miIdentAgte, identEquipo);
                    miEquipo.setTeamMemberStatus( miStatus); 
                    this.getEnvioHechos().insertarHecho(miStatus);
                    this.getItfMotorDeReglas().addGlobalVariable(VocabularioRosace.IdentObjGlobalMiEquipo, miEquipo);
                    this.trazas.aceptaNuevaTrazaEjecReglas(miIdentAgte, this.getIdentTarea()+ "  Actualizo mi estatus. Mi Rol en equipo :  " + miStatus.getIdRobotRol());
                    }
                   else this.trazas.trazar(miIdentAgte, "No se ha encontrado el fichero de inicializacion de Estatus", InfoTraza.NivelTraza.error);
                      
       } catch (Exception e) {
       }                
   }
     
}
