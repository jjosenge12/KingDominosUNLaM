package parteGraficaJuego;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import clasesLogicas.Bot;
import clasesLogicas.Carta;
import clasesLogicas.Jugador;
import clasesLogicas.Partida;
import comunicacionClienteServidor.Cliente;
import comunicacionClienteServidor.MensajeACliente;
import comunicacionClienteServidor.MensajeAServidor;
import comunicacionClienteServidor.MensajePartidaEnJuego;
import parteGraficaClienteServidor.SalaDeEspera;

public class GUI extends JFrame {

	private static final long serialVersionUID = 4460429712849713216L;
	// Texturas por defecto
	private static String texturaCarta = "./assets/mazos/original.png";
	private static String texturaCastilloAmarillo = "./assets/castilloAmarillo.png";
	private static String texturaCastilloAzul = "./assets/castilloAzul.png";
	private static String texturaCastilloRojo = "./assets/castilloRojo.png";
	private static String texturaCastilloVerde = "./assets/castilloVerde.png";
	private static String texturaCorona = "./assets/corona.png";
	private static String texturaVacia = "./assets/vacio.png";
	static BufferedImage bufferCastilloAmarillo;
	static BufferedImage bufferCastilloAzul;
	static BufferedImage bufferCastilloRojo;
	static BufferedImage bufferCastilloVerde;
	static BufferedImage bufferCarta;
	static BufferedImage bufferVacio;
	static BufferedImage bufferCorona;
	private Sonido sonido;

	private final static int TAM_FICHA = 80;
	private static double LARGO_VENTANA;
	private static double ALTO_VENTANA;
	static int TAM_TABLEROS;
	static PanelTableroSeleccion pSeleccion;
	private TablerosJugadores tableros;
	private PanelInformacion informacion;

	private static CountDownLatch latchPosicionElegida = new CountDownLatch(1);
	public static volatile int[] coordenadasElegidas = new int[4];
	public static PanelFicha fichaElegida;

	private int xVentana;
	private int yVentana;
	private String nombreSala;
	private List<Jugador> jugadores;
	private String modoDeJuego;
	private Cliente cliente;
	private boolean rendido = false;
	private static int idJugador;
	private static int turnoJugadorActual;
	private static boolean puedeJugar = false;
	private int partidasRestantes;
	private boolean dinastia;
	private List<Integer> puntajesTotales;
	private SalaDeEspera salaDeEspera;

	public GUI(MensajeACliente mensajeACliente, SalaDeEspera salaDeEspera) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\assets\\rey.png"));
		this.salaDeEspera = salaDeEspera;
		this.cliente = salaDeEspera.getCliente();
		procesarConfiguracion(mensajeACliente);
		setVisible(true);
		setResizable(false);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salirDeVentana();
			}

		});
