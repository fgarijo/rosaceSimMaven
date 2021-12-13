/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ControlCenterGui2.java
 *
 * Created on 29-dic-2011, 20:42:57
 */
package icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp;

import icaro.aplicaciones.InfoSimulador.informacion.RobotStatus1;
import icaro.aplicaciones.InfoSimulador.informacion.Victim;
import icaro.aplicaciones.InfoSimulador.informacion.VocabularioRosace;
import java.awt.Component;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.animator.SceneAnimator;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author FGarijo
 */
public class VisorEditorEscenarios1 extends javax.swing.JFrame {

    /** Creates new form ControlCenterGui2 */
    private int intervaloSecuencia = 10000; // valor por defecto. Eso deberia ponerse en otro sitio
    private int numMensajesEnviar = 3;
    private boolean primeraVictima = true;
    private ArrayList identsRobotsEquipo ;
    private javax.swing.JLabel jLabelAux;
    private String directorioTrabajo;
    private String rutassrc = "src/";   //poner "src/main/java" si el proyecto de icaro se monta en un proyecto maven
//    private String rutaIconosEscenarios = "src/main/resources/iconosEscenarios/";
    private static  Image IMAGErobot,IMAGEmujer,IMAGEmujerRes ;
//    private String rutaPersistenciaEscenario = "\\src\\persistenciaEscenarios\\";
    private String directorioPersistencia = VocabularioRosace.NombreDirectorioPersistenciaEscenarios+File.separator;
    private String imageniconoHombre = "Hombre.png";
    private String imageniconoMujer = "Mujer.png";
    private String imageniconoMujerRescatada = "MujerRescatada.png";
    private String imageniconoHombreRescatado = "HombreRescatado.png";
    private String imageniconoRobot = "Robot.png";
    private final String modeloOrganizativoInicial = "SinDefinir";
    private Map<String, JLabel> tablaEntidadesEnEscenario;
    private ArrayList <JLabel> listaEntidadesEnEscenario;
    private volatile ArrayList <String> listaIdentsRobots;
    private volatile ArrayList <String> listaIdentsVictimas;
    private JPanel panelVisor;
    private ObjectScene scene;
    private LayerWidget layer;
    JLabel entidadSeleccionada=null;
    private WidgetAction moveAction = ActionFactory.createMoveAction ();
    private Point ultimoPuntoClic ;
    private SceneAnimator animator ;
    private boolean intencionUsuarioCrearRobot;
    private boolean intencionUsuarioCrearVictima;
    private boolean entidadSeleccionadaParaMover;
    private boolean escenarioGuardado;
    private boolean escenarioModificado;
    private int numeroRobots, numeroVictimas;
    private volatile GestionEscenariosSimulacion gestionEscComp;
    private  EscenarioSimulacionRobtsVictms escenarioActualComp;
    private ComponentMover moverComp;
    private ControladorGestionEscenarios controladorGestionEsc;
    private volatile PersistenciaVisualizadorEscenarios persistencia;
    private String modeloOrganizativo;
    private String identEscenario;
    private File ultimoFicheroEscenarioSeleccionado;
    private final int ENTIDAD_ROBOT=1;
    private final int ENTIDAD_VICTIMA=2;
    private final String ROBOT="Robot";
    private final String VICTIMA="Victima";
    private String numRobots = "Robts_";
    private String numVictims = "Victs_";
    private String orgModelo = "Org_";
    private String  orgModeloInicial = "SinDefinir";
    private String subIndiceEscRepetido = "_0";
    private String identEquipo ="Equipo1";

     public  VisorEditorEscenarios1(ControladorGestionEscenarios controlador) throws Exception {
//        super("visor Escenario ");
        initComponents();
        jTextFieldIdentEquipo.setVisible(false);
        jLabelIdentEquipo.setVisible(false);
        moverComp =new ComponentMover();
        moverComp.addMenuAcciones(jPopupMenuAcionEntidad);
        controladorGestionEsc = controlador;
//        directorioTrabajo = System.getProperty("user.dir");
        numeroRobots=0;  numeroVictimas=0;
//        tablaEntidadesEnEscenario = new HashMap<String, JLabel>();
        listaEntidadesEnEscenario = new ArrayList < >();
        listaIdentsRobots= new ArrayList < >();
        listaIdentsVictimas= new ArrayList < >();
    }

        public synchronized void cambiarIconoVictimaARescatada(String valor_idVictima) {
        System.out.println("se cambia el icono de la victima a rescatada: "+valor_idVictima );

    System.out.println("se cambia el icono de la victima a rescatada");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialogAvisoErrorDefNumEntidades = new javax.swing.JDialog();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPopupMenuAcionEntidad = new javax.swing.JPopupMenu();
        jMenuItemEliminar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGuardar = new javax.swing.JMenuItem();
        jPopupMenuAddEntidades = new javax.swing.JPopupMenu();
        jMenuItemAddRobot = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAddVictima = new javax.swing.JMenuItem();
        jFileChooser1 = new javax.swing.JFileChooser();
        jOptionPane1 = new javax.swing.JOptionPane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menu2 = new java.awt.Menu();
        menuBar2 = new java.awt.MenuBar();
        menu3 = new java.awt.Menu();
        menu4 = new java.awt.Menu();
        menuBar3 = new java.awt.MenuBar();
        menu5 = new java.awt.Menu();
        menu6 = new java.awt.Menu();
        menuBar4 = new java.awt.MenuBar();
        menu7 = new java.awt.Menu();
        menu8 = new java.awt.Menu();
        jTextFieldModeloOrganizacion = new javax.swing.JTextField();
        robotIcon = new javax.swing.JLabel();
        intervalNumRobots = new javax.swing.JTextField();
        victimaIcon1 = new javax.swing.JLabel();
        intervalNumVictimas = new javax.swing.JTextField();
        jTextFieldIdentEscenario = new javax.swing.JTextField();
        jButtonGuardarEscenario = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        jLabelOrganizacion = new javax.swing.JLabel();
        jLabelIdentEscenario = new javax.swing.JLabel();
        jTextFieldIdentEquipo = new javax.swing.JTextField();
        jLabelIdentEquipo = new javax.swing.JLabel();
        GestionEscenarios = new javax.swing.JMenuBar();
        jMenuEditarEscenario = new javax.swing.JMenu();
        jMenuItemNuevoEscenario = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAbrir = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItemEliminarEscenario = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGuardarEscenario = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSalir = new javax.swing.JMenuItem();
        jMenuEquipo = new javax.swing.JMenu();
        jMenuNombreEquipo = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuOrgEquipo = new javax.swing.JMenu();
        jCheckBoxMenuIgualitario = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuJerarquico = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuOtros = new javax.swing.JCheckBoxMenuItem();
        jMenuAddEtidad = new javax.swing.JMenu();
        jMenuItemCrearRobot = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCrearVictima = new javax.swing.JMenuItem();

        jDialogAvisoErrorDefNumEntidades.setTitle("Error: Definición de entidades en el escenario");
        jDialogAvisoErrorDefNumEntidades.setBounds(new java.awt.Rectangle(20, 20, 335, 88));
        jDialogAvisoErrorDefNumEntidades.setType(java.awt.Window.Type.POPUP);

        jButton1.setText("Aceptar");

        jLabel1.setText("El numero de entidades no puede ser mayor que 20");

        javax.swing.GroupLayout jDialogAvisoErrorDefNumEntidadesLayout = new javax.swing.GroupLayout(jDialogAvisoErrorDefNumEntidades.getContentPane());
        jDialogAvisoErrorDefNumEntidades.getContentPane().setLayout(jDialogAvisoErrorDefNumEntidadesLayout);
        jDialogAvisoErrorDefNumEntidadesLayout.setHorizontalGroup(
            jDialogAvisoErrorDefNumEntidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogAvisoErrorDefNumEntidadesLayout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jDialogAvisoErrorDefNumEntidadesLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialogAvisoErrorDefNumEntidadesLayout.setVerticalGroup(
            jDialogAvisoErrorDefNumEntidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogAvisoErrorDefNumEntidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1))
        );

