package icaro.aplicaciones.agentes.ControladorSimuladorReactivo.comportamiento;

//import icaro.infraestructura.entidadesBasicas.EventoRecAgte;
import icaro.aplicaciones.InfoSimulador.informacion.VictimasSalvadas;
import icaro.aplicaciones.InfoSimulador.informacion.InfoAgteVictimaRescatada;
import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.InfoCasoSimulacion;
import icaro.aplicaciones.InfoSimulador.informacion.InfoAgteAsignacionVictima;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import icaro.aplicaciones.InfoSimulador.informacion.OrdenFinCasoSimulacion;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import icaro.aplicaciones.InfoSimulador.informacion.InfoPeticionSimulacion;
import icaro.aplicaciones.InfoSimulador.informacion.OrdenParada;
import icaro.aplicaciones.InfoSimulador.informacion.InfoRescateVictima;
import icaro.aplicaciones.InfoSimulador.informacion.OrdenCentroControl;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEstadoAgente;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.GestionCasosSimulacion;
import icaro.aplicaciones.InfoSimulador.utils.ConstantesRutasEstadisticas;
import icaro.aplicaciones.recursos.recursoPersistenciaEntornosSimulacion.ItfUsoRecursoPersistenciaEntornosSimulacion;
import icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.ItfUsoRecursoVisualizadorEntornosSimulacion;
import icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp.EscenarioSimulacionRobtsVictms;
import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.patronAgenteReactivo.control.acciones.AccionesSemanticasAgenteReactivo;
import icaro.infraestructura.recursosOrganizacion.configuracion.ItfUsoConfiguracion;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza.NivelTraza;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;

public class AccionesAgteControladorSimulador extends AccionesSemanticasAgenteReactivo {

    private int numMensajesEnviar; // numero total de nodos que hay en nodeLst
    private ItfUsoRecursoVisualizadorEntornosSimulacion itfUsoRecursoVisualizadorEntornosSimulacion;   //Para visualizar graficas de estadisticas
    private InfoEquipo equipo;
    private String identificadorEquipo;
    private String modeloOrganizativo;
    private ArrayList identsAgtesEquipo;
    private boolean peticionParar = false;
    private ItfUsoConfiguracion itfconfig;
    private long tiempoInicialDeLaSimulacion = 0;      //Variable para obtener el tiempo al iniciar la simulacion
    private int numeroVictimasEntorno = 0;            //Numero de victimas actuales que se han introducido en el entorno    
    private int numeroVictimasAsignadas = 0;          //Numero de victimas asignadas a robots
    private int numeroVictimasDiferentesSimulacion; //Numero de victimas diferentes que van a intervenir en el proceso de simulacion
    private int numeroRobotsSimulacion = 0;         //Numero de robots diferentes que van a intervenir en el proceso de simulacion
    private int intervaloSecuencia = 5000;                 //Intervalo uniforme de envio de la secuencia de victimas
    private ArrayList<Victim> victimasDefinidas;     //Victimas definidas en la simulacion 
    private Map<String, Victim> victims2Rescue;      //Victimas que han sido enviadas al equipo    //Victimas que han sido asignadas a algun robot, es decir, algun robot se ha hecho responsable para salvarla
    private Map<String, InfoAgteAsignacionVictima> infoVictimasAsignadas;
    private ItfUsoRecursoPersistenciaEntornosSimulacion itfUsoRecursoPersistenciaEntornosSimulacion;
//    private InfoAsignacionVictima infoAsigVictima;
    private InfoCasoSimulacion infoCasoSimul;
    private int contadorRobotsQueContestanFinsimulacion = 0;
    final int nMM = this.numMensajesEnviar; // numeroMaximoDeMensajes a  enviar										
    private GestionCasosSimulacion gestionCasosSimulacion;
    private boolean peticionTerminacionSimulacionUsuario = false;
    private boolean robotEstatusEquipoInicializado = false;
    private EscenarioSimulacionRobtsVictms escenarioActual;
    private String identFicheroEscenario;
    private int ordenesEnviadas;
    private String modalidadSimulacion;
    private boolean inicializarSerieCasos;
    private int intervaloEnvioPeticiones;
    private InfoPeticionSimulacion infoUltimaPeticionSimulacion;
    private boolean primeraSimulacion = true;
    private boolean primeraVictimaEnviada = false;

