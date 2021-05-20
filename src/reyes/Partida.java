package reyes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Partida {
	private List<Carta> mazo;
	private Jugador[] jugadores;
	private static final int DEFAULT_TAM_TABLERO = 5;
	private static final int DEFAULT_CANT_CARTAS = 48;
	private static final int DEFAULT_CANT_JUGADORES = 2;
	private int tamanioTablero;
	private int cantidadCartas;
	private int cantidadJugadores;

	public Partida() {
		this.tamanioTablero = DEFAULT_TAM_TABLERO;
		this.cantidadCartas = DEFAULT_CANT_CARTAS;
		this.cantidadJugadores = DEFAULT_CANT_JUGADORES;
	}
	
	public Partida(int cantidadJugadores, int tamanioTablero, int cantidadCartas) {
		this.cantidadCartas = cantidadCartas;
		this.tamanioTablero = tamanioTablero;
		this.cantidadJugadores = cantidadJugadores;
	}

	public List<Carta> getMazo() {
		return mazo;
	}

	public List<Carta> mezclarMazo(List<Carta> cartas) {
		List<Carta> cartasMezcladas = new ArrayList<Carta>(cantidadCartas);

		for (int i = 0; i < cantidadCartas; i++) {
			int numeroAleatorio = numeroAleatorioEntre(0, cantidadCartas - i - 1);
			cartasMezcladas.add(cartas.remove(numeroAleatorio));
		}

		return cartas = cartasMezcladas;
	}

	private int numeroAleatorioEntre(int mayorIgualQue, int menorIgualQue) {
		int numeroAleatorio = (int) Math.floor(Math.random() * (menorIgualQue - mayorIgualQue + 1) + mayorIgualQue);
		return numeroAleatorio;
	}

	public List<Carta> armarMazo() {
		List<Carta> cartas = new ArrayList<Carta>(cantidadCartas);
		File file = new File("./assets/cartas.txt");
		Scanner scanner;

		try {
			int idCarta = 1;
			scanner = new Scanner(file);
			while (idCarta <= cantidadCartas) {
				
				if (scanner.hasNextLine()) {
					String tipoIzq = scanner.nextLine();
					int cantCoronasI = Integer.parseInt(scanner.nextLine());
					String tipoDer = scanner.nextLine();
					int cantCoronasD = Integer.parseInt(scanner.nextLine());
					cartas.add(new Carta(idCarta, tipoIzq, cantCoronasI, tipoDer, cantCoronasD));
					System.out.println(idCarta);
					idCarta++;
				} else {
					scanner.close(); //volvemos al inicio del archivo y seguimos cargando
					scanner = new Scanner(file);
				}
			}
			//si la cantidad de cartas no fuera multiplo de 48, podria suceder que scanner siguiera abierto
			scanner.close(); 
		} catch (FileNotFoundException e) {
			System.out.println("No se encontro el archivo de cartas");
			return null;
		}

		return cartas;
	}

	public boolean iniciarPartida() {
		if(cantidadJugadores > 4 || cantidadJugadores < 2) {
			System.out.println("La cantidad de jugadores es invalida!!");
			return false;
		}
		//El c�digo puede funcionar sin problemas con cualquier cantidad de cartas 
		//mientras el total sea m�ltiplo de 4, pues siempre se roba de a 4 cartas.
		//Sin embargo, el enunciado tiene la limitaci�n de 48 para todos los modos.
		//Se puede quitar esta validaci�n en el futuro si quisieramos agregar otros modos.
		if(cantidadCartas != 48) {
			System.out.println("La cantidad de cartas tiene que ser 48! (limitaci�n por parte del enunciado)");
		}
		List<Integer> turnos = determinarTurnosIniciales();
		List<Carta> cartasAElegirSig = new ArrayList<Carta>();
		this.jugadores = new Jugador[cantidadJugadores];

		// Creamos a cada jugador
		for (int i = 0; i < jugadores.length; i++) {
			jugadores[i] = new Jugador("Jugador " + (i + 1), tamanioTablero);
		}
		//armamos y mezclamos el mazo
		mazo = new ArrayList<Carta>(armarMazo());
		mazo = mezclarMazo(mazo);
		
		int rondas = 0;
		while (mazo.size() > 1) {
			System.out.println("--------Ronda: " + ++rondas + "--------");
			cartasAElegirSig.clear();
			quitarNCartasDelMazo(mazo, this.cantidadJugadores, cartasAElegirSig);
			cartasAElegirSig.sort(Carta::compareTo);
			elegirCartas(cartasAElegirSig, turnos);
		}
		int i = 0;
		for (Jugador jugador : jugadores) {
			System.out.println("-------Tablero de Jugador " + ++i + "-------");
			System.out.println(jugador.tablero);
			jugador.tablero.puntajeTotal();
		}
		return (cantidadCartas/cantidadJugadores == rondas);
	}

	private void elegirCartas(List<Carta> cartasAElegir, List<Integer> turnos) {
		int numeroElegido;
		List<Integer> numerosElegidos = new LinkedList<Integer>();
		Map<Integer, Integer> nuevoOrdenDeTurnos = new TreeMap<Integer, Integer>();
		for (int i = 0; i < turnos.size(); i++) {
			do {
				/// ESTO DEBE CAMBIARSE CUANDO EL USUARIO PUEDA JUGAR YA QUE EL DECIDIRA QUE
				/// CARTA QUIERE
				numeroElegido = numeroAleatorioEntre(0, cartasAElegir.size() - 1);
			} while (numerosElegidos.contains(numeroElegido));
			numerosElegidos.add(numeroElegido);
			Carta cartaElegida = cartasAElegir.get(numeroElegido);
			int turno = turnos.get(i);

			//TODO falta la logica de cuando el jugador elige la carta pueda ponerla en su tablero
			jugadores[turno].eligeCarta(cartaElegida, numeroElegido);
			jugadores[turno].insertaEnTablero(cartaElegida);
			// Los numeros elegidos se guardan en un map, ya que el menor de estos decide
			// quien comienza el turno siguiente
			nuevoOrdenDeTurnos.put(numeroElegido, turno);
		}
		turnos.clear();
		for (Map.Entry<Integer, Integer> entrada : nuevoOrdenDeTurnos.entrySet()) {
			// modifico turnos de acuerdo al numero elegido
			turnos.add(entrada.getValue());
		}
	}

	public void mostrarCartas(Set<Carta> cartas) {
		for (Carta carta : cartas) {
			System.out.println(carta);
		}
	}

	public void quitarNCartasDelMazo(List<Carta> mazo, int n, List<Carta> cartas) {
		for (int i = 0; i < n; i++) {
			cartas.add(mazo.remove(0));
		}

	}

	private List<Integer> determinarTurnosIniciales() {
		List<Integer> turnos = new ArrayList<Integer>();
		List<Integer> idJugadores = new ArrayList<Integer>(4);

		for (int i = 0; i < cantidadJugadores; i++) {
			idJugadores.add(i);
		}
		for (int i = 0; i < cantidadJugadores; i++) {
			int numeroAleatorio = numeroAleatorioEntre(0, cantidadJugadores - i - 1);
			turnos.add(idJugadores.remove(numeroAleatorio));
		}

		return turnos;
	}
}
