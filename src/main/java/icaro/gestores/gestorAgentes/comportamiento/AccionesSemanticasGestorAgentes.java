package icaro.gestores.gestorAgentes.comportamiento;

import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.entidadesBasicas.comunicacion.AdaptadorRegRMI;
import icaro.infraestructura.entidadesBasicas.comunicacion.EventoRecAgte;
import icaro.infraestructura.entidadesBasicas.comunicacion.InfoContEvtMsgAgteReactivo;
import icaro.infraestructura.entidadesBasicas.comunicacion.MensajeSimple;
import icaro.infraestructura.entidadesBasicas.descEntidadesOrganizacion.DescInstancia;
import icaro.infraestructura.entidadesBasicas.descEntidadesOrganizacion.DescInstanciaAgente;
import icaro.infraestructura.entidadesBasicas.descEntidadesOrganizacion.DescInstanciaAgenteAplicacion;
import icaro.infraestructura.entidadesBasicas.descEntidadesOrganizacion.DescInstanciaGestor;
import icaro.infraestructura.entidadesBasicas.descEntidadesOrganizacion.jaxb.TipoAgente;
import icaro.infraestructura.entidadesBasicas.excepciones.ExcepcionEnComponente;
import icaro.infraestructura.entidadesBasicas.factorias.FactoriaComponenteIcaro;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazGestion;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.patronAgenteCognitivo.factoriaEInterfacesPatCogn.FactoriaAgenteCognitivo;
import icaro.infraestructura.patronAgenteReactivo.control.acciones.AccionesSemanticasAgenteReactivo;
import icaro.infraestructura.patronAgenteReactivo.factoriaEInterfaces.FactoriaAgenteReactivo;
import icaro.infraestructura.patronAgenteReactivo.factoriaEInterfaces.ItfGestionAgenteReactivo;
import icaro.infraestructura.patronAgenteReactivo.factoriaEInterfaces.ItfUsoAgenteReactivo;
import icaro.infraestructura.patronAgenteReactivo.factoriaEInterfaces.imp.HebraMonitorizacion;
import icaro.infraestructura.recursosOrganizacion.configuracion.ItfUsoConfiguracion;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza.NivelTraza;
import icaro.infraestructura.recursosOrganizacion.repositorioInterfaces.imp.ClaseGeneradoraRepositorioInterfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Implementacion por defecto de las acciones que se ejecutaron por parte del gestor de agentes.
 * @author     Felipe Polo
 */
public class AccionesSemanticasGestorAgentes extends AccionesSemanticasAgenteReactivo {

    /**
     * Almacen de los nombres de los agentes que este gestor debe gestionar
     */
    protected Vector<String> nombresAgentesGestionados = new Vector<String>();
    /**
     * Depuracion para pruebas
     */
    protected static boolean DEBUG = true;
    /**
     * Hebra para la monitorizacion
     */
    private HebraMonitorizacion hebra;
    private ItfUsoConfiguracion config;
    private ItfUsoAgenteReactivo itfUsoPropiadeEsteAgente;
    /**
     * Tiempo de monitorizacion
     */
    protected final static String SUBTIPO_COGNITIVO = "Cognitivo";
    private TipoAgente tipoAgenteaCrear;
    private int tiempoParaNuevaMonitorizacion;
    private int maxNumIntentosCreacionCompGestionados = 3;
    private String esteNodo;
    private DescInstanciaGestor descGestorAgentes;
    private Boolean misInterfacesEstanEnElRegistroRMILocal = false;
    private ArrayList<DescInstancia> listaDescripcionesAgtesACrear;
    private ArrayList<DescInstanciaAgente> listaDescripcionesGestoresNodo;
    private Integer indiceAgteACrear = 0;
    private Object[] sinParametros = null;

    /**
     * Constructor
     */
    public AccionesSemanticasGestorAgentes() {
        super();
    }

