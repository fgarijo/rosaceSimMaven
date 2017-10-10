/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp;

import org.icaro.aplicaciones.Rosace.informacion.RobotCapability;
import org.icaro.aplicaciones.Rosace.informacion.RobotStatus1;
import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import org.icaro.aplicaciones.recursos.recursoPersistenciaEntornosSimulacion.ItfUsoRecursoPersistenciaEntornosSimulacion;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.openide.util.Exceptions;

/**
 *
 * @author FGarijo
 */
public class ControladorGestionEscenariosRosace {

    private NotificadorInfoUsuarioSimulador notifEvts;
    private int intervaloSecuencia = 1000; // valor por defecto. Eso deberia ponerse en otro sitio
    private int numMensajesEnviar = 3;
    private boolean primeraVictima = true;
    private VisorControlSimuladorRosace visorControlSim;
    private ArrayList identsRobotsEquipo;
    private javax.swing.JLabel jLabelAux;
    private String directorioTrabajo;
    private String tituloVentanaVisor = "ROSACE Scenario Visor";
    private String rutassrc = "src/";   //poner "src/main/java" si el proyecto de icaro se monta en un proyecto maven
    private String rutapaqueteConstructorEscenariosROSACE = "utilsDiseniaEscenariosRosace/";
    private static Image IMAGErobot, IMAGEmujer, IMAGEmujerRes;
    private String rutaIconos = "\\src\\utilsDiseniaEscenariosRosace\\";
//    private String rutaPersistenciaEscenario = "\\src\\persistenciaEscenarios\\";
    private String directorioPersistencia = VocabularioRosace.NombreDirectorioPersistenciaEscenarios + File.separator;
    private String imageniconoHombre = "Hombre.png";
    private String imageniconoMujer = "Mujer.png";
    private String imageniconoMujerRescatada = "MujerRescatada.png";
    private String imageniconoHombreRescatado = "HombreRescatado.png";
    private String imageniconoRobot = "Robot.png";
    private String modeloOrganizativoInicial = "Igualitario";
    private String tituloAvisoEscenarioNoDefinido = "Escenario indefinido";
    private String mensajeEscenarioNoDefinido = "El esceneraio de simulación no esta definido ";
    private String recomendacionDefinirEscenario = " Abrir un escenario con el menu de edicion o crear un escenario nuevo";
    private String mensajeEscenarioNoSeleccionado = "No se ha seleccionado el esceneraio de simulación ";
    private String tituloAvisoEscenSinRobotsDefinidos = "Escenario sin Robots definidos";
    private String mensajeEscenarioSinRobots = "No se han definido Robots en el escenario ";
    private String recomendacionDefinirRobots = " Definir Robots y Victimas con el botón derecho para poder guardar el escenario ";
    private Map<String, JLabel> tablaEntidadesEnEscenario;
    private ArrayList<JLabel> listaEntidadesEnEscenario;
    private JPanel panelVisor;
    JLabel entidadSeleccionada = null;
    private WidgetAction moveAction = ActionFactory.createMoveAction();
    private Point ultimoPuntoClic;
    private boolean intencionUsuarioCrearRobot;
    private boolean intencionUsuarioCrearVictima;
//    private boolean entidadSeleccionadaParaMover;
    private boolean escenarioSimulAbierto = false;
    private boolean escenarioEdicionAbierto = false;
    private int numeroRobots, mumeroVictimas;
    private volatile GestionEscenariosSimulacion gestionEscComp;
    private volatile EscenarioSimulacionRobtsVictms escenarioEdicionComp, escenarioSimulComp;
    private volatile PersistenciaVisualizadorEscenarios persistenciaLocal;
    private String modeloOrganizativo;
    private String identEquipoActual;
    private VisorEditorEscenarios1 visorEditorEscen;
    private VisorMovimientoEscenario visorMovimientoEscen;
    private ItfUsoRecursoPersistenciaEntornosSimulacion itfPersistenciaSimul;
    private boolean visorControlSimuladorIniciado;
    private boolean visorMovientoIniciado;
    private boolean creandoEscenario;
//    private boolean escenarioSimulacionAbierto;
    private boolean peticionObtenerEscenarioValido;
    private boolean escenarioValidoObtenido;
    private boolean robotsVisualizados;
    private boolean victimasVisualizadas;
    public static final int MOD_EDICION = 0;
    public static final int MOD_SIMULACION = 1;
    public static final int FICHERO_SELECCIONADO = 0;
    public static final int USUARIO_CANCELA_SELECCION = 1;
//    private boolean simulando;
//    private boolean editando;

    private javax.swing.JFileChooser jFileChooser1;
    int maxIntentosPeticionSeleccionEscenario = 2;
    private ArrayList identsVictims;
    private boolean avisoSeleccionVictima;
    private MemComunControladores memComunControladores;
    private EstadoControladorEscenarios estadoCrtEsc;
    private AccionesExternas accExt;

    public ControladorGestionEscenariosRosace(NotificadorInfoUsuarioSimulador notificadorInfoUsuarioSimulador) {
        notifEvts = notificadorInfoUsuarioSimulador;

    }

    public void setIftRecPersistencia(ItfUsoRecursoPersistenciaEntornosSimulacion itfPersisSimul) {
        itfPersistenciaSimul = itfPersisSimul;
    }

