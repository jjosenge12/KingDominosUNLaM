package parteGraficaClienteServidor;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MenuConexion extends JFrame {

	private static final long serialVersionUID = -5609715499660842305L;
	private JPanel panel;
	private JTextField txtIp;
	private JTextField txtPuerto;
	private JTextField txtUsuario;
	private JButton btnConectarse;


	public MenuConexion(Lobby lobby) {
		setIconImage(Lobby.icono);
		setVisible(true);
		setTitle("Menu de conexion");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 200);
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);
		FlowLayout fl_panel = new FlowLayout(FlowLayout.CENTER, 5, 25);
		panel.setLayout(fl_panel);
		
				JLabel lblUsuario = new JLabel("Usuario:");
				panel.add(lblUsuario);
		
				txtUsuario = new JTextField();
				txtUsuario.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						btnConectarse.setEnabled(true);
					}
				});
				
						txtUsuario.setToolTipText("Usuario:");
						panel.add(txtUsuario);
						txtUsuario.setColumns(10);

		JLabel lblIp = new JLabel("IP:");
		panel.add(lblIp);

		txtIp = new JTextField();
		txtIp.setText("localhost");
		panel.add(txtIp);
		txtIp.setColumns(10);

		JLabel lblPuerto = new JLabel("Puerto:");
		panel.add(lblPuerto);

		txtPuerto = new JTextField();
		txtPuerto.setText("50000");
		panel.add(txtPuerto);
		txtPuerto.setColumns(10);

		btnConectarse = new JButton("Conectarse");
		btnConectarse.setEnabled(false);
		btnConectarse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lobby.crearUsuario(txtIp.getText(), Integer.parseInt(txtPuerto.getText()), txtUsuario.getText());
				dispose();
			}
		});
		panel.add(btnConectarse);

	}

}
