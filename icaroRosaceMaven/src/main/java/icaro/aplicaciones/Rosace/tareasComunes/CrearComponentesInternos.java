/*
 * GenerarInfoTerminacion.java
 *
 * Created on 22 de mayo de 2007, 11:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;

import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.agentes.componentesInternos.InfoCompInterno;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.FactoriaAbstrCompInterno;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.infraestructura.entidadesBasicas.excepciones.ExcepcionEnComponente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaAsincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FJG
 */
public class CrearComponentesInternos extends TareaAsincrona {

    @Override
    public void ejecutar(Object... params) {
        try {
            InfoCompInterno infoCompMov = FactoriaAbstrCompInterno.instance().crearComponenteInterno("CompMovimiento", itfProcObjetivos);
            ItfUsoMovimientoCtrl itfcomp = (ItfUsoMovimientoCtrl) infoCompMov.getitfAccesoComponente();
            itfcomp.inicializarInfoMovimiento(0, null, Integer.SIZE);
            this.itfProcObjetivos.insertarHecho(infoCompMov);
            this.trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "se ejecuta la tarea : " + this.identTarea + " Se crea infoCompMov");
        } catch (ExcepcionEnComponente ex) {
            Logger.getLogger(CrearComponentesInternos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
