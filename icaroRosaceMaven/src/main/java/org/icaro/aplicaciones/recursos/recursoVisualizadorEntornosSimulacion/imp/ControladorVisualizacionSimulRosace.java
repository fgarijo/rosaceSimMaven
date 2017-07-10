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
import org.icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
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
public class ControladorVisualizacionSimulRosace {
     private NotificadorInfoUsuarioSimulador notifEvts;
    private int intervaloSecuencia = 2000; // valor por defecto. Eso deberia ponerse en otro sitio
    private int numMensajesEnviar = 3;
    private boolean primeraVictima = true;
    private VisorControlSimuladorRosace visorControlSim;
    private ArrayList identsRobotsEquipo ;
    private javax.swing.JLabel jLabelAux;
    private String directorioTrabajo;
     private String tituloVentanaVisor = "ROSACE Scenario Visor";
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
    private String tituloAvisoIntentosSobrepsados= "Intentos para obtener Escenario sobrapasados";
    private String mensajeEscenarioNoDefinido= "El esceneraio de simulaci�n no esta definido ";
    private String recomendacionDefinirEscenario= " Abrir un escenario con el menu de edicion o crear un escenario nuevo";
    private String caracteristicasFicheroSimulacion;
    private String mensajeEscenarioNoSeleccionado= "No se ha seleccionado el esceneraio de simulaci�n ";
    private String tituloAvisoEscenSinRobotsDefinidos= "Escenario sin Robots definidos";
    private String mensajeEscenarioSinRobots= "No se han definido Robots en el escenario ";
    private String recomendacionDefinirRobots= " Definir Robots y Victimas con el bot�n derecho para poder guardar el escenario ";
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
    private volatile EscenarioSimulacionRobtsVictms escenarioEdicionComp, escenarioSimulComp;
    private volatile PersistenciaVisualizadorEscenarios persistenciaLocal;
    private String modeloOrganizativo;
    private String identEquipoActual;
    private VisorCreacionEscenarios1 visorEditorEscen;
    private VisorMovimientoEscenario visorMovimientoEscen;
    private ItfUsoRecursoPersistenciaEntornosSimulacion itfPersistenciaSimul;
    private boolean visorControlSimuladorIniciado;
    private boolean visorMovientoIniciado;
//    private boolean escenarioSimulacionAbierto;
    private boolean peticionObtenerEscenarioValido;
    private boolean escenarioValidoObtenido;
    private boolean robotsVisualizados;
    private boolean victimasVisualizadas;
    private boolean simulacionEnCurso;
    public static final int MOD_EDICION=0; 
    public static final int MOD_SIMULACION=1;
    public static final int FICHERO_SELECCIONADO = 0;
    public static final int USUARIO_CANCELA_SELECCION = 1;
    public static final int INTENTOS_SELECCION_SOBREPASADOS = 2;
    public static final int FICHERO_VALIDO = 3;
//    private boolean simulando;
//    private boolean editando;
    private javax.swing.JFileChooser jFileChooser1;
    private int maxIntentosPeticionSeleccionEscenario = 2;
    private int resultadoObtencionFicheroSimulacion;
    private ArrayList identsVictims;
    private boolean avisoSeleccionVictima;
    private MemComunControladores memComunControladores;
    private ItfUsoRecursoTrazas trazas;
    
