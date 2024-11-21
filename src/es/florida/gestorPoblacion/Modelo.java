package es.florida.gestorPoblacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;

/**
 * Clase que representa el modelo principal de gestión de población,
 * encargada de conectar con la base de datos y realizar operaciones de gestión.
 */
public class Modelo {
	
	private String usuarioLogueado;
	private String rolUsuario;
	private Connection conexionActual;
	private String rutaArchivoCSV;
	private boolean errorConsulta;

	
	/**
	 * Constructor vacío para la clase Modelo.
	 */
	public Modelo() {
		
	}
	
	/**
	 * Conecta con la base de datos utilizando las credenciales proporcionadas.
	 * 
	 * @param usu  Nombre de usuario para la conexión.
	 * @param pass Contraseña del usuario para la conexión.
	 */
	public void conectarBD(String usu,String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/population", usu, convertToMD5(pass));
			setConexionActual(con);
		} catch (Exception e) {
			System.out.println(e);
			JOptionPane.showMessageDialog(null, "No se puede conectar con la base de datos", "Alerta", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Cierra la conexión actual con la base de datos si existe.
	 */
	public void cerrarConexionDB() {
		if (getConexionActual() != null) {
            try {
            	getConexionActual().close();
            } catch (Exception e) {
                System.out.println("Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }
	}
	
	/**
	 * Exporta el contenido de un JTable a un archivo CSV.
	 * 
	 * @param model    Modelo de la tabla a exportar.
	 * @param filePath Ruta donde se guardará el archivo CSV.
	 */
	public void exportarJTableACSV(DefaultTableModel model, String filePath) {
        if (filePath == null) return;
		try {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            // Escribir las cabeceras de las columnas
            for (int i = 0; i < model.getColumnCount(); i++) {
            	bw.append(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                	bw.append(";");
                }
            }
            bw.append("\n");

            // Escribir los datos de las filas
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                	bw.append(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) {
                    	bw.append(";");
                    }
                }
                bw.append("\n");
            }
            bw.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Muestra un cuadro de diálogo para seleccionar el directorio donde guardar el archivo CSV.
	 * 
	 * @return La ruta del archivo seleccionada o null si se cancela la operación.
	 */
	public String elegirDirectorio() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
	    
	    // Añadir filtro para mostrar solo archivos .csv y directorios
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
	    fileChooser.setFileFilter(filter);
	    
	    // Mostrar el cuadro de diálogo de guardar archivo
	    int resultado = fileChooser.showDialog(null, "Guardar archivo");

	    if (resultado == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        String filePath = selectedFile.getAbsolutePath();
	        
	        // Añadir extensión ".csv" si no está ya presente
	        if (!filePath.toLowerCase().endsWith(".csv")) {
	            filePath += ".csv";
	        }

	        // Devolver la ruta del archivo seleccionado con la extensión ".csv"
	        return filePath;
	    }
	    
	    // Devolver null si el usuario cancela la operación
	    return null;
	}
	
	/**
	 * Realiza una consulta SELECT a la base de datos y devuelve los resultados.
	 * 
	 * @param sql Consulta SQL a ejecutar.
	 * @return Lista de listas de cadenas que representan los datos obtenidos.
	 */
	public List<List<String>> realizarConsulta(String sql) {
		setErrorConsulta(false);
		
		List<List<String>> data = new ArrayList<>();
		
		String primeraPalabra = sql.trim().split(" ")[0];
		if (!primeraPalabra.equalsIgnoreCase("select")) {
			JOptionPane.showMessageDialog(null, "Solo se permiten consultas SELECT", "Alerta", JOptionPane.WARNING_MESSAGE);
			return data;
		}
		
		try {
			PreparedStatement stmt = getConexionActual().prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// Añadir los encabezados (nombres de columnas)
			List<String> headers = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				headers.add(metaData.getColumnName(i));
			}
			data.add(headers);

			// Añadir los datos de las filas
			while (rs.next()) {
				List<String> row = new ArrayList<>();
				for (int i = 1; i <= columnCount; i++) {
					row.add(rs.getString(i));
				}
				data.add(row);
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			setErrorConsulta(true);
		}
		
		return data;
	}
	
	/**
	 * Intenta autenticar a un usuario con el nombre de usuario y contraseña proporcionados.
	 * 
	 * @param user Nombre de usuario.
	 * @param pass Contraseña.
	 * @return El tipo de usuario si el login es exitoso, de lo contrario una cadena vacía.
	 */
	public String loginUsuario(String user, String pass) { 
		String tipoUsuario = "";
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/population", "admin", convertToMD5("admin"));
			PreparedStatement stmt = con.prepareStatement("SELECT type FROM users WHERE login = ? AND password = ?"); // Obtener el tipo del usuario para crear la sesión.
			stmt.setString(1, user);
            stmt.setString(2, convertToMD5(pass));
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				tipoUsuario = rs.getString("type");
				setRolUsuario(tipoUsuario);
				setUsuarioLogueado(user);
				
				conectarBD(usuarioLogueado, pass); // Establecemos la sesión.
            } else {
            	JOptionPane.showMessageDialog(null, "Usuario incorrecto", "Alerta", JOptionPane.WARNING_MESSAGE);
            }
			rs.close();
			stmt.close();
			con.close();
			return tipoUsuario;
			 
		} catch (Exception e) {
			System.out.println(e);
			if (getConexionActual() != null) cerrarConexionDB();
			return tipoUsuario;
		}
	}
	
	/**
	 * Convierte una contraseña en un hash MD5.
	 * 
	 * @param password Contraseña a convertir.
	 * @return Contraseña convertida a hash MD5 o null en caso de error.
	 */
	private String convertToMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // Formatea cada byte como un valor hexadecimal
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	/**
     * Crea un nuevo usuario en la base de datos.
     * 
     * @param user        Nombre del nuevo usuario.
     * @param pass        Contraseña del nuevo usuario.
     * @param passConfirm Confirmación de la contraseña.
     */
	public void crearUsuario(String user, String pass, String passConfirm) {
		if (user.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No se admiten campos vacíos", "Alerta", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		try {
			PreparedStatement stmt = getConexionActual().prepareStatement("SHOW TABLES LIKE 'population'");
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				
				stmt = getConexionActual().prepareStatement("SELECT COUNT(*) FROM users WHERE login = ?");
	            stmt.setString(1, user);
	            ResultSet rsUser = stmt.executeQuery();
	            rsUser.next();
	            int userCount = rsUser.getInt(1);
	            rsUser.close();

	            if (userCount > 0) {
	                JOptionPane.showMessageDialog(null, "El usuario ya existe", "Alerta", JOptionPane.WARNING_MESSAGE);
	                stmt.close();
	                return;
	            }

				if (pass.equals(passConfirm)) {
					stmt = getConexionActual().prepareStatement("INSERT INTO users (login, password, type) VALUES (?, ?, ?)");
					stmt.setString(1, user);
					stmt.setString(2, convertToMD5(pass));
					stmt.setString(3, "client");
					int filasInsertadasInsert = stmt.executeUpdate();
					
					stmt = getConexionActual().prepareStatement("CREATE USER ? IDENTIFIED BY ?");
					stmt.setString(1, user);
					stmt.setString(2, convertToMD5(pass));
					stmt.executeUpdate();
					
					stmt = getConexionActual().prepareStatement("GRANT SELECT on population.population TO ?");
					stmt.setString(1, user);
					stmt.executeUpdate();
					
					stmt = getConexionActual().prepareStatement("FLUSH PRIVILEGES");
					stmt.executeUpdate();
					
					if (filasInsertadasInsert > 0) {
						JOptionPane.showMessageDialog(null, "Usuario creado: " + user, "Alerta", JOptionPane.PLAIN_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Alerta", JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "No existen datos que consultar, no se puede crear usuarios", "Alerta", JOptionPane.WARNING_MESSAGE);
			}
			rs.close();
			stmt.close();
			
		} catch (Exception e) { 
			System.out.println(e.getMessage() + "\n" + e);
		}
	}
	
	/**
     * Importa un archivo CSV para cargar los datos en la base de datos y generar archivos XML.
     * 
     * @return Contenido de los archivos XML generados en formato de cadena.
     */
	public String importarCSV() {
		elegirArchivoCSV();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(getRutaArchivoCSV()));
			String cabeceras = br.readLine();
			String[] arrayCabeceras = cabeceras.split(";");

            PreparedStatement stmt = getConexionActual().prepareStatement("DROP TABLE IF EXISTS population");
			stmt.executeUpdate();
			
			// Se prepara la sentencia sql para crear la tabla population.
			StringBuilder crearConsultaTabla = new StringBuilder("CREATE TABLE population (");
            for (String cabecera : arrayCabeceras) {
            	crearConsultaTabla.append(cabecera.trim()).append(" VARCHAR(30), ");
            }
            
            crearConsultaTabla.setLength(crearConsultaTabla.length() - 2); // Eliminar la última coma y espacio, luego cerrar el paréntesis.
            crearConsultaTabla.append(")");
            
            stmt = getConexionActual().prepareStatement(crearConsultaTabla.toString());
            stmt.executeUpdate();
            stmt.close();
            
            // Crear una lista de todo el contenido del archivo CSV sin la cabecera ya que se ha utilizado anteriormente.
            String linea;
            List<String[]> data = new ArrayList<>();
            while ((linea = br.readLine()) != null) {
                String[] values = linea.split(";");
                data.add(values);
            }
            br.close();
            
            crearArchivosXML(data, arrayCabeceras);
            
            // Insertar en la base de datos los datos de cada XML.
            File xmlFolder = new File("xml");
            File[] xmlFiles = xmlFolder.listFiles();
            for (File file : xmlFiles) {
            	insertarXMLABaseDatos(getConexionActual(), file);
            }   
			
			return construirXML();

		} catch (Exception e) {
			System.out.println(e);
			return construirXML();
		}
		
	}
	
	/**
     * Inserta los datos de un archivo XML en la base de datos.
     * 
     * @param conn    Conexión a la base de datos.
     * @param xmlFile Archivo XML que contiene los datos a insertar.
     */
	public static void insertarXMLABaseDatos(Connection conn, File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            
            String country = doc.getElementsByTagName("country").item(0).getTextContent();
            String population = doc.getElementsByTagName("population").item(0).getTextContent();
            String density = doc.getElementsByTagName("density").item(0).getTextContent();
            String area = doc.getElementsByTagName("area").item(0).getTextContent();
            String fertility = doc.getElementsByTagName("fertility").item(0).getTextContent();
            String age = doc.getElementsByTagName("age").item(0).getTextContent();
            String urban = doc.getElementsByTagName("urban").item(0).getTextContent();
            String share = doc.getElementsByTagName("share").item(0).getTextContent();

            String sql = "INSERT INTO population (country, population, density, area, fertility, age, urban, share) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, country);
            stmt.setString(2, population);
            stmt.setString(3, density);
            stmt.setString(4, area);
            stmt.setString(5, fertility);
            stmt.setString(6, age);
            stmt.setString(7, urban);
            stmt.setString(8, share);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
     * Construye el contenido de los archivos XML presentes en la carpeta 'xml' para a posteriori mostrarlo en la interfaz.
     * 
     * @return Cadena con el contenido de todos los archivos XML.
     */
	private String construirXML() {
		File xmlDir = new File("xml");
		StringBuilder allXmlContent = new StringBuilder();

		if (xmlDir.exists() || xmlDir.isDirectory()) {

			for (File fileXML : xmlDir.listFiles()) {
				try {
					BufferedReader rd = new BufferedReader(new FileReader(fileXML));
					String line;
					while ((line = rd.readLine()) != null) {
						allXmlContent.append(line).append("\n");
					}
					rd.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				allXmlContent.append("\n");
			}
			return allXmlContent.toString();
		}
		return "Error";
	}
	
	/**
     * Crea archivos XML basados en los datos proporcionados y los almacena en la carpeta 'xml'.
     * 
     * @param data    Lista de filas de datos que se utilizarán para crear los archivos XML.
     * @param columns Nombres de las columnas que se utilizarán como etiquetas en los archivos XML.
     */
	private void crearArchivosXML(List<String[]> data, String[] columns) {
        File xmlDir = new File("xml");
        if (!xmlDir.exists()) {
            xmlDir.mkdir(); // Crear carpeta xml si no existe.
        }

        for (String[] row : data) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element rootElement = doc.createElement("country_data");
                doc.appendChild(rootElement);

                for (int i = 0; i < columns.length; i++) {
                    // Crear elementos para cada columna
                    Element element = doc.createElement(columns[i]);
                    element.appendChild(doc.createTextNode(row[i]));
                    rootElement.appendChild(element);

                }

                // Escribir el archivo XML
                String fileName = row[0] + ".xml"; // Nombre del archivo basado en la columna "country"
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(xmlDir, fileName));
                transformer.transform(source, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	/**
     * Muestra un cuadro de diálogo para seleccionar un archivo CSV.
     * Almacena la ruta del archivo seleccionado en la variable rutaArchivoCSV.
     */
	private void elegirArchivoCSV() {
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos .csv", "csv");
	    fileChooser.setFileFilter(filter);
	    
		int resultado = fileChooser.showDialog(null, "Seleccionar archivo");
		
		if (resultado == JFileChooser.APPROVE_OPTION) {
			setRutaArchivoCSV(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private Connection getConexionActual() {
		return conexionActual;
	}

	private void setConexionActual(Connection conexionActual) {
		this.conexionActual = conexionActual;
	}
	
	public boolean isErrorConsulta() {
		return errorConsulta;
	}

	public void setErrorConsulta(boolean errorConsulta) {
		this.errorConsulta = errorConsulta;
	}

	public String getRutaArchivoCSV() {
		return rutaArchivoCSV;
	}

	public void setRutaArchivoCSV(String rutaArchivoCSV) {
		this.rutaArchivoCSV = rutaArchivoCSV;
	}

	public String getRolUsuario() {
		return rolUsuario;
	}

	public void setRolUsuario(String rolUsuario) {
		this.rolUsuario = rolUsuario;
	}

	public String getUsuarioLogueado() {
		return usuarioLogueado;
	}

	public void setUsuarioLogueado(String usuarioLogueado) {
		this.usuarioLogueado = usuarioLogueado;
	}
	
	

}
