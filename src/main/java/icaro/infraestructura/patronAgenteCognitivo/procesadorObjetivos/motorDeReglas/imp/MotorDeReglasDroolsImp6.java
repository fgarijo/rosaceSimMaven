
/*
 * Creado 3 de mayo de 2010, 11:56
 * Modificado 5 de noviembre de 2011
 *
 * @author Francisco J Garijo & JM Gascuegna
 */
package icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.motorDeReglas.imp;
//import icaro.aplicaciones.recursos.recursoMorse.imp.configDebugging;

import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.patronAgenteCognitivo.factoriaEInterfacesPatCogn.AgenteCognitivo;
import icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.motorDeReglas.ItfConfigMotorDeReglas;
import icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.motorDeReglas.ItfMotorDeReglas;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.drools.core.event.ActivationCreatedEvent;
import org.drools.core.event.AfterActivationFiredEvent;
import org.drools.core.event.BeforeActivationFiredEvent;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
//import org.drools.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;

/**
 * Drools implementation of the Rule Engine
 *
 * @
 * @
 * @author JM Gascuegna
 */
public class MotorDeReglasDroolsImp6 implements ItfMotorDeReglas, ItfConfigMotorDeReglas {

    private AgenteCognitivo agent;               //inicializada en el constructor
    private KieSession kSesion = null;   //inicializada en el metodo compileRules
    private EntryPoint entrypoint; //inicializada en el metodo compileRules  
    private ItfUsoRecursoTrazas trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
    private ArrayList<String> ficheroReglasCompilados = new ArrayList();
    private ArrayList<KnowledgeBuilder> KbuildersObtenidos = new ArrayList();
    private KieBuilder kbuilder;
    private KieServices kieServices;
    private FactoriaMotorDeReglasDroolsImp6 miFactoria;
    private boolean depuracionActivationRulesDebugging = false;
    private boolean depuracionHechosInsertados = false;
    private boolean depuracionHechosModificados = false;
    private boolean depuracionWorkingMemoryDebugging = false;
    private boolean factHandlesMonitoringINSERT_DEBUGGING = false;
    private boolean factHandlesMonitoringRETRACT_DEBUGGING = false;
    private boolean factHandlesMonitoring_beforeActivationFired_DEBUGGING = false;
    private boolean factHandlesMonitoring_DEBUGGING = false;  //ANTES ESTABA A true      
    private boolean factHandlesMonitoringUPDATE_DEBUGGING = false;
    private boolean factHandlesMonitoring_afterActivationFired_DEBUGGING = false;
    private boolean terminarSesion = false;
    private Environment env;
    private String agentId;
//    private AgendaEventListenerImpl agendaEventListener;
    private DefaultAgendaEventListener agendaEventListener;
    private boolean depuracionActivacionReglas;
    private boolean depuracionGestionHechos;
    private RuleRunTimeEventListenerImpl ruleRunTimeEventListener;

    /**
     * Constructor for the drools implementation
     *
     * @param agent Cognitive Agent
     */
    public MotorDeReglasDroolsImp6(AgenteCognitivo agent) {

        this.agent = agent; //referencia al agente
        this.agentId = agent.getIdentAgente();

    }

    public MotorDeReglasDroolsImp6(AgenteCognitivo agent, FactoriaMotorDeReglasDroolsImp6 factoriaMtReglas) {

        this.agent = agent; //referencia al agente
        this.agentId = agent.getIdentAgente();
        this.miFactoria = factoriaMtReglas;

    }

    @Override
    public void fireRules() {
        kSesion.fireAllRules();
        if (terminarSesion) {
            kSesion.dispose();
        }
    }

    public void crearSesionConConfiguracionStandard(KieBase kbase) {

        String kiepcks = kbase.getKiePackages().toString();
        kSesion = kbase.newKieSession();
        entrypoint = kSesion.getEntryPoint("DEFAULT");
    }

