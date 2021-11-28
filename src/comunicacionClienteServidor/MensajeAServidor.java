package comunicacionClienteServidor;

import java.io.Serializable;

public class MensajeAServidor implements Serializable {

	private static final long serialVersionUID = -5905903694983224221L;
	private String texto;
	private String nombreSala;
	private int tipo;
	private MensajePartidaEnJuego msjPartidaEnJuego;

	public MensajeAServidor(String texto, String nombreSala, int tipo) {
		this.texto = texto;
		this.nombreSala = nombreSala;
		this.tipo = tipo;
	}

	public MensajeAServidor(MensajePartidaEnJuego msjPartidaEnJuego,int tipo, String nombreSala) {
		this.msjPartidaEnJuego = msjPartidaEnJuego;
		this.nombreSala = nombreSala;
		this.tipo = tipo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public int getTipo() {
		return tipo;
	}

	public String getNombreSala() {
		return nombreSala;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "MensajeAServidor [mensaje=" + texto + ", nombreSala=" + nombreSala + ", tipo=" + tipo + "]";
	}

	public MensajePartidaEnJuego getMsjPartidaEnJuego() {
		return msjPartidaEnJuego;
	}
}
