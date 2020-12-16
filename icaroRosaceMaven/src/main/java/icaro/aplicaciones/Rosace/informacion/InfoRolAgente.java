/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.Rosace.informacion;

/**
 *
 * @author FGarijo
 */
public class InfoRolAgente extends InfoInteraccionBasica implements Cloneable{
    private String identRolAgte ;
    private String identEquipoAgte ;
    public InfoRolAgente (String agentId, String equipoId,String rolId, String iteracionId ){
        super();
        this.setAgteIniciadorId(agentId);
        this.setrefIteracion(iteracionId);
        identRolAgte = rolId;
        identEquipoAgte = equipoId;
    }    
    public String getidentRolAgte(){
         return identRolAgte ;
    }
    public void setidentRolAgte(String agentRolId){
        identRolAgte =agentRolId;
    }
    public String getidentEquipoAgte(){
         return identEquipoAgte ;
    }
    public void setidentEquipoAgte(String identEquipo){
        identEquipoAgte = identEquipo;
    }
    @Override
    public Object clone(){
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            System.out.println(" no se puede duplicar");
        }
        return obj;
    }
}