    public void setVisorControlSimulador(VisorControlSimuladorRosace visorControl) {
        visorControlSim = visorControl;
    }

    public void initVisualizadores() {
//       String  directorioPersistencia = VocabularioRosace.IdentDirectorioPersistenciaEscenarios+File.separator;
//            VisorControlSimuladorRosace visorSc;
        gestionEscComp = new GestionEscenariosSimulacion();
        estadoCrtEsc = new EstadoControladorEscenarios();
        accExt = new AccionesExternas();

        try {
            HashSet identsEscenariosCreados= itfPersistenciaSimul.obtenerIdentsEscenarioSimulacion();
            if(identsEscenariosCreados!=null&&!identsEscenariosCreados.isEmpty()){
                                gestionEscComp.setIdentsEscenariosSimulacion(identsEscenariosCreados);
                                estadoCrtEsc.escenariosCreados=true;
            }
            visorEditorEscen = new VisorEditorEscenarios1(this);
            visorEditorEscen.setGestorEscenarionComp(gestionEscComp);
            visorEditorEscen.setDirectorioPersistencia(directorioPersistencia);
            visorControlSim.setDirectorioPersistencia(directorioPersistencia);
//              visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
//              visorControlSim.setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public synchronized void peticionCrearEscenario() {
        // La peticion viene del visualizador del control de simulacion
        // verificar que no esta creando otro escenario
        if (!creandoEscenario) {//abrirEditorEscenarios y esperar hasta que se guarde o se cierre el editor
            escenarioEdicionComp = gestionEscComp.crearEscenarioSimulacion();
            escenarioEdicionComp.initEscenario();
            visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
            visorEditorEscen.setVisible(true);
            this.creandoEscenario = true;
            this.escenarioEdicionAbierto=true;
            this.memComunControladores.setescenarioEdicionAbierto(true);
            this.memComunControladores.setescenarioEdicion(escenarioEdicionComp);
        }
    }

//    public void peticionAbrirEscenario() {
////        throw new UnsupportedOperationException("Not supported yet."); 
////    File ficheroSeleccionado=   visorControlSim.solicitarSeleccionFichero(directorioPersistencia);
//        if (!visorControlSim.hayFicherosCreados()) {
//            this.visorControlSim.visualizarConsejo("Sin Escenarios de simulacion", "No hay escenarios creados", "Abrimos el editor de escenarios para definir Robots y Victimas");
//            peticionCrearEscenario();
//        } else {
//             if (escenarioEdicionAbierto) {
////                    this.visorControlSim.visualizarConsejo("Escenario Edicion Abierto", "Solo se permite editar un escenario", "Guardamos el escenario abierto");
////                    visorEditorEscen.setVisible(false);
////            escenarioActualAbierto=false; Sin Terminar !!!!!!!!!!!!!!
////                }
//            File ficheroSeleccionado = visorControlSim.solicitarSeleccionFichero();
//            if (ficheroSeleccionado == null) {
//                visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado, recomendacionDefinirEscenario);
//            } else {
//                try {
//                    escenarioEdicionComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
//                } catch (Exception ex) {
//                    Exceptions.printStackTrace(ex);
//                    System.out.println("Desde peticion Abrir escenario . No se encuetra el fichero Ident Fichero : " + ficheroSeleccionado.getAbsolutePath());
//                }
//                escenarioEdicionComp.setGestorEscenarios(gestionEscComp);
//                identEquipoActual = escenarioEdicionComp.getIdentEscenario();
////                visorControlSim.setIdentEscenarioActual(identEquipoActual);
////                visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
//                identsRobotsEquipo = escenarioEdicionComp.getListIdentsRobots();
//                this.memComunControladores.setescenarioEdicion(escenarioEdicionComp);
////               
//                if (identsRobotsEquipo != null) {
//                    visorControlSim.visualizarIdentsEquipoRobot(identsRobotsEquipo);
//                }
//            }
//        }
//    }

    public void peticionAbrirEscenarioEdicion() {
        this.peticionObtenerEscenarioValido = true;
        int numeroIntentos = 0;
        boolean ficheroSeleccionadoValido = false;
        EscenarioSimulacionRobtsVictms escenario = null;
        while (numeroIntentos <= this.maxIntentosPeticionSeleccionEscenario && !ficheroSeleccionadoValido) {
            escenario = this.seleccionarUnFicheroEntreLosExistentes();
            if (escenario != null) {
                ficheroSeleccionadoValido = true;
            } else {
                visorEditorEscen.visualizarConsejo("Fichero seleccionado Nulo ", "No se ha seleccionada ningun fichero", "Seleccione otro fichero o  cree uno nuevo ");
            }
            numeroIntentos++;
        }
        if (ficheroSeleccionadoValido){
            String identEscenarioSeleccionado = escenario.getIdentEscenario();
            if(this.escenarioEdicionAbierto&&!escenarioEdicionComp.getIdentEscenario().equals(identEscenarioSeleccionado)){
            // Ya tiene abierto el escenario
            visorEditorEscen.setVisible(true);
            }else{
            escenarioEdicionComp = escenario;
            escenarioEdicionComp.setGestorEscenarios(gestionEscComp);
            identEquipoActual = escenarioEdicionComp.getIdentEscenario();
            this.memComunControladores.setescenarioEdicion(escenarioEdicionComp);
            this.visorEditorEscen.visualizarEscenario(escenario);
            this.memComunControladores.setescenarioEdicionAbierto(true);
//        this.memComunControladores.setcambioEnEscenarioSimulacion(true);
//        this.memComunControladores.setescenarioSimulacionAbierto(true);  
        }
        }
    }

    public void peticionObtenerEscenarioSimulacion(String modOrganizativo, int numRobots) {
        this.peticionObtenerEscenarioValido = true;
        int numeroIntentos = 0;
        int resultadoSeleccion;
        boolean ficheroSeleccionadoValido = false;
        this.memComunControladores.setmodorganizDefinidoEnOrg(modOrganizativo);
        this.memComunControladores.setnumRobotsdefsEnOrganizacion(numRobots);
        EscenarioSimulacionRobtsVictms escenario = null;
        while (numeroIntentos <= this.maxIntentosPeticionSeleccionEscenario && !ficheroSeleccionadoValido) {
            resultadoSeleccion = visorEditorEscen.selecciondeFichero();
            if (resultadoSeleccion == this.USUARIO_CANCELA_SELECCION) {
                numeroIntentos = maxIntentosPeticionSeleccionEscenario;
            } else if (resultadoSeleccion == this.FICHERO_SELECCIONADO) {
                escenario = obtenerComputacionalDePersistencia(visorEditorEscen.getUltimoFicheroEscenarioSeleccionado());
                if (escenario == null) {
                    visorEditorEscen.visualizarConsejo("Fichero seleccionado Nulo ", "Se debe eleccionar un fichero con modelo organizativo : " + modOrganizativo
                            + " y numero  = : " + numRobots, "Seleccione otro fichero o  cree uno nuevo ");
                } else if (escenario.getmodeloOrganizativo().equals(modOrganizativo)
                        && escenario.getNumRobots() == numRobots) {
                    ficheroSeleccionadoValido = true;
                } else {
                    visorEditorEscen.visualizarConsejo("Fichero seleccionado No valido ", "El modelo organizativo del fichero seleccionado debe ser: " + modOrganizativo
                            + " y el  numero de Robots debe ser de = : " + numRobots, " pero el fichero selecionado no tiene estas características. Seleccione otro fichero o  cree uno nuevo ");
                }
            }
            numeroIntentos++;
        }
        if (ficheroSeleccionadoValido) {
            escenarioSimulComp = escenario;
            escenarioSimulComp.setGestorEscenarios(gestionEscComp);
            identEquipoActual = escenarioSimulComp.getIdentEscenario();
//        visorControlSim.setIdentEscenarioActual(identEquipoActual);
//        visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
//        identsRobotsEquipo=escenarioSimulComp.getListIdentsRobots();
//        visorControlSim.visualizarIdentsEquipoRobot(identsRobotsEquipo);
            this.memComunControladores.setescenarioMovimiento(escenarioSimulComp);
            this.memComunControladores.setcambioEnEscenarioSimulacion(true);
//        this.memComunControladores.setescenarioSimulacionAbierto(true); 
            notifEvts.sendInfoEscenarioObtenidodoValido(escenarioSimulComp);
        } else {
            notifEvts.sendNotifAgteControlador("escenarioNoDefinidoTrasAgotarLosIntentos");
        }
    }

    public void peticionObtenerEscenarioSimulacion(String identFichero, String modOrganizativo, int numRobots) {
//     EscenarioSimulacionRobtsVictms escenarioAobtener=null;
        
        if (!estadoCrtEsc.escenariosCreados) {
             this.visorEditorEscen.visualizarConsejo("Sin Escenarios de simulacion", "No hay escenarios creados",
                    "Para iniciar la simulacion se necesita crear un escenario con Modelo Organizativo: " + modOrganizativo
                    + " y Numero de Robots : " + numRobots);
            peticionCrearEscenario();
            notifEvts.sendNotifAgteControlador("UsuarioCreandoEscenarioSimulacion");
        }else // hay escenarios creados
            if(gestionEscComp.existeEscenario(identFichero)){ // existe el fichero especificado
               escenarioSimulComp= accExt.obtenerComputacionalDePersistencia(identFichero);
               memComunControladores.setescenarioSimulacion(escenarioSimulComp);
            escenarioValidoObtenido = true;
            peticionObtenerEscenarioValido = false;
            notifEvts.sendInfoEscenarioObtenidodoValido(escenarioSimulComp);
            }else // no existe el fichero especificado       
            if (accExt.existEscenario(modOrganizativo, numRobots))// existe algun fichero creado con modelo org y nun robots requeridos
            {
                this.visorControlSim.visualizarConsejo("Escenario Simulacion no definido", "No se encuentra el escenario : " + identFichero,
                        "Para iniciar la simulacion puede seleccionar un  escenario con Modelo Organizativo: " + modOrganizativo
                        + " Numero de Robots : " + numRobots  + " o crear uno nuevo ");
                this.peticionObtenerEscenarioSimulacion(modOrganizativo, numRobots);
            }else {
                 this.visorControlSim.visualizarConsejo("Escenario Simulacion no definido", "No se encuentra el escenario : " + identFichero,
                        "Para iniciar la simulacion debe crear un  escenario con Modelo Organizativo: " + modOrganizativo
                        + " Numero de Robots : " + numRobots );
            memComunControladores.setmodorganizDefinidoEnOrg(modOrganizativo);
            memComunControladores.setnumRobotsdefsEnOrganizacion(numRobots);
            this.peticionCrearEscenario();
             notifEvts.sendNotifAgteControlador("UsuarioCreandoEscenarioSimulacion");
            }
    }

    private EscenarioSimulacionRobtsVictms seleccionarUnFicheroEntreLosExistentes() {
        EscenarioSimulacionRobtsVictms escenarioSeleccionado = null;
        if (!gestionEscComp.hayEscenariosCreados()) {
            this.visorEditorEscen.visualizarConsejo("Sin Escenarios de simulacion", "No hay escenarios creados", "Abrimos el editor de escenarios para definir Robots y Victimas");
            peticionCrearEscenario();
            return null;
        }
        File ficheroSeleccionado = visorEditorEscen.solicitarSeleccionFichero();
        if (ficheroSeleccionado == null) {
            visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado, recomendacionDefinirEscenario);
        } else {
            try {
                escenarioSeleccionado = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("Desde peticion Abrir escenario . No se encuentra el fichero Ident Fichero : " + ficheroSeleccionado.getAbsolutePath());
            }
        }
        return escenarioSeleccionado;
    }

