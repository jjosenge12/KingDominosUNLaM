package parteGraficaClienteServidor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import clasesLogicas.Carta;
import clasesLogicas.Ficha;
import clasesLogicas.Mazo;
import comunicacionClienteServidor.Cliente;
import comunicacionClienteServidor.MensajeAServidor;
import comunicacionClienteServidor.Sala;
import parteGraficaJuego.GUI;
import parteGraficaJuego.PanelFicha;

public class MenuCreacionPartida extends JFrame {

	private static final long serialVersionUID = -7979669249255750493L;
	private JPanel contentPane;
	private List<JTextField> txtJugadores = new ArrayList<JTextField>(4);
	private JComboBox<Integer> cantJugadores;
	private JRadioButton rdbtnElGranDuelo;
	private List<JRadioButton> rdbtns;
	private JLabel lblTexturas;
	private JComboBox<String> texturas;
	private JRadioButton rdbtnDinastia;
	private JRadioButton rdbtnElReinoMedio;
	private JRadioButton rdbtnArmonia;
	private JComboBox<String> mazos;
	private JLabel lblMazo;
	private JButton btnVisualizarMazo;
	private Cliente cliente;
	private Sala sala;
	private List<String> usuarios;

	public MenuCreacionPartida(Cliente cliente, Sala sala, SalaDeEspera salaDeEspera, String nombresUsuarios) {

		setIconImage(Lobby.icono);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salaDeEspera.menuCerrado();
			}
		});
		this.cliente = cliente;
		this.sala = sala;
		usuarios = new ArrayList<String>();
		String[] arrayNombresUsuarios = nombresUsuarios.split("\\n");
		for (String nombre : arrayNombresUsuarios) {
			usuarios.add(nombre);
		}
		setVisible(true);
		setTitle("Menu");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JButton btnComenzar = new JButton("Comenzar partida");
		btnComenzar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarDatos();
			}
		});
		contentPane.add(btnComenzar, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(30, 50, 0, 0));
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		lblMazo = new JLabel("Mazo");
		GridBagConstraints gbc_lblMazo = new GridBagConstraints();
		gbc_lblMazo.insets = new Insets(0, 0, 5, 5);
		gbc_lblMazo.anchor = GridBagConstraints.EAST;
		gbc_lblMazo.gridx = 1;
		gbc_lblMazo.gridy = 0;
		panel.add(lblMazo, gbc_lblMazo);

		mazos = new JComboBox<String>();
		File archivoMazo = new File("./assets");

		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		};

		File[] files = archivoMazo.listFiles(textFilter);

		for (File file : files) {
			mazos.addItem(file.getName().substring(0, file.getName().length() - 4));
		}
		mazos.setSelectedIndex(2);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 0;
		panel.add(mazos, gbc_comboBox);

		btnVisualizarMazo = new JButton("Visualizar mazo");
		btnVisualizarMazo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClickVisualizarMazo();
			}
		});
		GridBagConstraints gbc_btnVisualizarMazo = new GridBagConstraints();
		gbc_btnVisualizarMazo.insets = new Insets(0, 0, 5, 5);
		gbc_btnVisualizarMazo.gridx = 4;
		gbc_btnVisualizarMazo.gridy = 0;
		panel.add(btnVisualizarMazo, gbc_btnVisualizarMazo);

		JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem("New radio item");
		GridBagConstraints gbc_radioButtonMenuItem = new GridBagConstraints();
		gbc_radioButtonMenuItem.insets = new Insets(0, 0, 5, 0);
		gbc_radioButtonMenuItem.gridx = 5;
		gbc_radioButtonMenuItem.gridy = 0;
		panel.add(radioButtonMenuItem, gbc_radioButtonMenuItem);

		lblTexturas = new JLabel("Texturas");
		GridBagConstraints gbc_lblTexturas = new GridBagConstraints();
		gbc_lblTexturas.anchor = GridBagConstraints.EAST;
		gbc_lblTexturas.insets = new Insets(0, 0, 5, 5);
		gbc_lblTexturas.gridx = 1;
		gbc_lblTexturas.gridy = 1;
		panel.add(lblTexturas, gbc_lblTexturas);

		texturas = new JComboBox<String>();

		textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".png");
			}
		};
		File archivoMazosConTextura = new File("./assets/mazos/");
		files = archivoMazosConTextura.listFiles(textFilter);

		for (File file : files) {
			texturas.addItem(file.getName().substring(0, file.getName().length() - 4));
		}
		texturas.setSelectedIndex(1);
		GridBagConstraints gbc_texturas = new GridBagConstraints();
		gbc_texturas.insets = new Insets(0, 0, 5, 5);
		gbc_texturas.fill = GridBagConstraints.HORIZONTAL;
		gbc_texturas.gridx = 2;
		gbc_texturas.gridy = 1;
		panel.add(texturas, gbc_texturas);

		JLabel lblCantJugadores = new JLabel("Cantidad de jugadores:");
		GridBagConstraints gbc_lblCantJugadores = new GridBagConstraints();
		gbc_lblCantJugadores.insets = new Insets(0, 0, 5, 5);
		gbc_lblCantJugadores.gridx = 1;
		gbc_lblCantJugadores.gridy = 2;
		panel.add(lblCantJugadores, gbc_lblCantJugadores);

		cantJugadores = new JComboBox<Integer>();
		cantJugadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cantJugadoresSeleccionado(usuarios.size());
			}
		});

		GridBagConstraints gbc_cantJugadores = new GridBagConstraints();
		gbc_cantJugadores.insets = new Insets(0, 0, 5, 5);
		gbc_cantJugadores.fill = GridBagConstraints.HORIZONTAL;
		gbc_cantJugadores.gridx = 2;
		gbc_cantJugadores.gridy = 2;
		panel.add(cantJugadores, gbc_cantJugadores);

		ButtonGroup radioButtons = new ButtonGroup();
		rdbtnElGranDuelo = new JRadioButton("El gran duelo");
		radioButtons.add(rdbtnElGranDuelo);
		GridBagConstraints gbc_rdbtnElGranDuelo = new GridBagConstraints();
		gbc_rdbtnElGranDuelo.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnElGranDuelo.gridx = 4;
		gbc_rdbtnElGranDuelo.gridy = 3;
		panel.add(rdbtnElGranDuelo, gbc_rdbtnElGranDuelo);

		JTextField txtJugador1 = new JTextField();
		txtJugadores.add(txtJugador1);
		txtJugador1.setEnabled(false);
		txtJugador1.setText("Jugador 1");
		GridBagConstraints gbc_txtJugador1 = new GridBagConstraints();
		gbc_txtJugador1.insets = new Insets(0, 0, 5, 5);
		gbc_txtJugador1.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtJugador1.gridx = 1;
		gbc_txtJugador1.gridy = 4;
		panel.add(txtJugador1, gbc_txtJugador1);
		txtJugador1.setColumns(10);

		rdbtns = new ArrayList<JRadioButton>();
		JRadioButton rdbtnBot1 = new JRadioButton("Bot");
		rdbtns.add(rdbtnBot1);
		rdbtnBot1.setEnabled(false);
		GridBagConstraints gbc_rdbtnBot1 = new GridBagConstraints();
		gbc_rdbtnBot1.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBot1.gridx = 2;
		gbc_rdbtnBot1.gridy = 4;
		panel.add(rdbtnBot1, gbc_rdbtnBot1);

		rdbtnDinastia = new JRadioButton("Dinastia");
		radioButtons.add(rdbtnDinastia);
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton.gridx = 4;
		gbc_rdbtnNewRadioButton.gridy = 4;
		panel.add(rdbtnDinastia, gbc_rdbtnNewRadioButton);

		JTextField txtJugador2 = new JTextField();
		txtJugadores.add(txtJugador2);
		txtJugador2.setText("Jugador 2");
		GridBagConstraints gbc_txtJugador2 = new GridBagConstraints();
		gbc_txtJugador2.insets = new Insets(0, 0, 5, 5);
		gbc_txtJugador2.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtJugador2.gridx = 1;
		gbc_txtJugador2.gridy = 5;
		panel.add(txtJugador2, gbc_txtJugador2);
		txtJugador2.setColumns(10);

		JRadioButton rdbtnBot2 = new JRadioButton("Bot");
		rdbtns.add(rdbtnBot2);
		rdbtnBot2.setEnabled(false);
		GridBagConstraints gbc_rdbtnBot2 = new GridBagConstraints();
		gbc_rdbtnBot2.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBot2.gridx = 2;
		gbc_rdbtnBot2.gridy = 5;
		panel.add(rdbtnBot2, gbc_rdbtnBot2);

		rdbtnElReinoMedio = new JRadioButton("El reino medio");
		radioButtons.add(rdbtnElReinoMedio);
		GridBagConstraints gbc_rdbtnNewRadioButton_1 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton_1.gridx = 4;
		gbc_rdbtnNewRadioButton_1.gridy = 5;
		panel.add(rdbtnElReinoMedio, gbc_rdbtnNewRadioButton_1);

		JTextField txtJugador3 = new JTextField();
		txtJugadores.add(txtJugador3);
		txtJugador3.setEnabled(false);
		txtJugador3.setEditable(false);
		txtJugador3.setText("Jugador 3");
		GridBagConstraints gbc_txtJugador3 = new GridBagConstraints();
		gbc_txtJugador3.insets = new Insets(0, 0, 5, 5);
		gbc_txtJugador3.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtJugador3.gridx = 1;
		gbc_txtJugador3.gridy = 6;
		panel.add(txtJugador3, gbc_txtJugador3);
		txtJugador3.setColumns(10);

		JRadioButton rdbtnBot3 = new JRadioButton("Bot");
		rdbtns.add(rdbtnBot3);
		rdbtnBot3.setEnabled(false);

		GridBagConstraints gbc_rdbtnBot3 = new GridBagConstraints();
		gbc_rdbtnBot3.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBot3.gridx = 2;
		gbc_rdbtnBot3.gridy = 6;
		panel.add(rdbtnBot3, gbc_rdbtnBot3);

		rdbtnArmonia = new JRadioButton("Armonia");
		radioButtons.add(rdbtnArmonia);
		GridBagConstraints gbc_rdbtnNewRadioButton_2 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_2.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton_2.gridx = 4;
		gbc_rdbtnNewRadioButton_2.gridy = 6;
		panel.add(rdbtnArmonia, gbc_rdbtnNewRadioButton_2);

		JTextField txtJugador4 = new JTextField();
		txtJugadores.add(txtJugador4);
		txtJugador4.setEnabled(false);
		txtJugador4.setEditable(false);
		txtJugador4.setText("Jugador 4");
		GridBagConstraints gbc_txtJugador4 = new GridBagConstraints();
		gbc_txtJugador4.insets = new Insets(0, 0, 0, 5);
		gbc_txtJugador4.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtJugador4.gridx = 1;
		gbc_txtJugador4.gridy = 7;
		panel.add(txtJugador4, gbc_txtJugador4);
		txtJugador4.setColumns(10);

		JRadioButton rdbtnBot4 = new JRadioButton("Bot");
		rdbtns.add(rdbtnBot4);
		rdbtnBot4.setEnabled(false);
		GridBagConstraints gbc_rdbtnBot4 = new GridBagConstraints();
		gbc_rdbtnBot4.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnBot4.gridx = 2;
		gbc_rdbtnBot4.gridy = 7;
		panel.add(rdbtnBot4, gbc_rdbtnBot4);
		actualizarComboBox(usuarios.size());
		actualizarNombresJugadores(usuarios);
	}

	private void actualizarNombresJugadores(List<String> usuarios) {

		int i = 0;
		while (i < usuarios.size()) {
			JTextField jTextField = txtJugadores.get(i);
			jTextField.setText(usuarios.get(i));
			jTextField.setEnabled(false);
			jTextField.setEditable(false);
			i++;
		}
		while (i < 4) {
			JTextField jTextField = txtJugadores.get(i);
			jTextField.setText("Jugador " + (i + 1));
			jTextField.setEnabled(false);
			jTextField.setEditable(false);
			i++;
		}
		cantJugadoresSeleccionado(usuarios.size());
	}

	private void actualizarComboBox(int cantJugadoresActualizado) {
		List<Integer> jugadores = new ArrayList<Integer>(cantJugadoresActualizado);
		for (int i = Math.max(cantJugadoresActualizado, 2); i <= 4; i++) {
			jugadores.add(i);
		}
		if (jugadores.isEmpty()) {
			cantJugadores.setEnabled(false);
		} else {
			cantJugadores.setEnabled(true);
		}
		cantJugadores.setModel(new DefaultComboBoxModel<Integer>(jugadores.toArray(new Integer[jugadores.size()])));
		cantJugadores.setSelectedIndex(0);
	}

	protected void onClickVisualizarMazo() {
		JFrame tempFrame = new JFrame();
		JPanel tempContentPane = new JPanel();
		tempFrame.setSize(1340, 540);
		tempContentPane.setLayout(null);
		tempFrame.setContentPane(tempContentPane);

		PanelFicha pF = null;
		GUI.seleccionarTextura((String) texturas.getSelectedItem());
		GUI.cargarTexturas();
		Mazo mazo = new Mazo(48, (String) mazos.getSelectedItem());
		List<Carta> cartas = mazo.getCartas();
		int i = 0;
		for (Carta carta : cartas) {
			Ficha[] fichas = carta.getFichas();
			pF = new PanelFicha(fichas[0], 0, 0, 1, 1);
			int tamFicha = PanelFicha.getTamFicha();
			pF.setBounds(i % 16 * tamFicha, (i / 16) * tamFicha, tamFicha, tamFicha);
			tempContentPane.add(pF);
			i++;
			pF = new PanelFicha(fichas[1], 0, 0, 1, 1);
			pF.setBounds(i % 16 * tamFicha, (i / 16) * tamFicha, tamFicha, tamFicha);
			i++;
			tempContentPane.add(pF);
		}
		tempFrame.setVisible(true);

	}

	protected void cantJugadoresSeleccionado(int cantUsuarios) {
		JTextField txtJugador2 = txtJugadores.get(1);
		JTextField txtJugador3 = txtJugadores.get(2);
		JTextField txtJugador4 = txtJugadores.get(3);
		JRadioButton rdbtnBot2 = rdbtns.get(1);
		JRadioButton rdbtnBot3 = rdbtns.get(2);
		JRadioButton rdbtnBot4 = rdbtns.get(3);
		int cant = (int) cantJugadores.getSelectedItem();
		switch (cant) {
		case 2:
			rdbtnElGranDuelo.setEnabled(true);
			if (cantUsuarios < 2) {
				txtJugador2.setEditable(true);
				txtJugador2.setEnabled(true);
				rdbtnBot2.setSelected(true);
			} else {
				txtJugador2.setEditable(false);
				txtJugador2.setEnabled(false);
				rdbtnBot2.setSelected(false);
			}
			rdbtnBot3.setSelected(false);
			rdbtnBot4.setSelected(false);
			txtJugador3.setEditable(false);
			txtJugador4.setEditable(false);
			txtJugador3.setEnabled(false);
			txtJugador4.setEnabled(false);
			break;
		case 3:
			rdbtnElGranDuelo.setSelected(false);
			rdbtnElGranDuelo.setEnabled(false);
			if (cantUsuarios < 3) {
				txtJugador3.setEditable(true);
				txtJugador3.setEnabled(true);
				rdbtnBot3.setSelected(true);
			} else {
				txtJugador3.setEditable(false);
				txtJugador3.setEnabled(false);
				rdbtnBot3.setSelected(false);
			}
			rdbtnBot4.setSelected(false);
			txtJugador4.setEditable(false);
			txtJugador4.setEnabled(false);

			break;
		case 4:
			rdbtnElGranDuelo.setSelected(false);
			rdbtnElGranDuelo.setEnabled(false);
			if (cantUsuarios < 3) {
				txtJugador3.setEditable(true);
				txtJugador3.setEnabled(true);
				rdbtnBot3.setSelected(true);
			} else {
				txtJugador3.setEditable(false);
				txtJugador3.setEnabled(false);
				rdbtnBot3.setSelected(false);
			}
			if (cantUsuarios < 4) {
				txtJugador4.setEditable(true);
				txtJugador4.setEnabled(true);
				rdbtnBot4.setSelected(true);
			} else {
				txtJugador4.setEditable(false);
				txtJugador4.setEnabled(false);
				rdbtnBot4.setSelected(false);
			}
			break;
		}
		rdbtnBot2.setEnabled(false);
		rdbtnBot3.setEnabled(false);
		rdbtnBot4.setEnabled(false);
	}

	protected void enviarDatos() {

		int cant = (int) cantJugadores.getSelectedItem();
		JTextField txtJugador1 = txtJugadores.get(0);
		JTextField txtJugador2 = txtJugadores.get(1);
		JTextField txtJugador3 = txtJugadores.get(2);
		JTextField txtJugador4 = txtJugadores.get(3);
		JRadioButton rdbtnBot1 = rdbtns.get(0);
		JRadioButton rdbtnBot2 = rdbtns.get(1);
		JRadioButton rdbtnBot3 = rdbtns.get(2);
		JRadioButton rdbtnBot4 = rdbtns.get(3);
		String nombreJugadores = "";
		String mensajeCrearPartida = "";

		if (rdbtnBot1.isSelected()) {
			mensajeCrearPartida += "B";
		} else {
			mensajeCrearPartida += "J";
		}
		nombreJugadores += txtJugador1.getText() + "|";
		if (rdbtnBot2.isSelected()) {
			mensajeCrearPartida += "B";
		} else {
			mensajeCrearPartida += "J";
		}
		nombreJugadores += txtJugador2.getText() + "|";
		if (cant > 2) {
			if (rdbtnBot3.isSelected()) {
				mensajeCrearPartida += "B";
			} else {
				mensajeCrearPartida += "J";
			}
			nombreJugadores += txtJugador3.getText() + "|";
			if (cant > 3) {
				if (rdbtnBot4.isSelected()) {
					mensajeCrearPartida += "B";
				} else {
					mensajeCrearPartida += "J";
				}
				nombreJugadores += txtJugador4.getText() + "|";
			}

		}
		mensajeCrearPartida += "," + nombreJugadores;
//		mensajeCrearPartida += "," + tamTablero;

		String textura = (String) texturas.getSelectedItem();
		mensajeCrearPartida += "," + textura;
		this.setVisible(false);
		String mazo = (String) mazos.getSelectedItem();
		mensajeCrearPartida += "," + mazo;
		String modoDeJuego = "Clasico";
		if (rdbtnElGranDuelo.isSelected())
			modoDeJuego = "ElGranDuelo";
		if (rdbtnDinastia.isSelected())
			modoDeJuego = "Dinastia";
		if (rdbtnElReinoMedio.isSelected())
			modoDeJuego = "ReinoMedio";
		if (rdbtnArmonia.isSelected())
			modoDeJuego = "Armonia";
		mensajeCrearPartida += "," + modoDeJuego;
		MensajeAServidor msj = new MensajeAServidor(mensajeCrearPartida, sala.getNombreSala(), 13);
		cliente.enviarMensaje(msj);

		this.dispose();
	}

}
