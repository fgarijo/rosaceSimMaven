/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icaro.infraestructura.entidadesBasicas.descEntidadesOrganizacion;

import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;
import java.util.logging.Level;
import org.openide.util.Exceptions;
import java.io.FileInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ComprobadorRutasEntidades {

    private Logger logger = Logger
            .getLogger(this.getClass().getCanonicalName());
    private Class[] clasesUltimoDirectorio;
    private String identUltimoDirectorio;

    public ComprobadorRutasEntidades() {
    }

    public boolean existeSchemaDescOrganizacion() {
// Se cmprueba la existencia del fichero en la ruta predefinida de la organizacion
        File schema = new File(NombresPredefinidos.DESCRIPCION_SCHEMA);
        if (!schema.exists()) {
          return  ( buscarFichero(NombresPredefinidos.DESCRIPCION_SCHEMA, "schemas/")!=null);
//            logger.fatal("No se ha encontrado el fichero de descripcion:"
//                    + "\n\t\t\t" + schema.getAbsolutePath()
//                    + ".\n Compruebe la ruta y el nombre del fichero.");
        
        }
        return (schema.exists());
    }

    public boolean existeDescOrganizacion1(String identFicherodescripcion) {
// Se cmprueba la existencia del fichero en la ruta predefinida de la organizacion
        String descXML = NombresPredefinidos.RUTA_DESCRIPCIONES + identFicherodescripcion + ".xml";
        File ficheroDescripcion = new File(descXML);
        if (!ficheroDescripcion.exists()) {
            logger.fatal("No se ha encontrado el fichero de descripcion:"
                    + "\n\t\t\t" + ficheroDescripcion.getAbsolutePath()
                    + ".\n Compruebe la ruta y el nombre del fichero.");
        }
        return (ficheroDescripcion.exists());
    }
    public String buscarFichero(String identFicherodescripcion, String rutaBusqueda ) {
        // devuelve la ruta del fichero encontrado o null si no lo encuentra
        //   identFicherodescripcion = identFicherodescripcion+".xml";
        //   String rutaBusqueda = NombresPredefinidos.RUTA_DESCRIPCIONES.replace(".", File.separator);
//        String rutaBusqueda = NombresPredefinidos.RUTA_DESCRIPCIONES;
        int posSeparadorDirect = identFicherodescripcion.lastIndexOf("/");
        if (posSeparadorDirect > 0) {
            posSeparadorDirect = posSeparadorDirect + 1;
            rutaBusqueda = rutaBusqueda + identFicherodescripcion.substring(0, posSeparadorDirect);
            identFicherodescripcion = identFicherodescripcion.substring(posSeparadorDirect);
        }
        rutaBusqueda = rutaBusqueda.replace(".", File.separator);
//        identFicherodescripcion = identFicherodescripcion + ".xml";
        String rutaFicheroVisitado = null;
//       String rutaBusqueda = utils.Constantes.rutassrc + rutaComportamiento;
        Boolean ficheroEncontrado = false;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(rutaBusqueda);

            Queue<File> ficherosRuta = new LinkedList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                ficherosRuta.add(new File(resource.getFile()));
            }
            if (ficherosRuta == null) {
                return null;
            }
            while (!ficherosRuta.isEmpty() && !ficheroEncontrado) {
                for (File ficheroVisitado : ficherosRuta.poll().listFiles()) {
                    if (ficheroVisitado.isDirectory()) {
                        ficherosRuta.add(ficheroVisitado);
                    } else if (ficheroVisitado.isFile()) {
                        String nombFicherovisitado = ficheroVisitado.getName();
                        if (ficheroVisitado.getName().equals(identFicherodescripcion)) {
                            ficheroEncontrado = true;
                            return ficheroVisitado.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    public boolean existeDescOrganizacion(String identFicherodescripcion) {
        // La ruta del comportamiento no incluye el fichero 
        //  File.separator + rutaComportamiento.replace(".", File.separator);
        //    identFicherodescripcion = identFicherodescripcion+".xml";
        //   String rutaBusqueda = NombresPredefinidos.RUTA_DESCRIPCIONES.replace(".", File.separator);
//       String rutaBusqueda = utils.Constantes.rutassrc + rutaComportamiento;
        String rutaBusqueda = NombresPredefinidos.RUTA_DESCRIPCIONES;
        int posSeparadorDirect = identFicherodescripcion.lastIndexOf(".");
        if (posSeparadorDirect > 0) {
            rutaBusqueda = rutaBusqueda + identFicherodescripcion.substring(0, posSeparadorDirect);
        }
        Boolean ficheroEncontrado = false;
        Queue<File> ficherosRuta = new LinkedList<File>();
        ficherosRuta.add(new File(rutaBusqueda));
        if (ficherosRuta == null) {
            return false;
        }
        while (!ficherosRuta.isEmpty() && !ficheroEncontrado) {
            for (File ficheroVisitado : ficherosRuta.poll().listFiles()) {
                if (ficheroVisitado.isDirectory()) {
                    ficherosRuta.add(ficheroVisitado);
                } else if (ficheroVisitado.isFile()) {
                    String nombFicherovisitado = ficheroVisitado.getName();
                    if (ficheroVisitado.getName().equals(identFicherodescripcion)) {
                        ficheroEncontrado = true;
                    }
                }
            }
        }
        return ficheroEncontrado;
    }

    public String buscarDescOrganizacion(String identFicherodescripcion) {
        // devuelve la ruta del fichero encontrado o null si no lo encuentra
        //   identFicherodescripcion = identFicherodescripcion+".xml";
        //   String rutaBusqueda = NombresPredefinidos.RUTA_DESCRIPCIONES.replace(".", File.separator);
        String rutaBusqueda = NombresPredefinidos.RUTA_DESCRIPCIONES;
        int posSeparadorDirect = identFicherodescripcion.lastIndexOf("/");
        if (posSeparadorDirect > 0) {
            posSeparadorDirect = posSeparadorDirect + 1;
            rutaBusqueda = rutaBusqueda + identFicherodescripcion.substring(0, posSeparadorDirect);
            identFicherodescripcion = identFicherodescripcion.substring(posSeparadorDirect);
        }
        rutaBusqueda = rutaBusqueda.replace(".", File.separator);
        identFicherodescripcion = identFicherodescripcion + ".xml";
        String rutaFicheroVisitado = null;
//       String rutaBusqueda = utils.Constantes.rutassrc + rutaComportamiento;
        Boolean ficheroEncontrado = false;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(rutaBusqueda);

            Queue<File> ficherosRuta = new LinkedList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                ficherosRuta.add(new File(resource.getFile()));
            }
            if (ficherosRuta == null) {
                return null;
            }
            while (!ficherosRuta.isEmpty() && !ficheroEncontrado) {
                for (File ficheroVisitado : ficherosRuta.poll().listFiles()) {
                    if (ficheroVisitado.isDirectory()) {
                        ficherosRuta.add(ficheroVisitado);
                    } else if (ficheroVisitado.isFile()) {
                        String nombFicherovisitado = ficheroVisitado.getName();
                        if (ficheroVisitado.getName().equals(identFicherodescripcion)) {
                            ficheroEncontrado = true;
                            return ficheroVisitado.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private String normalizarRuta(String ruta) {
        /*Esta funcin cambia la primera letra del nombre y la pone en minsculas*/
        String primero = ruta.substring(0, 1).toLowerCase(); //obtengo el primer carcter en minsculas
        String rutaNormalizada = primero + ruta.substring(1, ruta.length());

        return rutaNormalizada;
    }

    public Class obtenerClaseAccionesSemanticas(String rutaComportamiento) {
        try {
            if (!rutaComportamiento.equals(identUltimoDirectorio)) {
                clasesUltimoDirectorio = getClasses(rutaComportamiento);
                identUltimoDirectorio = rutaComportamiento;
            }
            for (Class clase : clasesUltimoDirectorio) {
                if (clase.getSimpleName().startsWith(NombresPredefinidos.PREFIJO_CLASE_ACCIONES_SEMANTICAS)) {
                    return clase;
                }
            }
        } catch (ClassNotFoundException | IOException ex) {
            java.util.logging.Logger.getLogger(ComprobadorRutasEntidades.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Class obtenerClaseAccionesReactivo(String identClase, String rutaComportamiento) {
        try {
            if (!rutaComportamiento.equals(identUltimoDirectorio)) {
                clasesUltimoDirectorio = getClasses(rutaComportamiento);
                identUltimoDirectorio = rutaComportamiento;
            }
            for (Class clase : clasesUltimoDirectorio) {
//                String claseN = clase.getSimpleName();
//                int posicion= claseN.indexOf(NombresPredefinidos.NOMBRE_ACCIONES_SEMANTICAS);
                if (clase.getSimpleName().equalsIgnoreCase(identClase)) {
                    return clase;
                }
            }

        } catch (ClassNotFoundException | IOException ex) {
            java.util.logging.Logger.getLogger(ComprobadorRutasEntidades.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public boolean existeClase(String rutaClase) {
        Class clase;
        try {
            clase = Class.forName(rutaClase);
            return (clase != null);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean existeFichero(String rutaFicheroBusqueda) {

        File f = new File(rutaFicheroBusqueda);

        return (f.exists());
    }
    public boolean existeRecursoClasspath(String recursoClassPath) {
        InputStream input = this.getClass().getResourceAsStream(
                recursoClassPath);
        logger.debug(recursoClassPath + "?" + ((input != null) ? "  OK" : "  null"));
        return (input != null);
    }
    private String primeraMinuscula(String nombre) {
        String firstChar = nombre.substring(0, 1);
        return nombre.replaceFirst(firstChar, firstChar.toLowerCase());
    }

    /**
     * Todavia sin utilizar ni probar Scans all classes accessible from the
     * context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            String directoryPath = directory.getPath();
           if ( directoryPath.contains(".jar")){
               //            URISyntaxException {
               String JAR_PATH = directoryPath.substring(0, directoryPath.indexOf("!"));
               JAR_PATH= JAR_PATH.replace(File.pathSeparator, "/");
               Set<String> identClases = getCrunchifyClassNamesFromJar(JAR_PATH);
               
//                Assert.assertEquals(EXPECTED_CLASS_NAMES, classNames);
//              }
        }
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
//private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
//    List<Class> classes = new ArrayList<Class>();
//    if (!directory.exists()) {
//    return classes;
//    }
//    File[] files = directory.listFiles();
//        for (File file : files) {
//            if (file.isDirectory()) {
//            assert !file.getName().contains(".");
//            classes.addAll(findClasses(file, packageName + "." + file.getName()));
//            } else if (file.getName().endsWith(".class")) {
//            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
//            }
//        }
//    return classes;
//    }
    public static Set<String> getCrunchifyClassNamesFromJar(String crunchifyJarName) {
		Set<String> classNames = new HashSet<>();
		try {
			JarInputStream crunchifyJarFile = new JarInputStream(new FileInputStream(crunchifyJarName));
			JarEntry crunchifyJar;
 
			while (true) {
				crunchifyJar = crunchifyJarFile.getNextJarEntry();
				if (crunchifyJar == null) {
					break;
				}
				if ((crunchifyJar.getName().endsWith(".class"))) {
					String className = crunchifyJar.getName().replaceAll("/", "\\.");
					String myClass = className.substring(0, className.lastIndexOf('.'));
					classNames.add(myClass);
				}
			}
//			classes.add("Jar File Name", crunchifyJarName);
//			crunchifyObject.put("List of Class", listofClasses);
		} catch (Exception e) {
			System.out.println("Oops.. Encounter an issue while parsing jar" + e.toString());
		}
		return classNames;
	}
    
    public String verificarRutaClasesAccion(String rutaClaseAS, String rutaComportamiento) {
        // buscamos las clases existente en la ruta de comportamiento. Puede ser la clase AccionesSemanticas y/o otras clases de tipo Accion
        // Si no se encuentra la clase de AS  se devuelve null, y si se encuentra se devuelve la ruta de la clase
        // lo dejamos para cuando se construya el auntomata 
        // verificamos si la ruta es un directorio o si es el nombre de la clase Acciones semnanticas
        try {
            if (rutaClaseAS.indexOf(NombresPredefinidos.NOMBRE_ACCIONES_SEMANTICAS) > 0
                    && existeClase(rutaClaseAS)) {
                return rutaClaseAS;
            }
            if (rutaComportamiento == null) {
                return null;
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = rutaComportamiento.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            if (dirs.isEmpty()) {
                if (rutaComportamiento.indexOf(NombresPredefinidos.NOMBRE_ACCIONES_SEMANTICAS) > 0
                        && existeClase(rutaComportamiento)) {
                    return rutaComportamiento;
                } else {
                    return null;
                }
            }
            ArrayList<Class> classes = new ArrayList<Class>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, rutaComportamiento));
            }
            if (classes.isEmpty()) {
                return null;
            }
            for (Class clazz : classes) {
                if (clazz.getSimpleName().startsWith(NombresPredefinidos.NOMBRE_ACCIONES_SEMANTICAS)) {
                    return rutaComportamiento + "." + clazz.getSimpleName();
                }
            }
            return null;
        } catch (Exception e) {
        }
        return null;
    }
    /**
     * Load all classes from a package.
     *
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getAllClassesFromPackage(final String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    /**
     * Find file in package.
     *
     * @param directory
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    public static Set<Class> getClassesFromJarFile(File jarFile) throws IOException, ClassNotFoundException {
    Set<String> classNames = getClassNamesFromJarFile(jarFile);
    Set<Class> classes = new HashSet<>(classNames.size());
    try (URLClassLoader cl = URLClassLoader.newInstance(
           new URL[] { new URL("jar:file:" + jarFile + "!/") })) {
        for (String name : classNames) {
            Class clazz = cl.loadClass(name); // Load the class by its name
            classes.add(clazz);
        }
    }
    return classes;
}
    public static Set<String> getClassNamesFromJarFile(File givenFile) throws IOException {
    Set<String> classNames = new HashSet<>();
    try (JarFile jarFile = new JarFile(givenFile)) {
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName()
                  .replace("/", ".")
                  .replace(".class", "");
                classNames.add(className);
            }
        }
        return classNames;
    }
}
}
