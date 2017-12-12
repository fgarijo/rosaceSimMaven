/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icaro.aplicaciones.Rosace.informacion;

import org.icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import java.io.Serializable;

/**
 *
 * @author FGarijo
 * Se manda como justificacion el estatus del robot que hace la propuesta
 */
public class PeticionAsumirObjetivo extends PeticionAgente implements Serializable{
    private Objetivo objetivoTransferir;
    private Object infoComplementaria;
    private boolean peticionAsumida;
   public PeticionAsumirObjetivo (String idAgteEmisor, Objetivo objtivoAtransferir, RobotStatus1 justificacion){
       super(idAgteEmisor);
       super.setMensajePeticion(VocabularioRosace.MsgePeticionAsumirObjetivo);
       super.setJustificacion(justificacion.clone());
       super.setidentObjectRefPeticion(objtivoAtransferir.getobjectReferenceId());
       objetivoTransferir = objtivoAtransferir;
   }
   public Objetivo getObjetivoTransferir(){
       return objetivoTransferir;
   }
   public synchronized void setinfoComplementaria(Object complInfo){
       infoComplementaria=complInfo;
   }
   public synchronized Object getinfoComplementaria(){
       return infoComplementaria;
   }
   public void setpeticionAsumida(boolean status){
       peticionAsumida=status;
   }
   public boolean getpeticionAsumida(){
       return peticionAsumida;
   }
}