        jPopupMenuAcionEntidad.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuAcionEntidadPopupMenuWillBecomeVisible(evt);
            }
        });
        jPopupMenuAcionEntidad.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                jPopupMenuAcionEntidadMenuKeyPressed(evt);
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });

        jMenuItemEliminar.setText("Eliminar");
        jMenuItemEliminar.setToolTipText("");
        jMenuItemEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEliminarActionPerformed(evt);
            }
        });
        jPopupMenuAcionEntidad.add(jMenuItemEliminar);
        jPopupMenuAcionEntidad.add(jSeparator3);

        jMenuItemGuardar.setText("Guardar");
        jMenuItemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuardarActionPerformed(evt);
            }
        });
        jPopupMenuAcionEntidad.add(jMenuItemGuardar);

        jMenuItemAddRobot.setText("Añadir Robot");
        jMenuItemAddRobot.setActionCommand("AddRobot");
        jMenuItemAddRobot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddRobotActionPerformed(evt);
            }
        });
        jPopupMenuAddEntidades.add(jMenuItemAddRobot);
        jPopupMenuAddEntidades.add(jSeparator4);

        jMenuItemAddVictima.setText("Añadir Victima");
        jMenuItemAddVictima.setActionCommand("AddVictima");
        jMenuItemAddVictima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddVictimaActionPerformed(evt);
            }
        });
        jPopupMenuAddEntidades.add(jMenuItemAddVictima);

        jFileChooser1.setDialogTitle("Seleccion de escenario");
        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        menu1.setLabel("File");
        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        menu3.setLabel("File");
        menuBar2.add(menu3);

        menu4.setLabel("Edit");
        menuBar2.add(menu4);

        menu5.setLabel("File");
        menuBar3.add(menu5);

        menu6.setLabel("Edit");
        menuBar3.add(menu6);

        menu7.setLabel("File");
        menuBar4.add(menu7);

        menu8.setLabel("Edit");
        menuBar4.add(menu8);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editor de Escenarios");
        setMinimumSize(new java.awt.Dimension(30, 30));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        jTextFieldModeloOrganizacion.setName("Modelo Organizacion"); // NOI18N

        robotIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconosEscenarios/Robot.png"))); // NOI18N
        robotIcon.setText("Robots");
        robotIcon.setIconTextGap(2);

        intervalNumRobots.setText("0");
        intervalNumRobots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intervalNumRobotsActionPerformed(evt);
            }
        });

        victimaIcon1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        victimaIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconosEscenarios/Mujer.png"))); // NOI18N
        victimaIcon1.setText("Victimas");
        victimaIcon1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        victimaIcon1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        victimaIcon1.setIconTextGap(2);

        intervalNumVictimas.setText("0");
        intervalNumVictimas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intervalNumVictimasActionPerformed(evt);
            }
        });

        jTextFieldIdentEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIdentEscenarioActionPerformed(evt);
            }
        });

        jButtonGuardarEscenario.setText("Guardar");
        jButtonGuardarEscenario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonGuardarEscenarioMousePressed(evt);
            }
        });
        jButtonGuardarEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarEscenarioActionPerformed(evt);
            }
        });

        jLabelOrganizacion.setText("Organización");

        jLabelIdentEscenario.setText("Ident Escenario");

        jTextFieldIdentEquipo.setToolTipText("");
        jTextFieldIdentEquipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIdentEquipoActionPerformed(evt);
            }
        });

        jLabelIdentEquipo.setLabelFor(jTextFieldIdentEquipo);
        jLabelIdentEquipo.setText("Ident Equipo");

        jMenuEditarEscenario.setText("Edición");

        jMenuItemNuevoEscenario.setText("Nuevo Escenario");
        jMenuItemNuevoEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNuevoEscenarioActionPerformed(evt);
            }
        });
        jMenuEditarEscenario.add(jMenuItemNuevoEscenario);
        jMenuEditarEscenario.add(jSeparator5);

        jMenuItemAbrir.setText("Abrir");
        jMenuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirActionPerformed(evt);
            }
        });
        jMenuEditarEscenario.add(jMenuItemAbrir);
        jMenuEditarEscenario.add(jSeparator6);

        jMenuItemEliminarEscenario.setText("Eliminar");
        jMenuItemEliminarEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEliminarEscenarioActionPerformed(evt);
            }
        });
        jMenuEditarEscenario.add(jMenuItemEliminarEscenario);
        jMenuEditarEscenario.add(jSeparator8);

        jMenuItemGuardarEscenario.setText("Guardar");
        jMenuItemGuardarEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuardarEscenarioActionPerformed(evt);
            }
        });
        jMenuEditarEscenario.add(jMenuItemGuardarEscenario);
        jMenuEditarEscenario.add(jSeparator1);

        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalirActionPerformed(evt);
            }
        });
        jMenuEditarEscenario.add(jMenuItemSalir);

        GestionEscenarios.add(jMenuEditarEscenario);

        jMenuEquipo.setText("Equipo");
        jMenuEquipo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jMenuNombreEquipo.setText("Nombre del Equipo");
        jMenuNombreEquipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNombreEquipoActionPerformed(evt);
            }
        });
        jMenuEquipo.add(jMenuNombreEquipo);
        jMenuEquipo.add(jSeparator9);

        jMenuOrgEquipo.setText("Organización del Equipo");

        jCheckBoxMenuIgualitario.setText("Igualitario");
        jCheckBoxMenuIgualitario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuIgualitarioActionPerformed(evt);
            }
        });
        jMenuOrgEquipo.add(jCheckBoxMenuIgualitario);

        jCheckBoxMenuJerarquico.setText("Jerarquico");
        jCheckBoxMenuJerarquico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuJerarquicoActionPerformed(evt);
            }
        });
        jMenuOrgEquipo.add(jCheckBoxMenuJerarquico);

        jCheckBoxMenuOtros.setText("Otros");
        jCheckBoxMenuOtros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuOtrosActionPerformed(evt);
            }
        });
        jMenuOrgEquipo.add(jCheckBoxMenuOtros);

        jMenuEquipo.add(jMenuOrgEquipo);

        GestionEscenarios.add(jMenuEquipo);

        jMenuAddEtidad.setText("Añadir entidad");

        jMenuItemCrearRobot.setText("Robot");
        jMenuItemCrearRobot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCrearRobotActionPerformed(evt);
            }
        });
        jMenuAddEtidad.add(jMenuItemCrearRobot);
        jMenuAddEtidad.add(jSeparator2);

        jMenuItemCrearVictima.setText("Victima");
        jMenuItemCrearVictima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCrearVictimaActionPerformed(evt);
            }
        });
        jMenuAddEtidad.add(jMenuItemCrearVictima);

        GestionEscenarios.add(jMenuAddEtidad);

        setJMenuBar(GestionEscenarios);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator7)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelOrganizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldModeloOrganizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelIdentEquipo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(robotIcon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intervalNumRobots, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(victimaIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intervalNumVictimas, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(64, 64, 64))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldIdentEquipo, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelIdentEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldIdentEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonGuardarEscenario)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldIdentEquipo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelIdentEquipo)
                    .addComponent(jLabelIdentEscenario)
                    .addComponent(jTextFieldIdentEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonGuardarEscenario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intervalNumVictimas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(victimaIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(intervalNumRobots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(robotIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldModeloOrganizacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOrganizacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(722, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here
        if (evt.getButton()==3){
            jPopupMenuAddEntidades.show(this,evt.getX(),evt.getY());
            ultimoPuntoClic = new Point(evt.getX(),evt.getY());
        }else {
        String tipoEntidad="Robot";
         if(intencionUsuarioCrearVictima)tipoEntidad="Victima";
         Point puntoCursor = MouseInfo.getPointerInfo().getLocation();
         this.crearIconoRobVict(tipoEntidad,puntoCursor.x,puntoCursor.y );
        }
    }//GEN-LAST:event_formMouseClicked

    private void jButtonGuardarEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarEscenarioActionPerformed
        // TODO add your handling code here:
        System.out.println("Ha pulsado el botón Guardar escenario");
        actualizarCoordenadasEntidades();
        controladorGestionEsc.peticionGuardarEscenario(escenarioActualComp,moverComp.getCambios());
    }//GEN-LAST:event_jButtonGuardarEscenarioActionPerformed

    private void intervalNumRobotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intervalNumRobotsActionPerformed
        // TODO add your handling code here:
        int num1 = Integer.parseInt(intervalNumRobots.getText());
        System.out.println("Numero de robots");
                if (num1>20 ){
//                    JOptionPane.showInputDialog(rootPane,"El numero de robots tiene que ser menor que 20");
           JOptionPane.showMessageDialog(rootPane,"El numero de robots tiene que ser menor que 20","Error en Numero Entidades", JOptionPane.ERROR_MESSAGE);
               
                }else System.out.println("Definido el numero de robots : "+ num1);

    }//GEN-LAST:event_intervalNumRobotsActionPerformed

    private void jMenuItemGuardarEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuardarEscenarioActionPerformed
        // TODO add your handling code here:
         // TODO add your handling code here:
        actualizarCoordenadasEntidades();
        controladorGestionEsc.peticionGuardarEscenario (escenarioActualComp,moverComp.getCambios());
    }//GEN-LAST:event_jMenuItemGuardarEscenarioActionPerformed

    private void jButtonGuardarEscenarioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonGuardarEscenarioMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonGuardarEscenarioMousePressed

    private void intervalNumVictimasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intervalNumVictimasActionPerformed
        // TODO add your handling code here:
        int num1 = Integer.parseInt(intervalNumVictimas.getText());
        System.out.println("Numero de victimas");
                if (num1>20 ){
                    JOptionPane.showMessageDialog(rootPane,"El numero de victimas tiene que ser menor que 20","Error en Numero Entidades", JOptionPane.ERROR_MESSAGE);
                    
                }else System.out.println("Definido el numero de victimas : "+ num1);
    }//GEN-LAST:event_intervalNumVictimasActionPerformed

    private void jMenuItemCrearRobotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCrearRobotActionPerformed
        // TODO add your handling code here:
        intencionUsuarioCrearRobot = true;
        intencionUsuarioCrearVictima = false;
    }//GEN-LAST:event_jMenuItemCrearRobotActionPerformed

    private void jMenuItemCrearVictimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCrearVictimaActionPerformed
        // TODO add your handling code here:
         intencionUsuarioCrearVictima = true;
         intencionUsuarioCrearRobot = false;
    }//GEN-LAST:event_jMenuItemCrearVictimaActionPerformed

    private void jTextFieldIdentEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIdentEscenarioActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTextFieldIdentEscenarioActionPerformed

    private void jMenuItemEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEliminarActionPerformed
        // TODO add your handling code here:
        System.out.println("Eliminar menu: "+ "  entidad = "   );
