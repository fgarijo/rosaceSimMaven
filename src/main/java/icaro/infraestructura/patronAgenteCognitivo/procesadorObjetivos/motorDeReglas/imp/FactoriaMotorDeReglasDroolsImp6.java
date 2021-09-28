/*
 * FactoriaAdaptadorMotorReglasDrools.java
 *
 * Creado 18 de abril de 2017, 11:56
 *
 * 
 */
package icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.motorDeReglas.imp;

import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.patronAgenteCognitivo.factoriaEInterfacesPatCogn.AgenteCognitivo;
import icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.motorDeReglas.FactoriaMotorDeReglas;
import icaro.infraestructura.patronAgenteCognitivo.procesadorObjetivos.motorDeReglas.ItfMotorDeReglas;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.internal.utils.KieHelper;
import org.openide.util.Exceptions;


/**
 * Drools Rule Engine Factory
 *
 * @author F Garijo
 *
 */
public class FactoriaMotorDeReglasDroolsImp6 extends FactoriaMotorDeReglas {

    private ItfUsoRecursoTrazas trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
    private ArrayList<String> ficheroReglasCompilados = new ArrayList();
    private ArrayList<KieBase> kbIdsDefinidos = new ArrayList();
    private KieContainer kContainer;
    private KieBase kBase;
    private KieServices kServices;
    private KieModuleModel kModuleModel;
    private KieRepository kRepo;
    private KieBaseModel kBaseModel;
//    private KieFileSystem kFileSystem;
    private String idAgent;

    /**
     * Nuevas operaciones para compilar las reglas y evitar recompilarlas cuando
     * se crean clones
     */

    @Override
    public ItfMotorDeReglas crearMotorDeReglas(AgenteCognitivo agent) {
        //     return new MotorDeReglasDroolsImp(agent);
        //       return new MotorDeReglasDroolsImp2(agent);
        return new MotorDeReglasDroolsImp6(agent);
    }