    private EscenarioSimulacionRobtsVictms obtenerComputacionalDePersistencia(File ficheroSeleccionado) {
//    File ficheroSeleccionado=   visorEditorEscen.solicitarSeleccionFichero();
        EscenarioSimulacionRobtsVictms escenarioComp = null;
        if (ficheroSeleccionado == null) {
            visorEditorEscen.visualizarConsejo("Fichero Seleccionado null", mensajeEscenarioNoSeleccionado, recomendacionDefinirEscenario);
        } else {
            try {
                escenarioComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("Desde peticion Abrir escenario . No se encuentra el fichero Ident Fichero : " + ficheroSeleccionado.getAbsolutePath());
            }
        }
        return escenarioComp;
    }

    public void peticionEliminarEscenarioSimulActual() {
        if ( estadoCrtEsc.reqEliminarEscenarioSimulacion()){
            this.accExt.eliminarEscenarioSimulacion(escenarioSimulComp.getIdentEscenario());
            // Informar al agente controlador 
        }
        else visorControlSim.visualizarConsejo("Eliminar Escenario actual", "No se puede Eliminar El fichero de simulacion esta en uso", " Terminar la simulacion e intentarlo de nuevo");

            }
    public static void main(String args[]) {

        new ControladorGestionEscenariosRosace(new NotificadorInfoUsuarioSimulador("prueba1", "agente2")).initVisualizadores();

    }

