/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.tareasComunes;

import icaro.aplicaciones.Rosace.informacion.InfoAgteRescateVictima;
import icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Focus;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Informe;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaAsincrona;

/**
 *
 * @author Francisco J Garijo
 */
public class ProcesarInformeLlegadaDestino extends TareaAsincrona {

    private final int velocidadCruceroPordefecto = 1;// metros por segundo 
    private final int tiempoMedioRescate = 15; // minutos
    private final int gastoEnergiaMinuto = 5; //unidades de energia
    private final int costeEstimadoRescate = 0;
    private ItfUsoMovimientoCtrl itfcompMov;
    private Victim victimaRescatar;
    private String estadoComponente;

    @Override
    public void ejecutar(Object... params) {
        try {
//Suponemos que cuando llega al destino se salva la victima
            // habra que actualizar las victimas, los objetivos, el estado del movimiento  y cambiar el foco  
            // El robot esta parado, por tanto no hace falta en thread para dar la orden
            MisObjetivos misObjsAccion = (MisObjetivos) params[0];
            VictimsToRescue victims = (VictimsToRescue) params[1];
            Focus focoActual = (Focus) params[2];
            RobotStatus1 estatusRobot = (RobotStatus1) params[3];
            InfoCompMovimiento infoCompMov = (InfoCompMovimiento) params[4];
            Informe informeRecibido = (Informe) params[5];
            Objetivo objetivoConseguido = (Objetivo) params[6];
            Objetivo objetivoFocalizado = focoActual.getFoco();
            boolean actualizarNuevoObjetivoAccion = false;
            boolean actualizarEstatusRobot = false;
            itfcompMov = (ItfUsoMovimientoCtrl) infoCompMov.getitfAccesoComponente();
            String victimaRescatadaId = informeRecibido.getReferenciaContexto();
            estadoComponente = EstadoMovimientoRobot.RobotParado.name();
            estatusRobot.setestadoMovimiento(estadoComponente);
            victims.elimVictimAsignada(victimaRescatadaId);
            this.informarControladorRescateVictima(victimaRescatadaId); // informamos al agente controlador
            // Se actualizan los objetivos, se da por conseguido el objetivo salvar a la victima
            // se supone que este objetivo era el mas prioritario, si no lo era hay un problema
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, " Se Procesa el informe   recibido por el agente :" + identAgente + "\n"
                    + " Cuyo contenido:" + informeRecibido.contenidoInforme + " Se informa al Agte controlador del rescate de la victima" + victimaRescatadaId + "\n"
                    + " Elimino  la victima : " + victimaRescatadaId + " del conj de victimas asignadas. Las victimas asignadas restantes son: " + victims.getIdtsVictimsAsignadas() + "\n");    
            Thread accesoCompMovimiento = new Thread() {
                @Override
                public void run() {
                    itfcompMov.initContadorGastoEnergia();
                    itfcompMov.moverAdestino(victimaRescatar.getName(), victimaRescatar.getCoordinateVictim(), velocidadCruceroPordefecto);
                }
            };
            objetivoConseguido.setSolved();
            objetivoConseguido.setPriority(-1);
            misObjsAccion.eliminarObjetivo(objetivoConseguido);
            // Se actualiza el componente movimiento
            // se obtiene la victima mas proxima que estara en la posicion cero de las victimas asignadas
            victimaRescatar = victims.getVictimaMasProxima();
//                ArrayList victsAsig= victims.getVictimsAsignadas();
//                if(!victsAsig.isEmpty())victimaRescatar= victims.getVictimaARescatar((Integer)victsAsig.get(0));
//             trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + "\n"
//                     + " Mis victimas asignadas : " + victsAsig+ "\n");
            Objetivo nuevoObjetivoAccion = null;
            boolean robotEnProcesoDeDecision;
            String victimaImplicadaEnDecision = null;
            if (objetivoFocalizado == null || objetivoFocalizado.getState() == Objetivo.SOLVED) {
                robotEnProcesoDeDecision = false;
            } else {
                robotEnProcesoDeDecision = true;
                victimaImplicadaEnDecision = objetivoFocalizado.getobjectReferenceId();
            }
            // condicion para ejecutar una nueva victima entre las victimas asignadas
            // debe haber una victima rescatable y  debe tener asignado un robot responsable
            // si no lo tiene es que esta por asignar aunque conste como victima asignada. Esto se debe a que todavia no se ha 
            // concluido el proceso de asignacion de esa victima
//                if (victimaRescatar == null) nuevoObjetivoAccion=null;
//                else // hay victimas a rescatar y el robot no esta implicado en un proceso de decision
//                    if (victimaRescatar != null && !robotEnProcesoDeDecision) {
            if (victimaRescatar != null) {
                // quedan victimas asignadas por rescatar y el robot no esta en un proceso de decision que implique la victima
                // donde no se haya asignado todavia su coste
                String idVictimaRescatar = victimaRescatar.getName();
                if (!idVictimaRescatar.equals(victimaImplicadaEnDecision)) {
                    nuevoObjetivoAccion = new AyudarVictima(idVictimaRescatar);
                    nuevoObjetivoAccion.setPriority(victimaRescatar.getPriority());
                    trazas.aceptaNuevaTrazaEjecReglas(identAgente, " Quedan victimas asignadas y  la victima mas proxima es : " + idVictimaRescatar + "\n"
                            + " Se comprueba que se ha calculado el coste de la victima " + victimaRescatar.getisCostEstimated()
                            + " Se crea un objetivo AyudarVictima con ese identificador  :  " + idVictimaRescatar
                            + " y se ordena el salvamento . Mis victimas asignadas : " + victims.getIdtsVictimsAsignadas() + "\n");
                    nuevoObjetivoAccion.setSolving();
                    misObjsAccion.addObjetivo(nuevoObjetivoAccion);
                    estatusRobot.setidentDestino(idVictimaRescatar);
                    itfcompMov.moverAdestino(idVictimaRescatar, victimaRescatar.getCoordinateVictim(), velocidadCruceroPordefecto);
                    estadoComponente = EstadoMovimientoRobot.RobotEnMovimiento.name();
                    estatusRobot.setestadoMovimiento(estadoComponente);
                    actualizarEstatusRobot = true;
                    actualizarNuevoObjetivoAccion = true;
                }
                else {// La victima a rescatar es la misma que esta implicada en la decision
                // esperar a que termine el proceso de decision
                    actualizarNuevoObjetivoAccion = false;
                }
            }else  // La victima a rescatar es null . No hay victimas asignadas
                if (!robotEnProcesoDeDecision) {
                focoActual.setFoco(nuevoObjetivoAccion);
            } // Si esta en un proceso de decision no se cambia el foca para que continue con el proceso de decision
            misObjsAccion.setobjetivoEnCurso(nuevoObjetivoAccion);
            this.getEnvioHechos().actualizarHechoWithoutFireRules(estatusRobot);
            this.getEnvioHechos().eliminarHecho(informeRecibido);
            this.getEnvioHechos().actualizarHecho(objetivoConseguido);
            if (actualizarNuevoObjetivoAccion) {
                this.getEnvioHechos().actualizarHecho(nuevoObjetivoAccion);
            }
            if (robotEnProcesoDeDecision) {
                this.getEnvioHechos().actualizarHecho(objetivoFocalizado);
            }
            this.getEnvioHechos().actualizarHecho(focoActual);
            trazas.aceptaNuevaTrazaEjecReglas(this.identAgente, "Se Ejecuta la Tarea :" + this.identTarea + "\n"
                    + " El identificador de la victima  :" + victimaRescatadaId + " y el del ultimo objetivo : " + objetivoConseguido.getobjectReferenceId() + " coinciden " + "\n"
                    + "EstadoComponente : " + estadoComponente + " Objetivo acccion  en curso " + nuevoObjetivoAccion + "\n" + "  El foco estaba en el objetivo :  " + objetivoFocalizado + "\n");
        } catch (Exception e) {
        }

    }

    private void informarControladorRescateVictima(String idVictimaAsignada) {
        //ACTUALIZAR ESTADISTICAS
        //Inicializar y recuperar la referencia al recurso de estadisticas 
        // Supongo como prueba que la evaluacion es una constante, pero deberia obtenerse del RobotStatus
        long tiempoActual = System.currentTimeMillis();
        int gastoEnergia = itfcompMov.getContadorGastoEnergia(); // En terminos de energia consumida  para el rescate
        InfoAgteRescateVictima infoVictimaRescatada = new InfoAgteRescateVictima(this.identAgente, idVictimaAsignada, tiempoActual, gastoEnergia);
        InfoContEvtMsgAgteReactivo msg = new InfoContEvtMsgAgteReactivo("victimaRescatada", infoVictimaRescatada);
        this.getComunicator().enviarInfoAotroAgente(msg, VocabularioRosace.IdentAgteControladorSimulador);
    }

}