    public  ControladorVisualizacionSimulRosace (NotificadorInfoUsuarioSimulador notificadorInfoUsuarioSimulador){
        notifEvts=notificadorInfoUsuarioSimulador;
        
    }
    public void setIftRecPersistencia(ItfUsoRecursoPersistenciaEntornosSimulacion itfPersisSimul){
        itfPersistenciaSimul = itfPersisSimul;
    }
    public void setVisorControlSimulador(VisorControlSimuladorRosace visorControl){
        visorControlSim = visorControl;
    }
    public void setFincasoSimulacion(){
        simulacionEnCurso=false;
    }
    public  void setItfRecursoTrazas(ItfUsoRecursoTrazas trazasOrg) {
    this.trazas=trazasOrg;
    }
    public void initModelosYvistas(){
              visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
              visorControlSim.setVisible(true);               
    }
  public synchronized void peticionComenzarSimulacion(String identEscenarioActual, int intervaloSecuencia) {
      if ( this.memComunControladores.getescenarioSimulacionAbierto()){
          if(memComunControladores.getcambioEnEscenarioSimulacion()){
              escenarioSimulComp=memComunControladores.getescenarioMovimiento();
              memComunControladores.setcambioEnEscenarioSimulacion(false);
          }
          if (intervaloSecuencia <=0)visorControlSim.solicitarDefinicionItervaloSecuencia();
          notifEvts.sendPeticionSimulacionSecuenciaVictimasToRobotTeam(escenarioSimulComp,intervaloSecuencia);
          simulacionEnCurso= true;
      }else
//          visorControlSim.visualizarConsejo("Seleccion de Fichero ", "Seleccionar un fichero de modelo organizativo: "+this.memComunControladores.getmodorganizDefinidoEnOrg()
//            + " y numero de Robots = : "+ this.memComunControladores.getnumRobotsdefsEnOrganizacion(), "Si no tiene ninguno de estas caracteristicas cree uno nuevo ");
      if(memComunControladores.getescenarioEdicionAbierto()){
//            visorEditorEscen.setVisible(true);
            if (  visorControlSim.solicitarConfirmacion("Tiene un escenario abierto quiere utilizarlo como escenario de simulacion ??")){
              // enviar el fichero al controlador para que valide el numero de robots
//                this.notifEvts.sendInfoEscenarioSeleccionado(memComunControladores.getescenarioEdicion());
                escenarioSimulComp=memComunControladores.getescenarioEdicion();
                this.notifEvts.sendPeticionSimulacionSecuenciaVictimasToRobotTeam(escenarioSimulComp,intervaloSecuencia);
                simulacionEnCurso= true;
            }
      }else {
          visorControlSim.visualizarRecomenSeleccionFicheroSimulacion(tituloAvisoEscenarioNoDefinido,
                memComunControladores.getmodorganizDefinidoEnOrg(),memComunControladores.getnumRobotsdefsEnOrganizacion() );
          int resultadoObtenrEscenario = this.obtenerEscenarioSimulacion(modeloOrganizativo, numeroRobots);
         if( resultadoObtenrEscenario== this.FICHERO_VALIDO){
             this.notifEvts.sendPeticionSimulacionSecuenciaVictimasToRobotTeam(escenarioSimulComp,intervaloSecuencia);
             simulacionEnCurso= true;
         }
//         else if (resultadoObtenrEscenario== this.INTENTOS_SELECCION_SOBREPASADOS)
//            visorControlSim.visualizarRecomenSeleccionFicheroSimulacion(tituloAvisoIntentosSobrepsados,
//                memComunControladores.getmodorganizDefinidoEnOrg(),memComunControladores.getnumRobotsdefsEnOrganizacion() );  
        }
      }
  
  public  void peticionPararRobot(String identRobotSeleccionado) {
        
    }


 private void consejoUsuario(String mensajeConsejo, String recomendacion) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido,mensajeConsejo,recomendacion);
 }
     public static void main(String args[]) {
        
        new ControladorVisualizacionSimulRosace(new NotificadorInfoUsuarioSimulador("prueba1", "agente2")).initModelosYvistas();
      
     }

public  void peticionPararSimulacion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

