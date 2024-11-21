package es.florida.gestorPoblacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.table.DefaultTableModel;

/**
 * Clase Controlador que maneja la lógica de la aplicación,
 * conectando la Vista y el Modelo para gestionar la interacción con el usuario.
 */
public class Controlador {
	private Vista vista;
    private Modelo modelo;

    /**
     * Constructor para la clase Controlador.
     * 
     * @param vista  Objeto de la clase Vista que representa la interfaz de usuario.
     * @param modelo Objeto de la clase Modelo que representa la lógica de negocio y la base de datos.
     */
    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        initEventHandlers();
    }
    
    /**
     * Inicializa los controladores de eventos para los componentes de la vista.
     * Maneja la lógica asociada a los diferentes botones y entradas de la interfaz.
     */
    public void initEventHandlers() {
    	
    	// Acción para el botón "Acceder"
    	vista.getBtnAcceder().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	char[] arrayC = vista.getTxtContrasenya().getPassword();
            	String pass = new String(arrayC);

            	String rolUsuario = modelo.loginUsuario(vista.getTxtUsuario().getText(), pass);
            	
            	if (rolUsuario.equals("admin")) {
            		vista.getPanelAcceder().setVisible(false);
	            	vista.getBtnDesconectar().setVisible(true);
	            	vista.getPanelConsulta().setVisible(true);
	            	vista.getPanelImportarCSVAdmin().setVisible(true);
	            	
	            	vista.setBounds(vista.getLocation().x, vista.getLocation().y, 959, 532);
	            	vista.getLblverNombreUsuario().setBounds(586, 15, 212, 14);
	            	vista.getBtnDesconectar().setBounds(807, 11, 126, 23);
            	}
            	
            	if (rolUsuario.equals("client")) {
            		vista.getPanelAcceder().setVisible(false);
            		vista.getBtnDesconectar().setVisible(true);
	            	vista.getPanelConsulta().setVisible(true);
	            	
	            	vista.setBounds(vista.getLocation().x, vista.getLocation().y, 518, 532);
	            	vista.getLblverNombreUsuario().setBounds(586 - 441, 15, 212, 14);
	            	vista.getBtnDesconectar().setBounds(807 - 441, 11, 126, 23);
            	}
            	
            	vista.getLblverNombreUsuario().setText(modelo.getUsuarioLogueado());
            	
            	vista.getTxtUsuario().setText(null);
            	vista.getTxtContrasenya().setText(null);
            }
        });
    	
    	// Acción para el botón "Desconectar"
    	vista.getBtnDesconectar().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	modelo.cerrarConexionDB();
            	
            	vista.getLblverNombreUsuario().setText("");
            	
            	vista.getTxtConfirmarContrasenya().setText(null);
            	vista.getTxtConsultaSQL().setText(null);
            	vista.getTxtCrearContrasenya().setText(null);
            	vista.getTxtCrearUsuario().setText(null);
            	vista.getTable().setModel(new DefaultTableModel());
            	vista.getLblError().setVisible(false);
            	
            	// Mostramos como se ve al salir de la sesión
            	vista.getPanelAcceder().setVisible(true);
            	vista.getBtnDesconectar().setVisible(false);
            	vista.getPanelConsulta().setVisible(false);
            	vista.getPanelImportarCSVAdmin().setVisible(false);
            	
            	vista.setBounds(vista.getLocation().x, vista.getLocation().y, 518, 532);
            	vista.getLblverNombreUsuario().setBounds(586, 15, 212, 14);
            	vista.getBtnDesconectar().setBounds(807, 11, 126, 23);
            }
        });
    	
    	// Acción para el botón "Crear Usuario"
    	vista.getBtnCrearUsu().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				char[] passArray = vista.getTxtCrearContrasenya().getPassword();
            	String pass = new String(passArray);
            	
            	char[] passConfirmArray = vista.getTxtConfirmarContrasenya().getPassword();
            	String passConfirm = new String(passConfirmArray);
            	
				modelo.crearUsuario(vista.getTxtCrearUsuario().getText(), pass, passConfirm);
				
			}
		});
    	
    	// Acción para el botón "Importar CSV"
    	vista.getBtnImportarCSV().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String xmlAMostrar = modelo.importarCSV();
				vista.getTextArea().setText(xmlAMostrar);
			}
		});
    	
    	// Acción para el botón "Ejecutar"
    	vista.getBtnEjecutar().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String sql = vista.getTxtConsultaSQL().getText();
				
				List<List<String>> listaBidimensionalDatos = modelo.realizarConsulta(sql);
					
				if (listaBidimensionalDatos.isEmpty()) {
					modelo.setErrorConsulta(true);
					
					// Aparece un letrero ERROR cuando ha habido un fallo en la consulta sql.
			        vista.getLblError().setVisible(modelo.isErrorConsulta());
		            return;
		        }
				vista.getLblError().setVisible(modelo.isErrorConsulta());
				
				// Extraer encabezados y datos
		        List<String> listaCabeceras = listaBidimensionalDatos.get(0);
		        List<List<String>> listabidimensionalContenido = listaBidimensionalDatos.subList(1, listaBidimensionalDatos.size());

		        // Convertir los encabezados y datos a un formato compatible con JTable
		        String[] arrayCabeceras = listaCabeceras.toArray(new String[0]);
		        String[][] arrayBidimensionalContenido = new String[listabidimensionalContenido.size()][listaCabeceras.size()];

		        for (int i = 0; i < listabidimensionalContenido.size(); i++) {
		            List<String> row = listabidimensionalContenido.get(i);
		            for (int j = 0; j < row.size(); j++) {
		            	arrayBidimensionalContenido[i][j] = row.get(j);
		            }
		        }
		        
		        vista.getTable().setModel(new DefaultTableModel(arrayBidimensionalContenido, arrayCabeceras));
			}
		});
    	
    	// Acción para el botón "Exportar CSV"
    	vista.getBtnExportarCSV().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Hay que sacar el modelo para sacar los datos de un JTable
				DefaultTableModel model = (DefaultTableModel) vista.getTable().getModel();
				modelo.exportarJTableACSV(model, modelo.elegirDirectorio());
				
			}
		});
    	
    }
}
