/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.recursos.recursoCreacionEntornosSimulacion.imp;

import org.icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
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
public class ControladorEditorEntornosSimulacion {
     private NotificadorInfoUsuarioEdicionEscenarios notifEvts;
//    private int intervaloSecuencia = 10000; // valor por defecto. Eso deberia ponerse en otro sitio
//    private int numMensajesEnviar = 3;
//    private boolean primeraVictima = true;
//    private VisorControlSimuladorRosace visorControlSim;
    private ArrayList identsRobotsEquipo ;
    private javax.swing.JLabel jLabelAux;
    private String directorioTrabajo;
     private String tituloVentanaVisor = "Editor de Escenarios";
    private String rutassrc = "src/";   //poner "src/main/java" si el proyecto de icaro se monta en un proyecto maven
    private String rutapaqueteConstructorEscenariosROSACE = "utilsDiseniaEscenariosRosace/";
    private static  Image IMAGErobot,IMAGEmujer,IMAGEmujerRes ;
    private String rutaIconos = "\\src\\utilsDiseniaEscenariosRosace\\";
//    private String rutaPersistenciaEscenario = "\\src\\persistenciaEscenarios\\";
    private String directorioPersistencia = VocabularioRosace.NombreDirectorioPersistenciaEscenarios+File.separator;
     private String imageniconoHombre = "Hombre.png";
    private String imageniconoMujer = "Mujer.png";
    private String imageniconoMujerRescatada = "MujerRescatada.png";
    private String imageniconoHombreRescatado = "HombreRescatado.png";
    private String imageniconoRobot = "Robot.png";
    private String modeloOrganizativoInicial = "Igualitario";
    private String tituloAvisoEscenarioNoDefinido= "Escenario indefinido";
    private String mensajeEscenarioNoDefinido= "El esceneraio de simulacion no esta definido ";
    private String recomendacionDefinirEscenario= " Abrir un escenario con el menu de edicion o crear un escenario nuevo";
    private String mensajeEscenarioNoSeleccionado= "No se ha seleccionado el esceneraio de simulacion ";
    private String tituloAvisoEscenSinRobotsDefinidos= "Escenario sin Robots definidos";
    private String mensajeEscenarioSinRobots= "No se han definido Robots en el escenario ";
    private String recomendacionDefinirRobots= " Definir Robots y Victimas con el boton derecho para poder guardar el escenario ";
    private Map<String, JLabel> tablaEntidadesEnEscenario;
    private ArrayList <JLabel> listaEntidadesEnEscenario;
    private JPanel panelVisor;
    JLabel entidadSeleccionada=null;
    private WidgetAction moveAction = ActionFactory.createMoveAction ();
    private Point ultimoPuntoClic ;
    private boolean intencionUsuarioCrearRobot;
    private boolean intencionUsuarioCrearVictima;
    private boolean entidadSeleccionadaParaMover;
    private boolean escenarioActualAbierto = false ;
    private int numeroRobots, mumeroVictimas;
    private volatile GestionEscenariosSimulacion gestionEscComp;
    private volatile EscenarioSimulacionRobtsVictms escenarioActualComp;
    private volatile PersistenciaVisualizadorEscenarios persistencia;
    private String modeloOrganizativo;
    private String identEquipoActual;
    private VisorCreacionEscenarios1 visorEditorEscen;
    private boolean visorControlSimuladorIniciado;
    private String idUltimoEscenarioAbierto;
//    private ultimoEscenarioAbierto ;
    
    public  ControladorEditorEntornosSimulacion (NotificadorInfoUsuarioEdicionEscenarios notificadorInfoUsuarioSimulador){
        notifEvts=notificadorInfoUsuarioSimulador;
    }
    public void initModelosYvistas(){
       String  directorioPersistencia = VocabularioRosace.NombreDirectorioPersistenciaEscenarios+File.separator;
//            VisorControlSimuladorRosace visorSc;
             persistencia= new PersistenciaVisualizadorEscenarios();
             gestionEscComp= new GestionEscenariosSimulacion();
            gestionEscComp.setIdentsEscenariosSimulacion(persistencia.obtenerIdentsEscenarioSimulacion(directorioPersistencia));
                try {
                    gestionEscComp = new GestionEscenariosSimulacion();
                    gestionEscComp.setIdentsEscenariosSimulacion(persistencia.obtenerIdentsEscenarioSimulacion(directorioPersistencia));
//        escenarioActualComp = gestionEscComp.crearEscenarioSimulacion();
//                    visorControlSim = new VisorControlSimuladorRosace(this);
                    visorEditorEscen= new VisorCreacionEscenarios1(this);
                   
             
//                    persistencia= new PersistenciaVisualizadorEscenarios();
//                    visor.setPersistencia(persistencia);
//                    visor.setGestorEscenarionComp(gestionEscComp);
//                    visor.setEscenarioActualComp(gestionEscComp.crearEscenarioSimulacion());
//                    visor.setIdentEquipoActual()
//                    visor.actualizarInfoEquipoEnEscenario();
//                    visorControlSim.setVisible(true);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                     
    }

//  public  void peticionComenzarSimulacion(String identEquipoActual, int intervaloSecuencia) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    if ( escenarioActualComp ==null)visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido,mensajeEscenarioNoDefinido,recomendacionDefinirEscenario);
//    else if (identEquipoActual== null)visorControlSim.setIdentEquipo(escenarioActualComp.getIdentEscenario());
//    else if (intervaloSecuencia <=0)visorControlSim.solicitarDefinicionItervaloSecuencia();
//        else notifEvts.sendPeticionSimulacionSecuenciaVictimasToRobotTeam(intervaloSecuencia);
//    }

  public  void peticionCrearEscenario() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      escenarioActualComp = gestionEscComp.crearEscenarioSimulacion();
//      visorEditorEscen.setEscenarioActualComp(escenarioActualComp);
      visorEditorEscen.visualizarEscenario(escenarioActualComp);
      visorEditorEscen.setVisible(true);
  }

