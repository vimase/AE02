package es.florida.gestorPoblacion;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

/**
 * Clase Vista que representa la interfaz gráfica de la aplicación de gestión de población.
 * Extiende JFrame y contiene todos los elementos gráficos necesarios para interactuar con el usuario.
 */
public class Vista extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsuario;
	private JPasswordField txtContrasenya;
	private JTextField txtCrearUsuario;
	private JPasswordField txtCrearContrasenya;
	private JPasswordField txtConfirmarContrasenya;
	private JTable table;
	private JTextField txtConsultaSQL;
	private JButton btnAcceder;
	private JPanel panelConsulta;
	private JPanel panelImportarCSVAdmin;
	private JButton btnDesconectar;
	private JButton btnCrearUsu;
	private JPanel panelAcceder;
	private JButton btnImportarCSV;
	private JTextArea textArea;
	private JButton btnEjecutar;
	private JLabel lblError;
	private JLabel lblverNombreUsuario;
	private JScrollPane scrollPane_1;
	private JButton btnExportarCSV;

	/**
     * Constructor de la clase Vista.
     * Configura los elementos gráficos de la interfaz, su apariencia y su disposición.
     */
	public Vista() {
		getContentPane().setBackground(new Color(144, 144, 144));
		setTitle("Consultas Población");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 518, 532);
		getContentPane().setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		
		lblverNombreUsuario = new JLabel("");
		lblverNombreUsuario.setForeground(new Color(60, 60, 60));
		lblverNombreUsuario.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblverNombreUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
		lblverNombreUsuario.setBounds(586, 15, 212, 32);
		getContentPane().add(lblverNombreUsuario);
		
		btnDesconectar = new JButton("Desconectarse");
		btnDesconectar.setBounds(807, 11, 126, 23);
		getContentPane().add(btnDesconectar);
		btnDesconectar.setVisible(false);
		
		panelAcceder = new JPanel();
		panelAcceder.setBackground(new Color(192, 192, 192));
		panelAcceder.setBounds(0, 0, 943, 47);
		getContentPane().add(panelAcceder);
		panelAcceder.setLayout(null);
		
		txtUsuario = new JTextField();
		txtUsuario.setBounds(71, 11, 86, 25);
		panelAcceder.add(txtUsuario);
		txtUsuario.setColumns(10);
		
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(10, 14, 55, 14);
		panelAcceder.add(lblUsuario);
		
		JLabel lblContrasenya = new JLabel("Contraseña:");
		lblContrasenya.setBounds(170, 14, 77, 14);
		panelAcceder.add(lblContrasenya);
		
		txtContrasenya = new JPasswordField();
		txtContrasenya.setBounds(257, 12, 77, 24);
		panelAcceder.add(txtContrasenya);
		
		btnAcceder = new JButton("Acceder");
		btnAcceder.setBounds(359, 12, 89, 23);
		panelAcceder.add(btnAcceder);
		
		panelImportarCSVAdmin = new JPanel();
		panelImportarCSVAdmin.setBackground(new Color(186, 179, 206));
		panelImportarCSVAdmin.setBounds(502, 45, 441, 448);
		getContentPane().add(panelImportarCSVAdmin);
		panelImportarCSVAdmin.setLayout(null);
		panelImportarCSVAdmin.setVisible(false);
		
		JLabel lblImportarCSV = new JLabel("Importar CSV a Base de Datos y visualizar XML");
		lblImportarCSV.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblImportarCSV.setBounds(10, 11, 383, 19);
		panelImportarCSVAdmin.add(lblImportarCSV);
		
		JLabel lblImprtarCSV = new JLabel("Importar CSV:");
		lblImprtarCSV.setBounds(125, 41, 91, 14);
		panelImportarCSVAdmin.add(lblImprtarCSV);
		
		btnImportarCSV = new JButton("Importar");
		btnImportarCSV.setBounds(226, 37, 89, 23);
		panelImportarCSVAdmin.add(btnImportarCSV);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 62, 420, 207);
		panelImportarCSVAdmin.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JPanel panelCrearUsuAdmin = new JPanel();
		panelCrearUsuAdmin.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelCrearUsuAdmin.setBounds(95, 280, 266, 157);
		panelImportarCSVAdmin.add(panelCrearUsuAdmin);
		panelCrearUsuAdmin.setBackground(new Color(186, 179, 206));
		panelCrearUsuAdmin.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Crear usuario");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 11, 118, 19);
		panelCrearUsuAdmin.add(lblNewLabel);
		
		JLabel lblCrearUsuario = new JLabel("Usuario:");
		lblCrearUsuario.setBounds(93, 41, 56, 14);
		panelCrearUsuAdmin.add(lblCrearUsuario);
		
		txtCrearUsuario = new JTextField();
		txtCrearUsuario.setColumns(10);
		txtCrearUsuario.setBounds(159, 41, 86, 20);
		panelCrearUsuAdmin.add(txtCrearUsuario);
		
		JLabel lblCrearConsenya = new JLabel("Contraseña:");
		lblCrearConsenya.setBounds(74, 68, 75, 14);
		panelCrearUsuAdmin.add(lblCrearConsenya);
		
		txtCrearContrasenya = new JPasswordField();
		txtCrearContrasenya.setBounds(159, 68, 86, 19);
		panelCrearUsuAdmin.add(txtCrearContrasenya);
		
		JLabel lblConfirmarContrasenya = new JLabel("Confirmar contraseña:");
		lblConfirmarContrasenya.setBounds(20, 93, 138, 14);
		panelCrearUsuAdmin.add(lblConfirmarContrasenya);
		
		txtConfirmarContrasenya = new JPasswordField();
		txtConfirmarContrasenya.setBounds(159, 93, 86, 19);
		panelCrearUsuAdmin.add(txtConfirmarContrasenya);
		
		btnCrearUsu = new JButton("Crear");
		btnCrearUsu.setBounds(93, 123, 89, 23);
		panelCrearUsuAdmin.add(btnCrearUsu);
		
		panelConsulta = new JPanel();
		panelConsulta.setBackground(new Color(255, 223, 223));
		panelConsulta.setBounds(0, 45, 502, 448);
		getContentPane().add(panelConsulta);
		panelConsulta.setLayout(null);
		panelConsulta.setVisible(false);
		
		JLabel lblConsultar = new JLabel("Consulta SQL y visualizar datos");
		lblConsultar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblConsultar.setBounds(10, 11, 268, 19);
		panelConsulta.add(lblConsultar);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 97, 482, 306);
		panelConsulta.add(scrollPane_1);
		
		table = new JTable();
		scrollPane_1.setViewportView(table);
		
		JLabel lblConsultaSQL = new JLabel("Escribe la consulta SQL:");
		lblConsultaSQL.setBounds(10, 41, 161, 14);
		panelConsulta.add(lblConsultaSQL);
		
		txtConsultaSQL = new JTextField();
		txtConsultaSQL.setBounds(152, 38, 340, 20);
		panelConsulta.add(txtConsultaSQL);
		txtConsultaSQL.setColumns(10);
		
		btnEjecutar = new JButton("Ejecutar consulta");
		btnEjecutar.setBounds(162, 66, 149, 23);
		panelConsulta.add(btnEjecutar);
		
		btnExportarCSV = new JButton("Exportar");
		btnExportarCSV.setBounds(234, 414, 89, 23);
		panelConsulta.add(btnExportarCSV);
		
		JLabel lblExportarCSV = new JLabel("Exportar a CSV:");
		lblExportarCSV.setBounds(136, 418, 106, 14);
		panelConsulta.add(lblExportarCSV);
		
		lblError = new JLabel("ERROR");
		lblError.setForeground(new Color(255, 0, 0));
		lblError.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblError.setBounds(321, 66, 89, 23);
		panelConsulta.add(lblError);
		lblError.setVisible(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		
		setVisible(true);
	}
	
	

	public JButton getBtnExportarCSV() {
		return btnExportarCSV;
	}
	
	public JTable getTable() {
		return table;
	}
	
	public JLabel getLblverNombreUsuario() {
		return lblverNombreUsuario;
	}

	public JLabel getLblError() {
		return lblError;
	}

	public JButton getBtnEjecutar() {
		return btnEjecutar;
	}

	public JTextField getTxtConsultaSQL() {
		return txtConsultaSQL;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public JTextField getTxtUsuario() {
		return txtUsuario;
	}

	public JPasswordField getTxtContrasenya() {
		return txtContrasenya;
	}
	
	public JButton getBtnAcceder() {
		return btnAcceder;
	}
	
	public JPanel getPanelConsulta() {
		return panelConsulta;
	}

	public JPanel getPanelImportarCSVAdmin() {
		return panelImportarCSVAdmin;
	}

	public JButton getBtnDesconectar() {
		return btnDesconectar;
	}
	
	public JPanel getPanelAcceder() {
		return panelAcceder;
	}

	public JButton getBtnCrearUsu() {
		return btnCrearUsu;
	}
	
	public JTextField getTxtCrearUsuario() {
		return txtCrearUsuario;
	}

	public JPasswordField getTxtCrearContrasenya() {
		return txtCrearContrasenya;
	}

	public JPasswordField getTxtConfirmarContrasenya() {
		return txtConfirmarContrasenya;
	}

	public JButton getBtnImportarCSV() {
		return btnImportarCSV;
	}
}
