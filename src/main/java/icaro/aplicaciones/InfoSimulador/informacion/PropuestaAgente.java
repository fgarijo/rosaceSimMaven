/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.InfoSimulador.informacion;

import java.io.Serializable;

/**
 *
 * @author Francisco J Garijo
 */
public class PropuestaAgente implements Serializable {
    // Mensajes validos en las propuestas
   
    public String identAgente;
    public String identAgenteReceptor;
    public String mensajePropuesta;
    public String identObjectRefPropuesta; // identificador de un objeto o contexto al que se refiere la propuesta
    public Object justificacion;

    public PropuestaAgente( ) {
        
    }
    
    public PropuestaAgente(String identAgenteEmisor) {
        identAgente= identAgenteEmisor;
        identAgenteReceptor=null;
        mensajePropuesta =null;
        justificacion = null;
        identObjectRefPropuesta= null;
    }
    public PropuestaAgente(String identAgenteEmisor,String idAgteReceptor) {
        identAgente= identAgenteEmisor;
        identAgenteReceptor=idAgteReceptor;
        mensajePropuesta =null;
        justificacion = null;
        identObjectRefPropuesta= null;
    }
    
    public void   setMensajePropuesta(String msgPropuesta){
        mensajePropuesta =msgPropuesta;
    }
    
    public String   getMensajePropuesta(){
        return mensajePropuesta;
    }

    public String   getIdentAgente(){
        return identAgente;
    }
    public String   getIdentAgenteReceptor(){
        return identAgenteReceptor;
    }
    public void    setIdentAgenteReceptor(String idAgteReceptor){
         identAgenteReceptor =idAgteReceptor ;
    }
 public void   setIdentObjectRefPropuesta(String msgPropuesta){
        identObjectRefPropuesta =msgPropuesta;
    }
    
    public String   getIdentObjectRefPropuesta(){
        return identObjectRefPropuesta;
    }
    public void   setJustificacion(Object contJustificacion){
        justificacion =contJustificacion;
    }

    public Object   getJustificacion(){
        return justificacion;
    }

    @Override
    public String toString(){
        if ( justificacion == null )
            return "Propuesta:-> Agente Emisor :"+identAgente+ " ObjectRefPropuesta :+" + identObjectRefPropuesta+ " MensajePropuesta :+" + mensajePropuesta+ "  Justificacion: null "+"\n ";
        else 
            return "Propuesta:-> Agente Emisor :"+identAgente+ " ObjectRefPropuesta :+" + identObjectRefPropuesta+ " MensajePropuesta :+" + mensajePropuesta+ "  Justificacion: "+justificacion.toString() +"\n ";
    }
    
}
