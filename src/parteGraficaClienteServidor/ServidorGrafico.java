package parteGraficaClienteServidor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import comunicacionClienteServidor.HiloServidor;

public class ServidorGrafico extends JFrame {

	private static final long serialVersionUID = -5235899935745609822L;
	private JPanel contentPane;
	private JTextField txtPuerto;

	JTextPane txtInfo;
	JButton btnIniciar;
	HiloServidor hiloServidor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServidorGrafico frame = new ServidorGrafico();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServidorGrafico() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\assets\\rey-servidor.png"));
		setTitle("Creacion del servidor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel pnlCenter = new JPanel();
		contentPane.add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new BorderLayout(0, 0));

		JPanel pnlBottom = new JPanel();
		pnlCenter.add(pnlBottom, BorderLayout.NORTH);

		JLabel lblPuerto = new JLabel("Puerto:");
		pnlBottom.add(lblPuerto);

		txtPuerto = new JTextField();
		txtPuerto.setText("50000");
		pnlBottom.add(txtPuerto);
		txtPuerto.setColumns(10);

		btnIniciar = new JButton("Iniciar servidor");
		btnIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iniciarServidor(Integer.parseInt(txtPuerto.getText()));
			}
		});
		pnlBottom.add(btnIniciar);

		txtInfo = new JTextPane();
		txtInfo.setEditable(false);
		pnlCenter.add(txtInfo, BorderLayout.CENTER);
	}

	public void mostrarInfo(String info) {
		txtInfo.setText(txtInfo.getText() + info + "\n");

	}

	private void iniciarServidor(int puerto) {
		txtPuerto.setEditable(false);
		txtPuerto.setEnabled(false);
		btnIniciar.setEnabled(false);
		hiloServidor = new HiloServidor(this, puerto);
		hiloServidor.start();
	}

	public void puertoOcupado() {
		JOptionPane.showMessageDialog(this, "Puerto ya ocupado", "Error en creacion de puertos",
				JOptionPane.WARNING_MESSAGE);
		txtPuerto.setEditable(true);
		txtPuerto.setEnabled(true);
		txtPuerto.setText("");
		btnIniciar.setEnabled(true);
	}

}