    public void peticionGuardarEscenario(EscenarioSimulacionRobtsVictms escenarioComp) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // si no hay un escenario actual definido indicamos al usuario que no hay escenario definido
        // si en el escenario a guardar no hay robots ni victimas se lo decimos
        escenarioEdicionComp = escenarioComp;
        if (escenarioEdicionComp.getNumRobots() <= 0) {
            visorEditorEscen.visualizarConsejo(tituloAvisoEscenSinRobotsDefinidos, mensajeEscenarioSinRobots, recomendacionDefinirRobots);
        } else {
            int respuesta = visorEditorEscen.confirmarPeticionGuardarEscenario("Se va a guardar el escenario : ");
            if (respuesta == JOptionPane.OK_OPTION) {

                ArrayList<String> robotNombres = escenarioComp.getListIdentsRobots();
                for (String ideRobot : robotNombres) {
//                 String ideRobot = (String)robtIter.next();
                    RobotStatus1 infoRobot = (RobotStatus1) escenarioComp.getRobotInfo(ideRobot);
                    List<RobotCapability> capacidades = infoRobot.getRobotCapabilities();
                    System.out.println("Desde peticion Guardar Lista de capacidades a guardar del robot  : " + ideRobot + "Capacidades : " + capacidades.toString());
                }
                System.out.println("Desde peticion Guardar Numero de Robots  : " + escenarioEdicionComp.getNumRobots() + " Numero de victimas : " + escenarioEdicionComp.getNumVictimas());
//             if (itfPersistenciaSimul==null)   
//                persistenciaLocal.guardarInfoEscenarioSimulacion(directorioPersistencia, escenarioEdicionComp);
//             else 
                try {
                    itfPersistenciaSimul.guardarInfoEscenarioSimulacion(escenarioEdicionComp);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
//                if (peticionObtenerEscenarioValido && !escenarioValidoObtenido) {
//                    // se envia el escenario al agente controlador que puede estar esperandolo
//                    notifEvts.sendInfoEscenarioSeleccionado(escenarioEdicionComp);
//                }
            }
//            if (!visorMovientoIniciado) {
//                visorControlSim.visualizarIdentsEquipoRobot(escenarioComp.getListIdentsRobots());
//                visorControlSim.setIdentEscenarioActual(escenarioComp.getIdentEscenario());
//            }
        }
    }

