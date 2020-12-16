/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.informacion;

import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.entidadesBasicas.excepciones.ExcepcionEnComponente;
import icaro.infraestructura.recursosOrganizacion.configuracion.ItfUsoConfiguracion;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FGarijo
 */
public class InfoEquipo {
      private String  identEquipo  ;
      private String  identAgentePropietario;
      private String  identAgenteJefeEquipo;
      private String  identMiRolEnEsteEquipo = null;
      private Integer numberOfTeamMembers;
      private boolean inicioContactoConEquipo = false;
      private boolean teamAgentIdsDefinidos = false;
      private boolean teamAgentIdsWithMyRolDefinidos = false;
      private ItfUsoConfiguracion itfConfig  ;
      private ArrayList<String> teamRobotIds;
      private ArrayList<String> teamRobotIdsWithMyRol;
      private ArrayList<String> teamRobotIdsActivos;
      private Map<String, RobotStatus1> teamInfoAgentStatus;
    
     public  InfoEquipo (String agtePropietarioId, String identEquipo){       
             this.identEquipo = identEquipo;
             identAgentePropietario= agtePropietarioId;
             getIdentsAgentesEquipoFromConfig(identEquipo);
             identAgenteJefeEquipo=getIdentJefeEquipoFromConfig();
             initializeAgentTeamStatus();
    }
    
