/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.icaro.aplicaciones.agentes.agenteAplicacionSubordinadoConCambioRolCognitivo.tareas;

import org.icaro.aplicaciones.Rosace.informacion.*;
import org.icaro.aplicaciones.agentes.agenteAplicacionSubordinadoConCambioRolCognitivo.informacion.ExptComunicacionConJefe;
import org.icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.CausaTerminacionTarea;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

/**
 *
 * @author Francisco J Garijo
 */


//NOTA JM: esta tarea se llama en la regla "Procesar propuestas de otro agente para ir yo" 

public class ProcesarEstatusComunicacionConJefe extends TareaSincrona {

    private String nombreAgenteEmisor;
    private ItfUsoRecursoTrazas trazas;
    private InfoEstatusComunicacionConOtroAgente comunicStatusRecibido;
    private ExptComunicacionConJefe expectativaParaValidar;
    private String identDeEstaTarea ;

	@Override
	public void ejecutar(Object... params) {
            try {
                  trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
                  Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
                  comunicStatusRecibido = (InfoEstatusComunicacionConOtroAgente)params[1];
                  ExptComunicacionConJefe expectativaParaValidar = (ExptComunicacionConJefe) params[2];
                  nombreAgenteEmisor = this.getAgente().getIdentAgente();
                  identDeEstaTarea = this.getIdentTarea();
                  expectativaParaValidar.procesarInfoEstatusComunicacion(comunicStatusRecibido);
                  this.getEnvioHechos().insertarHecho(expectativaParaValidar);
                  trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se actualiza la expectativa:  "+ expectativaParaValidar + "Por procesamiento del InfoEstatusComunicacionConOtroAgente : " +comunicStatusRecibido , InfoTraza.NivelTraza.debug));
               }         
            catch(Exception e) {
                  e.printStackTrace();
            }
    }
	
}
