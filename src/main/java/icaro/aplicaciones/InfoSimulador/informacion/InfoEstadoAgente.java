/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.InfoSimulador.informacion;

/**
 *
 * @author FGarijo
 */
public class InfoEstadoAgente {
    private String identEstado;
    private String identAgte;
    private String refIteracion;
    private boolean bloqueado;
    public InfoEstadoAgente (String agentId,String estadoId,String iteracionRef ){
        identAgte=agentId;
        identEstado=estadoId;
        refIteracion=iteracionRef;
}
    public void setidentEstado ( String estadoId){
        identEstado=estadoId;
        if(estadoId.equalsIgnoreCase(VocabularioRosace.NombreEstadoRobotBloqueado))bloqueado=true;
    }
    public String getidentEstado ( ){
        return identEstado;
    }
    public String getidentAgte(){
         return identAgte ;
    }
    public void setidentAgte(String agentId){
        identAgte =agentId;
    }
   
    public String getrefIteracion(){
         return refIteracion ;
    }
    public void setrefIteracion(String iterId){
        refIteracion =iterId;
    }
    public boolean getBloqueado(){
		return this.bloqueado;
	}

	public void setBloqueado(boolean b){
		this.bloqueado = b;
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
    
