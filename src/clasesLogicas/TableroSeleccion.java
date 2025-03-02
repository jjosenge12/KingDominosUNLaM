package clasesLogicas;

import java.util.List;

public class TableroSeleccion {
	private Ficha[][] tablero;

	public TableroSeleccion() {
		tablero = new Ficha[4][3];
	}

	public TableroSeleccion(List<Carta> cartasAElegir) {

		this();

		for (int i = 0; i < cartasAElegir.size(); i++) {
			Carta carta = cartasAElegir.get(i);
			if (carta != null) {
				ponerCarta(carta, i);
				carta.setDefault();
			}
		}
	}

	public TableroSeleccion(Carta carta) {

		this();
		carta.moverCarta(1, 1);
		ponerCarta(carta, 0);
	}

	public void ponerCarta(Carta carta, int indice) {
		Ficha[] fichas = carta.getFichas();

		int f1f = fichas[0].getFila() + indice;
		int f1c = fichas[0].getColumna();
		int f2f = fichas[1].getFila() + indice;
		int f2c = fichas[1].getColumna();

		tablero[f1f][f1c] = fichas[0];
		tablero[f2f][f2c] = fichas[1];
	}

	public Ficha[][] getTablero() {
		return tablero;
	}

}