public  void peticionGuardarEscenario(EscenarioSimulacionRobtsVictms escenarioComp) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    // si no hay un escenario actual definido indicamos al usuario que no hay escenario definido
    // si en el escenario a guardar no hay robots ni victimas se lo decimos
        escenarioEdicionComp = escenarioComp;
        if (escenarioEdicionComp.getNumRobots()<=0)visorEditorEscen.visualizarConsejo(tituloAvisoEscenSinRobotsDefinidos, mensajeEscenarioSinRobots, recomendacionDefinirRobots);
        else{
            int respuesta =visorEditorEscen.confirmarPeticionGuardarEscenario("Se va a guardar el escenario : ");
            if (respuesta==JOptionPane.OK_OPTION){
                
                 ArrayList<String> robotNombres = escenarioComp.getListIdentsRobots();
             for (String ideRobot:robotNombres){
//                 String ideRobot = (String)robtIter.next();
              RobotStatus1 infoRobot = (RobotStatus1) escenarioComp.getRobotInfo(ideRobot);
              List<RobotCapability> capacidades=infoRobot.getRobotCapabilities();
                 System.out.println("Desde peticion Guardar Lista de capacidades a guardar del robot  : " + ideRobot+"Capacidades : "+ capacidades.toString() );
             }
             System.out.println("Desde peticion Guardar Numero de Robots  : " + escenarioEdicionComp.getNumRobots()+" Numero de victimas : "+ escenarioEdicionComp.getNumVictimas());
//             if (itfPersistenciaSimul==null)   
//                persistenciaLocal.guardarInfoEscenarioSimulacion(directorioPersistencia, escenarioEdicionComp);
//             else 
                 try {
                     itfPersistenciaSimul.guardarInfoEscenarioSimulacion(escenarioEdicionComp);
                 } catch (Exception ex) {
                     Exceptions.printStackTrace(ex);
                 }
                if ( peticionObtenerEscenarioValido&&!escenarioValidoObtenido){
                    // se envia el escenario al agente controlador que puede estar esperandolo
                    notifEvts.sendInfoEscenarioSeleccionado(escenarioEdicionComp);
                }
            }
            if (!visorMovientoIniciado){
            visorControlSim.visualizarIdentsEquipoRobot(escenarioComp.getListIdentsRobots());
            visorControlSim.setIdentEscenarioActual(escenarioComp.getIdentEscenario());
            }
        } 
    }
