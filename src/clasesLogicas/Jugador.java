package clasesLogicas;

import java.io.IOException;
import java.util.List;

import parteGraficaJuego.GUI;

public class Jugador {

	private String nombre;
	protected Tablero tablero;
	protected boolean reinoCompletamenteOcupado = true;

	public Jugador(String nombre, int tamTablero) {
		this.nombre = nombre;
		this.tablero = new Tablero(tamTablero);
	}

	public Jugador(String nombre, int tamTablero, int idJugador) {
		this.nombre = nombre;
		this.tablero = new Tablero(tamTablero);
		setIdCastilloCentro(idJugador + 1);
	}

	public String getNombre() {
		return nombre;
	}

	public boolean insertaEnTablero(Carta carta, GUI ventana) {
		if (!tablero.esPosibleInsertarEnTodoElTablero(carta)) {
			reinoCompletamenteOcupado = false;
			ventana.mostrarError("La carta no se puede colocar en ninguna posicion, se descarta.");
			return false;
		}
		int[] posicion = new int[2];
		do {
			posicion = ventana.obtenerInputCoordenadas(carta);
		} while (!tablero.ponerCarta(carta, posicion[0], posicion[1], true, ventana));
		return true;
	}

	public String[] insertaEnTableroOnline(Carta carta, GUI ventana) {
		String[] insercion = new String[3];
		if (!tablero.esPosibleInsertarEnTodoElTablero(carta)) {
			reinoCompletamenteOcupado = false;
			ventana.mostrarError("La carta no se puede colocar en ninguna posicion, se descarta.");
			insercion[0] = "N";
			return insercion;
		}
		int[] posicion = new int[2];
		boolean pudoInsertar;
		do {
			posicion = ventana.obtenerInputCoordenadas(carta);
			insercion = tablero.ponerCartaOnline(carta, posicion[0], posicion[1], true, ventana);
			pudoInsertar = !(insercion == null);
		} while (!pudoInsertar);
		return insercion;
	}

	public int eligeCarta(List<Carta> cartasAElegir, GUI entrada, Partida partida) throws IOException {
		int cartaElegida = 0;
		do {
			cartaElegida = entrada.leerCartaElegida();
			for (int i = 0; i < cartasAElegir.size(); i++) {
				Carta c = cartasAElegir.get(i);
				if (c != null) {
					if (c.getId() == cartaElegida)
						cartaElegida = i;
				}

			}
		} while (cartasAElegir.get(cartaElegida) == null);

		return cartaElegida;
	}

	public int eligeCarta(List<Carta> cartasAElegir, GUI entrada) {
		int cartaElegida = 0;
		do {
			cartaElegida = entrada.leerCartaElegida();
			for (int i = 0; i < cartasAElegir.size(); i++) {
				Carta c = cartasAElegir.get(i);
				if (c != null) {
					if (c.getId() == cartaElegida)
						cartaElegida = i;
				}

			}
		} while (cartaElegida != Integer.MIN_VALUE && cartasAElegir.get(cartaElegida) == null);

		return cartaElegida;
	}

	public int getCantTerrenoColocado() {
		return tablero.getCantTerrenoColocado();
	}

	public void setTablero(int tamanioTablero) {
		this.tablero = new Tablero(tamanioTablero);
	}

	public Tablero getTablero() {
		return tablero;
	}

	public boolean tieneReinoCompletamenteOcupado() {
		return reinoCompletamenteOcupado;
	}

	public void setReinoCompletamenteOcupado(boolean reinoCompletamenteOcupado) {
		this.reinoCompletamenteOcupado = reinoCompletamenteOcupado;
	}

	public void setIdCastilloCentro(int i) {
		tablero.setIdCastilloCentro(i);
	}

	public int getTamanioTablero() {
		return tablero.getTamanio();
	}

}
