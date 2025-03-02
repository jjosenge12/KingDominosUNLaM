package parteGraficaJuego;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import clasesLogicas.Jugador;

public class TablerosJugadores extends JPanel {

	private static final long serialVersionUID = -5711758328587102246L;
	private int tamTablero;
	private int tamTableros = GUI.TAM_TABLEROS;
	private List<PanelJugador> tableros;
	private int[][] matrizCoordenadas;// Tiene 2 coordenadas, x del tablero e y del tablero;

	public TablerosJugadores(List<Jugador> jugadores) {
		setLayout(null);
		tableros = new ArrayList<PanelJugador>(jugadores.size());
		tamTablero = tamTableros / 2;
		matrizCoordenadas = new int[jugadores.size()][2];

		posicionarTableros(jugadores);
	}

	private void posicionarTableros(List<Jugador> jugadores) {
		int cantJugadores = jugadores.size();
		int x = 0, y = 0;

		for (int i = 0; i < jugadores.size(); i++) {
			PanelJugador panelJugador = new PanelJugador(jugadores.get(i), tamTablero, i);
			tableros.add(panelJugador);
			switch (i) {
			case 0:
				x = cantJugadores == 1 ? tamTablero / 2 : 0;
				y = cantJugadores != 2 ? 0 : tamTablero / 2;
				break;
			case 1:
				x = tamTablero;
				y = cantJugadores != 2 ? 0 : tamTablero / 2;
				break;
			case 2:
				y = tamTablero;
				x = cantJugadores != 3 ? 0 : tamTablero / 2;
				break;
			case 3:
				x = tamTablero;
				y = tamTablero;
				break;

			default:
				break;
			}

			matrizCoordenadas[i][0] = x;
			matrizCoordenadas[i][1] = y;
			panelJugador.setBounds(x, y, tamTablero, tamTablero);
			this.add(panelJugador);
		}
	}

	public synchronized int[] obtenerInputCoordenadas(GUI ventana) {
		int turno = GUI.getTurnoJugadorActual();
		int[] coordenadasElegidas = new int[2];
		while (GUI.coordenadasElegidas[0] == 0 && GUI.coordenadasElegidas[1] == 0) {
			try {
				GUI.getLatchPosicionElegida().await();
				// Al implementar cliente/servidor, esto es innecesario
				int xMouse = GUI.coordenadasElegidas[2];
				int yMouse = GUI.coordenadasElegidas[3];
				int xVentana = ventana.getXVentana();
				int yVentana = ventana.getYVentana();
				int xTablero = matrizCoordenadas[turno][0] + xVentana;
				int yTablero = matrizCoordenadas[turno][1] + yVentana;
				if (!((xMouse >= xTablero && xMouse <= xTablero + tamTablero)
						&& (yMouse >= yTablero && yMouse <= yTablero + tamTablero))) {
					GUI.coordenadasElegidas[0] = 0;
					GUI.coordenadasElegidas[1] = 0;
					JOptionPane.showMessageDialog(this, "Tablero incorrecto", "Movimiento no permitido",
							JOptionPane.ERROR_MESSAGE);
					GUI.setLatchPosicionElegida(new CountDownLatch(1));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error obteniendo coordenadas, clase TablerosJugador");
			}
		}
		coordenadasElegidas[0] = GUI.coordenadasElegidas[0];
		coordenadasElegidas[1] = GUI.coordenadasElegidas[1];
		GUI.setLatchPosicionElegida(new CountDownLatch(1));
		GUI.coordenadasElegidas[0] = 0;
		GUI.coordenadasElegidas[1] = 0;
		Sonido.playPonerCarta();
		return coordenadasElegidas;
	}

	public void actualizarTableros() {

		long tiempoInicial = System.currentTimeMillis();
		for (PanelJugador tableroIndividual : tableros) {
			tableroIndividual.actualizarTablero();
		}
		System.out.println("Render actualizarTableros: " + (System.currentTimeMillis() - tiempoInicial));
	}

	public void actualizarTablero(int indice, int fila, int columna) {
		tableros.get(indice).actualizarTablero(fila, columna);
	}

	public void pintarFicha(int fila, int columna, int indice, int acumPuntos, int cantCoronas) {
		tableros.get(indice).pintarFicha(fila, columna, acumPuntos, cantCoronas, indice);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color cAnterior = g.getColor();
		g.setColor(new Color(0xAB7632));
		g.fillRect(0, 0, tamTableros, tamTableros);
		g.setColor(cAnterior);
	}
}
