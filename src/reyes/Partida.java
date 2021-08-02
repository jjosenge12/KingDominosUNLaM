package reyes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SwingApp.GUI;

public class Partida {

	public static List<Integer> calcularPuntajesFinalesOnline(List<Jugador> jugadores, GUI ventana,
			String modoDeJuego) {
		ventana.deshabilitarBotonesPuntaje();
		List<Integer> puntajesFinales = new ArrayList<Integer>();
		for (int i = 0; i < jugadores.size(); i++) {
			Jugador jugador = jugadores.get(i);
			int puntaje = jugador.getTablero().puntajeTotal(ventana, i);
			boolean reinoMedio = modoDeJuego.contains("ReinoMedio");
			boolean armonia = modoDeJuego.contains("Armonia");
			if (reinoMedio && jugador.getTablero().estaCastilloEnMedio()) {
				ventana.mostrarVentanaMensaje(
						"El jugador: " + jugador.getNombre() + " gana 10 puntos por el bono de reino medio");
				puntaje += 10;
			}
			if (armonia && jugador.tieneReinoCompletamenteOcupado()) {
				ventana.mostrarVentanaMensaje(
						"El jugador: " + jugador.getNombre() + " gana 5 puntos por el bono de armonia");
				puntaje += 5;
			}
			puntajesFinales.add(puntaje);
//			this.puntajes.put(jugador, puntaje);
		}
		ventana.habilitarBotonesPuntaje();
		return puntajesFinales;
	}

	public static Map<Jugador, Integer> determinarGanadoresOnline(List<Jugador> jugadores,
			List<Integer> puntajesFinales, GUI ventana) {
		Map<Jugador, Integer> ganadoresPorPunto = obtenerGanadoresPorPuntosOnline(jugadores, puntajesFinales);
		return ganadoresPorPunto.size() == 1 ? ganadoresPorPunto
				: obtenerGanadoresPorTerrenoOnline(jugadores, ganadoresPorPunto, ventana);
	}

	private static Map<Jugador, Integer> obtenerGanadoresPorPuntosOnline(List<Jugador> jugadores,
			List<Integer> puntajesFinales) {
		int maxPuntaje = 0;
		Map<Jugador, Integer> ganadoresPorPunto = new HashMap<Jugador, Integer>();
		for (int i = 0; i < puntajesFinales.size(); i++) {
			Integer puntaje = puntajesFinales.get(i);
			if (puntaje > maxPuntaje) {
				maxPuntaje = puntaje;
				ganadoresPorPunto.clear();
				ganadoresPorPunto.put(jugadores.get(i), puntaje);
			} else if (puntaje == maxPuntaje) {
				ganadoresPorPunto.put(jugadores.get(i), puntaje);
			}
		}
		return ganadoresPorPunto;
	}

	private static Map<Jugador, Integer> obtenerGanadoresPorTerrenoOnline(List<Jugador> jugadores,
			Map<Jugador, Integer> ganadoresPorPunto, GUI ventana) {
		ventana.mostrarVentanaMensaje("Empate por puntos, se define por cantidad de terreno colocado");
		// Si hay mas de un ganador por puntos, se define por cantidad de terreno
		int maxTerreno = 0;
		Map<Jugador, Integer> ganadoresPorTerreno = new HashMap<Jugador, Integer>();
		for (int i = 0; i < ganadoresPorPunto.size(); i++) {
			int cantTerreno = jugadores.get(i).getCantTerrenoColocado();
			cantTerreno *= 2;// se puede multiplicar o no por 2, ya que cada ficha tiene 2 "terrenos", me
								// parece mas claro asi, es indistinto.
			if (cantTerreno > maxTerreno) {
				maxTerreno = cantTerreno;
				ganadoresPorTerreno.clear();
				ganadoresPorTerreno.put(jugadores.get(i), cantTerreno);
			} else if (cantTerreno == maxTerreno) {
				ganadoresPorTerreno.put(jugadores.get(i), cantTerreno);
			}
		}
		return ganadoresPorTerreno;
	}

}
