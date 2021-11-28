package comunicacionClienteServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import clasesLogicas.Carta;
import parteGraficaClienteServidor.ServidorGrafico;

public class HiloComunicacionClienteServidor extends Thread {
	private Socket socket;
	private Map<String, Sala> mapaSalas;
	private Map<String, PartidaEnServidor> mapaPartidas;
	private Map<String, Socket> mapaNombreSocket;
	private Map<Socket, ObjectOutputStream> mapaSocketsObjectOuput;
	private ServidorGrafico servidorGrafico;

	public HiloComunicacionClienteServidor(ServidorGrafico servidorGrafico, Socket socket, Map<Socket, ObjectOutputStream> mapa, Map<String, Socket> mapaNombreSocket,
			Map<String, Sala> mapaSalas, Map<String, PartidaEnServidor> mapaPartidas) {
		this.servidorGrafico=servidorGrafico;
		this.socket = socket;
		this.mapaNombreSocket = mapaNombreSocket;
		this.mapaPartidas = mapaPartidas;
		this.mapaSalas = mapaSalas;
		this.mapaSocketsObjectOuput = mapa;
	}

	public void run() {
		ObjectInputStream entrada = null;
		ObjectOutputStream salida = null;
		MensajeAServidor mensajeAServidor = null;
		MensajeACliente mensajeACliente = null;
		String nombreCliente = null;
		// Recibo de datos del cliente
		try {
			entrada = new ObjectInputStream(socket.getInputStream());
			salida = new ObjectOutputStream(socket.getOutputStream());
			mensajeAServidor = (MensajeAServidor) entrada.readObject();
			nombreCliente = mensajeAServidor.getTexto();
			// Si ya existe un usuario con ese nombre, desconecto al cliente
			if (mapaNombreSocket.containsKey(nombreCliente)) {
				mensajeACliente = new MensajeACliente(null, null, -2);
				salida.writeObject(mensajeACliente);
				salida.flush();
				salida.reset();
				entrada.close();
				salida.close();
				socket.close();
				servidorGrafico.mostrarInfo("Cliente desconectado por nombre de usuario existente");
				return;
			}
		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		servidorGrafico.mostrarInfo("Cliente conectado:"+nombreCliente+"\t Direccion:"+socket.getInetAddress());
		mapaNombreSocket.put(nombreCliente, socket);

		try {
			mapaSocketsObjectOuput.put(socket, salida);
			/*
			 * A cada socket le pertenece un ObjectOutputStream ya que si se crean nuevos
			 * ObjectOutputStream, estos insertan un header en el OutputStream y dificulta
			 * la recepcion de mensajes por parte del cliente.
			 */

			// Se le envia un mensaje 0 al cliente para habilitar su interfaz.
			mensajeACliente = new MensajeACliente(null, null, 0);
			salida.writeObject(mensajeACliente);
			salida.flush();
			salida.reset();
			actualizarSalas();
			int tipoMensaje = 1;

			// El cliente envia un 0 para desconectarse
			while (tipoMensaje != 0) {
				mensajeAServidor = (MensajeAServidor) entrada.readObject();
				tipoMensaje = mensajeAServidor.getTipo();

				switch (tipoMensaje) {
				case 2:// El cliente crea una sala por un mensaje tipo 2
					agregarSala(mensajeAServidor);
					break;
				case 3:// El cliente borra una sala por un mensaje tipo 3
					quitarSala(mensajeAServidor);
					break;
				case 4:// El cliente se une a una sala por un mensaje tipo 4
					unirseASala(mensajeAServidor);
					break;
				case 5:// El cliente sale de una sala por un mensaje tipo 5
					salirDeSala(mensajeAServidor);
					break;
				case 6:// El cliente envia un mensaje a una sala por un mensaje tipo 6
					recibirMensajeSala(mensajeAServidor);
					break;
				case 7:// El cliente envia un mensaje a una sala por un mensaje tipo 6
					recibirPedidoTiemposSesion(mensajeAServidor);
					break;
				case 8:// Se envia al cliente la lista de usuarios en la sala
					enviarListaUsuariosSala(mensajeAServidor);
					break;
				case 12:// recibo un paquete con la carta elegida y posicion
					procesarTurnoJugador(mensajeAServidor);
					break;
				case 13:// creacion de partida
					crearPartida(mensajeAServidor);
					break;
				case 14:// Se envia al cliente la lista de usuarios en la sala
					enviarListaUsuariosSala(mensajeAServidor);
					break;
				case 15:// Se recibe la rendicion del usuario
					recibirRendicionDelUsuario(mensajeAServidor);
					break;
				}

				if (tipoMensaje != 0) {
					actualizarSalas();// Se actualizan las salas en cada ciclo.
				}

			}
		} catch (IOException | ClassNotFoundException e) {

			servidorGrafico.mostrarInfo("Error en lectura de mensaje en HiloServidor");

			// Si hubo una falla en la lectura del mensaje, se quita al usuario de todas las
			// salas
			quitarUsuarioDeTodasLasSalas(socket);

		} finally {
			try {
				// Desconexion del cliente del servidor
				if (!socket.isClosed()) {
					entrada.close();
					salida.close();
					socket.close();
				}
				mapaSocketsObjectOuput.remove(socket);
				mapaNombreSocket.remove(mensajeAServidor.getTexto());
				servidorGrafico.mostrarInfo("Cliente desconectado normalmente");
			} catch (IOException e) {
				servidorGrafico.mostrarInfo("Error desconectando cliente");
				e.printStackTrace();
			}
		}

	}

	private void enviarMensajeAUsuario(MensajeACliente msj, String usuario) {
		Socket socket = mapaNombreSocket.get(usuario);
		ObjectOutputStream salida = mapaSocketsObjectOuput.get(socket);
		try {
			if (salida != null) {
				salida.writeObject(msj);
				salida.flush();
				salida.reset();
			}
		} catch (IOException e) {
			servidorGrafico.mostrarInfo("Error en envio de mensaje a usuario:" + usuario);
			e.printStackTrace();
		}
	}

	private void enviarMensajeATodosLosSockets(MensajeACliente msj) {
		ObjectOutputStream salida;

		try {

			for (Map.Entry<Socket, ObjectOutputStream> entry : mapaSocketsObjectOuput.entrySet()) {
				ObjectOutputStream objectOutputStream = entry.getValue();
				salida = objectOutputStream;
				salida.writeObject(msj);
				salida.flush();
				salida.reset();
			}

		} catch (IOException e) {
			servidorGrafico.mostrarInfo("Error envio mensaje actualizando sala");
			e.printStackTrace();
		}

	}

	private void actualizarSalas() {
		List<Sala> listaSalas = new ArrayList<Sala>();

		for (Map.Entry<String, Sala> entry : mapaSalas.entrySet()) {
			Sala sala = entry.getValue();
			listaSalas.add(sala);
		}

		// mensaje tipo 1: actualizacion de salas
		MensajeACliente msj = new MensajeACliente(null, listaSalas, 1);
		enviarMensajeATodosLosSockets(msj);

	}

	private void agregarSala(MensajeAServidor mensaje) {
		if (!mapaSalas.containsKey(mensaje.getNombreSala())) {
			// Indice 0: nombre sala, Indice 1:nombre creador de la sala.
			String[] cSala = mensaje.getNombreSala().split(",");
			Sala sala = new Sala(cSala[0], cSala[1]);
			mapaSalas.put(sala.getNombreSala(), sala);
		} else {
			MensajeACliente msj = new MensajeACliente(null, null, 12);
			enviarMensajeAUsuario(msj, mensaje.getTexto());
		}
	}

	private void quitarSala(MensajeAServidor mensaje) {
		Sala sala = mapaSalas.get(mensaje.getNombreSala());
		mapaSalas.remove(sala.getNombreSala());
	}

	private void unirseASala(MensajeAServidor mensajeServidor) {
		Sala sala = mapaSalas.get(mensajeServidor.getNombreSala());
		MensajeACliente msj;
		boolean partidaEnProceso = sala.getPartidaEnProceso();

		if (sala.getCantUsuarios() < 4 && !partidaEnProceso) {

			long tiempoInicioSesion = System.currentTimeMillis();
			sala.agregarUsuario(mensajeServidor.getTexto(), tiempoInicioSesion);
			// mensaje tipo 2: une al usuario a la sala
			msj = new MensajeACliente(null, 2, sala.getNombreSala());
			enviarMensajeAUsuario(msj, mensajeServidor.getTexto());
			String notificacion = mensajeServidor.getTexto() + " se ha unido a la sala";
			MensajeAServidor msjServidor = new MensajeAServidor(notificacion, sala.getNombreSala(), 0);
			recibirMensajeSala(msjServidor);
			String creador = sala.getCreador();
			if (sala.getCantUsuarios() > 0 && sala.getUsuariosConectados().contains(creador)) {
				// Avisa al creador, si entro un usuario a la sala, esto sirve para actualizar
				// el menu en caso de que ya este abierto cuando entre otro jugador
				msj.setTipo(17);
				enviarMensajeAUsuario(msj, creador);
			}

		} else {

			int tipoMensaje = partidaEnProceso ? 20 : 15;

			msj = new MensajeACliente(null, tipoMensaje, null);
			enviarMensajeAUsuario(msj, mensajeServidor.getTexto());
		}

	}

	private void salirDeSala(MensajeAServidor mensajeServidor) {
		Sala sala = mapaSalas.get(mensajeServidor.getNombreSala());
		sala.eliminarUsuario(mensajeServidor.getTexto());

		// mensaje tipo 3: saca al usuario de la sala
		MensajeACliente msj = new MensajeACliente(null, 3, sala.getNombreSala());
		enviarMensajeAUsuario(msj, mensajeServidor.getTexto());

		String notificacion = mensajeServidor.getTexto() + " se ha desconectado de la sala";
		MensajeAServidor msjServidor = new MensajeAServidor(notificacion, sala.getNombreSala(), 0);
		recibirMensajeSala(msjServidor);

		if (sala.getCantUsuarios() > 0 && !sala.getPartidaEnProceso()) {
			String creador = sala.getCreador();
			msj.setTipo(17);
			enviarMensajeAUsuario(msj, creador);
		}
	}

	private void quitarUsuarioDeTodasLasSalas(Socket socketAEliminar) {
		String nombreAEliminar = "";
		for (Map.Entry<String, Socket> entry : mapaNombreSocket.entrySet()) {

			String nombre = entry.getKey();

			if (entry.getValue() == socketAEliminar) {
				nombreAEliminar = nombre;
			}
		}

		if (!nombreAEliminar.equals("")) {
			for (Map.Entry<String, Sala> entry : mapaSalas.entrySet()) {

				Sala sala = entry.getValue();
				boolean contieneUsuario = sala.getUsuariosConectados().contains(nombreAEliminar);

				if (contieneUsuario) {

					MensajeAServidor notificacionDesconexion = new MensajeAServidor(
							"Servidor: Se ha interrumpido la conexion con el usuario:" + nombreAEliminar,
							sala.getNombreSala(), 0);

					sala.eliminarUsuario(nombreAEliminar);
					recibirMensajeSala(notificacionDesconexion);

					if (sala.getPartidaEnProceso()) {

						PartidaEnServidor partida = mapaPartidas.get(sala.getNombreSala());
						int id = partida.getIDUsuario(nombreAEliminar);

						if (partida.getTurnos().get(0) == id) {
							enviarRendicion(id, partida);
						} else {
							partida.agregarAListaRendidos(id);
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	private void recibirMensajeSala(MensajeAServidor mensajeServidor) {
		// El servidor recibe el mensaje y lo envia a todos los usuarios de la sala
		Sala sala = mapaSalas.get(mensajeServidor.getNombreSala());

		// Se concatena la hora con el mensaje enviado por el cliente.
		Date tiempo = new Date();
		int horas = tiempo.getHours();
		String txtHoras = horas < 10 ? "0" + horas : "" + horas;
		int minutos = tiempo.getMinutes();
		String txtMinutos = minutos < 10 ? "0" + minutos : "" + minutos;
		String hora = "(" + txtHoras + ":" + txtMinutos + ")";
		String mensaje = hora + mensajeServidor.getTexto();

		// mensaje tipo 4: envia mensaje a la sala.
		MensajeACliente msjCliente = new MensajeACliente(mensaje, 4, sala.getNombreSala());
		List<String> usuariosEnSala = sala.getUsuariosConectados();

		for (String usuario : usuariosEnSala) {
			enviarMensajeAUsuario(msjCliente, usuario);
		}
	}

	private void enviarListaUsuariosSala(MensajeAServidor mensaje) {
		String sala = mensaje.getNombreSala();
		Sala salaActual = mapaSalas.get(sala);
		List<String> usuarios = salaActual.getUsuariosConectados();
		String cad = "";
		for (String user : usuarios) {
			cad += user + "\n";
		}
		int tipo = mensaje.getTipo() == 8 ? 7 : 16;
		/*
		 * mensaje tipo 7:envia la lista de usuarios en la sala mensaje tipo 16:envia la
		 * lista de usuarios en la sala para menu iniciar partida
		 */
		MensajeACliente msj = new MensajeACliente(cad, tipo, salaActual.getNombreSala());
		enviarMensajeAUsuario(msj, mensaje.getTexto());
	}

	private void recibirPedidoTiemposSesion(MensajeAServidor mensaje) {
		Sala sala = mapaSalas.get(mensaje.getNombreSala());

		List<String> usuarios = sala.getUsuariosConectados();
		Map<String, Long> tiempos = sala.getTiempoUsuarios();

		String cad = "";
		Long tiempoActual = System.currentTimeMillis();
		for (String usuario : usuarios) {
			Long tiempoInicial = tiempos.get(usuario);
			int horas = (int) ((tiempoActual - tiempoInicial) / 3600000);
			int minutos = (int) (((tiempoActual - tiempoInicial) % 3600000) / 60000);
			int segundos = (int) ((((tiempoActual - tiempoInicial) % 3600000) % 60000) / 1000);
			String txtHoras = horas < 10 ? ("0" + horas) : horas + "";
			String txtMinutos = minutos < 10 ? ("0" + minutos + "") : minutos + "";
			String txtSegundos = segundos < 10 ? ("0" + segundos + "") : segundos + "";
			cad += usuario + "->" + txtHoras + ":" + txtMinutos + ":" + txtSegundos + "\n";
		}

		// mensaje tipo 5:envia los tiempos de conexion
		MensajeACliente msjCliente = new MensajeACliente(cad, 5, sala.getNombreSala());
		enviarMensajeAUsuario(msjCliente, mensaje.getTexto());
	}

	private void crearPartida(MensajeAServidor mensajeServidor) {
		String nombreSala = mensajeServidor.getNombreSala();
		Sala sala = mapaSalas.get(nombreSala);
		String stringConfiguracion = mensajeServidor.getTexto();
		String[] configuracion = stringConfiguracion.split(",");
		String tiposJugadores = configuracion[0];
		String[] nombresJugadores = configuracion[1].split("\\|");
		List<Character> jugadores = new ArrayList<Character>();

		for (int i = 0; i < tiposJugadores.length(); i++) {
			char tipoJugador = tiposJugadores.charAt(i);
			jugadores.add(tipoJugador);
		}
		String nombreMazo = configuracion[3];
		String modoDeJuego = configuracion[4];
		String variante = nombreMazo + "|" + modoDeJuego;

		List<String> listaNombresJugadores = Arrays.asList(nombresJugadores);
		PartidaEnServidor partida = new PartidaEnServidor(nombreSala, jugadores, listaNombresJugadores, variante);
		mapaPartidas.put(nombreSala, partida);

		// Se envia la configuracion de la partida a todos los usuarios para que inicien
		// su interfaz grafica
		List<String> usuarios = sala.getUsuariosConectados();
		MensajeACliente msj = new MensajeACliente("", 12, sala.getNombreSala());

		sala.setPartidaEnProceso(true);
		for (int i = 0; i < usuarios.size(); i++) {
			String usuario = usuarios.get(i);
			msj.setTexto(stringConfiguracion + "," + i);
			enviarMensajeAUsuario(msj, usuario);
		}

		// Se pone en marcha la partida jugandose el primer turno
		jugarTurnoSiguiente(partida);

	}

	private void jugarTurnoSiguiente(PartidaEnServidor partida) {
		Sala sala = mapaSalas.get(partida.getNombreSala());
		List<String> usuariosEnSala = sala.getUsuariosConectados();

		// Si no hay usuarios en la sala finaliza la partida

		if (usuariosEnSala.size() == 0) {

			servidorGrafico.mostrarInfo("Partida finalizada por salida de usuarios");
			sala.setPartidaEnProceso(false);
			return;
		}

		// Si cartas es igual a null, no hay mas cartas en el mazo
		List<Carta> cartas = partida.quitar4CartasDelMazo();
		if (cartas == null) {
			// Se avisa a todos los usuarios que termino la partida
			MensajeACliente msjACliente = new MensajeACliente("", 18, sala.getNombreSala());

			for (String usuario : usuariosEnSala) {
				enviarMensajeAUsuario(msjACliente, usuario);
			}

			// Si se juega el modo dinastia se reinician los atributos de la partida
			int partidasRestantes = partida.getPartidasRestantes();

			if (partidasRestantes > 0) {
				partida.inicializarAtributos();
				cartas = partida.quitar4CartasDelMazo();
			} else {
				sala.setPartidaEnProceso(false);
				servidorGrafico.mostrarInfo("Partida finalizada correctamente");
				return;
			}
		}

		List<Integer> turnos = partida.getTurnos();
		int turnoActual = turnos.get(0);
		List<Character> tipoJugadores = partida.getTipoJugadores();
		Character tipoJugadorActual = tipoJugadores.get(turnoActual);

		// Si es un jugador real y no un bot
		if (tipoJugadorActual == 'J') {

			// Si en la lista de rendidos esta el id actual, entonces se envia la rendicion
			if (partida.isJugadorRendido(turnoActual)) {
				enviarRendicion(turnoActual, partida);
			} else {
				// Se avisa a todos los usuarios que es el turno del jugador X
				MensajePartidaEnJuego msjPartida = new MensajePartidaEnJuego(turnoActual, cartas);
				MensajeACliente msjTurno = new MensajeACliente(14, msjPartida, sala.getNombreSala());

				for (String usuario : usuariosEnSala) {
					enviarMensajeAUsuario(msjTurno, usuario);
				}
			}
		} else {
			// Si es un bot, el bot en PartidaEnServidor realiza la jugada y se procesa el
			// turno para enviarse a todos los usuarios de la sala
			MensajeAServidor msjAServidor = partida.juegaBot(turnoActual, cartas);
			procesarTurnoJugador(msjAServidor);
		}

	}

	private void procesarTurnoJugador(MensajeAServidor mensajeAServidor) {
		// Se recibe la jugada y se envia a todos los usuarios de la sala
		PartidaEnServidor partida = mapaPartidas.get(mensajeAServidor.getNombreSala());
		MensajePartidaEnJuego msjPartidaEnJuego = mensajeAServidor.getMsjPartidaEnJuego();
		int numCartaElegida = msjPartidaEnJuego.getNumCartaElegida();
		int numJugador = msjPartidaEnJuego.getIdJugador();
		Sala sala = mapaSalas.get(mensajeAServidor.getNombreSala());
		List<String> usuariosConectados = sala.getUsuariosConectados();
		MensajeACliente msjACliente = new MensajeACliente(13, msjPartidaEnJuego, sala.getNombreSala());

		for (String usuario : usuariosConectados) {
			enviarMensajeAUsuario(msjACliente, usuario);
		}

		partida.jugadorIElijeCartaJ(numJugador, numCartaElegida);
		jugarTurnoSiguiente(partida);
	}

	private void recibirRendicionDelUsuario(MensajeAServidor msj) {
		String nombreCliente = msj.getTexto();
		// Se saca al cliente de la sala para que no reciba posteriores jugadas.
		MensajeAServidor msjAServidor = new MensajeAServidor(nombreCliente, msj.getNombreSala(), 5);
		salirDeSala(msjAServidor);

		PartidaEnServidor partida = mapaPartidas.get(msj.getNombreSala());
		int idEnPartida = partida.getIDUsuario(nombreCliente);

		/*
		 * Si el jugador se rindio en su turno entonces la rendicion se envia en ese
		 * instante, de caso contrario la rendicion se guarda en una lista y sera
		 * enviada cuando sea el turno correspondiente
		 */
		if (partida.getTurnos().get(0) == idEnPartida) {
			enviarRendicion(idEnPartida, partida);
		} else {
			partida.agregarAListaRendidos(idEnPartida);
		}

	}

	private void enviarRendicion(int idEnPartida, PartidaEnServidor partida) {
		// Actualizo los ids correspondientes en partida y envio la rendicion a todos
		// los usuarios de la sala
		partida.actualizarIds(idEnPartida);
		Sala sala = mapaSalas.get(partida.getNombreSala());
		List<String> usuariosConectados = sala.getUsuariosConectados();
		MensajeACliente msjACliente = new MensajeACliente("" + idEnPartida, 19, sala.getNombreSala());

		for (String usuario : usuariosConectados) {
			enviarMensajeAUsuario(msjACliente, usuario);
		}
		jugarTurnoSiguiente(partida);
	}
}