//        JLabel compAeliminar =(JLabel)moverComp.getUltimoComponenteSeleccionado();
//        compAeliminar.setVisible(false);
        eliminarEntidadSeleccionada();
       
    }//GEN-LAST:event_jMenuItemEliminarActionPerformed

    private void jMenuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuardarActionPerformed
        // TODO add your handling code here:
         System.out.println("Ha pulsado el botón Guardar Escenario");
         escenarioModificado=moverComp.getCambios();
         if(!escenarioGuardado||escenarioModificado){
         actualizarCoordenadasEntidades();
         controladorGestionEsc.peticionGuardarEscenario(escenarioActualComp,escenarioModificado);
         escenarioGuardado=true;escenarioModificado=false;
         }
    }//GEN-LAST:event_jMenuItemGuardarActionPerformed

    private void jPopupMenuAcionEntidadPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuAcionEntidadPopupMenuWillBecomeVisible
        // TODO add your handling code here:
//         jPopupMenuAcionEntidad.show(, 200, 200);
    }//GEN-LAST:event_jPopupMenuAcionEntidadPopupMenuWillBecomeVisible

    private void jPopupMenuAcionEntidadMenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_jPopupMenuAcionEntidadMenuKeyPressed
        // TODO add your handling code here:
//        jPopupMenuAcionEntidad.show(this, evt.getX(), e.getY());
    }//GEN-LAST:event_jPopupMenuAcionEntidadMenuKeyPressed

    private void jMenuItemAddRobotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddRobotActionPerformed
        // TODO add your handling code here:
        intencionUsuarioCrearRobot = true;
        intencionUsuarioCrearVictima = false;
        this.crearIconoRobVict("Robot",ultimoPuntoClic.x,ultimoPuntoClic.y );
       
    }//GEN-LAST:event_jMenuItemAddRobotActionPerformed

    private void jMenuItemAddVictimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddVictimaActionPerformed
        // TODO add your handling code here:
         intencionUsuarioCrearRobot = false;
        intencionUsuarioCrearVictima = true;
        this.crearIconoRobVict("Victima",ultimoPuntoClic.x,ultimoPuntoClic.y );
    }//GEN-LAST:event_jMenuItemAddVictimaActionPerformed

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    private void jMenuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirActionPerformed
        // TODO add your handling code here:    