    public void configurarGestor() {
        try {
            config = (ItfUsoConfiguracion) itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_USO
                    + NombresPredefinidos.CONFIGURACION);
            itfUsoPropiadeEsteAgente = (ItfUsoAgenteReactivo) itfUsoRepositorio.obtenerInterfazUso(nombreAgente);
            tiempoParaNuevaMonitorizacion = Integer.parseInt(config.getValorPropiedadGlobal(NombresPredefinidos.INTERVALO_MONITORIZACION_ATR_PROPERTY));
            descGestorAgentes = config.getDescInstanciaGestor(NombresPredefinidos.NOMBRE_GESTOR_AGENTES);
            esteNodo = descGestorAgentes.getNodo().getNombreUso();
            String maxIntentos = config.getValorPropiedadGlobal("maxIntentosCompGestionados");
            if (maxIntentos != null) {
                maxNumIntentosCreacionCompGestionados = Integer.parseInt(maxIntentos);
            }
            this.informaraMiAutomata("gestor_configurado");
//            this.itfUsoPropiadeEsteAgente.aceptaEvento(new EventoRecAgte(
//                    "gestor_configurado",
//                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES,
//                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES));
        } catch (Exception e) {
            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes", "Hubo problemas al configurar el gestor de agentes.",
                    InfoTraza.NivelTraza.error));
        }
    }

    /**
     * Crea los agentes que se especifiquen en la configuracion o los localiza
     * si se encuentran remotos
     *
     */
    public void crearAgentes() {
        // Si se produce excepcion al crear alguno de los agentes, se aborta el
        // proceso.
        try {
            logger.debug("GestorAgentes: Creando los agentes definidos en la configuracion.");

            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Creando los agentes definidos en la configuracion.",
                    InfoTraza.NivelTraza.debug));

            // se examina la configuracion y se obtiene la lista de agentes a
            // crear
            if (config == null) {
                config = (ItfUsoConfiguracion) ClaseGeneradoraRepositorioInterfaces.instance().obtenerInterfaz(
                        NombresPredefinidos.ITF_USO + NombresPredefinidos.CONFIGURACION);
            }

            listaDescripcionesAgtesACrear = (ArrayList<DescInstancia>) config.getDescInstanciaGestor(NombresPredefinidos.NOMBRE_GESTOR_AGENTES).getComponentesGestionados();
            if (listaDescripcionesAgtesACrear.isEmpty()) {

                this.informaraMiAutomata("listaAgentesGestionadosVacia", sinParametros);
                logger.debug("GestorAgentes: La lista de agentes gestionados es vacia.");
                trazas.aceptaNuevaTraza(new InfoTraza(
                        "GestorAgentes",
                        "La lista de agentes gestionados es vacia. Terminado el proceso de creacion de agentes definidos en la configuracion.",
                        InfoTraza.NivelTraza.debug));

            } else if (indiceAgteACrear == listaDescripcionesAgtesACrear.size()) { // Se han creado todos los agentes
                this.informaraMiAutomata("agentes_creados", sinParametros);
            } else //
            if (crearAgente(indiceAgteACrear)) {
                indiceAgteACrear++;
                this.informaraMiAutomata("agente_creado", sinParametros);
            }

        } catch (Exception ex) {
            logger.error("GestorAgentes: Hubo problemas al crear los agentes desde la configuracion. Recuperando errores de creacion.");
            ex.printStackTrace();

            trazas.aceptaNuevaTraza(new InfoTraza(
                    "GestorAgentes",
                    "Hubo problemas al crear los agentes desde la configuracion. Recuperando errores de creacion.",
                    InfoTraza.NivelTraza.error));

            try {
                this.informaraMiAutomata("error_en_creacion_agentes", sinParametros);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Crea un agente reactivo y lo registra en el repositorio
     *
     * @param nombreAgente
     */
    private void crearAgenteReactivoEnEsteNodo(DescInstanciaAgenteAplicacion descInstAgente) throws Exception {
        String identAgenteACrear = descInstAgente.getId();
        try {

            FactoriaComponenteIcaro.instanceAgteReactInpObj().crearAgenteReactivo(descInstAgente);

            // indico a quien debe reportar
            ((ItfGestionAgenteReactivo) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + identAgenteACrear)).setGestorAReportar(
                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES);

        } catch (Exception ex) {

            logger.error("GestorAgentes: Error al crear el agente reactivo "
                    + identAgenteACrear + ".", ex);
            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Error al crear el agente reactivo " + identAgenteACrear + ".",
                    InfoTraza.NivelTraza.error));
            throw ex;
        }
    }

    private void crearAgenteCognitivoEnEsteNodo(DescInstanciaAgenteAplicacion descInstAgente) throws Exception {
        String identAgenteACrear = descInstAgente.getId();
        try {

            FactoriaAgenteCognitivo.instance().crearAgenteCognitivo(descInstAgente);

            // indico a quien debe reportar
//            ((ItfGestionAgenteReactivo) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + identAgenteACrear)).setGestorAReportar(
//                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES);
        } catch (Exception ex) {

            logger.error("GestorAgentes: Error al crear el agente cognitivo "
                    + identAgenteACrear + ".", ex);
            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Error al crear el agente cognitivo " + identAgenteACrear + ".",
                    InfoTraza.NivelTraza.error));
            throw ex;
        }
    }

    private void crearUnAgenteReactivo(String nombreAgente) throws Exception {
        try {

            DescInstanciaAgenteAplicacion instancia = config.getDescInstanciaAgenteAplicacion(nombreAgente);
            FactoriaComponenteIcaro.instanceAgteReactInpObj().crearAgenteReactivo(instancia);
            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Agente reactivo " + nombreAgente + " creado.",
                    InfoTraza.NivelTraza.debug));
            ((ItfGestionAgenteReactivo) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + nombreAgente)).setGestorAReportar(
                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES);

        } catch (Exception ex) {

            logger.error("GestorAgentes: Error al crear el agente reactivo "
                    + nombreAgente + ".", ex);

            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Error al crear el agente reactivo " + nombreAgente + ".",
                    InfoTraza.NivelTraza.error));

            throw ex;
        }
    }
    private void crearUnAgenteCognitivo(String nombreAgente) throws Exception {
        try {
            ItfUsoConfiguracion config = (ItfUsoConfiguracion) ClaseGeneradoraRepositorioInterfaces.instance().obtenerInterfaz(
                    NombresPredefinidos.ITF_USO + NombresPredefinidos.CONFIGURACION);
            logger.debug("GestorAgentes: Construyendo agente Cognitivo " + nombreAgente + ".");
            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Construyendo agente Cognitivo " + nombreAgente + ".",
                    InfoTraza.NivelTraza.debug));
            // creamos el agente y lo registramos en el repositorio
            // Agentes de aplicacion: local o remoto?
            DescInstanciaGestor descGestorAgentes = config.getDescInstanciaGestor(NombresPredefinidos.NOMBRE_GESTOR_AGENTES);
            String esteNodo = descGestorAgentes.getNodo().getNombreUso();
            DescInstanciaAgenteAplicacion instancia = config.getDescInstanciaAgenteAplicacion(nombreAgente);
            String nodoDestino = instancia.getNodo().getNombreUso();
            boolean ok = false;
            int intentos = 0;
            if (nodoDestino.equals(esteNodo)) {
                FactoriaAgenteCognitivo.instance().crearAgenteCognitivo(instancia);
            } else {
                while (!ok) {
                    ++intentos;
                    try {
                        ((FactoriaAgenteCognitivo) this.itfUsoRepositorio.obtenerInterfaz(
                                NombresPredefinidos.FACTORIA_AGENTE_COGNITIVO + nodoDestino)).crearAgenteCognitivo(instancia);
                        ok = true;
                    } catch (Exception e) {

                        trazas.aceptaNuevaTraza(new InfoTraza(
                                "GestorAgentes",
                                "Error al crear el agente " + nombreAgente + " en un nodo remoto. Se volveri a intentar en " + intentos + " segundos...\n nodo origen: " + esteNodo + "\t nodo destino: " + nodoDestino,
                                InfoTraza.NivelTraza.error));

                        logger.error("Error al crear el agente " + nombreAgente + " en un nodo remoto. Se volveri a intentar en "
                                + intentos + " segundos...\n nodo origen: " + esteNodo
                                + "\t nodo destino: " + nodoDestino);

                        Thread.sleep(1000 * intentos);
                        ok = false;
                    }
                }
            }

            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Agente Cognitivo " + nombreAgente + " creado.",
                    InfoTraza.NivelTraza.debug));

            Set<Object> conjuntoEventos = new HashSet<Object>();
            conjuntoEventos.add(EventoRecAgte.class);

        } catch (Exception ex) {

            logger.error("GestorAgentes: Error al crear el agente Cognitivo "
                    + nombreAgente + ".", ex);

            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Error al crear el agente Cognitivo " + nombreAgente + ".",
                    InfoTraza.NivelTraza.error));

            throw ex;
        }

    }

    private void crearUnAgenteEnNodoRemotoConRMI(DescInstanciaAgenteAplicacion descAgenteAcrear) throws Exception {
        // Obtener el RMI regitry del nodo y la interfaz del Gestor de nodo

        String identNodoAgente = descAgenteAcrear.getNodo().getNombreUso();
        String identAgenteAcrear = descAgenteAcrear.getId();
        if (!misInterfacesEstanEnElRegistroRMILocal) { //Las anyado -por ahora solo la interafz de uso
            if (!AdaptadorRegRMI.registroRMILocalCreado) {
                AdaptadorRegRMI.inicializar();
            }
            if (!AdaptadorRegRMI.addElement2LocalRegRMI(NombresPredefinidos.ITF_USO + nombreAgente, this.itfUsoPropiadeEsteAgente)) // No se ha podido anyadir la interfaz de uso al registro local
            {
                trazas.aceptaNuevaTraza(new InfoTraza("GestorRecursos",
                        "Error al anyadir mi interfaz al registro RMI local. " + nombreAgente + "No se ha podido anyadir  la itf de uso del Gestor de nodo en el registro RMI local " + identNodoAgente,
                        InfoTraza.NivelTraza.error));
            }
        }
        try {
            int intentos = 0;
            boolean ok = false;
//            DescInstanciaGestor gestorAgentes = config.getDescInstanciaGestor(identAgente);
//            String nodoDestino = gestorAgentes.getNodo().getNombreUso();
            while (!ok & (intentos <= maxNumIntentosCreacionCompGestionados)) {
                ++intentos;
                try {
                    // Se obtiene la interfaz remota del gestor de Nodo
                    // Se ordena al Gestor de Nodo que cree al Gestor. Se debe esperar la confirmacion
//                  String[] lista = nodeRegistry.list();
//                  Object obj = nodeRegistry.lookup(NombresPredefinidos.NOMBRE_GESTOR_NODO);
//                    ItfUsoAgenteReactivo itfUsoGestorNodo = (ItfUsoAgenteReactivo)nodeRegistry.lookup(NombresPredefinidos.NOMBRE_GESTOR_NODO);

                    ItfUsoAgenteReactivo itfUsoGestorNodo = (ItfUsoAgenteReactivo) AdaptadorRegRMI.getItfAgteReactRemoto(identNodoAgente, NombresPredefinidos.NOMBRE_GESTOR_NODO);
                    if (itfUsoGestorNodo != null) {
                        itfUsoGestorNodo.aceptaMensaje(new MensajeSimple(new InfoContEvtMsgAgteReactivo("peticion_crearAgente", (Object) identAgenteAcrear),
                                 this.nombreAgente, NombresPredefinidos.NOMBRE_GESTOR_NODO));
                        addDescripcionGN(identNodoAgente, itfUsoGestorNodo);
                        ok = true;
                    } else { // La interfaz del GN es null
                        trazas.aceptaNuevaTraza(new InfoTraza("GestorRecursos",
                                "Error al crear el recurso. " + identAgenteAcrear + "No se ha podido obtener la itf de uso del Gestor de nodo en el nodo remoto " + identNodoAgente,
                                InfoTraza.NivelTraza.error));
                    }
                } catch (Exception e) {
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente, "Error al crear el recurso:  " + identAgenteAcrear + " en un nodo remoto. Se volveraa intentar en " + intentos + " segundos...\n No se pudo acceder al Gestor de Nodo en el nodo: "
                            + identAgenteAcrear, InfoTraza.NivelTraza.error));
                    logger.error("Error al crear el recurso " + identAgenteAcrear + " en un nodo remoto. Se volvera a intentar en " + intentos + " segundos...\n No se pudo acceder al Gestor de Nodo en el nodo: " + identNodoAgente);

                }

                Thread.sleep(500 * intentos);
//                    ok = false;
            }

            if (!ok) {
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente, "No se pudo crear el agente " + identAgenteAcrear + " en un nodo remoto. Se supero el numero de intentos definido " + intentos, InfoTraza.NivelTraza.error));
                logger.error("Error al crear el recurso " + NombresPredefinidos.NOMBRE_GESTOR_RECURSOS + " en un nodo remoto. Se supero el numero de intentos definido " + intentos);
                this.generarInformeErrorIrrecuperable();

            }
            logger.debug("GestorRecursos : Recurso :" + identAgenteAcrear + "creado.");
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente, "Recurso :" + identAgenteAcrear + "creado.", InfoTraza.NivelTraza.debug));
        } catch (Exception e) {
            logger.error("GestorAgentes: Error al crear el agente " + identAgenteAcrear + "en el nodo remoto " + identNodoAgente, e);
            trazas.aceptaNuevaTraza(new InfoTraza(identAgenteAcrear,
                    "Error al crear el agente " + identAgenteAcrear + "en el nodo remoto " + identNodoAgente,
                    InfoTraza.NivelTraza.error));
            e.printStackTrace();
            try {
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    public void addDescripcionGN(String identNodoGN, InterfazUsoAgente itfUsoGestorNodo) {
        if (listaDescripcionesGestoresNodo == null) {
            listaDescripcionesGestoresNodo = new ArrayList<DescInstanciaAgente>();
        }
        // creamos la instancia y anyadimos la información corespondiente

        DescInstanciaAgente descGN = new DescInstanciaAgente();
        descGN.setId(NombresPredefinidos.NOMBRE_GESTOR_NODO + identNodoGN);
        descGN.setNodo(AdaptadorRegRMI.getDescrNodo(identNodoGN));
        descGN.setIntfUsoAgente(itfUsoGestorNodo);
        // anyado el gestor de nodo a la lista de descriptores de GN
        listaDescripcionesGestoresNodo.add(descGN);

    }

    /**
     * intenta crear de nuevo los agentes que especifique la config y hayan dado
     * problemas.
     */
    public void recuperarErrorCreacionAgentes() {
        // por defecto no se implementan politicas de recuperacion
        logger.debug("GestorAgentes: No es posible recuperar error en creacion de los agentes.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "No es posible recuperar error en creacion de los agentes. ",
                InfoTraza.NivelTraza.debug));

        try {
            this.informaraMiAutomata("imposible_recuperar_creacion");
//            this.itfUsoPropiadeEsteAgente.aceptaEvento(new EventoRecAgte(
//                    "imposible_recuperar_creacion",
//                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES,
//                    NombresPredefinidos.NOMBRE_GESTOR_AGENTES));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * arranca los agentes que se especifiquen en la config.
     */
    public void arrancarAgentes() {
        logger.debug("GestorAgentes: Arrancando agentes.");

        trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                "Arrancando agentes. ", InfoTraza.NivelTraza.debug));

        // centinela
        boolean errorEnArranque = false;
        boolean esAgenteRemoto = false;

        // recorremos todos los agentes que se han creado
        Enumeration enumAgentes = this.nombresAgentesGestionados.elements();
        while (enumAgentes.hasMoreElements()) {
            try {
                String nombre = (String) enumAgentes.nextElement();
                // para cada agente, recuperamos su itf de gestion
                logger.debug("GestorAgentes: Es necesario arrancar el agente "
                        + nombre + ", recuperando interfaz de gestion.");

                trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                        "Es necesario arrancar el agente " + nombre + ", recuperando interfaz de gestion. ",
                        InfoTraza.NivelTraza.debug));
                esAgenteRemoto = false;

                InterfazGestion itfGesAg = (InterfazGestion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + nombre);
                // arrancamos el agente

                if (itfGesAg == null) {// puede ser que se trate de un agente en un nodo remoto buscamos en los repositorios RMI
                    itfGesAg = (InterfazGestion) AdaptadorRegRMI.getItfAgenteRemoto(nombre, NombresPredefinidos.ITF_GESTION);
                    if (itfGesAg != null) {
                        esAgenteRemoto = true;
                    }
                }
                if (itfGesAg != null) { // arrancamos el agente que puede ser local o remoto
                    trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                            "Arrancando el agente " + nombre + ".",
                            InfoTraza.NivelTraza.debug));
                    logger.debug("GestorAgentes: Arrancando el agente " + nombre + ".");
                    itfGesAg.arranca();
                    logger.debug("GestorAgentes: Orden de arranque ha sido dada al agente " + nombre + ".");
                    trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                            "Orden de arranque ha sido dada al agente " + nombre + ".", InfoTraza.NivelTraza.debug));
                    // si el agente es remoto almacenamos sus interfaces en el repositorio de interfaces
                    if (esAgenteRemoto) {
                        itfUsoRepositorio.registrarInterfaz(NombresPredefinidos.ITF_GESTION + nombre, itfGesAg);
                        Remote itfUsoAg = AdaptadorRegRMI.getItfAgenteRemoto(nombre, NombresPredefinidos.ITF_USO);
                        itfUsoRepositorio.registrarInterfaz(NombresPredefinidos.ITF_USO + nombre, itfUsoAg);
                    }
                    } else {  // la interf de gestion  es null
                        trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                            "No se pudo arrancar  el agente " + nombre + ". Porque su interfaz no esta registrada en el repositorio",
                            InfoTraza.NivelTraza.error));
                        logger.debug("GestorAgentes: No se pudo arrancar  el agente  " + nombre
                            + ". Porque  su interfaz no esta registrada en el repositorio");
                    // Le debo enviar la posicion del agente en la lista de agentes a arrancar para que se pueda recuperar
                    // Lo dejamos pendiente
                    // Object[] parametros = new Object[] { nombre };
                        this.informaraMiAutomata("error_en_arranque_agente");
                    }

            } catch (Exception ex) {

                trazas.aceptaNuevaTraza(new InfoTraza(
                        "GestorAgentes",
                        "Hubo un problema al acceder a un interfaz remoto mientras se arrancaban los agentes en el gestor de agentes.",
                        InfoTraza.NivelTraza.error));

                logger.error("GestorAgentes: Hubo un problema al acceder a un interfaz remoto mientras se arrancaban los agentes en el gestor de agentes.");
                ex.printStackTrace();
                errorEnArranque = true;
            }
        }

        hebra = new HebraMonitorizacion(tiempoParaNuevaMonitorizacion,
                this.itfUsoPropiadeEsteAgente, "monitorizar");
        hebra.start();
        if (errorEnArranque) {
            logger.error("GestorAgente: Se produjo un error en el arranque de los agentes.");
            trazas.aceptaNuevaTraza(new InfoTraza(
                    "GestorAgentes",
                    "Se produjo un error en el arranque de los agentes.",
                    InfoTraza.NivelTraza.error));

            try {
                this.informaraMiAutomata("error_en_arranque_agentes");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.debug("GestorAgentes: Terminado proceso de arranque automatico de agentes.");

            trazas.aceptaNuevaTraza(new InfoTraza(
                    "GestorAgentes",
                    "Terminado proceso de arranque automatico de agentes.",
                    InfoTraza.NivelTraza.debug));

            try {
                this.informaraMiAutomata("agentes_arrancados_ok");
                this.comunicator.enviarInfoAotroAgente("gestor_agentes_arrancado_ok", NombresPredefinidos.NOMBRE_GESTOR_ORGANIZACION);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Decide quu hacer en caso de fallos en los agentes.
     */
    public void decidirTratamientoErrorIrrecuperable() {
        // el tratamiento sera siempre cerrar todo el chiringuito
        logger.debug("GestorAgentes: Se decide cerrar el sistema ante un error crutico irrecuperable.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Se decide cerrar el sistema ante un error critico irrecuperable.",
                InfoTraza.NivelTraza.debug));

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Terminado proceso de arranque automatico de agentes.",
                InfoTraza.NivelTraza.debug));

        try {
            this.informaraMiAutomata("tratamiento_terminar_agentes_y_gestor_agentes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * intenta arrancar los agentes que especifique la config y hayan dado
     * problemas.
     */
    public void recuperarErrorArranqueAgentes() {
        // por defecto no se implementan politicas de recuperacion
        //logger.debug("GestorAgentes: Fue imposible recuperar el error en el arranque de los agentes.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Fue imposible recuperar el error en el arranque de los agentes.",
                InfoTraza.NivelTraza.debug));

        try {
            this.informaraMiAutomata("imposible_recuperar_arranque");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elabora un informe del estado en el que se encuentran los agentes y lo
     * envia al sistema de trazas.
     */
    public void generarInformeErrorIrrecuperable() {
        // Producimos traza de un error
        logger.error("GestorAgentes: Finalizando gestor de agentes debido a un error irrecuperable.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Finalizando gestor de agentes debido a un error irrecuperable.",
                InfoTraza.NivelTraza.error));

        try {
            this.comunicator.enviarInfoAotroAgente("error_en_arranque_gestores", NombresPredefinidos.NOMBRE_GESTOR_ORGANIZACION);
            this.informaraMiAutomata("informe_generado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * da orden de terminar a un agente
     */
    public void finalizarAgente() {
        // esto hay que recuperarlo de los parametros
        logger.debug("GestorAgentes: finalizarAgente():Este metodo no esta implementado");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "finalizarAgente():Este metodo no esta implementado",
                InfoTraza.NivelTraza.debug));

        throw new UnsupportedOperationException();
    }

    /**
     * Crea y arranca un agente. Es necesario pasar las características del
     * agente a crear por parámetro.
     */
    public boolean crearAgente(Integer indice) throws Exception {
        Iterator<DescInstancia> iterador = listaDescripcionesAgtesACrear.iterator();
        logger.debug("GestorAgentes: Creando agente.");
        trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                "Creando agente.",
                InfoTraza.NivelTraza.debug));
        int j = 0;
//		String nombre;
        boolean error = false;
        boolean encontrado = false;
        DescInstancia agente = null;
        boolean esAgtRemoto = false;

        agente = listaDescripcionesAgtesACrear.get(indice);
        DescInstanciaAgenteAplicacion instanciaActual = (DescInstanciaAgenteAplicacion) agente;
        String identAgenteAcrear = instanciaActual.getId();

        logger.debug("GestorAgentes: Creando agente " + identAgenteAcrear
                + ".");
        // Si el agente es remoto damos la orden al gestor de nodo para que lo cree

        esAgtRemoto = esAgenteRemoto(identAgenteAcrear);

        if (esAgtRemoto) {
            crearUnAgenteEnNodoRemotoConRMI(instanciaActual);
        } else { // Se debe crear en este nodo

            trazas.aceptaNuevaTraza(new InfoTraza("GestorAgentes",
                    "Creando agente " + identAgenteAcrear + ".",
                    InfoTraza.NivelTraza.debug));

            // crearlos uno a uno dependiendo de su tipo
            tipoAgenteaCrear = instanciaActual.getDescComportamiento().getTipo();
            if (tipoAgenteaCrear == TipoAgente.REACTIVO) {
                crearAgenteReactivoEnEsteNodo(instanciaActual);
            } else if (tipoAgenteaCrear == TipoAgente.COGNITIVO || tipoAgenteaCrear == TipoAgente.ADO
                    || tipoAgenteaCrear == TipoAgente.DIRIGIDO_POR_OBJETIVOS) {
                crearAgenteCognitivoEnEsteNodo(instanciaActual);
            } else {
                trazas.aceptaNuevaTraza(new InfoTraza(
                        "GestorAgentes",
                        "El subtipo del agente " + identAgenteAcrear + " definido en la configuracion no es correcto.",
                        InfoTraza.NivelTraza.error));

                logger.error("El subtipo del agente " + identAgenteAcrear + " definido en la configuracion no es correcto.");
                error = true;
                throw new Exception("El subtipo del agente " + identAgenteAcrear + " definido en la configuracion no es correcto.");

            }
        }
        // si todo ha ido bien, debemos anyadirlo a la lista de objetos
        // gestionados por el gestor
        // Suponemos que todo ha ido bien y  lo anyadimos a  la lista de agentes gestionados

        // Si es un agente remoto debemos esperar a que se reciba  una confirmación de su creación
        if (!esAgtRemoto) {
            // Si la organizacion es distribuida lo registramos en en RMI
            if (!this.config.despliegueOrgEnUnSoloNodo()) {
                this.addEntityIntfs2Local_RMIregistry(identAgenteAcrear);
            }
            if (error == false) {
                logger.debug("GestorAgentes: Anyadiendo agente " + identAgenteAcrear + " a la lista de objetos gestionados.");
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                        "Anyadiendo agente " + identAgenteAcrear + " a la lista de objetos gestionados.",
                        InfoTraza.NivelTraza.debug));
                this.nombresAgentesGestionados.add(identAgenteAcrear);
                return true;
            }
        }
        return false;
    }

    public void addAgenteAListaAgtesGestionados(String identAgente) {

        logger.debug("GestorAgentes: Anyadiendo agente " + identAgente + " a la lista de objetos gestionados.");

        trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                "Anyadiendo agente " + identAgente + " a la lista de objetos gestionados.",
                InfoTraza.NivelTraza.debug));
        this.nombresAgentesGestionados.add(identAgente);
        // continuamos el proceso de creación.
        indiceAgteACrear++;
        this.informaraMiAutomata("agente_creado");

    }

    public Boolean esAgenteRemoto(String identAgente) {
        String nodoAgente;
        try {
            nodoAgente = config.getDescInstanciaAgenteAplicacion(identAgente).getNodo().getNombreUso();
            if (nodoAgente.equals(esteNodo)) {
                return false;
            } else {
                return true;
            }
        } catch (ExcepcionEnComponente ex) {
            Logger.getLogger(AccionesSemanticasGestorAgentes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AccionesSemanticasGestorAgentes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean esRecursoRemoto(String identRecurso) {
        String nodoRecurso;
        try {
            nodoRecurso = config.getDescInstanciaRecursoAplicacion(identRecurso).getNodo().getNombreUso();
            if (nodoRecurso.equals(esteNodo)) {
                return false;
            } else {
                return true;
            }
        } catch (ExcepcionEnComponente ex) {
            Logger.getLogger(AccionesSemanticasGestorAgentes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AccionesSemanticasGestorAgentes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean addEntityIntfs2Local_RMIregistry(String identEntity) {
        Boolean interfacesRegistradas = false;
        try {
            Remote itfEntity = (Remote) itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_USO + identEntity);
            if (itfEntity != null) {
                if (AdaptadorRegRMI.addElement2LocalRegRMI(NombresPredefinidos.ITF_USO + identEntity, itfEntity)) {
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                            "Se anyaden las Itfs de uso y de gestion de la entidad :" + identEntity + " -- al RMI registry local",
                            InfoTraza.NivelTraza.debug));
                } else {
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                            "No he podido anyadir la entidad :" + identEntity + " -- al RMI registry local.",
                            InfoTraza.NivelTraza.error));
                    informaraMiAutomata("error_en_registroRemoto_recurso");
                    return interfacesRegistradas;
                }
            } else {
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                        "No he podido anyadir la entidad :" + identEntity + " -- al RMI registry. La entidad no ha sido registrada en el registro local",
                        InfoTraza.NivelTraza.error));
                informaraMiAutomata("error_en_registroRemoto_recurso");
                return interfacesRegistradas;
            }
            itfEntity = (Remote) itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + identEntity);
            if (itfEntity != null) {
                if (AdaptadorRegRMI.addElement2LocalRegRMI(NombresPredefinidos.ITF_GESTION + identEntity, itfEntity)) {
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                            "Se anyaden las Itfs de uso y de gestion de la entidad :" + identEntity + " -- al RMI registry local",
                            InfoTraza.NivelTraza.debug));
                    interfacesRegistradas = true;
                } else {
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                            "No he podido anyadir la entidad :" + identEntity + " -- al RMI registry local.",
                            InfoTraza.NivelTraza.error));
                    informaraMiAutomata("error_en_registroRemoto_recurso");
                    return false;
                }
            } else {
                trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                        "No he podido anyadir la entidad :" + identEntity + " -- al RMI registry. La entidad no ha sido registrada en el registro local",
                        InfoTraza.NivelTraza.error));
                informaraMiAutomata("error_en_registroRemoto_recurso");
                return false;
            }

        } catch (Exception ex) {
            trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente,
                    "Error al registrar  la entidad " + identEntity + ".",
                    InfoTraza.NivelTraza.error));
            logger.error("GestorRecursos: Error al crear el recurso " + identEntity + ". En el registro RMI local", ex);
            informaraMiAutomata("error_en_registroRemoto_recurso");