    public void accionComenzar() {
        //Inicializar las interfaces a los recursos que se van a utilizar
        //----------------------------------------------------------------------------------------------------------------
        // INICIALIZACION DE VARIABLES RELACIONADAS CON LAS VICTIMAS
        //----------------------------------------------------------------------------------------------------------------			
//----------------------------------------------------------------------------------------------------------------
        // Inicializar variables para la comunicacion, el identificadorEquipo,
        // los identificadores de los agentes del equipo
        //----------------------------------------------------------------------------------------------------------------	
        try {
            itfconfig = (ItfUsoConfiguracion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.NOMBRE_ITF_USO_CONFIGURACION);
            itfUsoRecursoPersistenciaEntornosSimulacion = (ItfUsoRecursoPersistenciaEntornosSimulacion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_USO + "RecursoPersistenciaEntornosSimulacion1");
            itfUsoRecursoVisualizadorEntornosSimulacion = (ItfUsoRecursoVisualizadorEntornosSimulacion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_USO + "RecursoVisualizadorEntornosSimulacion1");
            itfUsoRecursoVisualizadorEntornosSimulacion.setIdentAgenteAReportar(this.nombreAgente);
            itfUsoRecursoVisualizadorEntornosSimulacion.setItfUsoPersistenciaSimulador(itfUsoRecursoPersistenciaEntornosSimulacion);
            identFicheroEscenario = itfconfig.getValorPropiedadGlobal(VocabularioRosace.NombreFicheroEscenarioSimulacion);
            identificadorEquipo = itfconfig.getValorPropiedadGlobal(VocabularioRosace.NombrePropiedadGlobalIdentEquipo);
            modeloOrganizativo = itfconfig.getValorPropiedadGlobal(VocabularioRosace.NombrePropiedadGlobalModeloOrganizacion);
            equipo = new InfoEquipo(this.nombreAgente, identificadorEquipo);
            identsAgtesEquipo = equipo.getTeamMemberIDs(); // Se obtienen de la configuracion
            comunicator = this.getComunicator();
            gestionCasosSimulacion = new GestionCasosSimulacion();
            numeroRobotsSimulacion = identsAgtesEquipo.size();
            this.itfUsoRecursoVisualizadorEntornosSimulacion.obtenerEscenarioSimulacion(identFicheroEscenario, modeloOrganizativo, identsAgtesEquipo.size());

        } catch (Exception e) {
            this.informaraMiAutomata("errorEnAccionComenzar");
        }
        trazas.trazar(this.nombreAgente, "Accion AccionComenzar completada ....", NivelTraza.debug);
    }

    public void obtenerEscenarioDefinidoEnDescrOrganizacion() {
        // Se intenta primero obtener el escenario cuyo identificador esta definido en la organizacion
        // Si no se obtiene nada se le pide a la interfaz de usuario
        try {
            if (identFicheroEscenario != null) {
                escenarioActual = itfUsoRecursoPersistenciaEntornosSimulacion.obtenerInfoEscenarioSimulacion(identFicheroEscenario);
            }
            if (escenarioActual != null) {
//                escenarioActual.renombrarIdentRobts(identsAgtesEquipo);
                this.actualizarInfoVisorSimulador(escenarioActual);
                this.informaraMiAutomata("escenarioDefinidoValido");
            } else {
               
                modeloOrganizativo = itfconfig.getValorPropiedadGlobal(VocabularioRosace.NOMBRE_PROPIEDAD_GLOBAL_MODELO_ORGANIZATIVO);
                this.numeroRobotsSimulacion = identsAgtesEquipo.size();
                 itfUsoRecursoVisualizadorEntornosSimulacion.notificarRecomendacion("Fichero de Escenario Nulo", " Error en la interpretación del fichero de simulación", " Selecione otro fichero "
                         + "con Modelo Organizativo : " + modeloOrganizativo + " y   Numero de robots : " + numeroRobotsSimulacion + " o cree unfichero nuevo con el mismo modelo organizativo y mismo numero de robots");
                itfUsoRecursoVisualizadorEntornosSimulacion.obtenerEscenarioSimulacion(modeloOrganizativo, numeroRobotsSimulacion);
            }
        } catch (Exception e) {
        }
        trazas.trazar(this.nombreAgente, "Accion obtenerEscenarioDefinidoEnDescrOrganizacion completada ....", NivelTraza.debug);
    }