    @Override
    public boolean crearKbSesionConNuevasReglas(InputStream fichero, String identFicheroReglas) {
        // Suponemos que ya hay un sesion creada y ahora se pide una sesion nueva con otras reglas

        KieBase kiebse = miFactoria.compilarReglas(agentId, fichero, identFicheroReglas);
        if (kiebse != null) {
            crearSesionConConfiguracionStandard(kiebse);
            return true;
        } else {
            return false;
        }
    }

    public boolean actualizarKbSesionConNuevasReglas(InputStream fichero, String identFicheroReglas) {
        // se crea una nueva sesion con la nuevas reglas y se incorporan los objetos de la memoria de la sesion anterior 

        KieBase kiebse = miFactoria.compilarReglas(agentId, fichero, identFicheroReglas);
        if (kiebse != null) {
            crearSesionConConfiguracionStandard(kiebse);
            return true;
        } else {
            return false;
        }
    }

    //El metodo assertFact se llama cuando desde una TAREA se hace una llamada del tipo this.getEnvioHechos().insertarHecho(...)
    @Override
    public synchronized void assertFact(Object objeto) {
        if (depuracionHechosInsertados) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : nuevo Hecho insertado : " + " clase : " + objeto.getClass().getSimpleName() + objeto,
                    InfoTraza.NivelTraza.debug));
        }
        if (objeto == null) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : Se intenta insertar en el motor un objeto null : ",
                    InfoTraza.NivelTraza.error));
        } else {
            kSesion.insert(objeto);
            kSesion.fireAllRules();

        }
    }

    //El metodo retracttFact se llama cuando desde una TAREA se hace una llamada del tipo this.getEnvioHechos().eliminarHecho(...)	
    @Override
    public synchronized void retracttFact(Object objeto) {
        if (depuracionHechosModificados) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "MRuleEngine: new fact retracted: " + objeto,
                    InfoTraza.NivelTraza.debug));
        }
        if (objeto == null) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : Se intenta insertar en el motor un objeto null : ",
                    InfoTraza.NivelTraza.error));
        } else {
            FactHandle fh2 = entrypoint.insert(objeto);
            entrypoint.retract(fh2);
            kSesion.fireAllRules();
        }
    }

    //El metodo updateFact se llama cuando desde una TAREA se hace una llamada del tipo this.getEnvioHechos().actualizarHecho(...)	    
    @Override
    public synchronized void updateFact(Object objeto) {
        if (depuracionHechosModificados) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "MRuleEngine: new fact updated: " + objeto,
                    InfoTraza.NivelTraza.debug));
        }

        if (objeto == null) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : Se intenta insertar en el motor un objeto null : ",
                    InfoTraza.NivelTraza.error));
        } else {
            FactHandle fh2 = entrypoint.insert(objeto);
            entrypoint.update(fh2, objeto);
            kSesion.fireAllRules();
        }

    }
    //El metodo assertFactWithoutFireRules se llama cuando desde una TAREA se hace una llamada del tipo 
    //this.getEnvioHechos().insertarHechoWithoutFireRules(...)	        
    //The next method does not call to fireAllRules inside its code.       
    @Override
    public synchronized void assertFactWithoutFireRules(Object objeto) {
        if (depuracionHechosInsertados) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : assertFactWithoutFireRules-> nuevo Hecho insertado : " + objeto,
                    InfoTraza.NivelTraza.debug));
        }
        if (objeto == null) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : Se intenta insertar en el motor un objeto null : ",
                    InfoTraza.NivelTraza.error));
        } else {
            kSesion.insert(objeto);
//                 kSesion.fireAllRules();
        }

    }
    //El metodo retractFactWithoutFireRules se llama cuando desde una TAREA se hace una llamada del tipo 
    //this.getEnvioHechos().eliminarHechoWithoutFireRules(...)	            
    //The next method does not call to fireAllRules inside its code.   
    @Override
    public synchronized void retractFactWithoutFireRules(Object objeto) {
        if (depuracionHechosModificados) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "MRuleEngine: retractFactWithoutFireRules -> new fact retracted: " + objeto,
                    InfoTraza.NivelTraza.debug));
        }
        if (objeto == null) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : Se intenta insertar en el motor un objeto null : ",
                    InfoTraza.NivelTraza.error));
        } else {
            FactHandle fh2 = entrypoint.insert(objeto);
            entrypoint.delete(fh2);
//                entrypoint.retract(fh2);       
//                kSesion.fireAllRules();
        }

    }
    //El metodo updateFactWithoutFireRules se llama cuando desde una TAREA se hace una llamada del tipo 
    //this.getEnvioHechos().actualizarHechoWithoutFireRules(...)	                
    //The next method does not call to fireAllRules inside its code.   
    @Override
    public synchronized void updateFactWithoutFireRules(Object objeto) {
        if (depuracionHechosModificados) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "MRuleEngine: updateFactWithoutFireRules-> new fact updated: " + objeto,
                    InfoTraza.NivelTraza.debug));
        }
        if (objeto == null) {
            trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas : Se intenta insertar en el motor un objeto null : ",
                    InfoTraza.NivelTraza.error));
        } else {
            FactHandle fh2 = entrypoint.insert(objeto);
            entrypoint.update(fh2, objeto);
//                kSesion.fireAllRules();
        }
    }

    @Override
    public synchronized void addGlobalVariable(String nombre, Object object) {
        try {
	     	   kSesion.setGlobal(nombre, object);
//            kSesion.getGlobals().set(nombre, object);
        } catch (NullPointerException ex) {
            // log.error("ERROR al definir la variable global: " +nombre + " al agente. Revisar los atributos y valores de los objetos definidos en las reglas " +agent .getIdentAgente(), ex);
            trazas.aceptaNuevaTraza(new InfoTraza(agent.getIdentAgente(),
                    "ERROR al definir la variable global: " + nombre + " al agente . Revisar los atributos y valores de los objetos definidos en las reglas ",
                    InfoTraza.NivelTraza.debug));
        }
    }

    @Override
    public KieSession getStatefulKnowledgeSession() {
        return kSesion;
    }

    //type --> INSERT, RETRACT o UPDATE;    
    //object--> el objeto insertado, borrado o actualizado
    private void FactHandlesMonitoring_DEBUGGING(String monitoringType, String wmObject) {
        Collection<FactHandle> cFH;
        String s;
        Iterator it;
        cFH = kSesion.getFactHandles();
        s = "";
        it = cFH.iterator();
        while (it.hasNext()) {
            s = s + " \n " + it.next();
        }

        if (monitoringType.equals("INSERT")) {

            if (factHandlesMonitoringINSERT_DEBUGGING) {
                String info = "FactHandles WM _ despues de INSERT " + wmObject
                        + "( current size=" + kSesion.getFactHandles().size() + "): " + s + "\n\n";
                try {
                    //  	  this.itfUsoRecursoDepuracionCognitivo.mostrarInfoWM(info);
                    trazas.aceptaNuevaTrazaActivReglas(agentId, info);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        if (monitoringType.equals("RETRACT")) {
            if (factHandlesMonitoringRETRACT_DEBUGGING) {
                String info = "FactHandles WM _ despues de RETRACT " + wmObject
                        + "( current size=" + kSesion.getFactHandles().size() + "): " + s + "\n\n";
                try {
                    //     this.itfUsoRecursoDepuracionCognitivo.mostrarInfoWM(info);
                    trazas.aceptaNuevaTrazaActivReglas(agentId, info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (monitoringType.equals("UPDATE")) {
            if (factHandlesMonitoringUPDATE_DEBUGGING) {
                String info = "FactHandles WM _ despues de UPDATE " + wmObject
                        + "( current size=" + kSesion.getFactHandles().size() + "): " + s + "\n\n";
                try {
                    //       this.itfUsoRecursoDepuracionCognitivo.mostrarInfoWM(info);
                    trazas.aceptaNuevaTrazaActivReglas(agentId, info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void trazarRuleActivation() {
        if (depuracionActivationRulesDebugging) {
            kSesion.addEventListener(new DefaultAgendaEventListener());  //fin del addEventListener          	              	  
        }
    }

    private void trazarFactHandlesForRuleDebugging(String ruleId, String beforOrAfterRuleActivation) {
        String infoAmostrar = "";
        Boolean obtenerFactHandles = false;

        if (factHandlesMonitoring_beforeActivationFired_DEBUGGING
                && beforOrAfterRuleActivation.equals(NombresPredefinidos.DROOLS_Debugging_BEFORE_RuleFired)) {
            obtenerFactHandles = true;
            infoAmostrar = "FactHandles WM _ ANTES de  dispararse la regla " + ruleId
                    + "( current size=" + kSesion.getFactHandles().size() + "): " + "\n";
        } else if (factHandlesMonitoring_afterActivationFired_DEBUGGING
                && beforOrAfterRuleActivation.equals(NombresPredefinidos.DROOLS_Debugging_AFTER_RuleFired)) {
            obtenerFactHandles = true;
            infoAmostrar = "FactHandles WM _ DESPUES de  dispararse la regla " + ruleId
                    + "( current size=" + kSesion.getFactHandles().size() + "): " + "\n";
        }
        if (obtenerFactHandles) {
            Collection<FactHandle> cFH;
            Iterator it;
            cFH = kSesion.getFactHandles();
            it = cFH.iterator();
            while (it.hasNext()) {
                infoAmostrar = infoAmostrar + it.next() + " \n ";
            }
            try {
                //		this.itfUsoRecursoDepuracionCognitivo.mostrarInfoAR(infoAmostrar);
                trazas.aceptaNuevaTrazaActivReglas(agentId, infoAmostrar);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // str -> beforeActivationFired o afterActivationFired;   rule es el nombre de la regla

    private void printFactHandlesMonitoring(String str, String rule) {
        Collection<FactHandle> cFH;
        String s;
        Iterator it;
        cFH = kSesion.getFactHandles();
        s = "";
        it = cFH.iterator();
        while (it.hasNext()) {
            s = s + " \n " + it.next();
        }

        if (str.equals("afterActivationFired")) {
            s = s + " \n ";
        }

        String info = "FactHandles WM _ " + str + " dispararse la regla " + rule
                + "( current size=" + kSesion.getFactHandles().size() + "): " + s + "\n";
        try {
//			this.itfUsoRecursoDepuracionCognitivo.mostrarInfoAR(info);
            trazas.aceptaNuevaTrazaActivReglas(agentId, info);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void trazarWorkingMemory() {
        //MOSTRAR POR LA VENTANA DE TRAZAS LA EVOLUCION DE LA MEMORIA DE TRABAJO DEL AGENTE
        //HAY UNA PARA CADA AGENTE. EL NOMBRE DE LA TRAZA ES WM_NOMBREDELAINSTANCIADEAGENTE
        kSesion.addEventListener((RuleRuntimeEventListener) new RuleRunTimeEventListenerImpl());//fin del DefaultWorkingMemoryEventListener listener            	  
    }

    @Override
    public void reinicializarSesion() {
//        Iterator it;
         kSesion.halt();
        Collection<FactHandle> cFH = kSesion.getFactHandles();
        Iterator<FactHandle> it = cFH.iterator();
        while (it.hasNext()) {
            kSesion.retract(it.next());
        }
    }

    @Override
    public ItfConfigMotorDeReglas getItfConfigMotorDeReglas() {
        return this;
    }

    @Override
    public void setDepuracionActivationRulesDebugging(boolean boolValor) {
        depuracionActivationRulesDebugging = boolValor;

    }

    @Override
    public void setDepuracionActivationReglas(boolean activarDepuracion) {
        if (activarDepuracion && !depuracionActivacionReglas) {
            agendaEventListener = new DefaultAgendaEventListener();
            this.kSesion.addEventListener(agendaEventListener);
            depuracionActivacionReglas = true;
        } else if (!activarDepuracion && depuracionActivacionReglas) {
            this.kSesion.removeEventListener(agendaEventListener);
            depuracionActivacionReglas = false;
        }
    }

    public void setDepuracionGestionHechos(boolean activarDepuracion) {
        if (activarDepuracion && !depuracionGestionHechos) {
            ruleRunTimeEventListener = new RuleRunTimeEventListenerImpl();
            this.kSesion.addEventListener((RuleRuntimeEventListener) ruleRunTimeEventListener);
        } else if (!activarDepuracion && depuracionGestionHechos) {
            this.kSesion.removeEventListener((RuleRuntimeEventListener) ruleRunTimeEventListener);
        }
    }

    @Override
    public void setDepuracionHechosInsertados(boolean boolValor) {
        depuracionHechosInsertados = boolValor;

    }

    @Override
    public void setDepuracionHechosModificados(boolean boolValor) {
        depuracionHechosModificados = boolValor;

    }

    @Override
    public void setFactHandlesMonitoring_beforeActivationFired_DEBUGGING(boolean boolValor) {
        factHandlesMonitoring_beforeActivationFired_DEBUGGING = boolValor;
        if (depuracionActivationRulesDebugging) {
            trazarRuleActivation();
        }
    }

    @Override
    public void setfactHandlesMonitoring_afterActivationFired_DEBUGGING(boolean boolValor) {
        factHandlesMonitoring_afterActivationFired_DEBUGGING = boolValor;
        if (depuracionActivationRulesDebugging) {
            trazarRuleActivation();
        }
    }

    @Override
    public void setFactHandlesMonitoring_DEBUGGING(boolean boolValor) {
        factHandlesMonitoring_DEBUGGING = boolValor;
    }

    @Override
    public void setFactHandlesMonitoringINSERT_DEBUGGING(boolean boolValor) {
        factHandlesMonitoringINSERT_DEBUGGING = boolValor;
    }

    @Override
    public void setFactHandlesMonitoringRETRACT_DEBUGGING(boolean boolValor) {
        factHandlesMonitoringRETRACT_DEBUGGING = boolValor;
    }

    @Override
    public void setFactHandlesMonitoringUPDATE_DEBUGGING(boolean boolValor) {
        factHandlesMonitoringUPDATE_DEBUGGING = boolValor;
    }

    @Override
    public void compileRules(InputStream file) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class DefaultAgendaEventListener implements AgendaEventListener {

        public DefaultAgendaEventListener() {
        }

        public void activationCreated(ActivationCreatedEvent activationCreatedEvent) {

//            trazas.aceptaNuevaTrazaEjecReglas(agentId,
//                    "\n regla activada - " + activationCreatedEvent.getActivation().getRule().getName()+
//                            "\n objetos en la activacion : " + activationCreatedEvent.getActivation().getObjects() );
//        log.debug("Activacion creada: "+activationCreatedEvent.toString());
        }

        public void afterActivationFired(AfterActivationFiredEvent event) {
//            super.afterActivationFired( event );
//            String ruleName = event.getActivation().getRule().getName();
            //Mostrar todos los facthandles de la memoria de trabajo
            if (factHandlesMonitoring_afterActivationFired_DEBUGGING) {
//                String info1 = "\n Rule fired -> " + ruleName + "\n";
//                String info2 = "Facts in Agenda After  Rule fired -> " + event.getActivation().getFactHandles().toString() + "\n";
                try {
                    //           itfUsoRecursoDepuracionCognitivo.mostrarInfoAR(info1);
                    trazas.aceptaNuevaTrazaActivReglas(agentId, event.toString());
//                            itfUsoRecursoDepuracionCognitivo.mostrarInfoAR(info2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                trazarFactHandlesForRuleDebugging(,NombresPredefinidos.DROOLS_Debugging_AFTER_RuleFired);
            }
        }

        //Creeo que este metodo nos permite conocer los hechos que provocan que se dispare la regla
        public void beforeActivationFired(BeforeActivationFiredEvent event) {
//            String ruleName = event.getActivation().getRule().getName();
            //Mostrar todos los facthandles de la memoria de trabajo por la traza AR_Agente
            if (factHandlesMonitoring_beforeActivationFired_DEBUGGING) {
//                String info1 = "\nActivate Rule -> " + ruleName + "\n";
//                String info2 = "Facts Activating Rule -> " + event.getActivation().getFactHandles().toString() + "\n";
                trazas.aceptaNuevaTrazaActivReglas(agentId, event.toString());
                //     printFactHandlesMonitoring(NombresPredefinidos.DROOLS_Debugging_BEFORE_RuleFired,event.getActivation().getRule().getName());
//                trazarFactHandlesForRuleDebugging(ruleName,NombresPredefinidos.DROOLS_Debugging_BEFORE_RuleFired);
            }
        }

        @Override
        public void matchCreated(MatchCreatedEvent mce) {

            trazas.aceptaNuevaTrazaActivReglas(agentId, mce.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void matchCancelled(MatchCancelledEvent mce) {
            trazas.aceptaNuevaTrazaActivReglas(agentId, mce.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void beforeMatchFired(BeforeMatchFiredEvent bmfe) {
            trazas.aceptaNuevaTrazaActivReglas(agentId, bmfe.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void afterMatchFired(AfterMatchFiredEvent amfe) {

            trazas.aceptaNuevaTrazaActivReglas(agentId, amfe.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void agendaGroupPopped(AgendaGroupPoppedEvent agpe) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void agendaGroupPushed(AgendaGroupPushedEvent agpe) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent rfgae) {
            trazas.aceptaNuevaTrazaActivReglas(agentId, rfgae.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent rfgae) {
            trazas.aceptaNuevaTrazaActivReglas(agentId, rfgae.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent rfgde) {
            trazas.aceptaNuevaTrazaActivReglas(agentId, rfgde.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent rfgde) {
            trazas.aceptaNuevaTrazaActivReglas(agentId, rfgde.toString());
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private class RuleRunTimeEventListenerImpl {

        public RuleRunTimeEventListenerImpl() {
            super();

        }

        //Se reconoce tanto cuando (1) se hace un insert en una regla, como
        //(2) se hace en una tarea un this.getEnvioHechos().insertarHecho(...)
        public void objectInserted(ObjectInsertedEvent event) {

            String info = "INSERT->valor getObject: " + event.getObject().toString() + " , nroFactHandles -> "
                    + kSesion.getFactHandles().size() + "\n";
            try {
                //  itfUsoRecursoDepuracionCognitivo.mostrarInfoWM(info);
                trazas.aceptaNuevaTrazaActivReglas(agentId, info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (factHandlesMonitoring_DEBUGGING) {
                FactHandlesMonitoring_DEBUGGING("INSERT", event.getObject().toString());
            }
        }

        public void objectUpdated(ObjectUpdatedEvent event) {
            String info = "UPDATE->valor getOldObject: " + event.getOldObject() + " , getObject->" + event.getObject()
                    + " , nroFactHandles -> " + kSesion.getFactHandles().size() + "\n";
            try {
                //   itfUsoRecursoDepuracionCognitivo.mostrarInfoWM(info);
                trazas.aceptaNuevaTrazaActivReglas(agentId, info);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (factHandlesMonitoring_DEBUGGING) {
                FactHandlesMonitoring_DEBUGGING("UPDATE", event.getOldObject().toString());
            }
        }
    }

}
