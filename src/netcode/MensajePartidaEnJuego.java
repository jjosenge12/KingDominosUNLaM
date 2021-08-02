package netcode;

import java.io.Serializable;
import java.util.List;

import reyes.Carta;

public class MensajePartidaEnJuego implements Serializable {
	private static final long serialVersionUID = -6979577079114699806L;

	private int idJugador;
	private List<Carta> cartas;

	private int numCartaElegida;
	private int fila;
	private int columna;
	private int rotacion;
	private boolean pudoInsertar;
	private String nombreCliente;

	public MensajePartidaEnJuego(int idJugador, List<Carta> cartas) {
		this.idJugador = idJugador;
		this.cartas = cartas;
	}

	public MensajePartidaEnJuego(int idJugador, List<Carta> cartas, int numCartaElegida, int fila, int columna,
			int rotacion, boolean pudoInsertar) {
		this.idJugador = idJugador;
		this.cartas = cartas;
		this.numCartaElegida = numCartaElegida;
		this.fila = fila;
		this.columna = columna;
		this.rotacion = rotacion;
		this.pudoInsertar = pudoInsertar;
	}

	public MensajePartidaEnJuego(int idJugador, String nombreCliente) {
		this.idJugador = idJugador;
		this.nombreCliente = nombreCliente;
	}

	public int getIdJugador() {
		return idJugador;
	}

	public List<Carta> getCartas() {
		return cartas;
	}

	public int getNumCartaElegida() {
		return numCartaElegida;
	}

	public int getFila() {
		return fila;
	}

	public int getColumna() {
		return columna;
	}

	public int getRotacion() {
		return rotacion;
	}

	public boolean isPudoInsertar() {
		return pudoInsertar;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

}
