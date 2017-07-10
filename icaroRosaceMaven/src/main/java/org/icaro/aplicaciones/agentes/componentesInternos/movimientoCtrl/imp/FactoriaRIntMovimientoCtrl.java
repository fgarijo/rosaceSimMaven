/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp;

import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.FactoriaAbstrCompInterno;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import org.icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import org.icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.ItfUsoRecursoVisualizadorEntornosSimulacion;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import org.icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.factoriaEInterfacesPrObj.ItfProcesadorObjetivos;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import org.icaro.infraestructura.recursosOrganizacion.repositorioInterfaces.ItfUsoRepositorioInterfaces;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FGarijo
 */
public class FactoriaRIntMovimientoCtrl extends FactoriaAbstrCompInterno{
    private ItfUsoRecursoTrazas trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
    private ItfUsoRecursoVisualizadorEntornosSimulacion itfUsoRecVisEntornosSimul;
    public FactoriaRIntMovimientoCtrl(){
        
    }
   
    @Override
    public InfoCompMovimiento crearComponenteInterno(String identComponenteInterno, ItfProcesadorObjetivos procObj) {
        // se crea un componente movimiento en estado parado
       
        String identComponenteAcrear = procObj.getAgentId()+identComponenteInterno;
        MaquinaEstadoMovimientoCtrl maquinaEstados = new MaquinaEstadoMovimientoCtrl();
        maquinaEstados.SetComponentId(identComponenteAcrear);
        maquinaEstados.SetInfoContexto(procObj);
        try {
         ItfUsoRepositorioInterfaces repoItfs = NombresPredefinidos.REPOSITORIO_INTERFACES_OBJ;
         itfUsoRecVisEntornosSimul = (ItfUsoRecursoVisualizadorEntornosSimulacion)repoItfs.obtenerInterfazUso(VocabularioRosace.IdentRecursoVisualizadorEntornosSimulacion);
         maquinaEstados.SetItfUsoRecursoVisualizadorEntornosSimulacion(itfUsoRecVisEntornosSimul);
         maquinaEstados.cambiarEstado(MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot.RobotParado);
        } catch (Exception ex) {
            Logger.getLogger(FactoriaRIntMovimientoCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
//        ItfUsoMovimientoCtrl itfMov =(ItfUsoMovimientoCtrl) maquinaEstados.cambiarEstado(MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot.RobotParado);
        ItfUsoMovimientoCtrl itfMov =(ItfUsoMovimientoCtrl) maquinaEstados;
        InfoCompMovimiento infoCompCreado = new InfoCompMovimiento (identComponenteAcrear);
        infoCompCreado.setitfAccesoComponente(itfMov);
        return infoCompCreado;
 
    }
    
    @Override
     public  boolean verificarExisteInterfazUsoComponente(String identClaseQueImplementaInterfaz){
         return true;
     }
     
}