    public void peticionMostrarEscenarioMovimiento(EscenarioSimulacionRobtsVictms escenarioComp) {
        if (escenarioComp != null) {
            String identEscenarioSimulActual = escenarioSimulComp.getIdentEscenario();
            try {
                if (visorEditorEscen.isShowing()) {
                    visorEditorEscen.setVisible(false);
                }
                if (visorMovimientoEscen == null) {
                    visorMovimientoEscen = new VisorMovimientoEscenario(escenarioComp);
                    visorMovimientoEscen.visualizarEscenario();
                } else if (identEscenarioSimulActual.equals(escenarioComp.getIdentEscenario())) {
                    visorMovimientoEscen.cambiarPosicionRobotsEscenario(escenarioComp.getRobots());
                    visorMovimientoEscen.inicializarEstatusVictimas(escenarioComp.getVictims().entrySet());
                } else {
                    visorMovimientoEscen.actualizarEscenario(escenarioComp);
                }
//            visorMovimientoEscen.visualizarEscenario();
                escenarioSimulComp = escenarioComp;
                visorMovientoIniciado = true;
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void peticionMostrarEscenarioActual() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (escenarioSimulComp != null) {
            visorMovimientoEscen.setVisible(true);
        } else if (escenarioEdicionComp == null) {
            visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoDefinido, recomendacionDefinirEscenario);
        } else {
            visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
            escenarioEdicionAbierto = true;
        }
    }

    public boolean abrirVisorConEscenario(String identFicheroEscenarioSimulacion) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (!visorControlSimuladorIniciado) {
//        initModelosYvistas();
            if (identFicheroEscenarioSimulacion == null) { // Se abre el visor sin 
                visorEditorEscen.setVisible(true);
            }
            if (identFicheroEscenarioSimulacion != null) {
                try {
                    escenarioEdicionComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(identFicheroEscenarioSimulacion);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (escenarioEdicionComp == null) {
                this.visorControlSim.visualizarConsejo("fichero no encontrado ", "verifique el identificador del fichero ", "Puede abrir  un escenario existente o crear un nuevo escenario");
                return false;
            }
            visorControlSim.setIdentEscenarioActual(identFicheroEscenarioSimulacion);
            visorControlSim.visualizarIdentsEquipoRobot(escenarioEdicionComp.getListIdentsRobots());
            visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
            visorControlSimuladorIniciado = true;
            visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
        }
        return true;
    }

    public VisorMovimientoEscenario getVisorMovimiento() {
        if (visorMovientoIniciado) {
            return visorMovimientoEscen;
        } else {
            return null;
        }
    }

    public void peticionVisualizarIdentsRobots(ArrayList identList) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // si los identificadores recibidos son diferentes a los que hay en el escenario se cambian los del escenario
        // esto es un poco raro pero resuelve temporalmente el problema
        // cambiar los nombres en el computacional y tambien las etiquetas en el visualizador

        if (identList != null) {
            visorControlSim.visualizarIdentsEquipoRobot(identList);
            visorControlSim.setIdentEscenarioActual(escenarioEdicionComp.getIdentEscenario());
            this.robotsVisualizados = true;
            if (escenarioEdicionComp.getListIdentsRobots().size() != identList.size()) {
                visorControlSim.visualizarConsejo("Imposible cambiar los identificadores", "El numero de robots en el escenario actual y el recibido es diferente",
                        "revisar las definiciones de los escenarios");
            } else { // renombrar idents robots en el computacional y en la visualizacion
                this.escenarioEdicionComp.renombrarIdentRobts(identList);
                ArrayList<String> robotNombres = escenarioEdicionComp.getListIdentsRobots();
                for (String ideRobot : robotNombres) {
//                 String ideRobot = (String)robtIter.next();
                    RobotStatus1 infoRobot = (RobotStatus1) escenarioEdicionComp.getRobotInfo(ideRobot);
//              List<RobotCapability> capacidades=infoRobot.getRobotCapabilities();
                    System.out.println("Desde peticionVisualizarIdentsRobots  nuevo ident Robot : " + ideRobot);
                }
                Object[] victimasIdents = escenarioEdicionComp.getVictims().keySet().toArray();
                for (int i = 0; i < victimasIdents.length; i++) {
                    String idVictima = (String) victimasIdents[i];
                    System.out.println("Desde peticionVisualizarIdentsRobots  nuevo ident Vicitima : " + idVictima);
                }

                visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
            }
        }
    }

    public EscenarioSimulacionRobtsVictms obtenerEscenarioSeleccionadoValido(File ficheroSeleccionado, String orgModelo, int numRobots) {
        EscenarioSimulacionRobtsVictms escenarioObtenido = null;
        try {
            escenarioObtenido = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        if (escenarioObtenido != null
                && escenarioObtenido.getNumRobots() == numRobots && escenarioObtenido.getmodeloOrganizativo().equalsIgnoreCase(orgModelo)) {
            // se envia notificacion al agente controlador con el computacional obtenido
            escenarioObtenido.setGestorEscenarios(gestionEscComp);
            escenarioValidoObtenido = true;
        } else {
            visorControlSim.visualizarConsejo("Fichero seleccionado No valido ", "El modelo organizativo del fichero seleccionado: " + orgModelo
                    + " o el  numero de Robots = : " + numRobots + " No coinciden ", "Seleccione otro fichero o  cree uno nuevo ");
        }
        return escenarioObtenido;

    }

    public boolean hayEscenarioSimulAbierto() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return escenarioSimulAbierto;
    }

    public int peticionConfirmacionInformacion(String preguntaAconfirmar) {
        return visorEditorEscen.solicitarConfirmacion(preguntaAconfirmar);
    }

    public File peticionSeleccionarEscenario() {

        int numerointentos = 0;
        File ficheroSeleccionado = null;
        while (numerointentos < maxIntentosPeticionSeleccionEscenario && ficheroSeleccionado == null) {
            ficheroSeleccionado = visorControlSim.solicitarSeleccionFichero();
            if (ficheroSeleccionado == null) {
                visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado, recomendacionDefinirEscenario);
            }
            numerointentos++;
        }
        return ficheroSeleccionado;
//       this.notifEvt.informaraOtroAgenteReactivo(new InfoContEvtMsgAgteReactivo("escenarioDefinidoPorUsuario", escenarioActual), identAgenteaReportar);
    }

