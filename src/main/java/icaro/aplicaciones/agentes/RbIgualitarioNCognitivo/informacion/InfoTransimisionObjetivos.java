/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.aplicaciones.agentes.RbIgualitarioNCognitivo.informacion;

import icaro.aplicaciones.InfoSimulador.informacion.AceptacionPropuesta;
import icaro.aplicaciones.InfoSimulador.informacion.InfoEquipo;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class InfoTransimisionObjetivos implements Serializable {
    private String identEquipo = null;
    private String nombreAgente;
    private InfoEquipo miEquipo;
    private ArrayList<String> agentesEquipo, respuestasAgentes, propuestasEnviadas, propuestasAceptadas;
    private int numPropuestasAceptadas;
    public int respuestasRecibidas = 0;
    public boolean hanLlegadoTodasLasAceptaciones = false;
    public String idClaseObjetivos = null;
    public String refJustificacion = null;

    public InfoTransimisionObjetivos(String identAgente, InfoEquipo equipo, String refJustif) {
        miEquipo = equipo;
        agentesEquipo = miEquipo.getTeamMemberIDs();
        identEquipo = miEquipo.getTeamId();
        nombreAgente = identAgente;
//            respuestasAgentes = new ArrayList();
        propuestasEnviadas = new ArrayList();
        propuestasAceptadas = new ArrayList();
        respuestasRecibidas = 0;
        refJustificacion = refJustif;
    }

    public ArrayList getIdentsAgentesEquipo() {
        if (identEquipo != null) {
            return agentesEquipo;
        } else {
            return null;
        }
    }

    public String getIdenEquipo() {
        return identEquipo;
    }

    public void addInfoPropuestaEnviada(String refPropuesta) {
        if (!propuestasEnviadas.contains(refPropuesta)) {
            int ultimoIndice = propuestasEnviadas.size();
            propuestasEnviadas.add(ultimoIndice, refPropuesta);
            propuestasAceptadas.add(ultimoIndice, "");
        }
    }
    public synchronized String getIdentAgente() {
        return nombreAgente;
    }
    public synchronized Boolean tengoTodasLasPropuestasAceptadas() {

        return (numPropuestasAceptadas == propuestasAceptadas.size());
    }
    public synchronized void anotarAceptacionPropuesta(AceptacionPropuesta acptProp) {
        String idAgteEmisor = acptProp.getIdentAgente();
        String refPropuesta = acptProp.getidentObjectRefAcetPropuesta();
        int indiceRefPropuesta = propuestasEnviadas.indexOf(refPropuesta);
        if (indiceRefPropuesta >= 0) {
            propuestasAceptadas.add(indiceRefPropuesta, idAgteEmisor);
            numPropuestasAceptadas++;
            hanLlegadoTodasLasAceptaciones = (numPropuestasAceptadas == propuestasEnviadas.size());
        }
    }
    public synchronized void setidClaseObjetivos(String claseObjId) {
        idClaseObjetivos = claseObjId;
    }
    public synchronized String getidClaseObjetivos() {
        return idClaseObjetivos;
    }
    @Override
    public String toString() {
        return " ref Justificacion->" + refJustificacion
                + "; InfoTransmisionObjetivos: " + "Agente->" + nombreAgente
                + " ; equipo->" + agentesEquipo
                + " ; propuestas Enviadas ->" + propuestasEnviadas
                + " ; propuestas Aceptadas ->" + propuestasAceptadas
                + " ; hanLlegadoTodasLasAceptaciones->" + hanLlegadoTodasLasAceptaciones
                + " ; numeroAcepacionesRecibidas->" + numPropuestasAceptadas;
    }

}
