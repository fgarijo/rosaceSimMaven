/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.infraestructura.entidadesBasicas.procesadorCognitivo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Esta clase modela los objetivos gestionados por el agente En esta primera
 * version se modelan los objetivos que el agente tiene que realizar y que
 * requiren energia, utilizacion de sensores y otros recursos del agente. Se
 * excluyen los objetivos internos que implican procesos de razomiento o
 * decision Se utiliza una cola de prioridad donde se insertan los objetivos
 * pendientes y se eliminan cuando estan realizados
 *
 * @author FGarijo
 */
public class MisObjetivos {
//    protected PriorityBlockingQueue <Objetivo> misObjetivosPriorizados;

    protected ArrayList<Objetivo> misObjetivosPriorizados;
    protected SortedSet<String> setOfIGoalRefIds; // idetificadores de los objetos a los que se refieren los objetivos ej Identif de vicitmas
    public Objetivo objetivoEnCurso;
    public String categoria;
    protected Comparator c;

    public MisObjetivos() {
        c = new Comparator<Objetivo>() {
            @Override
            public int compare(Objetivo o1, Objetivo o2) {
                if (o1.getPriority() <= (o2.getPriority())) {
                    return 1;
                } //                else if (o1.getPriority() ==  o2.getPriority())return 0;
                else {
                    return -1;
                }
            }
        };
//        misObjetivosPriorizados = new PriorityBlockingQueue <> (20,c);
        misObjetivosPriorizados = new ArrayList();
        setOfIGoalRefIds = new TreeSet<>();
        objetivoEnCurso = null;
    }

    public synchronized void addObjetivo(Objetivo obj) {
        //if (misObjetivosPriorizados == null) misObjetivosPriorizados = new PriorityBlockingQueue <Objetivo> (11,c);
        //Objetivo o = new Objetivo();
        // verificamos que el objetivo no esta en la cola de objetivos
        String goalRefId = obj.getobjectReferenceId();
        if (goalRefId == null) {
            goalRefId = obj.getgoalId();
        }
        setOfIGoalRefIds.add(goalRefId);
         if(misObjetivosPriorizados.isEmpty()){
                    misObjetivosPriorizados.add(obj);
                    return;
                }
        int indiceObjetivo = this.misObjetivosPriorizados.indexOf(obj);
            if (indiceObjetivo>=0) { // si ya existe un objetivo se cambia por el nuevo
                this.misObjetivosPriorizados.set(indiceObjetivo, obj);
                return;
            } 
            // Si no existe se anyade en la posicion que corresponda segun su prioridad
                indiceObjetivo = misObjetivosPriorizados.size()-1; 
                int prioridadObjetivo = obj.getPriority();
                if (misObjetivosPriorizados.get(indiceObjetivo).getPriority() >= prioridadObjetivo ) {
                        misObjetivosPriorizados.add(obj);
                        return;
                }
                while (indiceObjetivo >= 0) {
                    if (misObjetivosPriorizados.get(indiceObjetivo).getPriority()>=prioridadObjetivo ) {
                        misObjetivosPriorizados.add(indiceObjetivo + 1, obj);
                        return;
                    } else {
                        indiceObjetivo--;
                    }
                }
                misObjetivosPriorizados.add(0, obj);

    }

    public void inicializar() {
        misObjetivosPriorizados = new ArrayList();
        setOfIGoalRefIds = new TreeSet<>();
        objetivoEnCurso = null;
    }

    public void setcategoria(String IdentCategoriaObjetivosDecision) {
        categoria = IdentCategoriaObjetivosDecision;
    }

    public String getcategoria() {
        return categoria;
    }

    public synchronized boolean eliminarObjetivo(Objetivo obj) {
        if (obj == null) {
            return false;
        }
        String goalRefId = obj.getobjectReferenceId();
        if (goalRefId == null) {
            return false;
        }
        if (existeObjetivoConEsteIdentRef(goalRefId)) {
            setOfIGoalRefIds.remove(goalRefId);
            misObjetivosPriorizados.remove(obj);
//           objetivoMasPrioritario= misObjetivosPriorizados.peek();
            return true;
        } else {
            return false;
        }
    }

    public synchronized Objetivo getobjetivoMasPrioritario() {
//        objetivoMasPrioritario= misObjetivosPriorizados.peek();
        if (misObjetivosPriorizados.isEmpty()) {
            return null;
        }
        return misObjetivosPriorizados.get(0);
    }

    public synchronized Objetivo getobjetivoEnCurso() {
//        objetivoMasPrioritario= misObjetivosPriorizados.peek();
        return objetivoEnCurso;
    }

    public synchronized void setobjetivoEnCurso(Objetivo obj) {
        // Esto es un poco engagnoso pq no garantiza que el objetivo agnadido sea el mas prioritario
        objetivoEnCurso = obj;
    }

    public void setobjetivoMasPrioritario(Objetivo obj) {
        // Esto es un poco engagnoso pq no garantiza que el objetivo agnadido sea el mas prioritario
        addObjetivo(obj);
    }

    public Boolean existeObjetivoConEsteIdentRef(String identRef) {

        return setOfIGoalRefIds.contains(identRef);

    }

//    public void cambiarPrioridad(Objetivo obj) {
//        Objetivo objetivoNuevo = obj; // obj tiene prioridad diferente que el que esta en la cola 
//        String goalRefId = obj.getobjectReferenceId();
//        if (goalRefId == null) {
//            goalRefId = obj.getgoalId();
//        }
//        if (goalRefId != null && setOfIGoalRefIds.contains(goalRefId)) {
//            this.misObjetivosPriorizados.remove(obj);
//            this.misObjetivosPriorizados.add(objetivoNuevo);
//        }
//    }

    public ArrayList<Objetivo> getMisObjetivosPriorizados() {

        return misObjetivosPriorizados;
    }

    public void deleteObjetivosSolved() {
        for (Objetivo ob:misObjetivosPriorizados ){
            //Hay al menos un objetivo    		
            String obrefId = ob.getobjectReferenceId();
            if (ob.getState() == Objetivo.SOLVED) {
                if (obrefId != null) {
                    setOfIGoalRefIds.remove(obrefId);
                }
                misObjetivosPriorizados.remove(ob);
            }
        }
    }

}