//			throw ex;
        }
        return interfacesRegistradas;
    }

    /**
     * Monitoriza secuencialmente todos los agentes activos que estan
     * definidos como necesarios en la configuracion.
     */
    public void monitorizarAgentes() {
        // if(DEBUG) System.out.println("GestorAgentes:Comienza ciclo
        // monitorizacion.");

        boolean errorEncontrado = false;
        // recuperar todos los interfaces de gestion del repositorio que estamos
        // gestionando
        Enumeration enume = nombresAgentesGestionados.elements();
        while (enume.hasMoreElements() && !errorEncontrado) {
            String nombre = (String) enume.nextElement();
            try {
                InterfazGestion itfGes = (InterfazGestion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + nombre);
                int monitoriz = itfGes.obtenerEstado();
                if (monitoriz == InterfazGestion.ESTADO_ERRONEO_IRRECUPERABLE || monitoriz == InterfazGestion.ESTADO_ERRONEO_RECUPERABLE || monitoriz == InterfazGestion.ESTADO_TERMINADO || monitoriz == InterfazGestion.ESTADO_TERMINANDO) {
                    errorEncontrado = true;
                    logger.debug("GestorAgentes:Agente " + nombre + " esta en estado erroneo o terminado.");

                    trazas.aceptaNuevaTraza(new InfoTraza(
                            "GestorAgentes",
                            "Agente " + nombre + " esta en estado erroneo o terminado.",
                            InfoTraza.NivelTraza.debug));

                }
            } catch (Exception ex) {
                errorEncontrado = true;
                logger.error("GestorAgentes: No se pudo acceder al repositorio.");
                ex.printStackTrace();
            }
        }

        if (errorEncontrado) {
            try {
                this.informaraMiAutomata("error_al_monitorizar");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.informaraMiAutomata("agentes_ok");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Da orden de terminacion a todos los agentes que se encuentran
     * activos/arrancando
     */
    public void terminarAgentesActivos() {
        logger.debug("GestorAgentes: Terminando los agentes que estan activos.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Terminando los agentes que estan activos.",
                InfoTraza.NivelTraza.debug));

        // recorremos todos los agentes gestionados
        Enumeration enumAgentes = this.nombresAgentesGestionados.elements();
        String nombre = "";
        while (enumAgentes.hasMoreElements()) {
            try {
                nombre = (String) enumAgentes.nextElement();
                // para cada agente, recuperamos su itf de gestion
                logger.debug("GestorAgentes: Recuperando Itf Gestion del agente " + nombre + ".");

                trazas.aceptaNuevaTraza(new InfoTraza(
                        "GestorAgentes",
                        "Recuperando Itf Gestion del agente " + nombre + ".",
                        InfoTraza.NivelTraza.debug));

                InterfazGestion itfGesAg = (InterfazGestion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + nombre);
                // finalizamos el agente
                logger.debug("GestorAgentes: Terminando el agente " + nombre + ".");
                try {
                    trazas.aceptaNuevaTraza(new InfoTraza(
                            "GestorAgentes",
                            "Terminando el agente " + nombre + ".",
                            InfoTraza.NivelTraza.debug));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (itfGesAg != null) {
                    itfGesAg.termina();
                    logger.debug("GestorAgentes: Orden de terminacion ha sido dada al agente " + nombre + ".");

                    trazas.aceptaNuevaTraza(new InfoTraza(
                            "GestorAgentes",
                            "Orden de terminacion ha sido dada al agente " + nombre + ".",
                            InfoTraza.NivelTraza.debug));
                }
            } catch (Exception ex) {
                logger.error("GestorAgentes: Hubo un problema al acceder a un interfaz remoto mientras se daba orden de terminacion al agente " + nombre + ".");

                trazas.aceptaNuevaTraza(new InfoTraza(
                        "GestorAgentes",
                        "Hubo un problema al acceder a un interfaz remoto mientras se daba orden de terminacion al agente " + nombre + ".",
                        InfoTraza.NivelTraza.error));

                ex.printStackTrace();
            }
        }
        logger.debug("GestorAgentes: Finalizado proceso de terminacion de todos los agentes.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Finalizado proceso de terminacion de todos los agentes.",
                InfoTraza.NivelTraza.debug));
        if (listaDescripcionesAgtesACrear == null) {
            try {
                this.informaraMiAutomata("agentes_terminados");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            peticionTerminacionGestoresNodo();
        }
    }

    public void peticionTerminacionGestoresNodo() {
        logger.debug("GestorAgentes: Envio de peticiones de terminacion a los gestores de nodo.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                nombreAgente,
                "Envio de peticiones de terminacion a los gestores de nodo",
                InfoTraza.NivelTraza.debug));
        if (listaDescripcionesGestoresNodo == null) {
            logger.error("GestorAgentes: No existen Gestores de nodo definidos .");
        } else {
            // recorremos todos los GN alamcenados
            Integer posInstGN = 0;
            Integer numGNConocidos = listaDescripcionesAgtesACrear.size();
            DescInstanciaAgente descGN;

            while (posInstGN < numGNConocidos) {

                descGN = this.listaDescripcionesGestoresNodo.get(posInstGN);
                // para cada agente, recuperamos su itf de gestion

//                InterfazUsoAgente itfUsosAg = (InterfazUsoAgente) descGN.getIntfUsoAgente();
                String identGN = descGN.getId();
                // finalizamos el agente
                logger.debug("GestorAgentes: Se envia mensaje de peticion de terminacion al GN  " + identGN + ".");
                try {
                    descGN.getIntfUsoAgente().aceptaMensaje(new MensajeSimple(new InfoContEvtMsgAgteReactivo("peticion_terminar_todo", null),
                             this.nombreAgente, NombresPredefinidos.NOMBRE_GESTOR_NODO));
                    trazas.aceptaNuevaTraza(new InfoTraza(nombreAgente, "Se envia mensaje de peticion de terminacion al GN" + identGN + ".",
                            InfoTraza.NivelTraza.debug));

                } catch (Exception ex) {
                    logger.error("GestorAgentes: Hubo un problema al enviar un mensaje al GEstor de Nodo  " + identGN + ".");

                    trazas.aceptaNuevaTraza(new InfoTraza(
                            nombreAgente,
                            "Hubo un problema al enviar un mensaje al GEstor de Nodo  " + identGN + ".",
                            InfoTraza.NivelTraza.error));
                    ex.printStackTrace();
                }
                posInstGN++;
            }
            logger.debug("GestorAgentes: Finalizado proceso de terminacion de todos los agentes.");
            trazas.aceptaNuevaTraza(new InfoTraza(
                    "GestorAgentes",
                    "Finalizado proceso de terminacion de todos los agentes.",
                    InfoTraza.NivelTraza.debug));

            try {
                this.informaraMiAutomata("agentes_terminados");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Intenta recuperar los errores detectados en la monitorizacion siguiendo
     * la politica definida para cada agente.
     */
    public void recuperarErrorAlMonitorizarAgentes() {
        // por defecto no se implementan politicas de recuperacion
        logger.debug("GestorAgentes: No se pudo recuperar el error de monitorizacion.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "No se pudo recuperar el error de monitorizacion.",
                InfoTraza.NivelTraza.debug));

        try {
            this.informaraMiAutomata("imposible_recuperar_error_monitorizacion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * destruye los recursos que se crearon a lo largo del ciclo de vida de este
     * agente.
     */
    public void terminarGestorAgentes() {

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Terminando gestor de agentes.",
                InfoTraza.NivelTraza.debug));

        if (this.hebra != null) {
            this.hebra.finalizar();
        }
        try {
            ((InterfazGestion) this.itfUsoRepositorio.obtenerInterfaz(NombresPredefinidos.ITF_GESTION + NombresPredefinidos.NOMBRE_GESTOR_AGENTES)).termina();
            //      this.informaraMiAutomata(nombreAgente, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            this.comunicator.enviarInfoAotroAgente("gestor_agentes_terminado", NombresPredefinidos.NOMBRE_GESTOR_ORGANIZACION);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pedirTerminacionAGestorOrg() {
        logger.debug("GestorAgentes: Pidiento terminacion al gestor de organizacion.");

        trazas.aceptaNuevaTraza(new InfoTraza(
                "GestorAgentes",
                "Pidiento terminacion al gestor de organizacion.",
                InfoTraza.NivelTraza.debug));
        // antes pido a los gestores de nodo que terminen
        try {
            peticionTerminacionGestoresNodo();
            this.comunicator.enviarInfoAotroAgente("peticion_terminar_todo", NombresPredefinidos.NOMBRE_GESTOR_ORGANIZACION);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void clasificaError() {
    }
}
