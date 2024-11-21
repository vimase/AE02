package es.florida.gestorPoblacion;

/**
 * Clase Principal que contiene el punto de entrada de la aplicación.
 * Se encarga de inicializar la Vista, el Modelo y el Controlador,
 * estableciendo la estructura MVC (Modelo-Vista-Controlador) de la aplicación.
 */
public class Principal {
	
	/**
     * Método principal que sirve como punto de entrada de la aplicación.
     * Inicializa la Vista, el Modelo y el Controlador para comenzar la ejecución del programa.
     * 
     * @param args Argumentos de la línea de comandos (no se utilizan en esta aplicación).
     */
	public static void main(String[] args) {
		Vista vista = new Vista();
		Modelo modelo = new Modelo();
		new Controlador(vista, modelo);
	}

}