   public  void peticionAbrirEscenario() {
//        throw new UnsupportedOperationException("Not supported yet."); 
    File ficheroSeleccionado=   visorEditorEscen.solicitarSeleccionFichero(directorioPersistencia);
    if (ficheroSeleccionado==null)visorEditorEscen.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado,recomendacionDefinirEscenario);
    else{
        escenarioActualComp = persistencia.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getAbsolutePath());
        escenarioActualComp.setGestorEscenarios(gestionEscComp);
        identEquipoActual=escenarioActualComp.getIdentEscenario();
        visorEditorEscen.setIdentEquipo(identEquipoActual);
        identsRobotsEquipo=escenarioActualComp.getListIdentsRobots();
        if(escenarioActualAbierto){
            visorEditorEscen.setVisible(false);
            escenarioActualAbierto=false;
        }
//        if( identsRobotsEquipo!=null) visorControlSim.visualizarIdentsEquipoRobot(identsRobotsEquipo);
    }
    }

   public  void peticionEliminarEscenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//  public   void peticionPararRobot(String identRobotSeleccionado) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }


// private void consejoUsuario(String mensajeConsejo, String recomendacion) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//     visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido,mensajeConsejo,recomendacion);
// }
     public static void main(String args[]) {
        
        new ControladorEditorEntornosSimulacion(new NotificadorInfoUsuarioEdicionEscenarios("prueba1", "agente2")).initModelosYvistas();
      
     }

    void peticionPararSimulacion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void peticionGuardarEscenario() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    // si no hay un escenario actual definido indicamos al usuario que no hay escenario definido
    // si en el escenario a guardar no hay robots ni victimas se lo decimos 
        if (escenarioActualComp.getNumRobots()<=0)visorEditorEscen.visualizarConsejo(tituloAvisoEscenSinRobotsDefinidos, mensajeEscenarioSinRobots, recomendacionDefinirRobots);
        else{
            int respuesta =visorEditorEscen.confirmarPeticionGuardarEscenario("Se va a guardar el escenario : ");
            if (respuesta==JOptionPane.OK_OPTION)persistencia.guardarInfoEscenarioSimulacion(directorioPersistencia, escenarioActualComp);
//            else if (respuesta==JOptionPane.)
//            visorControlSim.visualizarIdentsEquipoRobot(escenarioActualComp.getListIdentsRobots());
//            visorControlSim.setIdentEquipo(escenarioActualComp.getIdentEscenario());
        } 
    }

    void peticionMostrarEscenarioActual() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    if (escenarioActualAbierto)visorEditorEscen.setVisible(true);
    else if ( escenarioActualComp ==null)visorEditorEscen.visualizarConsejo(tituloAvisoEscenarioNoDefinido,mensajeEscenarioNoDefinido,recomendacionDefinirEscenario);
    else{
        visorEditorEscen.visualizarEscenario(escenarioActualComp);
        escenarioActualAbierto=true;
    }
    }

    boolean abrirVisorConEscenario(String identFicheroEscenarioSimulacion) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    if(!visorControlSimuladorIniciado){
        initModelosYvistas();
       escenarioActualComp= persistencia.obtenerInfoEscenarioSimulacion(identFicheroEscenarioSimulacion);
       if (escenarioActualComp ==null)return false ;
        visorEditorEscen.setIdentEquipo(identFicheroEscenarioSimulacion);
//        visorControlSim.visualizarIdentsEquipoRobot(escenarioActualComp.getListIdentsRobots());
        visorControlSimuladorIniciado=true;
    }
    return true;
    }

//    void peticionVisualizarIdentsRobots(ArrayList identList) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//   if (identList!=null)visorControlSim.visualizarIdentsEquipoRobot(identList);
//    }

    void cerrarEditor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void abrirEditor() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
// se deberia abrir  con el ultimo escenario visualizado
        if (escenarioActualComp !=null){
            
            visorEditorEscen.visualizarEscenario(escenarioActualComp);
        }
            
          visorEditorEscen.setVisible(true);  
    
    }

    EscenarioSimulacionRobtsVictms dameEscenario(String identEscenario) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return persistencia.obtenerInfoEscenarioSimulacion(identEscenario);
    
    }
}