    public void validarPeticionSimulacion(InfoPeticionSimulacion infoPeticion) {
        infoUltimaPeticionSimulacion = infoPeticion;
        try {
            EscenarioSimulacionRobtsVictms escenario = infoPeticion.getEscenarioSimulacion();
            if (escenario != null) {
                // se comprueba si es un nuevo escenario o si se trata del ultimo escenario utilizado
                if (escenarioActual.getIdentEscenario().equalsIgnoreCase(escenario.getIdentEscenario())) {
                    this.informaraMiAutomata("escenarioyTipoSimulacionValidos");
                } else // escenario diferente se valida que el numero de robots coincida
                if (this.numeroRobotsSimulacion != escenario.getNumRobots()) {
                    String causaError = "Numero Robots no coincide";
                    this.informaraMiAutomata("escenarioDefinidoNoValido", causaError);
                } else {
                    escenarioActual = escenario;
                    escenarioActual.setIdentEquipo(identificadorEquipo);
                    escenarioActual.renombrarIdentRobts(identsAgtesEquipo);
                    this.inicializarSerieCasos = true;
                    this.informaraMiAutomata("escenarioyTipoSimulacionValidos");
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        trazas.trazar(this.nombreAgente, " Ejecuto a ccion : validarPeticionSimulacion", NivelTraza.debug);
    }

    public void ValidarEscenarioRecibido(EscenarioSimulacionRobtsVictms escenario) {
        try {
            if (escenario != null) {
                victims2Rescue = escenario.getVictims();
                numeroVictimasDiferentesSimulacion = victims2Rescue.size();
                if (this.numeroRobotsSimulacion == escenario.getNumRobots()
                        && this.modeloOrganizativo.equalsIgnoreCase(escenario.getmodeloOrganizativo())) {
                    escenarioActual = escenario;
                    escenarioActual.setIdentEquipo(identificadorEquipo);
                    escenarioActual.renombrarIdentRobts(identsAgtesEquipo);
                    this.informaraMiAutomata("escenarioDefinidoValido", escenarioActual);
                } else {
                    this.itfUsoRecursoVisualizadorEntornosSimulacion.notificarRecomendacion(" ", "El numero de robots definido en la organizacion y el numero de robots"
                            + " en el escenario No coinciden", "Utilizar el editor para seleccionar un escenario");
                    this.itfUsoRecursoVisualizadorEntornosSimulacion.obtenerEscenarioSimulacion(modeloOrganizativo, numeroRobotsSimulacion);
                }
            } else {
                this.itfUsoRecursoVisualizadorEntornosSimulacion.notificarRecomendacion("Escenario de Simulacion nulo ", "No se ha definido el escenario de simulacion ", "Utilizar el editor para seleccionar un escenario");
                this.itfUsoRecursoVisualizadorEntornosSimulacion.obtenerEscenarioSimulacion(modeloOrganizativo, numeroRobotsSimulacion);
                this.informaraMiAutomata("escenarioDefinidoNoValido");
            }

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void actualizarInfoVisorSimulador(EscenarioSimulacionRobtsVictms escenarioDefinidoUsuario) {
        // suponemos que ya existe un escenario valido
        try {
            escenarioActual = escenarioDefinidoUsuario;
            escenarioActual.setIdentEquipo(identificadorEquipo); 
            escenarioActual.renombrarIdentRobts(identsAgtesEquipo);
            itfUsoRecursoVisualizadorEntornosSimulacion.notificarRecomendacion("Renombrado de Robots", "Se han renombrado los robots del escenario de acuerdo con la descripcion de la  organizacion", "");
            itfUsoRecursoVisualizadorEntornosSimulacion.mostrarEscenarioMovimiento(escenarioActual);
            victims2Rescue = escenarioActual.getVictims();
            this.numeroRobotsSimulacion = escenarioActual.getNumRobots();
            numeroVictimasDiferentesSimulacion = victims2Rescue.size();
            modeloOrganizativo = escenarioActual.getmodeloOrganizativo();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void ejecutarPetionSimulacionValida() {
        modalidadSimulacion = infoUltimaPeticionSimulacion.getModalidadSimulacion();
        try {
            if (!primeraVictimaEnviada) {
                victims2Rescue = escenarioActual.getVictims();
                numeroVictimasDiferentesSimulacion = victims2Rescue.size();
                itfUsoRecursoVisualizadorEntornosSimulacion.mostrarEscenarioMovimiento(escenarioActual);
                this.inicializarEstatusRobotsEquipo();
                this.inicializarInfoCasoSimulacion();
            }
            if (this.modalidadSimulacion.equals(VocabularioRosace.modoSimulacionSecuencia)) {
                this.intervaloEnvioPeticiones = infoUltimaPeticionSimulacion.getInterevaloSecuencia();
                this.sendSequenceOfSimulatedVictimsToRobotTeam(this.intervaloEnvioPeticiones);
            }
            if (this.modalidadSimulacion.equals(VocabularioRosace.modoSimulacionPeticionesUsuario)) {
                String identVictima = infoUltimaPeticionSimulacion.getIdVictima();
                sendSimulatedVictimToRobotTeam(identVictima);
                primeraVictimaEnviada = true;
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //Esta accion semantica se ejecuta cuando se envia el input "sendSequenceOfSimulatedVictimsToRobotTeam" en el 
    //metodo sendSequenceOfSimulatedVictimsToRobotTeam de la clase NotificacionEventosRecursoGUI3	
    public void sendSequenceOfSimulatedVictimsToRobotTeam(Integer intervaloSec) {
        this.intervaloSecuencia = intervaloSec;
        trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente, "Accion SendSequenceOfSimulatedVictimsToRobotTeam  ...."
                + intervaloSecuencia, InfoTraza.NivelTraza.debug));
        //--------------------------------------------------------------------------------------------------------------------
        // Inicializar y recuperar la referencia al recurso de estadisticas y el visualizador de estadisticas
        // Inicializar el numero de victimas diferentes que hay en la simulacion en el recurso PersistenciaEntornosSimulacion
        //--------------------------------------------------------------------------------------------------------------------
        long tiempoActual = 0;
        try {
            infoVictimasAsignadas = new HashMap<String, InfoAgteAsignacionVictima>();
            this.numeroRobotsSimulacion = this.escenarioActual.getNumRobots();
            trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente,
                    "Accion SendSequenceOfSimulatedVictimsToRobotTeam  .... "
                    + "Simulacion de tipo " + identificadorEquipo + " ; "
                    + numeroRobotsSimulacion + " robots en la simulacion ; "
                    + numeroVictimasDiferentesSimulacion + " Victimas Diferentes Simulacion "
                    + " (numero Mensajes a Enviar " + this.numMensajesEnviar + ") "
                    + " ; frecuencia de envio " + intervaloSecuencia + " milisegundos", InfoTraza.NivelTraza.debug));

        } catch (Exception e) {
        }

        //----------------------------------------------------------------------------------------------------------------	
        // Crear hilo responsable de realizar el envio de la secuencia de victimas a intervalos regulares de tiempo
        //----------------------------------------------------------------------------------------------------------------	
        Thread t = new Thread() {
            @Override
            public void run() {
                int i = 0;
                Victim victima;
                Object[] victimas = victims2Rescue.values().toArray();
                while ((i < numeroVictimasDiferentesSimulacion) && (peticionParar == false)) {
                    victima = (Victim) victimas[i];
                    OrdenCentroControl ccOrder = new OrdenCentroControl("ControlCenter", VocabularioRosace.MsgOrdenCCAyudarVictima, victima);
                    // Escribir nueva linea de estadistica en el fichero de llegada de victimas
                    String victimId = victima.getName();
                    InfoRescateVictima infoAsigVictima = new InfoRescateVictima(victimId);
                    try {
                        Victim valor = victims2Rescue.put(victimId, victima);
                        if (valor == null) //no estaba insertado
                        {
                            incrementarNumeroVictimasActuales();
                        }
                        infoAsigVictima.setTiempoPeticion(System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    comunicator.informaraGrupoAgentes(ccOrder, identsAgtesEquipo);
//                    }
                    infoCasoSimul.addInfoAsignacionVictima(infoAsigVictima);
                    i++;
                    try {
                        this.sleep(intervaloSecuencia);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }// fin del while. Se calcula el tiempo medio de envio de victimas
                int tiempoMedioEnvio = (int) (System.currentTimeMillis() - tiempoInicialDeLaSimulacion) / numeroVictimasDiferentesSimulacion;
                infoCasoSimul.setTiempoMedioEnvioPeticiones(tiempoMedioEnvio);
                // Se han enviado todas las victimas
                // Cerrar el fichero de estadistica en el fichero de llegada de victimas

            }
        };
        t.start();
    }

    public void ejecutarPeticionSimulacionVictima(InfoPeticionSimulacion infoPeticion) {
        try {
            String idVictima = infoPeticion.getIdVictima();
            if (infoCasoSimul.victimaNotificada(idVictima)) {
                this.itfUsoRecursoVisualizadorEntornosSimulacion.notificarRecomendacion("Aviso", "Notificacion de Victima ya enviada", "La notificacion sera ignorada");
            } else {
                sendSimulatedVictimToRobotTeam(idVictima);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void sendSimulatedVictimToRobotTeam(String idVictima) {
        trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente, "Accion SendVictimToRobotTeam  .... "
                + idVictima, InfoTraza.NivelTraza.debug));
        Victim victima = victims2Rescue.get(idVictima);
        OrdenCentroControl ccOrder = new OrdenCentroControl("ControlCenter", VocabularioRosace.MsgOrdenCCAyudarVictima, victima);
        comunicator.informaraGrupoAgentes(ccOrder, identsAgtesEquipo);
        InfoRescateVictima infoRescVictima = new InfoRescateVictima(idVictima);
        infoRescVictima.setTiempoPeticion(System.currentTimeMillis());
        infoCasoSimul.addInfoAsignacionVictima(infoRescVictima);
    }
    //Esta accion semantica se ejecuta cuando se envia el input "victimaAsignadaARobot" en la  
    //tarea sincrona GeneraryEncolarObjetivoActualizarFoco del agente Subordinado
    //Esta accion semantica se ejecuta cuando se envia el input "victimaAsignadaARobot" en la  
    //tarea sincrona EncolarObjetivoActualizarFoco del agente Igualitario (robotMasterIA)	
//    public void VictimaAsignadaARobot(Long tiempoReportado, String refVictima, String nombreAgenteEmisor, Integer miEvaluacion) {

    private void inicializarInfoCasoSimulacion() {

        victims2Rescue = escenarioActual.getVictims();
        tiempoInicialDeLaSimulacion = System.currentTimeMillis();
        infoCasoSimul = gestionCasosSimulacion.crearCasoSimulacion(identFicheroEscenario);
        infoCasoSimul.setInfoCasoSimulacion(identificadorEquipo, escenarioActual.getmodeloOrganizativo(), escenarioActual.getNumRobots(), escenarioActual.getNumVictimas());
        infoCasoSimul.setTiempoInicioEnvioPeticiones(tiempoInicialDeLaSimulacion);
        trazas.aceptaNuevaTraza(new InfoTraza("OrdenAsignacion",
                "\n" + " Escenario : " + escenarioActual.getIdentEscenario() + "\n", InfoTraza.NivelTraza.debug));
    }

    public void victimaAsignadaARobot(InfoRescateVictima infoAsignacion) {
        try {
            long tiempoReportado = (long) infoAsignacion.getTiempoAsignacion();
            String refVictima = infoAsignacion.getvictimaId();
            String nombreAgenteEmisor = infoAsignacion.getRobotRescatadorId();
            int miEvaluacion = (int) infoAsignacion.getcosteRescate();
            trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente,
                    "Accion VictimaAsignadaARobot  ... " + "tiempoActual->" + tiempoReportado + " ; refVictima->"
                    + refVictima + " ; nombreAgenteEmisor->" + nombreAgenteEmisor + " ; miEvaluacion->" + miEvaluacion, InfoTraza.NivelTraza.debug));
            itfUsoRecursoVisualizadorEntornosSimulacion.mostrarVictima(refVictima, VocabularioRosace.EstadoVictima.asignada.name());
            if (infoCasoSimul != null) {
                infoCasoSimul.addInfoAsignacionVictima(infoAsignacion);
            }
        } catch (Exception ex) {
            Logger.getLogger(AccionesAgteControladorSimulador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pararRobot(String robtId) {
        trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente,
                "Se manda una orden de parada al agente ->" + robtId,
                InfoTraza.NivelTraza.info));
        comunicator.enviarInfoAotroAgente(new OrdenParada(this.getNombreAgente(), null), robtId);
    }

    public void procesarInfoEstadoAgente(InfoEstadoAgente infoEstado) {
        String idAgteInformate = infoEstado.getidentAgte();
        trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente,
                "Se recibe informacion del estado del agente ->" + idAgteInformate,
                InfoTraza.NivelTraza.info));
        if (infoEstado.getidentEstado().equals(VocabularioRosace.EstadoMovimientoRobot.RobotBloqueado.name())) {
            Set identsVictimasAsignadas = infoCasoSimul.getvictimasAsignadas(idAgteInformate);
            if (identsVictimasAsignadas != null) {
                actualizarInfoVictimasAsignadas(identsVictimasAsignadas);
            }

        }
    }

    private void actualizarInfoVictimasAsignadas(Set conIdsVictimas) {
        Iterator iter = conIdsVictimas.iterator();
        int i = 0;
        String idVictima;
        try {
            while (iter.hasNext()) {
                idVictima = (String) iter.next();
                if (infoCasoSimul.getInfoRescateVictima(idVictima).getTiempoRescate() == 0) {
                    // se cambia el color y se elimina de las asignaciones del agente
                    itfUsoRecursoVisualizadorEntornosSimulacion.mostrarVictima(idVictima, VocabularioRosace.EstadoVictima.reAsignada.name());
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void notificarFinCasoSimulacion() {
        try {
            trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente,
                    "Se notifica el fin de la simulacion a los agentes del Equipo:identsAgtesEquipo->" + identsAgtesEquipo,
                    InfoTraza.NivelTraza.info));
            itfUsoRecursoVisualizadorEntornosSimulacion.finCasoSimulacion();
            this.primeraVictimaEnviada = false;
            comunicator.informaraGrupoAgentes(new OrdenFinCasoSimulacion(this.getNombreAgente(), null), identsAgtesEquipo);
        } catch (Exception e) {
        }
    }

    public void guardarCasoYconfirmarVisualizacionResultados() {
        try {
            itfUsoRecursoPersistenciaEntornosSimulacion.guardarInfoCasoSimulacion(this.infoCasoSimul);
            if (itfUsoRecursoVisualizadorEntornosSimulacion.peticionConfirmacionInformacion("Desea visualizar los resultados obtenidos en esta simulacion ?")) {
                itfUsoRecursoVisualizadorEntornosSimulacion.visualizarTiemposRescatePorRobot(infoCasoSimul);
                itfUsoRecursoVisualizadorEntornosSimulacion.visualizarLlegadaYasignacionVictimas(infoCasoSimul);
                itfUsoRecursoVisualizadorEntornosSimulacion.visualizarCosteEnergiaRescateVicitimas(infoCasoSimul);
            }
        } catch (Exception e1) {
        }
        this.informaraMiAutomata(VocabularioRosace.informacionResultadosGuardados);
    }

    public void visualizarYguardarResultadosCaso() {
        try {
            itfUsoRecursoVisualizadorEntornosSimulacion.visualizarTiemposRescatePorRobot(infoCasoSimul);
            itfUsoRecursoVisualizadorEntornosSimulacion.visualizarLlegadaYasignacionVictimas(infoCasoSimul);
            itfUsoRecursoVisualizadorEntornosSimulacion.visualizarCosteEnergiaRescateVicitimas(infoCasoSimul);
            itfUsoRecursoPersistenciaEntornosSimulacion.guardarInfoCasoSimulacion(this.infoCasoSimul);
        } catch (Exception e1) {
        }
    }

    public void procesarInfoVictimaAsignada(InfoAgteAsignacionVictima infoAsigVict) {
        // el robot que se ha quedado con la victima informa sobre los detalles de la asignacion
        // este agente incorpora el contexto de asigancion de la victima
        String idVictima = infoAsigVict.getVictimId();
        trazas.trazar(this.nombreAgente,
                "Info Asignacion Victima  ... " + "tiempoActual->" + infoAsigVict.getTiempoAsignacion() + " ; refVictima->"
                + idVictima + " ; nombreAgenteEmisor->" + infoAsigVict.getidAgteInformante() + " ; miEvaluacion->" + infoAsigVict.getEvaluacion(), InfoTraza.NivelTraza.debug);
        InfoRescateVictima infoAsigVictima = infoCasoSimul.getInfoRescateVictima(idVictima);
        infoAsigVictima.setcosteEstimadoEnAsignacion(infoAsigVict.getEvaluacion());
        infoAsigVictima.setRobotRescatadorId(infoAsigVict.getidAgteInformante());
        infoAsigVictima.setTiempoAsignacion(infoAsigVict.getTiempoAsignacion());
        try {
            itfUsoRecursoVisualizadorEntornosSimulacion.mostrarVictima(idVictima, VocabularioRosace.EstadoVictima.asignada.name());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        infoCasoSimul.addInfoAsignacionVictima(infoAsigVictima);
    }

    public void procesarInfoVictimaRescatada(InfoAgteVictimaRescatada infoRstVict) {
        // el robot que se ha quedado con la victima informa sobre los detalles de la asingnacion
        // este agente incorpora el contexto de asigancion de la victima
        String idVictima = infoRstVict.getVictimId();
        trazas.trazar(this.nombreAgente,
                "Info Salvacion Victima  ... " + "tiempoRescate->" + infoRstVict.getTiempoRescate() + " ; refVictima->"
                + idVictima + " ; nombreAgenteEmisor->" + infoRstVict.getRobotId() + " ; Energia Consumida en rescate->" + infoRstVict.getCosteRescate(), InfoTraza.NivelTraza.debug);
        InfoRescateVictima infoRescVictima = infoCasoSimul.getInfoRescateVictima(idVictima);
        infoRescVictima.setcosteRescate(infoRstVict.getCosteRescate()); // en terminos de energia consumida
        infoRescVictima.setRobotRescatadorId(infoRstVict.getRobotId());
        infoRescVictima.setTiempoRescate(infoRstVict.getTiempoRescate());
        infoCasoSimul.addInfoRescateVictima(infoRescVictima);
        try {
            itfUsoRecursoVisualizadorEntornosSimulacion.mostrarVictima(idVictima, VocabularioRosace.EstadoVictima.rescatada.name());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        if (infoCasoSimul.getnumeroVictimasEntorno() == infoCasoSimul.getNumeroVictimasRescatadas()) {
            trazarInfoRescateVictimas();

            this.informaraMiAutomata(VocabularioRosace.informacionFinSimulacion);
        }

    }

    //Esta accion semantica se ejecuta cuando se envia el input "finSimulacion" en la  
    //tarea sincrona FinalizarSimulacion del agente Subordinado y el igualitario
    //Nos permite generar un fichero EstadisticaFinalSimulacionAsignacionMisionV2.xml que resume que victimas han sido asignadas a cada robot.
    public void FinSimulacion(String robot, ArrayList idsVictimasFinalesAsignadas, Double tiempoTotalCompletarMisionAtenderVictimasFinalesAsignadas) {
// Definir las series a visualizar y los datos resumen del escenario
//        Visluarizar los graficos
        this.visualizarYguardarResultadosCaso();
        trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente, "Accion FinSimulacion  .... "
                + "robot->" + robot + " ; idsVictimasFinalesAsignadas->" + idsVictimasFinalesAsignadas
                + " ; tiempoTotalCompletarMisionAtenderVictimasFinalesAsignadas->" + tiempoTotalCompletarMisionAtenderVictimasFinalesAsignadas, InfoTraza.NivelTraza.debug));
        try {
            ArrayList<InfoAgteAsignacionVictima> infoAsignVictms = new ArrayList();
            infoAsignVictms = itfUsoRecursoPersistenciaEntornosSimulacion.obtenerInfoAsignacionVictimas();
            contadorRobotsQueContestanFinsimulacion++;
            if (contadorRobotsQueContestanFinsimulacion == identsAgtesEquipo.size()) {
                this.itfUsoRecursoVisualizadorEntornosSimulacion.mostrarResultadosFinSimulacion();
            }
        } catch (Exception e) {
        }
    }

    public void crearTempSolicitarDefinicionEscenario() {
        int tiempoEsperaDefinicionEscenario = 10000;
        this.generarTimeOut(tiempoEsperaDefinicionEscenario, "timeoutEsperaDefinicionEscenario", nombreAgente, nombreAgente);

    }

    public void solicitarDefinicionEscenario() {
        try {
            itfUsoRecursoVisualizadorEntornosSimulacion.notificarRecomendacion("Sin escenario Definido", "No se puede iniciar la simulacion sin definir un escenario",
                    "Abrir un escenario existente o crear uno nuevo");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void mostrarEscenarioActualSimulado() {

        trazas.aceptaNuevaTraza(new InfoTraza(this.nombreAgente, "Accion MostrarEscenarioActualSimulado  ....", InfoTraza.NivelTraza.debug));
        try {
            itfUsoRecursoVisualizadorEntornosSimulacion.mostrarEscenarioMovimiento(escenarioActual);

        } catch (Exception e) {
        }
    }

    public void mostrarRobotsActivos() {
        try {
            itfUsoRecursoVisualizadorEntornosSimulacion.mostrarIdentsEquipoRobots(this.identsAgtesEquipo);
        } catch (Exception ex) {
            Logger.getLogger(AccionesAgteControladorSimulador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PararRobot(String idRobot) {
        OrdenParada orden = new OrdenParada(nombreAgente);
        this.comunicator.enviarInfoAotroAgente(orden, idRobot);
    }

    public void inicializarEstatusRobotsEquipo() {
        // se envia a cada robot su estatus particular en el escenario : situacion, energia, rol y demas
        String identRobot;
        RobotStatus1 estatusRobot;
        for (Object identsAgtesEquipo1 : identsAgtesEquipo) {
            identRobot = (String) identsAgtesEquipo1;
            estatusRobot = (RobotStatus1) escenarioActual.getRobotInfo(identRobot).clone();
            comunicator.enviarInfoAotroAgente(estatusRobot, identRobot);
        }
        robotEstatusEquipoInicializado = true;
    }

    @Override
    public void clasificaError() {
        // TODO Auto-generated method stub
    }

    // ---------------------------------------------------
    // ----------- Metodos auxiliares -----------
    // ---------------------------------------------------
    private void incrementarNumeroVictimasActuales() {
        this.numeroVictimasEntorno++;
    }

    private void mostrarVentanaAlertaFinSimulacion() {

        String directorioTrabajo = System.getProperty("user.dir");  //Obtener directorio de trabajo      		

        String msg = "FIN DE LA SIMULACION !!!.\n";
        msg = msg + "Se ha completado la captura de todas las estadisticas para la simulacion actual.\n";
        msg = msg + "Los ficheros de estadisticas se encuentran en el directorio " + directorioTrabajo + "/" + ConstantesRutasEstadisticas.rutaDirectorioEstadisticas + "\n";
        msg = msg + "Los ficheros de estadisticas son los siguientes:\n";
        msg = msg + directorioTrabajo + "/" + ConstantesRutasEstadisticas.rutaficheroXMLEstadisticasLlegadaVictimas + "\n";
        msg = msg + directorioTrabajo + "/" + ConstantesRutasEstadisticas.rutaficheroXMLEstadisticasAsignacionVictimas + "\n";
        msg = msg + directorioTrabajo + "/" + ConstantesRutasEstadisticas.rutaficheroXMLRepartoTareasRobotsYTiempoCompletarlasV2 + "\n";
        msg = msg + directorioTrabajo + "/" + ConstantesRutasEstadisticas.rutaficheroXMLEstadisticasLlegadaYAsignacionVictimas + "\n";
        msg = msg + directorioTrabajo + "/estadisticas/" + "EstIntLlegadaYAsignacionVictims" + "FECHA.xml" + "\n";

        JOptionPane.showMessageDialog(null, msg);
    }

    private void informarResultadosSimulacion() {
        try {
            // visualizamos los resultados
//            this.itfUsoRecursoVisualizadorEntornosSimulacion.visualizarLlegadaYasignacionVictimas(identsAgtesEquipo, identsAgtesEquipo);
            this.itfUsoRecursoVisualizadorEntornosSimulacion.visualizarTiempoAsignacionVictimas(identsAgtesEquipo);
            // guardamos los resultados para poder consultarlos

        } catch (Exception ex) {
            Logger.getLogger(AccionesAgteControladorSimulador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void trazarInfoRescateVictimas() {
        trazas.aceptaNuevaTraza(new InfoTraza("OrdenRescate",
                "\n" + " Escenario : " + escenarioActual.getIdentEscenario() + "\n", InfoTraza.NivelTraza.debug));
        VictimasSalvadas victimasSrobot;
        for (String idRobot : equipo.getTeamMemberIDs()) {
            victimasSrobot = infoCasoSimul.getIdentsVictimasSalvadasRobot(idRobot);
            if (victimasSrobot != null) {
                trazas.aceptaNuevaTraza(new InfoTraza("OrdenRescate",
                        "\n" + " Victimas salvadas por el robot : " + idRobot
                        + victimasSrobot.getVictimas().toString(), InfoTraza.NivelTraza.debug));
            }
        }
    }
}
