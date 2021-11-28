package reyes;

import java.util.List;

import swingApp.GUI;

public class Bot extends Jugador {

	private int cPuntajeMax;
	private int fPuntajeMax;
	private int rotacionPuntajeMax;
	private boolean pudoInsertar;

	public Bot(String nombre, int tamTablero) {
		super(nombre, tamTablero);
		this.tablero = new TableroBot(tamTablero);
	}
	public Bot(String nombre, int tamTablero,int idJugador) {
		super(nombre, tamTablero);
		this.tablero = new TableroBot(tamTablero);
		setIdCastilloCentro(idJugador+1);
	}

	@Override
	public void setTablero(int tamanioTablero) {
		this.tablero = new TableroBot(tamanioTablero);
	}
	@Override
	public int eligeCarta(List<Carta> cartasAElegir, GUI ventana,Partida partida) {
		int numeroElegido = -1;
		int[] puntajeMaxYCoordenadas = new int[4];

		puntajeMaxYCoordenadas[0] = -2;// puntaje max
		puntajeMaxYCoordenadas[1] = 0;// x del puntaje max
		puntajeMaxYCoordenadas[2] = 0;// y del puntaje max
		puntajeMaxYCoordenadas[3] = 0;// rotacion del puntaje max

		for (int i = 0; i < cartasAElegir.size(); i++) {
			Carta carta = cartasAElegir.get(i);
			if (carta != null) {
				// si no la eligieron ya...
				int[] puntajeMaxYCoordenadasActual = maximoPuntajePosible(carta);
				int puntajeActual = puntajeMaxYCoordenadasActual[0];
				if (puntajeActual > puntajeMaxYCoordenadas[0]) {
					puntajeMaxYCoordenadas = puntajeMaxYCoordenadasActual;
					numeroElegido = i;
				}
				carta.setDefault();
			}
		}
		cPuntajeMax = puntajeMaxYCoordenadas[1];
		fPuntajeMax = puntajeMaxYCoordenadas[2];
		rotacionPuntajeMax = puntajeMaxYCoordenadas[3];
		return numeroElegido;
	}
	@Override
	public int eligeCarta(List<Carta> cartasAElegir, GUI ventana) {
		int numeroElegido = -1;
		int[] puntajeMaxYCoordenadas = new int[4];
		
		puntajeMaxYCoordenadas[0] = -2;// puntaje max
		puntajeMaxYCoordenadas[1] = 0;// x del puntaje max
		puntajeMaxYCoordenadas[2] = 0;// y del puntaje max
		puntajeMaxYCoordenadas[3] = 0;// rotacion del puntaje max
		
		for (int i = 0; i < cartasAElegir.size(); i++) {
			Carta carta = cartasAElegir.get(i);
			if (carta != null) {
				// si no la eligieron ya...
				int[] puntajeMaxYCoordenadasActual = maximoPuntajePosible(carta);
				int puntajeActual = puntajeMaxYCoordenadasActual[0];
				if (puntajeActual > puntajeMaxYCoordenadas[0]) {
					puntajeMaxYCoordenadas = puntajeMaxYCoordenadasActual;
					numeroElegido = i;
				}
				carta.setDefault();
			}
		}
		cPuntajeMax = puntajeMaxYCoordenadas[1];
		fPuntajeMax = puntajeMaxYCoordenadas[2];
		rotacionPuntajeMax = puntajeMaxYCoordenadas[3];
		return numeroElegido;
	}

	private int[] maximoPuntajePosible(Carta carta) {
		int fMin = tablero.getFMin();
		int fMax = tablero.getFMax();
		int cMin = tablero.getCMin();
		int cMax = tablero.getCMax();
		int puntajeMax = -1;
		int cPuntajeMax = 0;
		int fPuntajeMax = 0;
		int rotacion = 0;
		int[] puntajeYCoordenadas = new int[4];

		for (int f = fMin - 2; f <= fMax + 2; f++) {
			for (int c = cMin - 2; c <= cMax + 2; c++) {
				for (int i = 0; i < 4; i++) {
					if (tablero.ponerCarta(carta, c, f, false,null) == true) {
						int puntaje = tablero.puntajeTotal(false);
						if (puntaje > puntajeMax) {
							puntajeMax = puntaje;
							cPuntajeMax = c;
							fPuntajeMax = f;
							rotacion = i;
						}
						tablero.quitarCarta(carta);
						carta.setDefault();
					}
					for (int j = 0; j < i + 1; j++) {
						carta.rotarCarta();
					}
				}
			}
		}

		puntajeYCoordenadas[0] = puntajeMax;
		puntajeYCoordenadas[1] = cPuntajeMax;
		puntajeYCoordenadas[2] = fPuntajeMax;
		puntajeYCoordenadas[3] = rotacion;
		return puntajeYCoordenadas;
	}

	@Override
	public boolean insertaEnTablero(Carta cartaElegida, GUI ventana) {
		for (int i = 0; i < rotacionPuntajeMax; i++) {
			cartaElegida.rotarCarta();
		}
		pudoInsertar=tablero.ponerCarta(cartaElegida, cPuntajeMax, fPuntajeMax, false,null);
		if(!pudoInsertar) {
			reinoCompletamenteOcupado=false;
		}
		return pudoInsertar;
	}
	public int getCPuntajeMax() {
		return cPuntajeMax;
	}
	public int getFPuntajeMax() {
		return fPuntajeMax;
	}
	public int getRotacionPuntajeMax() {
		return rotacionPuntajeMax;
	}
	public boolean getPudoInsertar() {
		return pudoInsertar;
	}
	
	

}