    @Override
    public synchronized ItfMotorDeReglas crearMotorDeReglas(AgenteCognitivo agent, InputStream reglas, String rutaReglas) {
        //     Solo se crea el motor si la compilacion de las reglas es correcta    
        //    MotorDeReglasDroolsImp5 motorDrools = new  MotorDeReglasDroolsImp5(agent);
         idAgent= agent.getIdentAgente();
        rutaReglas = rutaReglas.replaceFirst("/", "");
        String kbAgent="kB"+idAgent;
         if(kServices==null)kServices= KieServices.Factory.get();
         if(kModuleModel==null)kModuleModel = kServices.newKieModuleModel();
         if(kRepo==null) kRepo = kServices.getRepository();
//         if(kFileSystem==null) kFileSystem = kServices.newKieFileSystem();
//         if(kContainer==null)kContainer = kServices.newKieContainer(kRepo.getDefaultReleaseId());
           if(ficheroReglasCompilados.isEmpty()||!ficheroReglasCompilados.contains(rutaReglas)) 
                            kBase =  crearKbaseyCompilReglas2(kbAgent,rutaReglas);
//        if(kContainer==null)kContainer = kServices.getKieClasspathContainer();
           else kBase = kbIdsDefinidos.get(ficheroReglasCompilados.indexOf(rutaReglas));
            if (kBase == null) {
//            trazas.aceptaNuevaTraza(new InfoTraza(agent.getIdentAgente(), "Motor de reglas Drools: ERROR en la compilacion de las reglas al crear el agente ", InfoTraza.NivelTraza.error));
            trazas.trazar(idAgent, "Motor de reglas Drools: ERROR en la compilacion de las reglas al crear el agente ", InfoTraza.NivelTraza.error);
            return null;
        } 
         
        MotorDeReglasDroolsImp6 motorDrools = new MotorDeReglasDroolsImp6(agent, this);
        motorDrools.crearSesionConConfiguracionStandard(kBase);
        return motorDrools;
    }
     public synchronized KieBase crearKbaseyCompilReglas(String kBagentId,String rutaReglas) {
        
         KieBaseModel kieBaseModel1 = kModuleModel.newKieBaseModel( kBagentId)
            .setDefault( true )
//            .addPackage("icaro.aplicaciones.agentes.agenteAplicacionAccesoCognitivo.procesoResolucionObjetivos")
            .setEqualsBehavior( EqualityBehaviorOption.EQUALITY );
        KieFileSystem kFileSystem=kServices.newKieFileSystem().writeKModuleXML(kModuleModel.toXML());
        Resource res = kServices.getResources().newClassPathResource(rutaReglas);
//        kFileSystem.write(rutaReglas, res);
        kFileSystem.write( res);
        KieBuilder kbuilder = kServices.newKieBuilder(kFileSystem);
        kbuilder.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        Results results = kbuilder.getResults();
        if (results.hasMessages(Level.ERROR)) {
//         Results results = kContainer.verify();
               List<Message> mensajes = results.getMessages();
                for (Iterator<Message> it = mensajes.iterator(); it.hasNext();) {
                    Message message = it.next();
                    System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
                    trazas.trazar(idAgent, message.getText(), InfoTraza.NivelTraza.error);
                    
                }
          return null;  
        }
                kContainer = kServices.newKieContainer(kRepo.getDefaultReleaseId());
                kBase= kContainer.getKieBase(kBagentId);
                ficheroReglasCompilados.add(rutaReglas);
                kbIdsDefinidos.add(kBase);
                String nombreKbs= kContainer.getKieBaseNames().toString();
        return  kBase;
     }
     public synchronized KieBase crearKbaseyCompilReglas2(String kBagentId,String rutaReglas) {
//        rutaReglas = rutaReglas.replaceFirst("/", "");
         KieHelper kieHelper= new KieHelper();
         kModuleModel= kServices.newKieModuleModel();
         KieBaseModel kieBaseModel1 = kModuleModel.newKieBaseModel( kBagentId)
            .setDefault( true )
            .setEqualsBehavior( EqualityBehaviorOption.EQUALITY );
//         kieBaseModel1.newKieSessionModel(idAgent).
         kieHelper.setKieModuleModel(kModuleModel);
          Resource res = kServices.getResources().newClassPathResource(rutaReglas);
         kieHelper.addResource(res);
        Results results = kieHelper.verify();
        if (results.hasMessages(Level.ERROR)) {
//         Results results = kContainer.verify();
               List<Message> mensajes = results.getMessages();
             for (Message message : mensajes) {
                 System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
                 trazas.trazar(idAgent, message.getText(), InfoTraza.NivelTraza.error);
             }
          return null;  
        }
        kBase = kieHelper.build();
        ficheroReglasCompilados.add(rutaReglas);
        kbIdsDefinidos.add(kBase);
        return kBase;
     }