    private   void  initializeAgentTeamStatus(){
           teamInfoAgentStatus = new HashMap<>();
           for(int i = 0; i < teamRobotIds.size();  i++ ) {
               String aux = (String) teamRobotIds.get(i); 
               teamInfoAgentStatus.put(aux, null);
                }
     }
     private void getIdentsAgentesEquipoFromConfig(String identEquipo){
            ArrayList agentesRegistrados = this.getIdentsAgentesAplicacionRegistrados();
            teamRobotIds = new ArrayList();
            teamRobotIdsActivos=new ArrayList();
            String aux; Boolean encontrado = false; 
            int numberOfRegAgts =agentesRegistrados.size();
           // int j = 0;
            for(int i = 0; i < numberOfRegAgts;  i++ ) {
                aux = (String) agentesRegistrados.get(i); 
                //Excluimos el propio agente
                if (aux.contains(identEquipo)& (!aux.contains(identAgentePropietario))) {       
                    teamRobotIds.add(aux);
                }            
            }
            this.numberOfTeamMembers =  teamRobotIds.size();    
     }
     public String getIdentJefeEquipoFromConfig(){
          try {
              return  itfConfig.getValorPropiedadGlobal(NombresPredefinidos.NOMBRE_PROPIEDAD_GLOBAL_JEFE_EQUIPO);
          } catch (ExcepcionEnComponente ex) {
              Logger.getLogger(InfoEquipo.class.getName()).log(Level.SEVERE, null, ex);
          } catch (RemoteException ex) {
             Logger.getLogger(InfoEquipo.class.getName()).log(Level.SEVERE, null, ex);
          }
          return null;
     }
    private ArrayList<String> getIdentsAgentesAplicacionRegistrados(){
       
         try {   
            itfConfig = (ItfUsoConfiguracion) NombresPredefinidos.REPOSITORIO_INTERFACES_OBJ.obtenerInterfaz(NombresPredefinidos.ITF_USO+"Configuracion");
  //        teamRobotIds = itfConfig.getIdentificadoresInstanciasAgentesAplicacion();
            return itfConfig.getIdentificadoresInstanciasAgentesAplicacion(); 
            } catch (Exception ex) {
            Logger.getLogger(InfoEquipo.class.getName()).log(Level.SEVERE, null, ex);
            return null;
            }  
         }
      public synchronized void procesarInfoRolAgente(InfoRolAgente infoRol){
          if( infoRol.getidentEquipoAgte().equals(identEquipo)){      
              addAgteAmiEquipo (infoRol.getAgteIniciadorId(),infoRol.getidentRolAgte() );
          }
      }
      public synchronized String getidentAgentePropietario(){ 
        return identAgentePropietario ;
      }
      private void addAgteAmiEquipo(String idAgte,String rolEnEquipoId){
          if (teamInfoAgentStatus.get(idAgte)== null){
              RobotStatus1 estatusAgte = new RobotStatus1();
              estatusAgte.setIdRobotRol(rolEnEquipoId);
              estatusAgte.setIdRobot(idAgte);
              // si no esta entre los que se han obtenido de la configuracion lo meto y si esta no se hace nada
              if(!teamRobotIds.contains(idAgte)){
                  teamRobotIds.add(idAgte);      
              }   
              teamInfoAgentStatus.put(idAgte, estatusAgte);
//              teamRobotIdsActivos.add(idAgte);  
          }
      }
     public synchronized void eliminarIdentAgteDeMiEquipo(String idAgte){
         if (!(idAgte.equals(identAgentePropietario))&&(teamRobotIds.indexOf(idAgte)>0)){
//             teamInfoAgentStatus.remove(idAgte);
             teamRobotIds.remove(teamRobotIds.indexOf(idAgte));
//             teamRobotIdsActivos.remove(teamRobotIds.indexOf(idAgte));
         }
     }
     public synchronized ArrayList<String> getTeamMemberIDsWithThisRol(String rolId){
         ArrayList<String> agtesConMismoRol = new ArrayList();
       //  int indiceagtesConMirol=0;
         RobotStatus1 estatusAgte;
         String identAgte;
         for (int i = 0; i < teamRobotIds.size();  i++ ){
             identAgte = teamRobotIds.get(i);
             if ( !identAgte.equals(this.identAgentePropietario)){
                estatusAgte =teamInfoAgentStatus.get(identAgte);
                if(estatusAgte != null)
                    if(estatusAgte.getIdRobotRol().equals(rolId)){
                        agtesConMismoRol.add(teamRobotIds.get(i));                      
                    }
            } 
         }
         return agtesConMismoRol;
     }
     public synchronized ArrayList<String> getTeamMemberIDsWithMyRol(){
         if (!teamAgentIdsWithMyRolDefinidos && identMiRolEnEsteEquipo != null){
             teamRobotIdsWithMyRol = getTeamMemberIDsWithThisRol(identMiRolEnEsteEquipo);
             if(!teamRobotIdsWithMyRol.isEmpty()) teamAgentIdsWithMyRolDefinidos = true;
         }
         return teamRobotIdsWithMyRol;        
     }
     public synchronized ArrayList<String> getTeamMemberIDs (){
         return this.teamRobotIds;
     }
     public synchronized ArrayList<String> getIDsMiembrosActivos (){
         RobotStatus1 estatusAgte;
         String identAgte;
         ArrayList<String> idsRobotsActivos = new ArrayList();
          for (String teamRobotId : teamRobotIds) {
              identAgte = teamRobotId;
              if ( !identAgte.equals(this.identAgentePropietario)){
                  estatusAgte =teamInfoAgentStatus.get(identAgte);
                  if(estatusAgte != null)
                      if(!estatusAgte.getBloqueado()){
                          idsRobotsActivos.add(identAgte);
                      }
              }
          }
         return idsRobotsActivos;
     }
     public synchronized RobotStatus1 getTeamMemberStatus(String identMember){ 
         return teamInfoAgentStatus.get(identMember);
     }
      public synchronized boolean getinicioContactoConEquipo(){ 
         return inicioContactoConEquipo;
     }
      public synchronized void setinicioContactoConEquipo(){ 
          inicioContactoConEquipo = true;
     }
     public synchronized String getTeamMemberRol(String identAgte){ 
         if ( identMiRolEnEsteEquipo != null && identAgte.equals(identAgentePropietario)) return this.identMiRolEnEsteEquipo;
         else return teamInfoAgentStatus.get(identAgte).getIdRobotRol();
     }
     public synchronized void setTeamMemberStatus( RobotStatus1 robotSt){ 
         teamInfoAgentStatus.put(robotSt.getIdRobot(), robotSt);
     }
     public synchronized void setEstadoTeamMember( InfoEstadoAgente robotEstado){
         String idTeamMember = robotEstado.getidentAgte();
         RobotStatus1 estatusRobot = teamInfoAgentStatus.get(idTeamMember);
         if(estatusRobot==null)estatusRobot= new RobotStatus1();
         estatusRobot.setestadoMovimiento(robotEstado.getidentEstado());
         if(robotEstado.getBloqueado())estatusRobot.setBloqueado(true);
         teamInfoAgentStatus.replace(idTeamMember, estatusRobot);
         
     }
      public synchronized String getEstadoTeamMember( String idRobot){ 
         return teamInfoAgentStatus.get(idRobot).getestadoMovimiento();
     }
    
     public synchronized Boolean isRobotStatusDefined(String robtId){ 
        return  teamInfoAgentStatus.containsKey(robtId);
     }
     public synchronized Integer getNumberOfTeamMembers(){ 
         return  numberOfTeamMembers;
     }
     public synchronized String getTeamId(){ 
        return identEquipo ;
     }
     public void  setTeamId(String idTeam){ 
         identEquipo = idTeam ;
     }
     public synchronized String getidentAgenteJefeEquipo(){ 
        return identAgenteJefeEquipo ;
     }
     public void  setidentAgenteJefeEquipo(String idAgtejefe){ 
         identAgenteJefeEquipo = idAgtejefe ;
     }
     public synchronized String getidentMiRolEnEsteEquipo(){ 
        return identMiRolEnEsteEquipo ;
     }
     public void  setidentMiRolEnEsteEquipo(String idRolAgte){ 
         identMiRolEnEsteEquipo = idRolAgte ;
     }
}
