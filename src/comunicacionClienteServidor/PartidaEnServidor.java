package comunicacionClienteServidor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import clasesLogicas.Bot;
import clasesLogicas.Carta;
import clasesLogicas.Mazo;

public class PartidaEnServidor {
	private String nombreSala;
	private List<Character> tipoJugadores;
	private List<Integer> turnos;
	private Map<Integer, Integer> nuevoOrdenDeTurnos;
	private List<Carta> cartas;
	private Map<Bot, Integer> mapaBots;
	private List<Integer> listaRendidos = new ArrayList<Integer>();
	private Mazo mazo;
	private int cantJugadores;
	private int partidasRestantes;
	// En el indice 0 se guarda el nombre del mazo y en el indice 1 se guarda el
	// modo de juego
	private String[] nombreMazoYModoDeJuego;
	private int tamTablero;
	private Map<Integer, String> mapaIDNombres;

	public PartidaEnServidor(String nombreSala, List<Character> tipoJugadores, List<String> nombresJugadores,
			String variante) {
		this.nombreSala = nombreSala;
		this.tipoJugadores = tipoJugadores;
		this.cantJugadores = tipoJugadores.size();
		this.nombreMazoYModoDeJuego = variante.split("\\|");
		this.tamTablero = nombreMazoYModoDeJuego[1].contains("ElGranDuelo") ? 7 : 5;

		// Si el modo dinastia esta activado, se jugaran 3 partidas y al final se
		// sumaran todos los puntajes
		partidasRestantes = nombreMazoYModoDeJuego[1].contains("Dinastia") ? 3 : 1;
		mapaBots = new HashMap<Bot, Integer>();
		mapaIDNombres = new HashMap<Integer, String>();

		for (int i = 0; i < nombresJugadores.size(); i++) {
			mapaIDNombres.put(i, nombresJugadores.get(i));
		}

		inicializarAtributos();
	}

	public void inicializarAtributos() {
		turnos = new ArrayList<Integer>(cantJugadores);

		for (int i = 0; i < cantJugadores; i++) {

			if (tipoJugadores.get(i) == 'B') {
				Bot bot = new Bot("Bot " + i, tamTablero);
				mapaBots.put(bot, i);
			}

			turnos.add(i);
		}

		Collections.shuffle(turnos);
		nuevoOrdenDeTurnos = new TreeMap<Integer, Integer>();
		mazo = new Mazo(48, nombreMazoYModoDeJuego[0]);
		mazo.mezclarMazo();
		cartas = new ArrayList<Carta>();
	}

	public List<Carta> quitar4CartasDelMazo() {
		/*
		 * Se quitan 4 cartas del mazo cuando se hayan elegido n cartas, n=cantidad de
		 * jugadores
		 */
		int elegidas = 0;
		for (Carta carta : cartas) {
			if (carta == null) {
				elegidas++;
			}
		}

		if (elegidas >= cantJugadores || cartas.isEmpty()) {
			/*
			 * Si ya no hay cartas en el mazo, se devuelve null, para que el servidor
			 * informe que termino la partida
			 */
			if (mazo.getTam() < 4) {
				partidasRestantes = getPartidasRestantes() - 1;
				return null;
			}
			mazo.quitarPrimerasNCartas(4, cartas);
		}

		return partidasRestantes <= 0 ? null : cartas;
	}

	public List<Integer> getTurnos() {
		/*
		 * Cada vez que se devuelve un turno, este es removido de la lista de turnos,
		 * cuando la lista este vacia, se colocaran los nuevos turnos dados por la carta
		 * elegida anterior
		 */
		if (turnos.isEmpty()) {
			for (Map.Entry<Integer, Integer> entry : nuevoOrdenDeTurnos.entrySet()) {
				turnos.add(entry.getValue());
			}
			nuevoOrdenDeTurnos.clear();
		}
		return turnos;
	}

	public void jugadorIElijeCartaJ(int jugador, int carta) {
		/* Se coloca el nuevo turno dado por la carta elegida */
		nuevoOrdenDeTurnos.put(carta, jugador);
		turnos.remove(0);
		cartas.set(carta, null);
	}

	public MensajeAServidor juegaBot(int turnoActual, List<Carta> cartas) {
		/* PartidaEnServidor es la que maneja las jugadas de los bots */
		Bot bot = null;
		for (Map.Entry<Bot, Integer> entry : mapaBots.entrySet()) {
			if (entry.getValue() == turnoActual)
				bot = entry.getKey();
		}
		int numCartaElegida = bot.eligeCarta(cartas, null);
		Carta cartaElegida = cartas.get(numCartaElegida);
		bot.insertaEnTablero(cartaElegida, null);

		int fila = bot.getFPuntajeMax();
		int columna = bot.getCPuntajeMax();
		int rotacion = bot.getRotacionPuntajeMax() + 1;
		boolean pudoInsertar = bot.getPudoInsertar();

		MensajePartidaEnJuego msjPartidaEnJuego = new MensajePartidaEnJuego(turnoActual, cartas, numCartaElegida, fila,
				columna, rotacion, pudoInsertar);
		return new MensajeAServidor(msjPartidaEnJuego, 12, nombreSala);
	}

	public void actualizarIds(int idRendido) {

		/*
		 * Cuando se rinde el usuario id=X los turnos que tengan id mayor a X deberan
		 * decrementarse, tambien deberan decrementarse los ids de los bots
		 */
		mapaIDNombres.remove(idRendido);
		tipoJugadores.remove(idRendido);
		turnos.remove(0);
		cantJugadores--;

		mapaBots.replaceAll((bot, id) -> (id >= idRendido) ? id - 1 : id);

		turnos.replaceAll((turno) -> (turno >= idRendido) ? --turno : turno);

		nuevoOrdenDeTurnos.replaceAll((numCarta, turno) -> (turno >= idRendido) ? --turno : turno);

		// Si queda un solo jugador, termina la partida
		if ((turnos.size() + nuevoOrdenDeTurnos.size()) == 1)
			partidasRestantes = 0;
	}

	public void agregarAListaRendidos(int idRendido) {
		listaRendidos.add(idRendido);
	}

	public boolean isJugadorRendido(int idJugador) {
		/*
		 * Devuelve true si el id del jugador se encuentra dentro de la lista de
		 * rendidos
		 */
		int i = 0;
		boolean rendido = false;

		while (i < listaRendidos.size() && listaRendidos.get(i) != idJugador) {
			i++;
		}

		if (i < listaRendidos.size()) {
			rendido = true;
			listaRendidos.remove(i);
		}

		if (rendido) {
			for (i = 0; i < listaRendidos.size(); i++) {
				int idRendido = listaRendidos.get(i);
				if (idRendido >= idJugador) {
					listaRendidos.set(i, --idRendido);
				}
			}
		}
		return rendido;
	}

	public Integer getIDUsuario(String nombreCliente) {
		for (Map.Entry<Integer, String> entry : mapaIDNombres.entrySet()) {
			if (entry.getValue().equals(nombreCliente))
				return entry.getKey();
		}
		return null;
	}

	public int getPartidasRestantes() {
		return partidasRestantes;
	}

	public List<Character> getTipoJugadores() {
		return tipoJugadores;
	}

	public String getNombreSala() {
		return nombreSala;
	}

}