//		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBounds(0, 0, 800, 600);
		setResizable(true);
		getContentPane().setBackground(new Color(0x5E411B));
		setLocationRelativeTo(null);
		setVisible(true);

		actualizarCoordenadas();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				actualizarCoordenadas();
			}
		});

		cargarTexturas();

		Dimension dimensiones = getContentPane().getSize();
		LARGO_VENTANA = (int) dimensiones.getWidth();
		ALTO_VENTANA = (int) dimensiones.getHeight();
		TAM_TABLEROS = (int) ALTO_VENTANA;
		actualizarInterfaz();

		try {
			Sonido s = new Sonido("./assets/Sound/main.wav");
			s.setVolume(0.01f);
			s.play(Clip.LOOP_CONTINUOUSLY);
			sonido = s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Inicializamos pSeleccion con cartas vacias, esto evita problemas con
		// condiciones de carrera
		// además, no es intuitivo que pSeleccion no exista hasta que se le envien
		// cartas, puesto
		// que delega la responsabilidad de su creacion a Partida, que no es una clase
		// grafica
		List<Carta> cartasAElegir = new ArrayList<Carta>();
		cartasAElegir.add(null);
		mostrarCartasAElegir(cartasAElegir);
		latchPosicionElegida = new CountDownLatch(1);

	}

	private void procesarConfiguracion(MensajeACliente mensajeACliente) {
		this.nombreSala = mensajeACliente.getNombreSala();
		String[] configuracion = mensajeACliente.getTexto().split(",");
		String tiposJugadores = configuracion[0];
		String[] nombresJugadores = configuracion[1].split("\\|");
		jugadores = new ArrayList<Jugador>();
		String nombreCliente = cliente.getNombre();
		String titulo = "Jugador:" + nombreCliente + " Sala:" + nombreSala;

		String textura = configuracion[2];
		this.modoDeJuego = configuracion[4];
		int tamTablero = modoDeJuego.contains("ElGranDuelo") ? 7 : 5;
		this.dinastia = modoDeJuego.contains("Dinastia");
		partidasRestantes = dinastia ? 3 : 1;
		seleccionarTextura(textura);

		for (int i = 0; i < tiposJugadores.length(); i++) {
			char tipo = tiposJugadores.charAt(i);
			Jugador jugador;
			if (tipo == 'B') {
				jugador = new Bot(nombresJugadores[i], tamTablero, i);
			} else {
				jugador = new Jugador(nombresJugadores[i], tamTablero, i);
			}
			jugadores.add(jugador);
		}
		if (dinastia) {
			puntajesTotales = new ArrayList<Integer>();
			for (int i = 0; i < jugadores.size(); i++) {
				puntajesTotales.add(0);
			}
			titulo += " Dinastia (Ronda 1 de 3).";
		}
		setTitle(titulo);

		idJugador = Integer.parseInt(configuracion[configuracion.length - 1]);
	}

	public static void seleccionarTextura(String textura) {
		texturaCarta = "./assets/mazos/" + textura + ".png";
	}

	public static void cargarTexturas() {
		try {
			GUI.bufferCastilloAmarillo = ImageIO.read(new File(texturaCastilloAmarillo));
			GUI.bufferCastilloAzul = ImageIO.read(new File(texturaCastilloAzul));
			GUI.bufferCastilloRojo = ImageIO.read(new File(texturaCastilloRojo));
			GUI.bufferCastilloVerde = ImageIO.read(new File(texturaCastilloVerde));
			GUI.bufferCarta = ImageIO.read(new File(texturaCarta));
			GUI.bufferVacio = ImageIO.read(new File(texturaVacia));
			GUI.bufferCorona = ImageIO.read(new File(texturaCorona));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error generando imagenes clase VentanaJueguito");
		}
	}

	public void actualizarInterfaz() {
		inicializarTableros(jugadores);
		inicializarPanelInformacion(jugadores);
		if (dinastia) {
			informacion.initBtnPuntajesAcumulados();
		}
		actualizarTableros();
	}

	private void inicializarPanelInformacion(List<Jugador> jugadores) {
		if (informacion != null) {
			this.remove(informacion);
			this.repaint();
		}

		int largoPanelInformacion = (int) LARGO_VENTANA - TAM_TABLEROS;
		int altoPanelInformacion = (int) ALTO_VENTANA / 2;
		informacion = new PanelInformacion(this, jugadores, largoPanelInformacion, altoPanelInformacion);
		informacion.setBounds(TAM_TABLEROS, 0, largoPanelInformacion, altoPanelInformacion);
		informacion.setBorder(BorderFactory.createLineBorder(Color.black));
		getContentPane().add(informacion);
	}

	public void inicializarTableros(List<Jugador> jugadores) {
		if (tableros != null) {
			this.remove(tableros);
			this.repaint();
		}

		tableros = new TablerosJugadores(jugadores);
		tableros.setBounds(0, 0, TAM_TABLEROS, TAM_TABLEROS);
		getContentPane().add(tableros);
	}

	public void actualizarTableros() {
		if (pSeleccion != null) {
			pSeleccion.setCartaElegida(null);
		}
		tableros.actualizarTableros();
		for (int i = 0; i < jugadores.size(); i++) {
			informacion.actualizarPuntaje(i);
		}

	}

	public void actualizarTablero(int indice, int fila, int columna) {
		informacion.actualizarPuntaje(indice);
		pSeleccion.setCartaElegida(null);
		tableros.actualizarTablero(indice, fila, columna);
	}

	public void mostrarCartasAElegir(List<Carta> cartasAElegir) {
		if (pSeleccion != null) {
			this.remove(pSeleccion);

		}

		int altoPanel = (int) (ALTO_VENTANA / 2);
		int largoPanel = (int) (LARGO_VENTANA - TAM_TABLEROS);

		pSeleccion = new PanelTableroSeleccion(cartasAElegir, largoPanel, altoPanel);
		pSeleccion.setBounds(TAM_TABLEROS, altoPanel, largoPanel, altoPanel);
		PanelTableroSeleccion.idCartaElegida = Integer.MIN_VALUE;
		this.getContentPane().add(pSeleccion);

		this.repaint();
	}

	public synchronized int leerCartaElegida() {
		try {
			pSeleccion.getLatchCartaElegida().await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error en leerCartaElegida");
		}
		this.repaint();
		int idCartaElegida = PanelTableroSeleccion.idCartaElegida;
		pSeleccion.setLatchCartaElegida(new CountDownLatch(1));
		Sonido.playCartaSeleccionada();
		return idCartaElegida;

	}

	public synchronized int[] obtenerInputCoordenadas(Carta carta) {

		// Si "carta" fue rotada, y luego se intentÃ³ insertar en un lugar invÃ¡lido,
		// Jugador se encarga
		// de dejar a Carta en su posiciÃ³n "default", este cambio requiere que
		// pSeleccion redibuje a
		// carta, puesto que en caso contrario se sigue viendo a la carta "rotada"
		// cuando en realidad
		// no estÃ¡, lo que genera que al insertar realmente la carta, se inserte en una
		// rotaciÃ³n inesperada.
		// onCartaElegida se encarga ya de redibujar la carta en su rotaciÃ³n.

		pSeleccion.onCartaElegida(carta);
		return tableros.obtenerInputCoordenadas(this);
	}

	public void actualizarTurno(MensajePartidaEnJuego mensajePartidaEnJuego) {
		List<Carta> cartas = mensajePartidaEnJuego.getCartas();
		turnoJugadorActual = mensajePartidaEnJuego.getIdJugador();
		puedeJugar = (turnoJugadorActual == idJugador);
		mostrarMensaje("Turno del jugador\n" + jugadores.get(turnoJugadorActual).getNombre());
		mostrarCartasAElegir(cartas);
		if (puedeJugar) {
			jugadorEligeYPosicionaCarta(cartas);
		}
	}

	private void jugadorEligeYPosicionaCarta(List<Carta> cartas) {
		Jugador jugador = jugadores.get(idJugador);
		int numCartaElegida = jugador.eligeCarta(cartas, this);
		if (!rendido) {
			String[] insercion = jugador.insertaEnTableroOnline(cartas.get(numCartaElegida), this);
			int fila = 0;
			int columna = 0;
			int rotacion = 0;

			boolean pudoInsertar = true;
			if (!insercion[0].equals("N")) {
				columna = Integer.parseInt(insercion[0]);
				fila = Integer.parseInt(insercion[1]);
				rotacion = Integer.parseInt(insercion[2]);
			} else {
				pudoInsertar = false;
			}
			if (pudoInsertar) {
				actualizarTablero(idJugador, fila, columna);
			}
			puedeJugar = false;
			MensajePartidaEnJuego msjPartidaEnJuego = new MensajePartidaEnJuego(idJugador, cartas, numCartaElegida,
					fila, columna, rotacion, pudoInsertar);
			MensajeAServidor msjAServidor = new MensajeAServidor(msjPartidaEnJuego, 12, nombreSala);
			cliente.enviarMensaje(msjAServidor);
		}
	}

	public void procesarTurnoOtroJugador(MensajePartidaEnJuego mensajePartidaEnJuego) {
		int idJugadorTurnoAnterior = mensajePartidaEnJuego.getIdJugador();

		if (idJugadorTurnoAnterior == idJugador) {
			return;
		}

		Jugador jugador = jugadores.get(idJugadorTurnoAnterior);

		if (!mensajePartidaEnJuego.isPudoInsertar()) {

			mostrarVentanaMensaje(jugador.getNombre() + " no pudo insertar la carta");
			jugador.setReinoCompletamenteOcupado(false);
			return;
		}

		List<Carta> cartas = mensajePartidaEnJuego.getCartas();
		int numCartaElegida = mensajePartidaEnJuego.getNumCartaElegida();
		int fila = mensajePartidaEnJuego.getFila();
		int columna = mensajePartidaEnJuego.getColumna();
		int rotacion = mensajePartidaEnJuego.getRotacion();
		Carta cartaElegida = cartas.get(numCartaElegida);

		cartaElegida.setDefault();

		for (int i = 1; i < rotacion; i++) {
			cartaElegida.rotarCarta();
		}

		jugador.getTablero().ponerCarta(cartaElegida, columna, fila, true, this);
		actualizarTablero(idJugadorTurnoAnterior, fila, columna);
	}

	public void rendirse() {
		rendido = true;
		pSeleccion.getLatchCartaElegida().countDown();
		latchPosicionElegida.countDown();
		String nombreCliente = cliente.getNombre();
		MensajeAServidor msjAServidor = new MensajeAServidor(nombreCliente, nombreSala, 15);
		cliente.enviarMensaje(msjAServidor);
		dispose();
	}

	public void recibirRendicionDeOtroJugador(MensajeACliente mensaje) {
		int idJugadorRendido = Integer.parseInt(mensaje.getTexto());

		mostrarVentanaMensaje(jugadores.remove(idJugadorRendido).getNombre() + " ha abandonado la partida :c");

		if (dinastia && !puntajesTotales.isEmpty())
			puntajesTotales.remove(idJugadorRendido);

		if (idJugador >= idJugadorRendido)
			idJugador--;

		if (jugadores.size() == 1) {
			turnoJugadorActual = -1;
			partidasRestantes = 0;
		}

		actualizarInterfaz();

	}

	public void partidaFinalizada() {
		turnoJugadorActual = -1;
		partidasRestantes--;
		List<Integer> puntajesParciales = Partida.calcularPuntajesFinalesOnline(jugadores, this, modoDeJuego);

		if (dinastia) {

			for (int i = 0; i < puntajesParciales.size(); i++) {

				Integer puntajeActual = puntajesParciales.get(i);
				int puntajeAnterior = puntajesTotales.get(i);

				puntajesTotales.set(i, puntajeAnterior + puntajeActual);
			}
		}

		terminarPartida(Partida.determinarGanadoresOnline(jugadores, puntajesParciales, this));
		if (dinastia) {
			if (partidasRestantes > 0) {
				reiniciarGUI();
			} else {
				terminarPartida(Partida.determinarGanadoresOnline(jugadores, puntajesTotales, this));
			}
		}

		if (partidasRestantes <= 0) {
			informacion.setTextoBtnRendirse("Salir");
			pSeleccion.setVisible(false);
		}

	}

	private void reiniciarGUI() {
		List<Jugador> nuevaListaJugadores = new ArrayList<Jugador>(jugadores.size());
		for (int i = 0; i < jugadores.size(); i++) {
			Jugador jugador = jugadores.get(i);
			Jugador nuevoJugador;
			if (jugador instanceof Bot) {
				nuevoJugador = new Bot(jugador.getNombre(), jugador.getTamanioTablero(), i);
			} else {
				nuevoJugador = new Jugador(jugador.getNombre(), jugador.getTamanioTablero(), i);
			}
			nuevaListaJugadores.add(nuevoJugador);
		}

		String nombreCliente = cliente.getNombre();

		jugadores = nuevaListaJugadores;
		setTitle("Jugador:" + nombreCliente + " Sala:" + nombreSala + " Dinastia (Ronda " + (3 - partidasRestantes + 1)
				+ " de 3).");
		actualizarInterfaz();
	}

	public void terminarPartida(Map<Jugador, Integer> map) {
		List<String> emojis = new ArrayList<String>(4);
		emojis.add("☜(ﾟヮﾟ☜)");
		emojis.add("╰(*°▽°*)╯");
		emojis.add("＼(ﾟｰﾟ＼)");
		emojis.add("(�?■_■)");
		Collections.shuffle(emojis);

		String mensajeDinastia = map.size() == 1 ? "Ganador de esta partida:\n" : "Ganadores de esta partida:\n";
		String mensajeNoDinastia = map.size() == 1 ? "Ganador:\n" : "Ganadores:\n";
		String mensaje = dinastia ? mensajeDinastia : mensajeNoDinastia;

		int i = 0;
		for (Map.Entry<Jugador, Integer> entry : map.entrySet()) {
			mensaje += entry.getKey().getNombre() + " con: " + entry.getValue() + " puntos" + emojis.get(i) + "\n";
			i++;
		}
		mostrarMensaje(mensaje);
		JOptionPane.showMessageDialog(this, mensaje, "Fin de partida", JOptionPane.PLAIN_MESSAGE);

		if (dinastia && partidasRestantes <= 1) {
			mensaje = getStringPuntajesAcumulados();
			mostrarMensaje(mensaje);
			JOptionPane.showMessageDialog(this, mensaje, "Fin de esta partida", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void mostrarPuntajesAcumulados() {

		String mensaje = getStringPuntajesAcumulados();
		JOptionPane.showMessageDialog(this, mensaje, "Puntajes acumulados", JOptionPane.PLAIN_MESSAGE);
	}

	private String getStringPuntajesAcumulados() {
		String mensaje = "Puntajes Acumulados:\n";
		for (int i = 0; i < puntajesTotales.size(); i++) {
			mensaje += jugadores.get(i).getNombre() + " con: " + puntajesTotales.get(i) + " puntos \n";
		}
		return mensaje;
	}

	public void salirDeVentana() {
		if (partidasRestantes <= 0) {
			dispose();
		} else {
			int resp = JOptionPane.showConfirmDialog(this, "¿Esta seguro de rendirse?", "Cerrar ventana",
					JOptionPane.YES_NO_OPTION);

			if (resp == JOptionPane.YES_OPTION)
				rendirse();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (sonido != null)
			sonido.stop();
		salaDeEspera.ventanaPartidaCerrada();
	}

	public void mostrarMensaje(String string) {
		informacion.mostrarInfo(string);
	}

	public void pintarFicha(int fila, int columna, int indice, int acumPuntos, int cantCoronas) {
		tableros.pintarFicha(fila, columna, indice, acumPuntos, cantCoronas);
	}

	public void setPSeleccionVisible(boolean b) {
		// Esto puede suceder por condiciones de carrera. Arreglar de manera mas
		// elegante luego
		if (pSeleccion != null)
			pSeleccion.setVisible(b);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Movimiento no permitido", JOptionPane.ERROR_MESSAGE);
	}

	public void mostrarVentanaMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
	}

	protected void actualizarCoordenadas() {
		this.xVentana = getX();
		this.yVentana = getY();
	}

	public void deshabilitarBotonesPuntaje() {
		informacion.deshabilitarBotones();
	}

	public void habilitarBotonesPuntaje() {
		informacion.habilitarBotones();
	}

	public int getXVentana() {
		return xVentana;
	}

	public int getYVentana() {
		return yVentana;
	}

	public static CountDownLatch getStartLatch() {
		return pSeleccion.getLatchCartaElegida();
	}

	public static int getTAM_FICHA() {
		return TAM_FICHA;
	}

	public static boolean isTurnoActual() {
		return puedeJugar;
	}

	public static int getTurnoJugadorActual() {
		return turnoJugadorActual;
	}

	public static CountDownLatch getLatchPosicionElegida() {
		return latchPosicionElegida;
	}

	public static void setLatchPosicionElegida(CountDownLatch latchPosicionElegida) {
		GUI.latchPosicionElegida = latchPosicionElegida;
	}

}