    public synchronized KieBase compilarReglas(String agentId, InputStream fichero, String ficheroReglas) {
        // verifico que no estan ya compiladas sin errores
        //   String ficheroReglas = fichero.toString() ;
        int indiceFicheroEnArray = ficheroReglasCompilados.indexOf(ficheroReglas);
        //       if (!ficheroReglasCompilados.isEmpty()){        
        if (indiceFicheroEnArray >= 0) {
//            return KbuildersObtenidos.get(indiceFicheroEnArray);
        } else { // se debe compilar
//        PackageBuilder builder = new PackageBuilder();
//      KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
            try {
//                String ruta = "src/icaro/aplicaciones/agentes/agenteAplicacionAccesoCognitivo/procesoResolucionObjetivos/reglasAgenteAcceso.drl";
//                String content = new String(Files.readAllBytes(Paths.get(ficheroReglas)),Charset.forName("UTF-8")); 
                
               KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        String nombreKbs= kContainer.getKieBaseNames().toString();
               Results results = kContainer.verify();
               List<Message> mensajes = results.getMessages();
                for (Iterator<Message> it = mensajes.iterator(); it.hasNext();) {
                    Message message = it.next();
                    System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
                }
                if(results.hasMessages(Message.Level.ERROR))
                    System.out.println(" Se han detectado errores" );
                for(Iterator<String> its =  kContainer.getKieBaseNames().iterator();its.hasNext();){
                     String kieBase =its.next();
                    System.out.println(">> Loading KieBase: "+ kieBase );
                }
 
//                KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
////                 kieFileSystem.write("src/icaro/aplicaciones/agentes/agenteAplicacionAccesoCognitivo/procesoResolucionObjetivos/reglasAgenteAcceso2.drl", content);
////             kieFileSystem.write("src/main/resources/stateFulSessionRule.drl", content);
////            Resource res = kieServices.getResources().newClassPathResource(ficheroReglas);
////                Resource res = kieServices.getResources().newInputStreamResource(fichero); // para probar
////                kieFileSystem=kieFileSystem.write(ficheroReglas, res);
//             KieBuilder  kbuilder = kieServices.newKieBuilder(kieFileSystem);
//                    kbuilder.buildAll();
//                Results resultsKb = kbuilder.getResults();
//                if (resultsKb.hasMessages(Message.Level.ERROR)) {
//                    System.out.println(resultsKb.getMessages());
//                    trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas Drools: ERROR al compilar las reglas. " + ficheroReglas, InfoTraza.NivelTraza.error));
//                    throw new IllegalStateException("### errors ###");
//                }else{
////         KieContainer   kContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
////                   KieSession kSession = kContainer.newKieSession();
////                  kSession.insert("hola");
//                   KieBaseConfiguration kbconf = kieServices.newKieBaseConfiguration();
//	KieBase kbase = kContainer.newKieBase(kbconf);
//                    return kbase;
//                    
//                }
               KieBase kbase2;
                kbase2 = kContainer.getKieBase();
                    return kbase2; 
            } catch (Exception e) {
                trazas.aceptaNuevaTraza(new InfoTraza(agentId, "Motor de Reglas Drools: ERROR al compilar las reglas. " + ficheroReglas, InfoTraza.NivelTraza.error));
                e.printStackTrace();
            }
        }
         return null;
    }
    public KieBase go(String rutaReglas) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
//        InputStream reglas= reglas;
//        String rutaReglas =rutaRegalas;
        String rutaReglas1 = "aplicaciones/agentes/agenteAplicacionAccesoCognitivo/procesoResolucionObjetivos/reglasAgenteAcceso.drl";
//           String content = new String(Files.readAllBytes(Paths.get(rutaReglas)), Charset.forName("UTF-8"));
KieModuleModel kieModuleModel = ks.newKieModuleModel();
KieBaseModel kieBaseModel1 = kieModuleModel.newKieBaseModel( "KBase1 ")
        .setDefault( true )
        .setEqualsBehavior( EqualityBehaviorOption.EQUALITY );
//        .setEventProcessingMode( EventProcessingOption.STREAM );
kfs.writeKModuleXML(kieModuleModel.toXML());
Resource res = ks.getResources().newClassPathResource(rutaReglas.replaceFirst("/", ""));
//kfs.write("src/main/resources/reglasAgenteAcceso.drl", res);
kfs.write( res);
KieBuilder kbuilder = ks.newKieBuilder(kfs);
kbuilder.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
if (kbuilder.getResults().hasMessages(Level.ERROR)) {
    throw new RuntimeException("Build Errors:\n" + kbuilder.getResults().toString());
}

KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
String nombreKbs= kContainer.getKieBaseNames().toString();
//KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
return kContainer.getKieBase("KBase1 ");
//KieBase kbase1 = kContainer.getKieBase("KBase1 ");
//KieBase kbase = kContainer.newKieBase(kbconf);
//String nombreKbs2= kContainer.getKieBaseNames().toString();
//return kbase;
//KieSession kSession =kbase.newKieSession();
////KieSession kSession = kContainer.newKieSession();
//kSession.getGlobals().set(NombresPredefinidos.ITFUSO_RECURSOTRAZAS_GLOBAL, this.trazas);
//kSession.insert("Hola1");
//kSession.insert("Hola");
//kSession.insert("Hola3");
//kSession.fireAllRules();
//Agenda  agendaMotor = kSession.getAgenda();
//Collection<FactHandle> cFH = kSession.getFactHandles();
    }