public  void peticionMostrarEscenarioMovimiento(EscenarioSimulacionRobtsVictms escenarioComp) {
    // Esta peticion viene del agente controlador y supone que el escenario es valido
    if(escenarioComp!=null){
        String identEscenarioAmostrar = escenarioComp.getIdentEscenario();
        try {
            if(this.memComunControladores.getescenarioSimulacionAbierto()){
                // verificamos si el escenario a visualizar esdiferente
                escenarioSimulComp= memComunControladores.getescenarioSimulacion();
                if(identEscenarioAmostrar.equals(this.escenarioSimulComp.getIdentEscenario())){
                     visorMovimientoEscen=memComunControladores.getvisorMovimiento();
//                     visorMovimientoEscen.cambiarPosicionRobotsEscenario(escenarioComp.getRobots());
//                     visorMovimientoEscen.actualizarEscenario(escenarioComp);
                     visorMovimientoEscen.cambiarPosicionRobotsEscenario(escenarioComp.getRobots());
                    visorMovimientoEscen.inicializarEstatusVictimas(escenarioComp.getVictims().entrySet());
                     
                }else {// Se trata de un escenario distinto al que se estaba trabajando
//                    escenarioSimulComp= escenarioComp;
//                    memComunControladores.setescenarioMovimiento(escenarioComp);
//                    visorMovimientoEscen.actualizarEscenario(escenarioSimulComp);
                    visorMovimientoEscen.setVisible(false);
                    visorMovimientoEscen= new VisorMovimientoEscenario(escenarioComp);
                       }
                  
            }else // abrir un visor con el escenario
//                try {
                    
                    visorMovimientoEscen= new VisorMovimientoEscenario(escenarioComp);
                    
//                }
//                catch (Exception ex) {
//                    Exceptions.printStackTrace(ex);
//                }
            visorMovimientoEscen.visualizarEscenario();
         escenarioSimulComp= escenarioComp;
          visorMovientoIniciado = true;
          this.memComunControladores.setescenarioSimulacionAbierto(true);
          this.memComunControladores.setescenarioSimulacion(escenarioSimulComp);
          this.memComunControladores.setevisorMovimiento(visorMovimientoEscen);
          this.actualizarEscenarioEnVisorControlSim();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
private void actualizarEscenarioEnVisorControlSim(){
    
    visorControlSim.setIdentEscenarioActual(escenarioSimulComp.getIdentEscenario());
    visorControlSim.visualizarIdentsEquipoRobot(escenarioSimulComp.getListIdentsRobots());
    visorControlSim.visualizarIdentsVictimas(escenarioSimulComp.getListIdentsVictims());
    
    
    
}
public  void peticionMostrarEscenarioActual() {
 boolean escenarioSimulacionDefinido= escenarioSimulComp != null;
 boolean escenarioEdicionAbierto = memComunControladores.getescenarioEdicionAbierto();
 boolean escenarioSimulacionAbierto = memComunControladores.getescenarioSimulacionAbierto();
    if (escenarioSimulComp != null)visorMovimientoEscen.setVisible(true);
    else if(escenarioEdicionAbierto){
//            visorEditorEscen.setVisible(true);
            if (  visorControlSim.solicitarConfirmacion("Tiene un escenario abierto quiere utilizarlo como escenario de simulacion ??")){
              // enviar el fichero al controlador para que valide el numero de robots
                this.notifEvts.sendInfoEscenarioSeleccionado(memComunControladores.getescenarioEdicion());
            }// No quiere usar el escenario de edicion como escenario de simulacion
           // Decir al usuario que no tiene un escenario definido
           // Para iniciar simulacion necesita definir un escenario con xx robots
         }
    else{
       
        visorControlSim.visualizarRecomenSeleccionFicheroSimulacion(tituloAvisoEscenarioNoDefinido,
                memComunControladores.getmodorganizDefinidoEnOrg(),memComunControladores.getnumRobotsdefsEnOrganizacion() );
    }
         
    }

public  boolean abrirVisorConEscenario(String identFicheroEscenarioSimulacion) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    if(!this.memComunControladores.getescenarioSimulacionAbierto()){
//        initModelosYvistas();
        if (identFicheroEscenarioSimulacion==null){ // Se abre el visor sin 
            visorEditorEscen.setVisible(true);
        }
        if(identFicheroEscenarioSimulacion !=null)
        try {
            escenarioEdicionComp= itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(identFicheroEscenarioSimulacion);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
       if (escenarioEdicionComp ==null){
           this.visorControlSim.visualizarConsejo("fichero no encontrado ", "verifique el identificador del fichero ", "Puede abrir  un escenario existente o crear un nuevo escenario");
           return false ;
       }
        visorControlSim.setIdentEscenarioActual(identFicheroEscenarioSimulacion);
        visorControlSim.visualizarIdentsEquipoRobot(escenarioEdicionComp.getListIdentsRobots());
        visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
        visorControlSimuladorIniciado=true;
        visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
    }
    return true;
    }
public boolean abrirVisorControlSimConEscenario(EscenarioSimulacionRobtsVictms escenarioComp){
    
    if (escenarioComp ==null){
           this.visorControlSim.visualizarConsejo("Escenario nulo ", "verifique el identificador del fichero ", "Revisar el nombre del fichero en la organizacion o seleccionar nuevo fichero");
           return false ;
       }
    escenarioSimulComp=escenarioComp;
    this.visorControlSim.setIntervaloEnvioMensajesDesdeCC(intervaloSecuencia);
    this.actualizarEscenarioEnVisorControlSim();
    return true;
}
 public  boolean abrirVisorMovimientoConEscenario(String identFicheroEscenarioSimulacion) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    if(!visorMovientoIniciado){
        try {
            //        initModelosYvistas();
            escenarioEdicionComp= itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(identFicheroEscenarioSimulacion);
            if (escenarioEdicionComp ==null){
                this.visorControlSim.visualizarConsejo("fichero no encontrado ", "verifique el identificador del ficero", null);
                return false ;
            }
            visorMovimientoEscen = new VisorMovimientoEscenario(escenarioEdicionComp);
            visorMovimientoEscen.visualizarEscenario();
//            visorControlSim.visualizarIdentsEquipoRobot(escenarioActualComp.getListIdentsRobots());
            visorControlSimuladorIniciado=true;
            visorMovientoIniciado = true;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    return true;
    }
 public VisorMovimientoEscenario getVisorMovimiento(){
    if(visorMovientoIniciado) return visorMovimientoEscen;
    else return null;
 } 

public void peticionVisualizarIdentsRobots(ArrayList identList) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   // si los identificadores recibidos son diferentes a los que hay en el escenario se cambian los del escenario
   // esto es un poco raro pero resuelve temporalmente el problema
    // cambiar los nombres en el computacional y tambien las etiquetas en el visualizador
    
    if (identList!=null){
        visorControlSim.visualizarIdentsEquipoRobot(identList);
        visorControlSim.setIdentEscenarioActual(escenarioEdicionComp.getIdentEscenario());
        this.robotsVisualizados = true;
        if (escenarioEdicionComp.getListIdentsRobots().size()!= identList.size() )
            visorControlSim.visualizarConsejo("Imposible cambiar los identificadores" , "El numero de robots en el escenario actual y el recibido es diferente",
            "revisar las definiciones de los escenarios");
        
        else{
        this.escenarioEdicionComp.renombrarIdentRobts(identList);
         ArrayList<String> robotNombres = escenarioEdicionComp.getListIdentsRobots();
             for (String ideRobot:robotNombres){
//                 String ideRobot = (String)robtIter.next();
              RobotStatus1 infoRobot = (RobotStatus1) escenarioEdicionComp.getRobotInfo(ideRobot);
//              List<RobotCapability> capacidades=infoRobot.getRobotCapabilities();
                 System.out.println("Desde peticionVisualizarIdentsRobots  nuevo ident Robot : " + ideRobot );
             } 
             Object[] victimasIdents = escenarioEdicionComp.getVictims().keySet().toArray();
             for( int i=0; i<victimasIdents.length;i++){
                 String idVictima = (String)victimasIdents[i];
                 System.out.println("Desde peticionVisualizarIdentsRobots  nuevo ident Vicitima : " + idVictima );
             }
             
        visorEditorEscen.visualizarEscenario(escenarioEdicionComp);
        }
    }
    }
public  void peticionSalvarVictima() {
    // verificar si hay un escenario de simulacion seleccionado valido y los robots estan visualizados
    // obtener las victimas del escenario, se visualizan y se le pide al usuario que seleccione una
    // situaciones a anotar : escenario valido seleccionado, robots en el escenario visualizados,victimas visualizadas 
    if(!memComunControladores.getescenarioSimulacionAbierto())
        if(memComunControladores.getescenarioEdicionAbierto()){
//            visorEditorEscen.setVisible(true);
            if (  visorControlSim.solicitarConfirmacion("Tiene un escenario abierto quiere utilizarlo como escenario de simulacion ??")){
              // enviar el fichero al controlador para que valide el numero de robots
                this.notifEvts.sendInfoEscenarioSeleccionadoValido(memComunControladores.getescenarioEdicion());
            }// No quiere usar el escenario de edicion como escenario de simulacion
           // Decir al usuario que no tiene un escenario definido
           // Para iniciar simulacion necesita definir un escenario con xx robots
         }
//                visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado,recomendacionDefinirEscenario);
        else visorControlSim.visualizarRecomenSeleccionFicheroSimulacion(tituloAvisoEscenarioNoDefinido,
                memComunControladores.getmodorganizDefinidoEnOrg(),memComunControladores.getnumRobotsdefsEnOrganizacion() );
    else{ //if(robotsVisualizados&&escenarioValidoObtenido){
        if(!victimasVisualizadas){
            identsVictims =  escenarioSimulComp.getListIdentsVictims();
            visorControlSim.visualizarIdentsVictimas(identsVictims);
        }
         visorControlSim.visualizarConsejo("Seleccion de Victima", "Debe seleccionar la victima a rescatar ", "Haga doble clic en el identificador de la victima");
    }
      System.out.println("Desde peticionVisualizarIdentsRobots   : " + escenarioValidoObtenido + robotsVisualizados);   
}
 public boolean verificarCaracteristicasEscenarioAbierto (String orgModelo,int numRobots){
         return (this.numeroRobots==numRobots) &&(this.modeloOrganizativo.equalsIgnoreCase(orgModelo));
    }
 public boolean verificarCaracteristicasEscenarioSeleccionado ( File ficheroSeleccionado,String orgModelo,int numRobots ){
         try {
             escenarioSimulComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());
         } catch (Exception ex) {
             Exceptions.printStackTrace(ex);
         }
        if(escenarioSimulComp == null ) return false;
     identEquipoActual=escenarioSimulComp.getIdentEscenario();
        numeroRobots = escenarioSimulComp.getNumRobots();
        modeloOrganizativo= escenarioSimulComp.getmodeloOrganizativo();
       if(this.numeroRobots==numRobots &&this.modeloOrganizativo.equalsIgnoreCase(orgModelo)){
           // se envia notificacion al agente controlador con el computacional obtenido
        escenarioSimulComp.setGestorEscenarios(gestionEscComp);  
        escenarioValidoObtenido = true;
        return true;
       }else {
           visorControlSim.visualizarConsejo("Fichero seleccionado No valido ", "El modelo organizativo del fichero seleccionado: "+orgModelo
            + " o el  numero de Robots = : "+ numRobots + " No coinciden ", "Seleccione otro fichero o  cree uno nuevo ");
           return false;
        }
 }

 public   boolean hayEscenarioAbierto() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return escenarioActualAbierto;
    }

 public  int obtenerEscenarioSimulacion(String modOrganizativo, int numRobots) {
     // Posibles resultados:
     //1:se obtiene un escenario  valido
     //2:se sobrepasa el numero de intentos para obtener el fichero
     //3:el usuario cancela la peticion para seleccionar el escenario
    this.peticionObtenerEscenarioValido=true;
    int numeroIntentos=0;
    int resultadoSeleccion=-1;
    boolean nuevoIntentoDeObtencion=true;
     modeloOrganizativo=modOrganizativo;
     numeroRobots=numRobots ;
    this.memComunControladores.setmodorganizDefinidoEnOrg(modOrganizativo);
    this.memComunControladores.setnumRobotsdefsEnOrganizacion(numRobots);
    EscenarioSimulacionRobtsVictms escenario = null;
    
    while( numeroIntentos<=this.maxIntentosPeticionSeleccionEscenario&&nuevoIntentoDeObtencion){
        resultadoSeleccion = visorControlSim.selecciondeFichero();
        if (resultadoSeleccion==this.USUARIO_CANCELA_SELECCION)nuevoIntentoDeObtencion=false;
        else 
            if(resultadoSeleccion==this.FICHERO_SELECCIONADO ){
            escenario = obtenerComputacionalDePersistencia(visorControlSim.getUltimoFicheroEscenarioSeleccionado());
            if (escenario ==null )visorControlSim.visualizarConsejo("Fichero seleccionado Nulo ", "Se debe eleccionar un fichero con modelo organizativo : "+modOrganizativo
                + " y numero  = : "+ numRobots , "Seleccione otro fichero o  cree uno nuevo "); 
            else if(  escenario.getmodeloOrganizativo().equals(modOrganizativo)
                       && escenario.getNumRobots()==numRobots){
                nuevoIntentoDeObtencion=false;
                resultadoSeleccion = this.FICHERO_VALIDO;
            }
                else
                visorControlSim.visualizarConsejo("Fichero seleccionado No valido ", "El modelo organizativo del fichero seleccionado debe ser: "+modOrganizativo
                    + " y el  numero de Robots debe ser de = : "+ numRobots ," pero el fichero selecionado no tiene estas caracter�sticas. Seleccione otro fichero o  cree uno nuevo "); 
            }
            numeroIntentos ++;
    }
    if(resultadoSeleccion == this.FICHERO_VALIDO){
        escenarioSimulComp= escenario;
        escenarioSimulComp.setGestorEscenarios(gestionEscComp);
        identEquipoActual=escenarioSimulComp.getIdentEscenario();
        this.memComunControladores.setescenarioMovimiento(escenarioSimulComp);
        this.memComunControladores.setcambioEnEscenarioSimulacion(true);
        }else if(numeroIntentos>=maxIntentosPeticionSeleccionEscenario){
            visorControlSim.visualizarConsejo("Se ha sobrepsado el numero de intentos de seleccion ", "El modelo organizativo del fichero para simular debe ser: "+modOrganizativo
                    + " y el  numero de Robots debe ser de = : "+ numRobots, " pero no se ha seleccionado ningun fichero con esas caracteristicas, Intentelo de nuevo o cree uno nuevo "); 
            resultadoSeleccion=this.INTENTOS_SELECCION_SOBREPASADOS;
        }
    return resultadoSeleccion;
}
    public boolean peticionConfirmacionInformacion(String preguntaAconfirmar){
        return visorControlSim.solicitarConfirmacion(preguntaAconfirmar);
    }
    public File peticionSeleccionarEscenario(){
     // suponemos que hay escenarios creados , auque lo podemos verificar de nuevo   
         int numerointentos = 0;
         File ficheroSeleccionado = null;
       while ( numerointentos<maxIntentosPeticionSeleccionEscenario && ficheroSeleccionado==null ){
         ficheroSeleccionado=   visorControlSim.solicitarSeleccionFichero();
          if (ficheroSeleccionado==null)visorControlSim.visualizarConsejo(tituloAvisoEscenarioNoDefinido, mensajeEscenarioNoSeleccionado,recomendacionDefinirEscenario);
         numerointentos++;
        }
       return ficheroSeleccionado;
//       this.notifEvt.informaraOtroAgenteReactivo(new InfoContEvtMsgAgteReactivo("escenarioDefinidoPorUsuario", escenarioActual), identAgenteaReportar);
    }

//    void notificacionUsuario(String textoNoticiacion) {
//        visorControlSim.visualizarConsejo("Seleccion de Fichero ", "Seleccionar un fichero de modelo organizativo: "+modOrganizativo
//            + " y numero de Robots = : "+ numRobots, "Si no tiene ninguno de estas caracteristicas cree uno nuevo ");
//    }
    private EscenarioSimulacionRobtsVictms obtenerComputacionalDePersistencia( File ficheroSeleccionado){
//    File ficheroSeleccionado=   visorEditorEscen.solicitarSeleccionFichero();
    EscenarioSimulacionRobtsVictms escenarioComp = null;
    if (ficheroSeleccionado==null)visorEditorEscen.visualizarConsejo("Fichero Seleccionado null", mensajeEscenarioNoSeleccionado,recomendacionDefinirEscenario);
    else{
           try {
               escenarioComp = itfPersistenciaSimul.obtenerInfoEscenarioSimulacion(ficheroSeleccionado.getName());   
           } catch (Exception ex) {
               Exceptions.printStackTrace(ex);
               System.out.println("Desde peticion Abrir escenario . No se encuentra el fichero Ident Fichero : " + ficheroSeleccionado.getAbsolutePath() );
           }   
        }
    return escenarioComp;
}

 public   void notifRecomendacionUsuario(String titulo, String motivo, String recomendacion) {
       visorControlSim.visualizarConsejo(titulo, motivo,recomendacion);
    }

 public   void victimaSeleccionadaParaSimulacion(String identVictimaSeleccionada) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     if (!avisoSeleccionVictima){
       this.visorControlSim.solicitarConfirmacion(" Se va a proceder a simular salvar a la victima : "+ identVictimaSeleccionada);
    }
        this.avisoSeleccionVictima=true;
    this.notifEvts.sendPeticionSimulacionVictimToRobotTeam(escenarioSimulComp,identVictimaSeleccionada);
    simulacionEnCurso= true;
//        this.notifEvts.sendPeticionInicioSimulacion(escenarioSimulComp,VocabularioRosace.modoSimulacionPeticionesUsuario,identVictimaSeleccionada);
    }