//      peticionAbrirEscenario();
        this.controladorGestionEsc.peticionAbrirEscenarioEdicion();
    }//GEN-LAST:event_jMenuItemAbrirActionPerformed

    private void jMenuItemNuevoEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNuevoEscenarioActionPerformed
        // Usuario quiere crear un escenario
        // se abre una  ventana vacia , si tiene otra abierta se le debería avisar de que se guardar
        // lo que tiene
        if (escenarioActualComp.getNumRobots()> 0){
//            peticionGuardarEscenario();
            this.controladorGestionEsc.peticionGuardarEscenario(escenarioActualComp,moverComp.getCambios());
            // Se avisa de que el escenario actual se va a guardar antes de abrir el nuevo
//            escenarioActualComp.setIdentEscenario(jTextFieldIdentEquipo.getText());
//       
//
//        //         String smsg = "Puede cambiar el valor en milisegundos en que deben enviarse las coordenadas";
//
//        String smsg = "Se va a guardar el escenario: " +jTextFieldIdentEquipo.getText() ;
//        JOptionPane.showConfirmDialog(rootPane, smsg,"Confirmar GuardarEscenario",JOptionPane.OK_CANCEL_OPTION );
//         persistencia.guardarInfoEscenarioSimulacion(directorioPersistencia, escenarioActualComp);
        }
        escenarioActualComp = gestionEscComp.crearEscenarioSimulacion();
//        jTextFieldIdentEquipo.setText()
        identEscenario= escenarioActualComp.getIdentEscenario();
        eliminarEntidadesEscenario();
        jTextFieldIdentEscenario.setText(identEscenario);
        intervalNumRobots.setText(""+0);
        intervalNumVictimas.setText(""+0);
        
    }//GEN-LAST:event_jMenuItemNuevoEscenarioActionPerformed

    private void jMenuItemEliminarEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEliminarEscenarioActionPerformed
        // TODO add your handling code here:
