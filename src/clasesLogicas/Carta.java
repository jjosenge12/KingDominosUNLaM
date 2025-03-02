package clasesLogicas;

import java.io.Serializable;

public class Carta implements Comparable<Carta>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6805522631985847792L;
	private int id;
	private Ficha[] fichas = new Ficha[2];
	private int rotacion = 1;

	public Carta(int idCarta, String tipoIzq, int cantCoronasI, String tipoDer, int cantCoronasD) {
		this.id = idCarta;
		fichas[0] = new Ficha(tipoIzq, cantCoronasI, 0, 0,this.id*2, this);
		fichas[1] = new Ficha(tipoDer, cantCoronasD, 0, 1,this.id*2+1, this);
	}
	/*
	 * Este constructor es para cartas con fichas personalizadas
	 */
	public Carta(int idCarta, Ficha fichaIzq, Ficha fichaDer) {
		this.id = idCarta;
		fichaIzq.setCarta(this);
		fichaDer.setCarta(this);
		fichas[0] = fichaIzq;
		fichas[1] = fichaDer;
	}
	//Constructor de copia
	public Carta(Carta carta) {
		Ficha[] cartaFichas = carta.getFichas();
		this.id = carta.getId();
		fichas[0] = new Ficha(cartaFichas[0].getTipo(), cartaFichas[0].getCantCoronas(), 0, 0,this.id*2, this);
		fichas[1] = new Ficha(cartaFichas[1].getTipo(), cartaFichas[1].getCantCoronas(), 0, 1,this.id*2+1, this);
	}
	public void setDefault() {
		this.rotacion = 1;
		this.fichas[0].setFila(0);
		this.fichas[0].setColumna(0);
		this.fichas[1].setFila(0);
		this.fichas[1].setColumna(1);
		this.fichas[0].setRotacion(rotacion);
		this.fichas[1].setRotacion(rotacion);
	}
	public int getId() {
		return id;
	}
	public Ficha[] getFichas() {
		return fichas;
	}

	public void rotarCarta() {
		int x1 = fichas[0].getFila();
		int y1 = fichas[0].getColumna();

		switch (rotacion) {
		case 1:
			fichas[1].setFila(x1 + 1);
			fichas[1].setColumna(y1);
			break;
		case 2:
			fichas[1].setFila(x1);
			fichas[1].setColumna(y1 - 1);
			break;
		case 3:
			fichas[1].setFila(x1 - 1);
			fichas[1].setColumna(y1);
			break;
		case 4:
			rotacion = 0;
			fichas[1].setFila(x1);
			fichas[1].setColumna(y1 + 1);
			break;
		}
		rotacion++;
		fichas[0].setRotacion(rotacion);
		fichas[1].setRotacion(rotacion);
	}
	public int getRotacion() {
		return rotacion;
	}
	@Override
	public String toString() {
		String ret = "";
		String[][] mat = { { "", "", "" }, { "", "", "" }, { "", "", "" } };
		Ficha ficha1 = fichas[0];
		Ficha ficha2 = fichas[1];
		mat[1][1] = ficha1.toString();
		switch (rotacion) {
		case 1:
			mat[1][2] = ficha2.toString();
			break;
		case 2:
			mat[2][1] = ficha2.toString();
			break;
		case 3:
			mat[1][0] = ficha2.toString();
			break;
		case 4:
			mat[0][1] = ficha2.toString();
			break;
		}
		for (String[] strings : mat) {
			for (String strings2 : strings) {
				ret += String.format("%9s" + "|", strings2);
			}
			ret += "\n";
		}
		return ret;
	}

	@Override
	public int compareTo(Carta otraCarta) {
		return this.id - otraCarta.id;
	}

	public void moverCarta(int fila, int columna) {
		this.fichas[0].setFila(this.fichas[0].getFila()+fila);
		this.fichas[0].setColumna(this.fichas[0].getColumna()+columna);
		this.fichas[1].setFila(this.fichas[1].getFila()+fila);
		this.fichas[1].setColumna(this.fichas[1].getColumna()+columna);
	}

}
