/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp;

/**
 *
 * @author FGarijo
 */
public class MemComunControladores {
    private boolean intencionUsuarioCrearRobot;
    private boolean intencionUsuarioCrearVictima;
    private boolean entidadSeleccionadaParaMover;
    private boolean escenarioSimulacionAbierto = false ;
    private boolean escenarioMovimientoAbierto = false ;
    private boolean simulacionEnCurso = false ;
    private boolean visualizandoResultados = false ;
    EscenarioSimulacionRobtsVictms escenarioSimulacion, escenarioMovimiento,escenarioEdicion;
    private int numeroRobots, mumeroVictimas;
    private VisorMovimientoEscenario visorMovimiento;
    private boolean cambioEnEscenarioSimulacion;
    private int numRobotsdefsEnOrganizacion;
    private String modorganizDefinidoEnOrg;
    private boolean escenarioEdicionAbierto;
    private boolean casoSimulacionPreparado;
    private boolean casoSimulacionIniciado;
    private boolean casoSimulacionFinalizado;
    private boolean paramSimulacionVisualizados;
    private boolean robtsVisualizados;
    private boolean victimsVisualizados;
    private boolean intervaloSecVisualizado;
    public MemComunControladores(){
        
    }
    
    public synchronized void setescenarioSimulacionAbierto(boolean estado){
         escenarioSimulacionAbierto=estado ;
    }
    public synchronized boolean getescenarioSimulacionAbierto(){
        return escenarioSimulacionAbierto;
    }
    public synchronized void setcasoSimulacionPreparado(boolean estado){
         casoSimulacionPreparado=estado ;
    }
    public synchronized boolean getcasoSimulacionPreparado(){
        return casoSimulacionPreparado= escenarioSimulacion!=null&&paramSimulacionVisualizados;
    }
    public synchronized void setcasoSimulacionIniciado(boolean estado){
         casoSimulacionIniciado=estado ;
         if(casoSimulacionIniciado)casoSimulacionFinalizado=false;
    }
    public synchronized boolean getcasoSimulacionIniciado(){
        return casoSimulacionIniciado;
    }
    public synchronized void setcasoSimulacionFinalizado(boolean estado){
         casoSimulacionFinalizado=estado ;
         if(casoSimulacionFinalizado)casoSimulacionIniciado=false;
    }
    public synchronized boolean getcasoSimulacionFinalizado(){
        return casoSimulacionFinalizado;
    }
     public synchronized void setrobtsVisualizados(boolean estado){
         robtsVisualizados=estado ;
    }
     public synchronized void setvictimsVisualizados(boolean estado){
         victimsVisualizados=estado ;
         
    }
     public synchronized void setintervaloSecVisualizado(boolean estado){
         intervaloSecVisualizado=estado ;
         if(intervaloSecVisualizado)paramSimulacionVisualizados=intervaloSecVisualizado&&victimsVisualizados&&robtsVisualizados;
    }
    public synchronized boolean getparamSimulacionVisualizados(){
        return intervaloSecVisualizado&&victimsVisualizados&&robtsVisualizados;
    }
    public synchronized void setescenarioMovimiento(EscenarioSimulacionRobtsVictms escenarioMov){
        escenarioMovimiento = escenarioMov;
    }
    public synchronized  EscenarioSimulacionRobtsVictms getescenarioMovimiento(){
        return escenarioMovimiento ;
    }
    public synchronized void setevisorMovimiento(VisorMovimientoEscenario visorMov){
        visorMovimiento = visorMov;
    }
    public synchronized  VisorMovimientoEscenario getvisorMovimiento(){
        return visorMovimiento ;
    }
    public synchronized void setescenarioSimulacion(EscenarioSimulacionRobtsVictms escenarioSim){
        escenarioSimulacion = escenarioSim;
        if (escenarioSim!=null ){
        this.numRobotsdefsEnOrganizacion=escenarioSimulacion.getNumRobots();
        this.modorganizDefinidoEnOrg=escenarioSimulacion.getmodeloOrganizativo();
        }
    }
    public synchronized EscenarioSimulacionRobtsVictms getescenarioSimulacion(){
        return escenarioSimulacion ;
    }
    public synchronized String getIdentescenarioSimulacion(){
        if(escenarioSimulacion!=null) return escenarioSimulacion.getIdentEscenario();
        else return null; 
    }
    public synchronized String getIdentescenarioEdicion(){
        if(escenarioEdicion!=null) return escenarioEdicion.getIdentEscenario();
        else return null; 
    }
    public synchronized void setescenarioEdicion(EscenarioSimulacionRobtsVictms escenarioEdi){
        escenarioEdicion = escenarioEdi;
    }
    public synchronized EscenarioSimulacionRobtsVictms getescenarioEdicion(){
        return escenarioEdicion ;
    }

    public synchronized boolean getcambioEnEscenarioSimulacion() {
        // se pone a true siempre que cambie el escenario de simulacion, bien porque se abra uno nuevo diferente al qeu habia
//        o porque se edite se guarde  y se vuelva a abrir
       return cambioEnEscenarioSimulacion;
    }
 public synchronized void setcambioEnEscenarioSimulacion(boolean estado){
         cambioEnEscenarioSimulacion=estado ;
    }
 public synchronized void setnumRobotsdefsEnOrganizacion(int numRobots){
         numRobotsdefsEnOrganizacion=numRobots ;
    }
 public synchronized int getnumRobotsdefsEnOrganizacion(){
         return numRobotsdefsEnOrganizacion;
    }
   public synchronized void setmodorganizDefinidoEnOrg(String modorg){
         modorganizDefinidoEnOrg=modorg ;
    }
 public synchronized String getmodorganizDefinidoEnOrg(){
         return modorganizDefinidoEnOrg;
    }

  public  void setescenarioEdicionAbierto(boolean b) {
       this.escenarioEdicionAbierto =b;
    }
  public  boolean getescenarioEdicionAbierto() {
       return escenarioEdicionAbierto;
    }
  public  boolean reqComenzarSimulacion() {
       return (escenarioSimulacion!=null)&&!casoSimulacionIniciado;
    }
  public boolean reqEscnrioEnEdicionParaSimular(){
    return  escenarioEdicionAbierto&&
              escenarioEdicion.getNumRobots()==numRobotsdefsEnOrganizacion&&
              escenarioEdicion.getmodeloOrganizativo().equalsIgnoreCase(modorganizDefinidoEnOrg);
  }

   
}