    void peticionCambiarPosicionRobot(String identRobot, Integer coordX, Integer coordY) {
        this.visorMovimientoEscen.cambiarPosicionRobot(identRobot, coordX, coordY);
    }

    void peticionMostrarVictimaRescatada(String identVictima) {
        this.visorMovimientoEscen.cambiarIconoEntidad(identVictima, VisorMovimientoEscenario.IMAGEmujerRes);

    }

    void abrirVisorConEscenarioComp(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
        if (!visorControlSimuladorIniciado) {
//        initModelosYvistas();
            // Se abre el visor sin 
            visorControlSim.setVisible(true);
        }
        if (escenarioSimulacion != null) {
            // visualizarContenido escenarioSimulacion
            this.escenarioSimulComp = escenarioSimulacion;
            visorControlSim.setIdentEscenarioActual(escenarioSimulacion.getIdentEscenario());
            visorControlSim.visualizarIdentsEquipoRobot(escenarioSimulacion.getListIdentsRobots());
            visorControlSim.visualizarIdentsVictimas(escenarioSimulacion.getListIdentsVictims());
            visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
            // acciones estado interno
            escenarioValidoObtenido = true;
            robotsVisualizados = true;
            victimasVisualizadas = true;
            if (visorMovimientoEscen == null) {
                try {
                    // crear VisorMovimientoyAbrirlo
                    visorMovimientoEscen = new VisorMovimientoEscenario(escenarioSimulComp);
                    visorMovientoIniciado = true;
                    this.memComunControladores.setescenarioMovimiento(escenarioSimulComp);
                    this.memComunControladores.setescenarioSimulacionAbierto(true);
                    this.memComunControladores.setevisorMovimiento(visorMovimientoEscen);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            visorMovimientoEscen.actualizarEscenario(escenarioSimulComp);
        }
        visorControlSimuladorIniciado = true;
    }

    void setMemComunControladores(MemComunControladores memoriaComunControladores) {
        this.memComunControladores = memoriaComunControladores;
    }

    void peticionSalirEditor() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    if(memComunControladores.getescenarioEdicionAbierto()){
        int resultadoPeticion= this.peticionConfirmacionInformacion(" tiene un escenario abierto.  ¿Desea guardarlo antes de salir ?");
        if(resultadoPeticion== JOptionPane.OK_OPTION) 
            this.peticionGuardarEscenario(escenarioSimulComp);
        else if(resultadoPeticion== JOptionPane.CANCEL_OPTION) return;
    }
    this.memComunControladores.setescenarioEdicionAbierto(false);
    this.visorEditorEscen.dispose();
    }
    
    

    private class EstadoControladorEscenarios {

        private boolean intencionUsuarioCrearRobot;
        private boolean intencionUsuarioCrearVictima;
        private boolean entidadSeleccionadaParaMover;
        private boolean escenarioSimulAbierto = false;
        private boolean escenarioEdicionAbierto = false;
        boolean escenariosCreados = false;
        private int numeroRobots, mumeroVictimas;
        private Point ultimoPuntoClic;
        boolean escenarioValidoObtenido = true;
        boolean robotsVisualizados = true;
        boolean victimasVisualizadas = true;
        boolean creandoEscenario;
        boolean visorEdicionEscenarioAbierto;
        boolean escenarioEnEdicionGuardado;
        boolean visorMovientoIniciado;
        boolean hayEscenDefinidosConModOrgyNumRobots;
        boolean simulacionIniciada;
        boolean simulacionFinalizada;

        public EstadoControladorEscenarios() {
        }

        boolean verificarModeloyRobots(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
            return (this.numeroRobots == escenarioSimulacion.getNumRobots()) && (modeloOrganizativo.equalsIgnoreCase(escenarioSimulacion.getmodeloOrganizativo()));
        }

        boolean reqAbrirVisorMovConEscenarioComp(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
            return (escenarioSimulacion != null && visorMovientoIniciado);
        }

        boolean reqAbrirVisorContrConEscenarioComp(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
            return (escenarioSimulacion != null && visorControlSimuladorIniciado);
        }

        boolean reqAbrirVisorCreEscConEscenarioComp(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
            return (escenarioSimulacion != null && visorEdicionEscenarioAbierto);
        }

        boolean reqVisualizarContenidoEscenarioSimulacion(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
            //    si hay un escenario de simulacion abierto el modelo organizativo y el numero de robots del escenario abierto debe ser igual al 
            // del escenario que se pretende visualizar
            if (escenarioSimulacion == null || visorControlSim == null) {
                return false;
            }
            if (!escenarioSimulAbierto) {
                return true;
            } else {
                return verificarModeloyRobots(escenarioSimulacion);
            }
        }

        boolean reqRenombrarEntidadesEscenario(EscenarioSimulacionRobtsVictms nuevoEscenario) {
            if (nuevoEscenario == null || !escenarioEdicionAbierto || escenarioEdicionComp != null) {
                return false;
            }
            if (nuevoEscenario.getNumRobots() <= 0 || nuevoEscenario.getNumVictimas() <= 0) {
                return false;
            }
            return true;
        }

        boolean reqAbrirVisorMovimientoConEscenario(EscenarioSimulacionRobtsVictms nuevoEscenario) {
            if (nuevoEscenario == null || !escenarioSimulAbierto) {
                return false;
            }
            if (nuevoEscenario.getNumRobots() <= 0 || nuevoEscenario.getNumVictimas() <= 0) {
                return false;
            }
            return true;
        }

        private boolean reqEliminarEscenarioSimulacion() {
            return (hayEscenarioSimulAbierto()&&simulacionFinalizada);
            
        }
    }

    private class AccionesExternas {

        public AccionesExternas() {
        }

        void cambiarIconoEntidad(String identVictima, String idIcono) {

        }

        boolean visualizarContenidoEscenarioSimulacion(EscenarioSimulacionRobtsVictms escenarioSimulacion) {
            if (estadoCrtEsc.reqVisualizarContenidoEscenarioSimulacion(escenarioSimulacion)) {
                // visualizarContenido escenarioSimulacion
                escenarioSimulComp = escenarioSimulacion;
                visorControlSim.setIdentEscenarioActual(escenarioSimulacion.getIdentEscenario());
                visorControlSim.visualizarIdentsEquipoRobot(escenarioSimulacion.getListIdentsRobots());
                visorControlSim.visualizarIdentsVictimas(escenarioSimulacion.getListIdentsVictims());
                visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
                // acciones estado interno
                escenarioValidoObtenido = true;
                robotsVisualizados = true;
                victimasVisualizadas = true;
            }
            return false;
        }

        private EscenarioSimulacionRobtsVictms obtenerUnFicheroEntreLosExistentes() {
            EscenarioSimulacionRobtsVictms escenarioSeleccionado = null;
            if (!gestionEscComp.hayEscenariosCreados()) {
                visorEditorEscen.visualizarConsejo("Sin Escenarios de simulacion", "No hay escenarios creados", "Abrimos el editor de escenarios para definir Robots y Victimas");
                peticionCrearEscenario();
                return null;
            }
            // precondiciones fichero Escenarios != null 
            File ficheroSeleccionado = visorEditorEscen.solicitarSeleccionFichero();
            if (ficheroSeleccionado == null) {
                visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado, recomendacionDefinirEscenario);
            } else {
                try {
                    escenarioSeleccionado = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    System.out.println("Desde peticion Abrir escenario . No se encuentra el fichero Ident Fichero : " + ficheroSeleccionado.getAbsolutePath());
                    return null;
                }
            }
            return escenarioSeleccionado;
        }

        boolean renombrarEntidadesEscenario(EscenarioSimulacionRobtsVictms nuevoEscenario) {
            // Precondiciones : escenario de edicion abierto,  nuevoEscenario  no nulo 
            // escenarioEdicon comp no nulo
            if (estadoCrtEsc.reqRenombrarEntidadesEscenario(nuevoEscenario)) {
                ArrayList<String> robotNombres = nuevoEscenario.getListIdentsRobots();
                escenarioEdicionComp.renombrarIdentRobts(robotNombres);
//            ArrayList<String> robotNombres = escenarioEdicionComp.getListIdentsRobots();
                for (String ideRobot : robotNombres) {
//                 String ideRobot = (String)robtIter.next();
                    RobotStatus1 infoRobot = (RobotStatus1) escenarioEdicionComp.getRobotInfo(ideRobot);
//              List<RobotCapability> capacidades=infoRobot.getRobotCapabilities();
                    System.out.println("Desde peticionVisualizarIdentsRobots  nuevo ident Robot : " + ideRobot);
                }
                Object[] victimasIdents = escenarioEdicionComp.getVictims().keySet().toArray();
                for (int i = 0; i < victimasIdents.length; i++) {
                    String idVictima = (String) victimasIdents[i];
                    System.out.println("Desde peticionVisualizarIdentsRobots  nuevo ident Vicitima : " + idVictima);
                }

                visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
                return true;
            }
            return false;
        }

        EscenarioSimulacionRobtsVictms obtenerEscenarioSeleccionadoValido(File ficheroSeleccionado, String orgModelo, int numRobots) {
            // precondiciones ficheroSeleccionado no nulo, numRobots>0 

            EscenarioSimulacionRobtsVictms escenarioObtenido = null;
            try {
                escenarioObtenido = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            if (escenarioObtenido != null
                    && escenarioObtenido.getNumRobots() == numRobots && escenarioObtenido.getmodeloOrganizativo().equalsIgnoreCase(orgModelo)) {
                // se envia notificacion al agente controlador con el computacional obtenido
                escenarioObtenido.setGestorEscenarios(gestionEscComp);
                escenarioValidoObtenido = true;
            } else {
                visorControlSim.visualizarConsejo("Fichero seleccionado No valido ", "El modelo organizativo del fichero seleccionado: " + orgModelo
                        + " o el  numero de Robots = : " + numRobots + " No coinciden ", "Seleccione otro fichero o  cree uno nuevo ");
            }
            return escenarioObtenido;

        }

        public boolean abrirVisorMovimientoConEscenario(String identFicheroEscenarioSimulacion) {
//        
            EscenarioSimulacionRobtsVictms escenarioParaVisualizar = obtenerComputacionalDePersistencia(identFicheroEscenarioSimulacion);
            if (escenarioParaVisualizar == null) {
                return false;
            }
//            if ( !estadoCrtEsc.escenarioSimulAbierto){
            if (visorMovimientoEscen == null) {
                try {
                    visorMovimientoEscen = new VisorMovimientoEscenario(escenarioParaVisualizar);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                visorMovimientoEscen.visualizarEscenario();
                escenarioSimulComp = escenarioParaVisualizar;
                estadoCrtEsc.visorMovientoIniciado = true;
            } else if (estadoCrtEsc.reqAbrirVisorMovConEscenarioComp(escenarioParaVisualizar)) {
                visorMovimientoEscen.visualizarIdentsEquipoRobot(identsVictims);
                escenarioSimulComp = escenarioParaVisualizar;
            } else {
                visorMovimientoEscen.visualizarConsejo("Fichero No valido", "El numero de Robots debe coincidir con el definido en la descripcion"
                        + " de la organizacion ", " Revisar el numero de robots en el escenario");
                return false;
            }
            return true;
        }

        public boolean abrirVisorCreEscConEscenarioComp(EscenarioSimulacionRobtsVictms escenarioSimulComp) {
            if (estadoCrtEsc.reqAbrirVisorCreEscConEscenarioComp(escenarioSimulComp)) {
//        initModelosYvistas();

                visorEditorEscen.setName(escenarioSimulComp.getIdentEscenario());
                visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
                visorControlSimuladorIniciado = true;
                visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
                estadoCrtEsc.visorEdicionEscenarioAbierto = true;
            }
            return true;
        }

        EscenarioSimulacionRobtsVictms obtenerComputacionalDePersistencia(File ficheroSeleccionado) {
//    File ficheroSeleccionado=   visorEditorEscen.solicitarSeleccionFichero();
            EscenarioSimulacionRobtsVictms escenarioComp = null;
            if (ficheroSeleccionado == null) {
                visorEditorEscen.visualizarConsejo("Fichero Seleccionado null", mensajeEscenarioNoSeleccionado, recomendacionDefinirEscenario);
            } else {
                try {
                    escenarioComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    System.out.println("Desde peticion Abrir escenario . No se encuentra el fichero Ident Fichero : " + ficheroSeleccionado.getAbsolutePath());
                }
            }
            return escenarioComp;
        }

        void notifRecomendacionUsuario(String titulo, String motivo, String recomendacion) {
            visorControlSim.visualizarConsejo(titulo, motivo, recomendacion);
        }

        EscenarioSimulacionRobtsVictms obtenerComputacionalDePersistencia(String identEscenario) {
//    File ficheroSeleccionado=   visorEditorEscen.solicitarSeleccionFichero();
            EscenarioSimulacionRobtsVictms escenarioComp = null;
            if (identEscenario == null) {
                visorEditorEscen.visualizarConsejo("Identificador null", "En nombre del fichero es null", " Verificar el identicador del fichero");
            } else {
                try {
                    escenarioComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(identEscenario);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    System.out.println("Desde peticion Abrir escenario . No se encuentra el fichero Ident Fichero : " + identEscenario);
                }
            }
            return escenarioComp;
        }
//         void notifRecomendacionUsuario(String titulo, String motivo, String recomendacion) {
//        visorControlSim.visualizarConsejo(titulo, motivo, recomendacion);

        void abrirEditorEscenarios() {

            visorEditorEscen.setVisible(true);

        }

        void crearEscenario() {
            if (!estadoCrtEsc.escenarioEdicionAbierto) {
                visorEditorEscen.setVisible(true);
            }
            escenarioEdicionComp = gestionEscComp.crearEscenarioSimulacion();
            visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
            escenarioEdicionComp.initEscenario();
            estadoCrtEsc.creandoEscenario = true;
            estadoCrtEsc.entidadSeleccionadaParaMover = true;
            estadoCrtEsc.escenarioEdicionAbierto = true;
        }

         boolean existEscenario(String modOrganizativo, int numRobots) {
            if (estadoCrtEsc.hayEscenDefinidosConModOrgyNumRobots) {
                return true;
            }
            try {
                if (itfPersistenciaSimul.existEscenarioSimulacion(modOrganizativo, numRobots)) {
                    estadoCrtEsc.hayEscenDefinidosConModOrgyNumRobots = true;
                    return true;
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
         boolean eliminarEscenarioSimulacion(String identEscenario){
             if(identEscenario==null) return false; 
//        confirmar eliminacion del fichero y si lo confirma 
        String smsg = "Se va a eliminar el escenario: " + identEscenario +  " Confirma la eliminacion ? ";
         if (!visorControlSim.solicitarConfirmacion(smsg)) return false ;
        gestionEscComp.eliminarEscenario(identEscenario);
         System.out.println("Se elimina el fichero :  "+ identEscenario);
         return true;
      }
         
    }
}