//        peticionEliminarEscenario();
            controladorGestionEsc.peticionEliminarEscenarioSimulGuardado();
    }//GEN-LAST:event_jMenuItemEliminarEscenarioActionPerformed

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        // TODO add your handling code here:
        escenarioModificado=moverComp.getCambios()||escenarioModificado;
        System.out.println(" Variable escenarioModificado : = "+ escenarioModificado );
        if(escenarioModificado)this.actualizarCoordenadasEntidades();
        this.controladorGestionEsc.peticionSalirEditor(escenarioModificado);
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        // TODO add your handling code here:
//         if (evt.getButton()==3){
//            jPopupMenuAddEntidades.show(this,evt.getX(),evt.getY());
//            ultimoPuntoClic = new Point(evt.getX(),evt.getY());
//        }else {
//        String tipoEntidad="Robot";
//         if(intencionUsuarioCrearVictima)tipoEntidad="Victima";
//         Point puntoCursor = MouseInfo.getPointerInfo().getLocation();
//         this.crearIconoRobVict(tipoEntidad,puntoCursor.x,puntoCursor.y );
//        }
    }//GEN-LAST:event_formMouseReleased

    private void jTextFieldIdentEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIdentEquipoActionPerformed
        // TODO add your handling code here:
       identEquipo= jTextFieldIdentEquipo.getText();
       actualizarIdentEquipo(identEquipo);
    }//GEN-LAST:event_jTextFieldIdentEquipoActionPerformed

    private void jCheckBoxMenuJerarquicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuJerarquicoActionPerformed
        // TODO add your handling code here:
        jCheckBoxMenuIgualitario.setSelected(false);
        jCheckBoxMenuOtros.setSelected(false);
        modeloOrganizativo = "Jerarquico";
        jTextFieldModeloOrganizacion.setText(modeloOrganizativo);
        escenarioActualComp.setmodeloOrganizativo(modeloOrganizativo);
        identEscenario=gestionEscComp.getIdentEscenario(modeloOrganizativo, numeroRobots, numeroVictimas);
        this.actualizarInfoEquipoEnEscenario();
        
    }//GEN-LAST:event_jCheckBoxMenuJerarquicoActionPerformed

    private void jMenuNombreEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNombreEquipoActionPerformed
        // TODO add your handling code here:
       
        identEquipo= solicitarIdentEquipo("Definir Identificador del equipo", identEquipo);
        actualizarIdentEquipo(identEquipo);
        
    }//GEN-LAST:event_jMenuNombreEquipoActionPerformed

    private void jCheckBoxMenuIgualitarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuIgualitarioActionPerformed
        // TODO add your handling code here:
        jCheckBoxMenuJerarquico.setSelected(false);
        jCheckBoxMenuOtros.setSelected(false);
         modeloOrganizativo = "Igualitario";
        jTextFieldModeloOrganizacion.setText(modeloOrganizativo);
        escenarioActualComp.setmodeloOrganizativo(modeloOrganizativo);
        identEscenario=gestionEscComp.getIdentEscenario(modeloOrganizativo, numeroRobots, numeroVictimas);
        this.actualizarInfoEquipoEnEscenario();
    }//GEN-LAST:event_jCheckBoxMenuIgualitarioActionPerformed

    private void jCheckBoxMenuOtrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuOtrosActionPerformed
        // TODO add your handling code here:
        jCheckBoxMenuJerarquico.setSelected(false);
        jCheckBoxMenuIgualitario.setSelected(false);
        modeloOrganizativo = "Otros";
        actualizarItemsMenuModeloOrganizacion(modeloOrganizativo);
    }//GEN-LAST:event_jCheckBoxMenuOtrosActionPerformed
    private void setIntervaloEnvioMensajesDesdeCC(int intervalo){
		intervaloSecuencia = intervalo ;
		int intervaloEnvioMensajesDesdeCC = 1000;
		String strintervaloEnvioMensajesDesdeCC = "";
    }
     private void jLabelMouseReleased(java.awt.event.MouseEvent evt) {                                   
        // TODO add your handling code here:
        // Si tiene una entidad seleccionada se suelta y se anotan la coordenadas
         System.out.println("Released" + " X= "+ evt.getX() +" Y = "+ evt.getY() );
         entidadSeleccionadaParaMover=false;
          evt.consume();
         
    } 
     private void actualizarIdentEquipo(String idEquipo){
          String identEquipoActual= escenarioActualComp.getIdentEquipo();
         if(idEquipo==null||idEquipo.isEmpty()||idEquipo.isBlank()){
            JOptionPane.showMessageDialog(jMenuEquipo, "El identificador del equipo debe tener más de un caracter");
           identEquipo= solicitarIdentEquipo("Definir Identificador del equipo", "identificación del Equipo");
        }
        if(!identEquipo.equals(identEquipoActual)){
        jLabelIdentEquipo.setVisible(true);
        jTextFieldIdentEquipo.setText(identEquipo);
         jTextFieldIdentEquipo.setVisible(true);
        escenarioActualComp.actualizarIdentRobts(identEquipo);
        escenarioActualComp.setIdentEquipo(identEquipo);
        this.visualizarEscenario(escenarioActualComp);
        }
     }
     private void actualizarItemsMenuModeloOrganizacion(String modeloOrganizacion){
        modeloOrganizativo = modeloOrganizacion;
        jTextFieldModeloOrganizacion.setText(modeloOrganizativo);
        escenarioActualComp.setmodeloOrganizativo(modeloOrganizativo);
        identEscenario=gestionEscComp.getIdentEscenario(modeloOrganizativo, numeroRobots, numeroVictimas);
        this.actualizarInfoEquipoEnEscenario();
     }
             
     public int solicitarConfirmacion(String texto, String tituloVentana){
        return( JOptionPane.showConfirmDialog(rootPane, texto,tituloVentana,JOptionPane.YES_NO_CANCEL_OPTION ));  
     }
     
     public String solicitarIdentEquipo(String texto,  String valorInicial){
         return JOptionPane.showInputDialog(rootPane, texto, valorInicial);
     }

    public void visualizarIdentsEquipoRobot ( ArrayList<String> equipoIds){
//        eqipoIds = eqipoIds.toArray();
        identsRobotsEquipo = equipoIds;
//        this.listaComponentes.setListData(identsRobotsEquipo.toArray());
//        listaComponentes.setVisible(true);
}
    
    public JLabel crearIconoRobVict(String tipoEntidad, int coordX, int coordY){
        
        JLabel label = new JLabel();
        int correccionX=-30;
        int correccionY=-93;
//        int correccionX=0;
//        int correccionY=0;
        coordX=coordX+correccionX;
        coordY=coordY+correccionY;
        String rutaImagen;
        String identEntidad="";
       if ( tipoEntidad.startsWith("Robot")){
            rutaImagen=VocabularioRosace.RUTA_ICONOS_ESCENARIOS+imageniconoRobot;
            identEntidad=geIdentEntidad(ENTIDAD_ROBOT);
           escenarioActualComp.addRoboLoc(identEntidad, new Point(coordX,coordY));
           numeroRobots++;
                   intervalNumRobots.setText(""+numeroRobots);    
       }
       else{
//        rutaImagen=directorioTrabajo+rutaIconos+imageniconoMujer;
        rutaImagen=VocabularioRosace.RUTA_ICONOS_ESCENARIOS+imageniconoMujer;
//        mumeroVictimas= escenarioActualComp.getNumVictimas();
//        numeroVictimas++;
//         intervalNumVictimas.setText(""+mumeroVictimas);
//         identEntidad=tipoEntidad+numeroVictimas;
//         if(listaIdentsVictimas.contains(identEntidad))identEntidad=geIdentEntidad(ENTIDAD_VICTIMA);
//             else listaIdentsVictimas.add(identEntidad);
        identEntidad=geIdentEntidad(ENTIDAD_VICTIMA);
         escenarioActualComp.addVictimLoc(identEntidad, new Point(coordX,coordY));
         numeroVictimas++;
         intervalNumVictimas.setText(""+numeroVictimas);
//         identEntidad=tipoEntidad+mumeroVictimas;
//         label.setText(tipoEntidad+mumeroVictimas);
       }      
        label.setText(identEntidad);
        label.setBounds(10, 10, 150, 150);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(label);
        label.setVisible(true);
        label.setIcon(new ImageIcon(rutaImagen));
        label.setLocation(coordX, coordY);
        this.listaEntidadesEnEscenario.add(label);
        identEscenario=gestionEscComp.getIdentEscenario(modeloOrganizativo, numeroRobots, numeroVictimas);
        actualizarInfoEquipoEnEscenario ();
        System.out.println( "Se crea la entidad : "+label.getText()+ " Coordenadas : X =" + coordX +" , Y = " +coordY );
//       tablaEntidadesEnEscenario.put(identEntidad, label);
//       listaEntidadesEnEscenario.add(label);
        
       moverComp.registerComponent(label);
       
        return label;
    }
    private synchronized String geIdentEntidad(int tipoEntidad){
        // si ya hay una entidad con ese el numero asignado se busca el entero minimo que no tiene asignada entidad
        String etiqueta = "";
        int indiceHueco=0; boolean huecoEncontrado=false;
            if(listaIdentsRobots==null)listaIdentsRobots= new ArrayList < >();
            if(listaIdentsVictimas==null)listaIdentsVictimas= new ArrayList < >();
            if( tipoEntidad==ENTIDAD_ROBOT){
                indiceHueco =listaIdentsRobots.indexOf("");
                if(indiceHueco<0){
                    int i=listaIdentsRobots.size()+1; etiqueta= identEquipo+ROBOT+i;
                    while(listaIdentsRobots.contains(etiqueta)){
                        i++;
                        etiqueta=identEquipo+ROBOT+i;
                    }
                    listaIdentsRobots.add(etiqueta);
    System.out.println( "Se crea la etiqueta : " +etiqueta+ " En la posicion de la lista:  " + (i-1) +" ,El tamagno de la lista es : " +listaIdentsRobots.size() );
                }
                else{
                    etiqueta=identEquipo+ROBOT+(indiceHueco+1); 
                    listaIdentsRobots.set(indiceHueco, etiqueta);
    System.out.println( "Se reutiliza la etiqueta : " +etiqueta+ " En la posicion de la lista:  " + indiceHueco +" ,El tamagno de la lista es : " +listaIdentsRobots.size() );
                }
            }else if( tipoEntidad==ENTIDAD_VICTIMA){
                 indiceHueco =listaIdentsVictimas.indexOf("");
                if(indiceHueco<0){
                    int i=listaIdentsVictimas.size()+1; etiqueta=VICTIMA+i;
                    while(listaIdentsVictimas.contains(etiqueta)){
                        i++;
                        etiqueta=VICTIMA+i;   
                    }
                     listaIdentsVictimas.add(etiqueta);
                }
                else{
                    etiqueta=VICTIMA+(indiceHueco+1); 
                    listaIdentsVictimas.set(indiceHueco, etiqueta);
                }
            }
        return etiqueta;   
    }
    public void addEntidadEnEscenario (String rutaIcono, String idEntidad, Point puntoLoc){
        escenarioModificado=true;
         JLabel label = new JLabel();
           label.setText(idEntidad);
        label.setBounds(10, 10, 150, 150);
        this.add(label);
        label.setVisible(true);
        label.setIcon(new ImageIcon(rutaIcono));
        label.setLocation(puntoLoc);
        moverComp.registerComponent(label);
        this.listaEntidadesEnEscenario.add(label);
        System.out.println( "Se crea la entidad : "+label.getText()+ " Punto : =" + puntoLoc );
//       tablaEntidadesEnEscenario.put(identEntidad, label);
    }
    public void eliminarEntidadSeleccionada (){
     JLabel entidadAeliminar=   (JLabel) moverComp.getUltimoComponenteSeleccionado();
     escenarioModificado=true;
     
//        escenrioSimComp.eliminarEntidad(((JLabel)moverComp.eliminarUltimoCompSeleccionado()).getName());
//     moverComp.deregisterComponent(entidadAeliminar);
int indiceEntidad =0;
     entidadAeliminar.setVisible(false);
     String identEntidad =entidadAeliminar.getText();
     if (identEntidad.contains("Robot")||identEntidad.contains("robot")){
                numeroRobots--;
                 indiceEntidad =listaIdentsRobots.indexOf(identEntidad);
                listaIdentsRobots.set(indiceEntidad,"");
                intencionUsuarioCrearRobot=true;
     }
            else if (identEntidad.contains("Victim")){
                numeroVictimas--;
                 indiceEntidad =listaIdentsVictimas.indexOf(identEntidad);
                listaIdentsVictimas.set(indiceEntidad,"");
                intencionUsuarioCrearVictima=true;
            }
       escenarioActualComp.eliminarEntidad(identEntidad);
       listaEntidadesEnEscenario.remove(entidadAeliminar);
        System.out.println( "Se elimina la entidad : "+entidadAeliminar.getText()+ "Indice en tabla ident :"+ indiceEntidad + " Coordenadas : X =" + entidadAeliminar.getX() +" , Y = " +entidadAeliminar.getY() );
        identEscenario=gestionEscComp.getIdentEscenario(modeloOrganizativoInicial, numeroRobots, numeroVictimas);
        actualizarInfoEquipoEnEscenario ();
    }
    public void actualizarInfoEquipoEnEscenario (){
//        escenarioModificado=true;
        jTextFieldModeloOrganizacion.setText(""+modeloOrganizativo);
        intervalNumRobots.setText(""+numeroRobots);
        intervalNumVictimas.setText(""+numeroVictimas);
//        identEquipoActual=orgModelo+modeloOrganizativo+numRobots+numeroRobots+numVictims+numeroVictimas+subIndiceEscRepetido
        jTextFieldIdentEscenario.setText(""+identEscenario);
        escenarioActualComp.setIdentEscenario(identEscenario);
        jTextFieldIdentEquipo.setText(""+identEquipo);
        escenarioActualComp.setIdentEquipo(identEquipo);
    }
