package comunicacionClienteServidor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sala implements Serializable {

	private static final long serialVersionUID = 1238067019847995537L;
	private String creador;
	private String nombreSala;
	private List<String> usuariosConectados;
	private Map<String, Long> tiempoUsuarios;
	private boolean partidaEnProceso=false;

	public Sala(String nombreSala, String creador) {
		this.creador = creador;
		this.nombreSala = nombreSala;
		this.usuariosConectados = new ArrayList<String>();
		this.tiempoUsuarios = new HashMap<String, Long>();
	}

	public String getNombreSala() {
		return nombreSala;
	}

	public void agregarUsuario(String nombreCliente, long tiempoInicioSesion) {
		usuariosConectados.add(nombreCliente);
		tiempoUsuarios.put(nombreCliente, tiempoInicioSesion);
	}

	public void eliminarUsuario(String nombreUsuario) {
		usuariosConectados.remove(nombreUsuario);
		tiempoUsuarios.remove(nombreUsuario);
	}

	public int getCantUsuarios() {
		return usuariosConectados.size();
	}

	@Override
	public String toString() {
		return "Sala [nombreSala=" + nombreSala + ", usuariosConectados=" + usuariosConectados + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombreSala == null) ? 0 : nombreSala.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sala other = (Sala) obj;
		if (nombreSala == null) {
			if (other.nombreSala != null)
				return false;
		} else if (!nombreSala.equals(other.nombreSala))
			return false;
		return true;
	}

	public List<String> getUsuariosConectados() {
		return usuariosConectados;
	}

	public Map<String, Long> getTiempoUsuarios() {
		return tiempoUsuarios;
	}

	public String getCreador() {
		return creador;
	}
	
	
	public void setPartidaEnProceso(boolean partidaEnProceso) {
		this.partidaEnProceso=partidaEnProceso;
	}
	public boolean getPartidaEnProceso() {
		return partidaEnProceso;
	}
}
