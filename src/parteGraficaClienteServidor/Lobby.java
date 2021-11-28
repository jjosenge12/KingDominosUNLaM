package parteGraficaClienteServidor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import comunicacionClienteServidor.Cliente;
import comunicacionClienteServidor.MensajeACliente;
import comunicacionClienteServidor.MensajeAServidor;
import comunicacionClienteServidor.Sala;

public class Lobby extends JFrame {

	private static final long serialVersionUID = -4698413876318966275L;
	public static final Image icono=Toolkit.getDefaultToolkit().getImage(".\\assets\\rey.png");
	private JPanel contentPane;
	private JButton btnCrearSala;
	private JList<String> listaSalas;
	private Map<String, Sala> mapaSalas;
	private Map<String, SalaDeEspera> mapaSalasAbiertas;
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private Cliente cliente;
	private JMenuItem menuItemConectarse;
	private JPanel panelBordeDerecho;
	private JButton btnBorrarSala;
	private JButton btnUnirseASala;
	private JPanel panelBotones;
	private JPanel panelInfoSala;
	JMenuItem btnDesconexion;

	private JTextPane textAreaInfoSala;

	public static void main(String[] args) {
		new Lobby();
	}

	public Lobby() {
		setIconImage(icono);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				desconectarse();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Conexion");
		menuBar.add(mnNewMenu);

		menuItemConectarse = new JMenuItem("Conectarse");
		menuItemConectarse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				conectarse();
			}
		});
		mnNewMenu.add(menuItemConectarse);

		btnDesconexion = new JMenuItem("Salir");
		btnDesconexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desconectarse();
				dispose();
			}
		});
		mnNewMenu.add(btnDesconexion);
		btnDesconexion.setEnabled(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		btnCrearSala = new JButton("\uD83D\uDCBB Crear Sala ");
		btnCrearSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ingresarNombreSalaACrear();
			}
		});
		btnCrearSala.setEnabled(false);
		contentPane.add(btnCrearSala, BorderLayout.SOUTH);

		listaSalas = new JList<String>();
		listaSalas.setBorder(new LineBorder(new Color(0, 0, 0)));
		listaSalas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!listaSalas.getValueIsAdjusting() && !listaSalas.isSelectionEmpty())
					mostrarInfoSala();
			}

		});
		listaSalas.setEnabled(false);
		listaSalas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaSalas.setModel(listModel);
		JScrollPane scroll = new JScrollPane(listaSalas);
		contentPane.add(scroll, BorderLayout.CENTER);

		panelBordeDerecho = new JPanel();
		panelBordeDerecho.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panelBordeDerecho, BorderLayout.EAST);
		panelBordeDerecho.setLayout(new GridLayout(0, 1, 0, 15));

		panelBotones = new JPanel();
		panelBordeDerecho.add(panelBotones);
		panelBotones.setLayout(new GridLayout(0, 1, 0, 0));

		btnUnirseASala = new JButton("\uD83D\uDEEB Unirse a sala");
		panelBotones.add(btnUnirseASala);
		btnUnirseASala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unirseASala();
			}
		});

		btnUnirseASala.setEnabled(false);

		btnBorrarSala = new JButton("\u274C Borrar Sala");
		panelBotones.add(btnBorrarSala);
		btnBorrarSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				borrarSala();
			}
		});
		btnBorrarSala.setEnabled(false);

		panelInfoSala = new JPanel();
		panelBordeDerecho.add(panelInfoSala);
		panelInfoSala.setLayout(new BorderLayout(0, 0));

		textAreaInfoSala = new JTextPane();
		textAreaInfoSala.setBackground(Color.WHITE);
		textAreaInfoSala.setEnabled(false);
		textAreaInfoSala.setEditable(false);
		panelInfoSala.add(textAreaInfoSala);
		setVisible(true);
		mapaSalas = new HashMap<String, Sala>();
		mapaSalasAbiertas = new HashMap<String, SalaDeEspera>();
	}

	private void conectarse() {
		new MenuConexion(this);
	}

	public void crearUsuario(String ip, int puerto, String nombreUsuario) {
		if (nombreUsuario != null && !nombreUsuario.equals("")) {
			try {
				cliente = new Cliente(nombreUsuario, ip, puerto, this);
				cliente.inicializarHiloCliente(this);
			} catch (UnknownHostException e) {
				mostrarErrorPorPantalla("Error host desconocido", "Error de conexion");
			} catch (IOException e) {
				mostrarErrorPorPantalla("No se puede conectar al servidor, intentelo nuevamente en unos minutos.",
						"Error de conexion");
			}

		}
	}

	public void activarBotones() {
		setTitle("Usuario:" + cliente.getNombre() + "- Lobby");
		btnCrearSala.setEnabled(true);
		listaSalas.setEnabled(true);
		menuItemConectarse.setEnabled(false);

		btnUnirseASala.setEnabled(true);
		btnBorrarSala.setEnabled(true);
		btnDesconexion.setEnabled(true);
	}

	public void servidorDejoDeResponder() {
		mostrarErrorPorPantalla("El servidor dejo de responder", "Error de conexion");
		setTitle("");
		btnCrearSala.setEnabled(false);
		listaSalas.setEnabled(false);
		menuItemConectarse.setEnabled(true);

		btnUnirseASala.setEnabled(false);
		btnBorrarSala.setEnabled(false);
		btnDesconexion.setEnabled(false);

		for (Map.Entry<String, SalaDeEspera> entry : mapaSalasAbiertas.entrySet()) {
			cerrarSala(entry.getKey());
		}
	}

	public void desconectarse() {
		if (cliente != null) {
			for (Map.Entry<String, SalaDeEspera> entry : mapaSalasAbiertas.entrySet()) {
				entry.getValue().cerrarSala();
			}

			cliente.cerrarHilo();
			MensajeAServidor msj = new MensajeAServidor(cliente.getNombre(), null, 0);
			cliente.enviarMensaje(msj);
			System.exit(0);
		}
	}

	public <T> void enviarMensaje(T mensaje) {
		cliente.enviarMensaje(mensaje);
	}

	public void enviarMensaje(MensajeAServidor mensaje) {
		cliente.enviarMensaje(mensaje);
	}

	public void mostrarInfoSala() {
		int cantUsuarios = 0;
		String salaElegida = listaSalas.getSelectedValue();
		Sala sala = mapaSalas.get(salaElegida);
		cantUsuarios = sala.getCantUsuarios();
		textAreaInfoSala.setText("Informacion de la sala:\n Conectados:" + cantUsuarios);
	}

	public void actualizarSalas(List<Sala> listaSalasActualizada) {
		mapaSalas.clear();
		for (Sala sala : listaSalasActualizada) {
			mapaSalas.put(sala.getNombreSala(), sala);
		}
		listModel.clear();
		for (Sala s : listaSalasActualizada) {
			listModel.addElement(s.getNombreSala());
		}
	}

	protected void ingresarNombreSalaACrear() {
		String respuesta = JOptionPane.showInputDialog(this, "Ingrese nombre de sala:", "");
		if (respuesta != null && !respuesta.equals("")) {
			String nombreCliente = cliente.getNombre();
//			Sala sala = new Sala(respuesta, nombreCliente);
			MensajeAServidor msj = new MensajeAServidor(nombreCliente, respuesta + "," + nombreCliente, 2);
			cliente.enviarMensaje(msj);
		}
	}

	protected void borrarSala() {
		String nombreSalaElegida = listaSalas.getSelectedValue();
		Sala salaAEliminar = null;

		if (nombreSalaElegida != null) {
			salaAEliminar = mapaSalas.get(nombreSalaElegida);
			if (salaAEliminar.getCantUsuarios() == 0) {
				MensajeAServidor msj = new MensajeAServidor(null, salaAEliminar.getNombreSala(), 3);
				cliente.enviarMensaje(msj);
			} else {
				JOptionPane.showMessageDialog(this, "Tiene que haber 0 usuarios en la sala para eliminarla",
						"No se puede borrar la sala", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void unirseASala() {
		String salaElegida = listaSalas.getSelectedValue();

		if (salaElegida != null) {// En esta implementacion solo se puede estar en una sala(partida) a la vez
			boolean puedeAbrir = verificarCantidadSalas(1);
			if (puedeAbrir) {
				boolean abierta = isSalaAbierta(salaElegida);
				if (abierta == false) {
					Sala sala = mapaSalas.get(salaElegida);
					MensajeAServidor msj = new MensajeAServidor(cliente.getNombre(), sala.getNombreSala(), 4);
					cliente.enviarMensaje(msj);
					salaActual = sala;
				}

			}
		}
	}

	public void abrirSala(String nombreSala) {
		Sala sala = mapaSalas.get(nombreSala);
		SalaDeEspera salaChat = new SalaDeEspera(sala, cliente);
		mapaSalasAbiertas.put(nombreSala, salaChat);
		mostrarInfoSala();
	}

	public void cerrarSala(String nombreSala) {
		if (mapaSalasAbiertas.size() > 0) {
			SalaDeEspera salaARemover = mapaSalasAbiertas.get(nombreSala);
			mapaSalasAbiertas.remove(nombreSala);
			if (salaARemover != null) {
				salaARemover.dispose();
			}
		}

	}

	private Sala salaActual;

	public void recibirMensaje(MensajeACliente mensaje) {
		String nombreSala = mensaje.getNombreSala();
		if (mapaSalasAbiertas.size() > 0) {
			SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(nombreSala);
			salaDeEspera.mostrarMensaje(mensaje.getTexto());
		}

	}

	public void recibirTiempos(MensajeACliente mensaje) {
		String nombreSala = mensaje.getNombreSala();
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(nombreSala);
		salaDeEspera.mostrarTiempos(mensaje.getTexto());
	}

	public void recibirListaUsuarios(MensajeACliente mensaje) {
		int tipo = mensaje.getTipo();
		if (tipo == 7) {
			mostrarListaUsuario(mensaje);
		} else {
			SalaDeEspera salaEspera = mapaSalasAbiertas.get(mensaje.getNombreSala());
			salaEspera.abrirMenuCreacionPartida(mensaje.getTexto());
		}
	}

	private void mostrarListaUsuario(MensajeACliente mensaje) {
		String nombreSala = mensaje.getNombreSala();
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(nombreSala);
		salaDeEspera.mostrarListaUsuarios(mensaje.getTexto());
	}

	public void mostrarErrorPorPantalla(String descripcion, String titulo) {
		JOptionPane.showMessageDialog(this, descripcion, titulo, JOptionPane.WARNING_MESSAGE);
	}

	public boolean verificarCantidadSalas(int salasMaximas) {

		int cantSalasAbiertas = mapaSalasAbiertas.size();
		if (cantSalasAbiertas < salasMaximas) {
			return true;
		} else {
			JOptionPane.showMessageDialog(this, "Se pueden abrir " + salasMaximas + " salas como maximo",
					"Cantidad de salas abiertas maximas", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

	public boolean isSalaAbierta(String salaElegida) {
		boolean abierta = mapaSalasAbiertas.containsKey(salaElegida);
		if (abierta) {
			JOptionPane.showMessageDialog(this, "Esa sala ya esta abierta", "Sala abierta",
					JOptionPane.INFORMATION_MESSAGE);
		}
		return abierta;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Sala getSalaActual() {
		return salaActual;
	}

	public void actualizarMenu(MensajeACliente mensaje) {
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(mensaje.getNombreSala());
		salaDeEspera.actualizarMenu();
	}

	public void unirseAPartida(MensajeACliente mensaje) {
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(mensaje.getNombreSala());
		salaDeEspera.unirseAPartida(mensaje);
	}

	public void recibirRendicionDeOtroJugador(MensajeACliente mensaje) {
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(mensaje.getNombreSala());
		salaDeEspera.recibirRendicionDeOtroJugador(mensaje);
	}

	public void partidaFinalizada(String nombreSala) {
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(nombreSala);
		salaDeEspera.partidaFinalizada();

	}

	public void actualizarTurnoPartida(MensajeACliente mensaje) {
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(mensaje.getNombreSala());
		salaDeEspera.actualizarTurno(mensaje);
	}

	public void procesarTurnoPartida(MensajeACliente mensaje) {
		SalaDeEspera salaDeEspera = mapaSalasAbiertas.get(mensaje.getNombreSala());
		salaDeEspera.procesarTurnoJugador(mensaje);

	}

}