//    private void actualizarIdentEscenario(String idenEscenario){
//        if(idenEscenario!=null)identEscenario=gestionEscComp.getNewVersionEscenario(idenEscenario);
//        else identEscenario = gestionEscComp.getIdentEscenario(modeloOrganizativo, numeroRobots, numeroVictimas);
//        escenarioActualComp.setIdentEscenario(idenEscenario);
//    }
   
    public void actualizarCoordenadasEntidades(){
        System.out.println( "Se actualizan las coordenadas. Numero de entidades : "+listaEntidadesEnEscenario.size());
        int numRobots= 0; int numVictims= 0;
        for (JLabel entidadLabel : listaEntidadesEnEscenario) {
            String identEntidad = entidadLabel.getText();
            if (identEntidad.contains("Robot")||identEntidad.contains("robot")){
                escenarioActualComp.addRoboLoc(identEntidad, entidadLabel.getLocation());
                numRobots++;
            }
            else if (identEntidad.contains("Victim")){
                escenarioActualComp.addVictimLoc(identEntidad, entidadLabel.getLocation());
                numVictims++;
            }
            System.out.println( "Se actualiza la entidad : "+identEntidad+ " Coordenadas : X =" + entidadLabel.getX() +" , Y = " +entidadLabel.getY() );
            System.out.println( "Robots y victimas despues de la actualizacion. Num Robots :  "+numRobots+ " Num Victimas : " + numVictims );
        }
        escenarioActualComp.setNumRobots(numRobots);
        escenarioActualComp.setNumVictimas(numVictims);
       System.out.println( "Robots y victimas despues de la actualizacion. Num Robots :  "+escenarioActualComp.getNumRobots()+ " Num Vicitimas : " + escenarioActualComp.getNumVictimas() );
    }
    public void setEscenarioActualComp(EscenarioSimulacionRobtsVictms escenActualComp){
        escenarioActualComp = escenActualComp;
    }
     public void visualizarEscenario(EscenarioSimulacionRobtsVictms infoEscenario ){
         eliminarEntidadesEscenario();        
         jTextFieldIdentEquipo.setVisible(true);
        jLabelIdentEquipo.setVisible(true);
         escenarioActualComp= infoEscenario;
         identEscenario=escenarioActualComp.getIdentEscenario();
         System.out.println( "Se Va a visualizar el escenario con identificador "+ identEscenario ); 
         listaIdentsRobots=infoEscenario.getListIdentsRobots();
         listaIdentsVictimas=infoEscenario.getListIdentsVictims();
         modeloOrganizativo=infoEscenario.getmodeloOrganizativo();
         numeroRobots = infoEscenario.getNumRobots();
         numeroVictimas = infoEscenario.getNumVictimas();
         identEquipo=infoEscenario.getIdentEquipo();       
         jTextFieldIdentEquipo.setText(""+identEquipo);
         jTextFieldModeloOrganizacion.setText(""+modeloOrganizativo);
//         intervalNumRobots.setText(""+numeroRobots);
//         intervalNumVictimas.setText(""+numeroVictimas);
         String rutaImagen;
         Set entidades;
         Iterator entries;
         if (numeroRobots>0) {
         rutaImagen=VocabularioRosace.RUTA_ICONOS_ESCENARIOS+imageniconoRobot;
          entidades = infoEscenario.getRobots();
          entries = entidades.iterator();
          Entry thisEntry;
          RobotStatus1 robotInfo;
        while (entries.hasNext()) {
             thisEntry = (Entry) entries.next();
             robotInfo = (RobotStatus1)thisEntry.getValue();
            addEntidadEnEscenario(rutaImagen,(String)thisEntry.getKey(),robotInfo.getLocPoint());
        }
         }
         if (numeroVictimas>0) {
            rutaImagen=VocabularioRosace.RUTA_ICONOS_ESCENARIOS+imageniconoMujer;
            entidades = infoEscenario.getSetVictims();
            entries = entidades.iterator();
            Entry thisEntry ;
            Victim victimInfo ;
            while (entries.hasNext()) {
                 thisEntry = (Entry) entries.next();
                 victimInfo = (Victim)thisEntry.getValue();
                addEntidadEnEscenario(rutaImagen,(String)thisEntry.getKey(),victimInfo.getLocPoint());
            }
         }
         this.actualizarInfoEquipoEnEscenario();
         this.setLocation(100,100);
         this.setVisible(true);
//          escenarioModificado= (!identEscenario.equals(this.identEquipoActual));
          System.out.println( "El identificador del escenario tras su visualizacion es : "+ identEscenario + " El escenario hasido modificado :" + escenarioModificado );
     }
     public void visualizarCrearEscenario(EscenarioSimulacionRobtsVictms infoEscenario ){
         escenarioActualComp=infoEscenario;
         identEscenario=escenarioActualComp.getIdentEscenario();
         System.out.println( "Se Va a visualizar el panel para crear un escenario con identificador "+ identEscenario ); 
//         jTextFieldIdentEquipo.setVisible(true);
//        jLabelIdentEquipo.setVisible(true);
         listaIdentsRobots=escenarioActualComp.getListIdentsRobots();
         listaIdentsVictimas=escenarioActualComp.getListIdentsVictims();
         modeloOrganizativo=escenarioActualComp.getmodeloOrganizativo();
         numeroRobots = escenarioActualComp.getNumRobots();
         numeroVictimas = escenarioActualComp.getNumVictimas();
         identEquipo=escenarioActualComp.getIdentEquipo();
         actualizarInfoEquipoEnEscenario ();
     } 
     
     public int confirmarPeticionGuardarEscenario (String msgConfirmacion){
         escenarioActualComp.setIdentificadorNormalizado();
         jTextFieldIdentEscenario.setText(escenarioActualComp.getIdentEscenario());
        String smsg = msgConfirmacion + jTextFieldIdentEscenario.getText();
       return JOptionPane.showConfirmDialog(rootPane, smsg,"Confirmar GuardarEscenario",JOptionPane.OK_CANCEL_OPTION );
     }
     private void eliminarEntidadesEscenario(){
         JLabel labelActual;             
         for( Component comp : this.getContentPane().getComponents() ){
                 if (comp instanceof JLabel){
                      labelActual = (JLabel)comp;
                     if (!labelActual.equals(jLabelIdentEscenario)&&!labelActual.equals(jLabelOrganizacion)
                             &&!labelActual.equals(robotIcon)&&!labelActual.equals(victimaIcon1)){                    
                         comp.setVisible(false);
                         remove(comp);
                         listaEntidadesEnEscenario.remove(labelActual);
                 System.out.println( "Se borra la entidad : "+ " Coordenadas :  =" + comp.getLocation() );         
                     }
                };
         } 
 
     }
     public int selecciondeFichero(String textoBotonAprob){
      FileNameExtensionFilter filter = new FileNameExtensionFilter("ficheros xml","xml","txt" );
      jFileChooser1.setFileFilter(filter);
      jFileChooser1.setApproveButtonText(textoBotonAprob);
      File dir = jFileChooser1.getCurrentDirectory();
     int returnVal = jFileChooser1.showOpenDialog(this);
      jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
       jFileChooser1.setCurrentDirectory(dir);
//       int returnVal = jFileChooser1.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        ultimoFicheroEscenarioSeleccionado = jFileChooser1.getSelectedFile();
     }return returnVal; // no ha seleccionado nada
     }
     
     public File getUltimoFicheroEscenarioSeleccionado(){
         return ultimoFicheroEscenarioSeleccionado;
     }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VisorEditorEscenarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VisorEditorEscenarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VisorEditorEscenarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VisorEditorEscenarios1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
       
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
            String  directorioPersistencia = VocabularioRosace.NombreDirectorioPersistenciaEscenarios+File.separator;
            VisorEditorEscenarios1 visor;
            PersistenciaVisualizadorEscenarios persistencia= new PersistenciaVisualizadorEscenarios();
            GestionEscenariosSimulacion gestionEscComp= new GestionEscenariosSimulacion();
            gestionEscComp.setIdentsEscenariosSimulacion(persistencia.obtenerIdentsEscenarioSimulacion(directorioPersistencia));
                try {
                    gestionEscComp = new GestionEscenariosSimulacion();
                    gestionEscComp.setIdentsEscenariosSimulacion(persistencia.obtenerIdentsEscenarioSimulacion(directorioPersistencia));
//        escenarioActualComp = gestionEscComp.crearEscenarioSimulación();
//                    visor = new VisorCreacionEscenarios1(new ControladorVisualizacionSimulRosace(notifEvts));
//             
////                    persistencia= new PersistenciaVisualizadorEscenarios();
//                    visor.setPersistencia(persistencia);
//                    visor.setGestorEscenarionComp(gestionEscComp);
//                    visor.setEscenarioActualComp(gestionEscComp.crearEscenarioSimulación());
//                    visor.actualizarInfoEquipoEnEscenario();
//                    visor.setVisible(true);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar GestionEscenarios;
    private javax.swing.JTextField intervalNumRobots;
    private javax.swing.JTextField intervalNumVictimas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonGuardarEscenario;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuIgualitario;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuJerarquico;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuOtros;
    private javax.swing.JDialog jDialogAvisoErrorDefNumEntidades;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelIdentEquipo;
    private javax.swing.JLabel jLabelIdentEscenario;
    private javax.swing.JLabel jLabelOrganizacion;
    private javax.swing.JMenu jMenuAddEtidad;
    private javax.swing.JMenu jMenuEditarEscenario;
    private javax.swing.JMenu jMenuEquipo;
    private javax.swing.JMenuItem jMenuItemAbrir;
    private javax.swing.JMenuItem jMenuItemAddRobot;
    private javax.swing.JMenuItem jMenuItemAddVictima;
    private javax.swing.JMenuItem jMenuItemCrearRobot;
    private javax.swing.JMenuItem jMenuItemCrearVictima;
    private javax.swing.JMenuItem jMenuItemEliminar;
    private javax.swing.JMenuItem jMenuItemEliminarEscenario;
    private javax.swing.JMenuItem jMenuItemGuardar;
    private javax.swing.JMenuItem jMenuItemGuardarEscenario;
    private javax.swing.JMenuItem jMenuItemNuevoEscenario;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JMenuItem jMenuNombreEquipo;
    private javax.swing.JMenu jMenuOrgEquipo;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenuAcionEntidad;
    private javax.swing.JPopupMenu jPopupMenuAddEntidades;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JTextField jTextFieldIdentEquipo;
    private javax.swing.JTextField jTextFieldIdentEscenario;
    private javax.swing.JTextField jTextFieldModeloOrganizacion;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.Menu menu3;
    private java.awt.Menu menu4;
    private java.awt.Menu menu5;
    private java.awt.Menu menu6;
    private java.awt.Menu menu7;
    private java.awt.Menu menu8;
    private java.awt.MenuBar menuBar1;
    private java.awt.MenuBar menuBar2;
    private java.awt.MenuBar menuBar3;
    private java.awt.MenuBar menuBar4;
    private javax.swing.JLabel robotIcon;
    private javax.swing.JLabel victimaIcon1;
    // End of variables declaration//GEN-END:variables

    public void setGestorEscenarionComp(GestionEscenariosSimulacion gestEscComp ) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    this.gestionEscComp=gestEscComp;
    }
    public EscenarioSimulacionRobtsVictms getEscenarionComp() {

    return this.escenarioActualComp;
    }