//    public static void main(String[] args) {
//        new KieFileSystemExample().go(System.out);
//    }
//private  Byte[] getContent(String identFichero){
//    InputStream reglas = this.getClass().getResourceAsStream(identFichero);
//    return reglas.
//}
 private static String getRule() {
        String s = "" +
                   "package org.drools.example.api.kiefilesystem \n\n" +
                   "import org.drools.example.api.kiefilesystem.Message \n\n" +
                   "global java.io.PrintStream out \n\n" +
                   "rule \"rule 10\" when \n" +
                   "    m : \"Hola\" \n" +
                   "then \n" +
                   "    out.println( \"Hola\" + \": \" +  \"Hola Hola Hola \" ); \n" +
                   "end \n" +
                   "rule \"rule 1\" when \n" +
                   "    m : Message( ) \n" +
                   "then \n" +
                   "    out.println( m.getName() + \": \" +  m.getText() ); \n" +
                   "end \n" +
                   "rule \"rule 2\" when \n" +
                   "    Message( text == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                   "then \n" +
                   "    insert( new Message(\"HAL\", \"Dave. I read you.\" ) ); \n" +
                   "end";

        return s;
}
//  public void go2(PrintStream out) {
//      String myRule = "import Drools.Message rule \"Hello World 2\" when message:Message (type==\"Test\") then System.out.println(\"Test, Drools!\"); end";
//      Resource myResource = ResourceFactory.newReaderResource((Reader) new StringReader(myRule));
//      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//      kbuilder.add(myResource, ResourceType.DRL);
//      if(kbuilder.hasErrors()) {
//          System.out.println(kbuilder.getErrors().toString());
//          throw new RuntimeException("unable to compile dlr");
//      }
//      Collection<KnowledgePackage> pkgs;
//      pkgs = kbuilder.getKnowledgePackages();
//      KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//      kbase.addKnowledgePackages(pkgs);
//      StatefulKnowledgeSession ksession;
//      ksession = kbase.newStatefulKnowledgeSession();
//  }
//  public void go3(){
////    MannersBenchmark pruebaManers =  new MannersBenchmark();
////    pruebaManers.arranca();
// KieServices kieServices = KieServices.Factory.get();
//            KieRepository kr = kieServices.getRepository();
//            KieFileSystem kfs = kieServices.newKieFileSystem();
////            String ruta = "aplicaciones/agentes/agenteAplicacionAccesoCognitivo/procesoResolucionObjetivos/reglasAgenteAcceso.drl";
//String ruta = "aplicaciones/agentes/agenteAplicacionAccesoCognitivo/procesoResolucionObjetivos/reglasPrueba1.drl";
//// KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
////                 kfs.write("src/icaro/aplicaciones/agentes/agenteAplicacionAccesoCognitivo/procesoResolucionObjetivos/reglasAgenteAcceso2.drl", content);
////             kieFileSystem.write("src/main/resources/stateFulSessionRule.drl", content);
//            Resource res = kieServices.getResources().newClassPathResource(ruta);
////                Resource res = ks.getResources().newInputStreamResource(fichero); // para probar
//                kfs=kfs.write(res);
//                KnowledgeBuilderConfiguration kbConfig =KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
//             KieBuilder  kbuilder = kieServices.newKieBuilder(kfs);
//             kbConfig.setProperty("drools.dialect.mvel.strict", "false");
//             System.setProperty("drools.dialect.mvel.strict", "false");
//                    kbuilder.buildAll();
//                Results resultsKb = kbuilder.getResults();
//                if (resultsKb.hasMessages(Message.Level.ERROR)) {
//                    System.out.println(resultsKb.getMessages());
//                    trazas.aceptaNuevaTraza(new InfoTraza("agent", "Motor de Reglas Drools: ERROR al compilar las reglas. " + ruta, InfoTraza.NivelTraza.error));
//                    throw new IllegalStateException("### errors ###");
//                }else{
//         KieContainer   kContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
////                   KieSession kSession = kContainer.newKieSession();
////                  kSession.insert("hola");
//                   KieBaseConfiguration kbconf = kieServices.newKieBaseConfiguration();
//	KieBase kbase = kContainer.newKieBase(kbconf);
//                }
//  }
}
