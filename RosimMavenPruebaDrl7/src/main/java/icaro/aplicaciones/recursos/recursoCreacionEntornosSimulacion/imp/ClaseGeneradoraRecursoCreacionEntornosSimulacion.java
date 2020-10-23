package icaro.aplicaciones.recursos.recursoCreacionEntornosSimulacion.imp;

import javax.swing.JFrame;

import icaro.aplicaciones.recursos.recursoCreacionEntornosSimulacion.ItfUsoRecursoCreacionEntornosSimulacion;
import icaro.infraestructura.patronRecursoSimple.imp.ImplRecursoSimple;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

public class ClaseGeneradoraRecursoCreacionEntornosSimulacion extends ImplRecursoSimple implements ItfUsoRecursoCreacionEntornosSimulacion {

//    private ItfUsoRecursoTrazas trazas; //Se inicializa en el constructor con la referencia al recurso de trazas. Asi ya estara disponible en cualquier metodo.
    private String idRecurso;  //Se inicializara en el constructor con el identificador, dado a la instancia del recurso, en la descripcion de la organizacion
    private NotificadorInfoUsuarioEdicionEscenarios notifEvt;
    private VisorCreacionEscenarios1 visorSc;
    private ControladorEditorEntornosSimulacion controladorIUSimulador;
    private String identAgenteaReportar;
    private boolean editorAbierto;

    public ClaseGeneradoraRecursoCreacionEntornosSimulacion(String idRecurso) throws Exception {
    
        super(idRecurso);
        this.idRecurso = idRecurso;
        try {
            trazas.aceptaNuevaTraza(new InfoTraza(idRecurso, "El constructor de la clase generadora del recurso " + idRecurso + " ha completado su ejecucion ....", InfoTraza.NivelTraza.debug));
            notifEvt = new NotificadorInfoUsuarioEdicionEscenarios(idRecurso, identAgenteaReportar);
            // un agente debe decirle al recurso a quien debe reportar . Se puede poner el agente a reportar fijo
            controladorIUSimulador = new ControladorEditorEntornosSimulacion(notifEvt);
            visorSc = new VisorCreacionEscenarios1(controladorIUSimulador);
        } catch (Exception e) {
            this.itfAutomata.transita("error");
            System.out.println("No se pudo usar el recurso de trazas");
            throw e;
        }
    }

    //Methods that implement CreacionEntornosSimulacion resource use interface
    @Override
    public void MostrarEscenarioActualSimulado(String identEscenario) throws Exception {

        if (visorSc == null) {
            visorSc = new VisorCreacionEscenarios1(controladorIUSimulador);
        }
        visorSc.setVisible(true);
        visorSc.setExtendedState(JFrame.NORMAL);
    }

    @Override
    public void cerrarEditor() throws Exception {
        if (editorAbierto) {
            controladorIUSimulador.cerrarEditor();
        }
//      visorSc.setVisible(true);
    }

    @Override
    public void abrirEditor() throws Exception {
        if (!editorAbierto) {
            controladorIUSimulador.abrirEditor();
        }
    }

    @Override
    public EscenarioSimulacionRobtsVictms getEscenarioRobotsVictims(String identEscenario) throws Exception {
        EscenarioSimulacionRobtsVictms escenarioSimulacion = controladorIUSimulador.dameEscenario(identEscenario);
        if (escenarioSimulacion == null) {
            String consejo = " El fichero con el escenario solicitado : " + identEscenario + "  No esta definido ";
            String recomendacion = " puede crearlo utilizando el editor ";
            visorSc.visualizarConsejo("Escenario Inexistente", consejo, recomendacion);
            if (!editorAbierto) {
                controladorIUSimulador.abrirEditor();
            }
            visorSc.setVisible(true);
        }
        return escenarioSimulacion;
    }

    @Override
    public void termina() {
        trazas.aceptaNuevaTraza(new InfoTraza(this.idRecurso, "Terminando recurso" + this.id + " ....", InfoTraza.NivelTraza.debug));

        //Si es un recurso de visualizacion es necesaria una llamar a dispose de la ventana de visualizacion. Algo parecido a lo siguiente	
        //this.jvariableLocalReferenciaVisualizador.dispose(); //Destruye los componentes utilizados por este JFrame y devuelve la memoria utilizada al Sistema Operativo 	 
        super.termina();
    }
}