public void visualizarConsejo (String titulo, String msgConsejo, String recomendacion){
         String nl = System.getProperty("line.separator");
        Object msgConsjyRec = msgConsejo+nl+recomendacion;
         JOptionPane.showMessageDialog(rootPane,msgConsjyRec, titulo,2);
     }
    private void peticionEliminarEscenario() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    File ficheroseleccionado= peticionUsuarioSeleccionarFichero(directorioPersistencia,"Seleccionar Fichero a Eliminar");
    if(ficheroseleccionado!=null){ 
//        confirmar eliminacion del fichero y si lo confirma 
        String smsg = "Se va a eliminar el escenario: " + ficheroseleccionado.getName();
      int respuesta=  JOptionPane.showConfirmDialog(rootPane, smsg,"Confirmar EliminarEscenario",JOptionPane.OK_CANCEL_OPTION );
      if ( respuesta== JOptionPane.OK_OPTION) {
      ficheroseleccionado.delete();
        gestionEscComp.eliminarEscenario(ficheroseleccionado.getName());
         System.out.println("Se elimina el fichero :  "+ficheroseleccionado.getName());
      }
    }         
    }

   public File peticionUsuarioSeleccionarFichero(String directorio, String motivo){
       FileNameExtensionFilter filter = new FileNameExtensionFilter("ficheros xml","xml","txt" );
      jFileChooser1.setDialogTitle(motivo);
      jFileChooser1.setFileFilter(filter);
      jFileChooser1.setApproveButtonText("Eliminar");
      jFileChooser1.setCurrentDirectory(new File(directorioPersistencia));
      jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
       int returnVal = jFileChooser1.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File selectedFile = jFileChooser1.getSelectedFile();
        return selectedFile;
   }else{
        // mensaje diciendo que no se ha seleccionado ningun fichero
        return null;
    }
   }
    public boolean hayFicherosCreados(){
          File dir = jFileChooser1.getCurrentDirectory();
      int numFiles = dir.list().length ;
      return(numFiles > 0);
      }
     public boolean getescenarioModificado(){
         escenarioModificado=moverComp.getCambios();
         return escenarioModificado;
     }
     public File solicitarSeleccionFichero(String textoABotonAprob){
      FileNameExtensionFilter filter = new FileNameExtensionFilter("ficheros xml","xml","txt" );
      jFileChooser1.setFileFilter(filter);
      jFileChooser1.setApproveButtonText(textoABotonAprob);
      File dir = jFileChooser1.getCurrentDirectory();
     int returnVal = jFileChooser1.showOpenDialog(this);
      jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
       jFileChooser1.setCurrentDirectory(dir);
//       int returnVal = jFileChooser1.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        return jFileChooser1.getSelectedFile();
     }return null; // no ha seleccionado nada
     }
     
     public boolean setDirectorioPersistencia(String dirPersistencia){
         FileNameExtensionFilter filter = new FileNameExtensionFilter("ficheros xml","xml","txt" );
          jFileChooser1.setFileFilter(filter);
        jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
        try {
       jFileChooser1.setCurrentDirectory(new File (dirPersistencia));
       return true;
        } catch (Exception ex) {
            return false;
        }
     }
}
