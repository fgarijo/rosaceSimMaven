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
public class PeticionConfirmarEquipo extends PeticionAgente implements Serializable{
   
   public PeticionConfirmarEquipo (String idAgteEmisor, String mensajePeticion, String identEquipo){
       super(idAgteEmisor);
       super.setMensajePeticion(VocabularioRosace.MsgePeticionConfirmarEquipo);
       super.setJustificacion(identEquipo);
       super.setidentObjectRefPeticion(identEquipo);
   }
//   
//   public void setpeticionAsumida(boolean status){
//       peticionAsumida=status;
//   }
//   public boolean getpeticionAsumida(){
//       return peticionAsumida;
//   }
}