public    void peticionCambiarPosicionRobot(String identRobot, Integer coordX, Integer coordY) {
       this.visorMovimientoEscen.cambiarPosicionRobot(identRobot, coordX, coordY);
    }

public  void peticionMostrarVictima(String identVictima,String estadoVictima)throws Exception{ 
     switch (estadoVictima) {
            case "esperandoRescate":
                this.visorMovimientoEscen.cambiarIconoEntidad(identVictima,VisorMovimientoEscenario.IMAGEmujer);
                break;
            case "asignada":
                this.visorMovimientoEscen.cambiarIconoEntidad(identVictima,VisorMovimientoEscenario.IMAGEmujerAsignada);
                break;
            case "reAsignada":
                this.visorMovimientoEscen.cambiarIconoEntidad(identVictima,VisorMovimientoEscenario.IMAGEmujerReAsignada);
                break;
            case "rescatada":
                this.visorMovimientoEscen.cambiarIconoEntidad(identVictima,VisorMovimientoEscenario.IMAGEmujerRes);
                break;
                default:
             throw new IllegalArgumentException("Estado de la victima no valido : " + estadoVictima);
        }  
    }

 public   void setMemComunControladores(MemComunControladores memoriaComunControladores) {
        this.memComunControladores=memoriaComunControladores;
    }

 public   void finCasoSimulacion() {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    this.notifRecomendacionUsuario("Fin simulacion", "Caso de Simulacion finalizado","Para ejecutar otro caso seleccione un nuevo escenario o cambie los parametros del escenario actual");
   if (this.visorControlSim.solicitarConfirmacion("Caso de Simulacion finalizado. Desea ejecutar otro caso con el mismo escenario ?" ))
   { this.simulacionEnCurso=false;
   this.visorControlSim.setVisible(true);
//        notifEvts.sendInfoEscenarioSeleccionadoValido(escenarioSimulComp);
   }
   else
//     this.notifRecomendacionUsuario("Ejecuci�n de nuevo caso", "","Para ejecutar otro caso con un nuevo escenario abra un nuevo escenario ");
   if (this.visorControlSim.solicitarConfirmacion("Desea abrir un  nuevo escenario  ?" )) {
   if((this.obtenerEscenarioSimulacion(memComunControladores.getmodorganizDefinidoEnOrg(),memComunControladores.getnumRobotsdefsEnOrganizacion())== this.FICHERO_VALIDO))
   {
//     Ya tiene un escenario de simulacion valido, pero los robots no estan actualizados
       // cerrar el escenario de simulacion que tenga abierto y poner en primer plano
       notifEvts.sendInfoEscenarioSeleccionadoValido(escenarioSimulComp);
   }else{// No ha seleccionado ningun fichero 
       visorMovimientoEscen.setVisible(false);
       visorControlSim.inicializarInfoEscenarioSimul();
       escenarioSimulComp=null;
       this.memComunControladores.setescenarioSimulacion(escenarioSimulComp);
       this.memComunControladores.setescenarioSimulacionAbierto(false);
   } 
   }
 } 
   public  void peticionTerminarCasoSimulacion() {
       this.visorMovimientoEscen.setVisible(false);
        notifEvts.sendNotifAgteControlador(VocabularioRosace.peticionTerminarSimulacionUsuario);
    }

  public  void peticionPararRobot() {
        if (simulacionEnCurso){
            visorControlSim.visualizarConsejo("Seleccion de Robot a parar", "Debe seleccionar el robot ", "Haga doble clic en el identificador del Robot");
        }else
            visorControlSim.visualizarConsejo("Simulaci�n no iniciada", "Debe iniciar la simulaci�n y luego ", "seleccionar el robot que se debe parar");
       
    }

 public   void robotSeleccionadoParaParar(String identRobotSeleccionado) {
        if (simulacionEnCurso){
           notifEvts.sendPeticionPararAgente(identRobotSeleccionado);
        }else
            visorControlSim.visualizarConsejo("Simulaci�n no iniciada", "Debe iniciar la simulaci�n y luego ", "seleccionar el robot que se debe parar");      
    }

  public  void peticionSalirSimulador() {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // podria obtener una confirmacion el propio contolador, pero la delaga en el Gestor de la organizacion
        notifEvts.pedirTerminacionGestorOrganizacion();
    }
    void peticionActivarTrazas() {
        this.trazas.visualizacionDeTrazas(true);
    }
 public   void peticionDesactivarTrazas() {
        this.trazas.visualizacionDeTrazas(false);
    }

 
}
