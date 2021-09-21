package icaro.aplicaciones;

import icaro.infraestructura.CreacionOrganizacion;

public class ArranqueICARO {

    /**
     * Arranque de la infraestructura ICARO con un SMA especificado en un fichero de configuraci�n
     * 
     * @param args
     *            Entrada: ruta del fichero de configuraci�n 
     *            (sin la extensi�n .xml) que debe estar en config.icaro.aplicaciones.descripcionOrganizaciones
     *            (p.ej. rosaceCicloCompleto/descripcionNrobotsNvictimas)
     */
    public static void main(String args[]) {
        // Es necesario especificar un fichero de descripci�n como argumento
        if (args.length == 0) {
            System.err.println("Error: Hace falta especificar como argumento la ruta del fichero de descripci�n.\n Ejemplo: directorio/descripcionAcceso");
            System.exit(-1);
        } 

        CreacionOrganizacion.arranca(args[0]);	// arranca la infraestructura pas�ndole el fichero de descripci�n de la organizaci�n
    }
}
  