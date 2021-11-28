package comunicacionClienteServidor;

import java.io.Serializable;
import java.util.List;

public class MensajeACliente implements Serializable{

	private static final long serialVersionUID = -3270450619107272291L;
	private String texto;
	private List<Sala> listaSalas;
	private String nombreSala;
	private int tipo;
	private MensajePartidaEnJuego msjPartida;
	
	public MensajeACliente(String texto, List<Sala> salas, int tipo) {
		this.texto = texto;
		this.listaSalas = salas;
		this.tipo = tipo;
	}
	public MensajeACliente(String texto, int tipo,String nombreSala) {
		this.texto = texto;
		this.nombreSala = nombreSala;
		this.tipo = tipo;
	}
	
	public MensajeACliente(int tipo,MensajePartidaEnJuego msjPartida,String nombreSala) {
		this.msjPartida = msjPartida;
		this.nombreSala = nombreSala;
		this.tipo = tipo;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public String getNombreSala() {
		return nombreSala;
	}
	
	public List<Sala> getSalas() {
		return listaSalas;
	}
	
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}

	public MensajePartidaEnJuego getMsjPartida() {
		return msjPartida;
	}
	

}
