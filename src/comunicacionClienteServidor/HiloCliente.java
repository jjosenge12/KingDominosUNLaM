package comunicacionClienteServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import parteGraficaClienteServidor.Lobby;

public class HiloCliente extends Thread {
	private ObjectInputStream entrada;
	private static Lobby ventana;
	private Socket socket;
	private int tipoMensaje;

	public HiloCliente(Socket socket, ObjectInputStream entrada, Lobby ventana) {
		this.socket = socket;
		this.entrada = entrada;
		HiloCliente.ventana = ventana;
	}

	public void run() {
		MensajeACliente mensaje;
		try {
			entrada = new ObjectInputStream(socket.getInputStream());
			tipoMensaje = 0;
			while (tipoMensaje != -1 && tipoMensaje != -2) {// Se cierra el hilo con un mensaje del servidor de tipo 1
				mensaje = (MensajeACliente) entrada.readObject();
				tipoMensaje = mensaje.getTipo();
				switch (tipoMensaje) {
				case 0:// 0:el cliente se conecto correctamente
					clienteAceptado();
					break;
				case 1:// 1:actualizar salas
					actualizarSalas(mensaje);
					break;
				case 2:// 2: el cliente se unio a una sala
					unirseASala(mensaje);
					break;
				case 3:// 3: el cliente salio de una sala
					salirDeSala(mensaje);
					break;
				case 4:// 4: el cliente recibio un mensaje en alguna de sus salas abiertas
					recibirMensaje(mensaje);
					break;
				case 5:
					// 5: recibe tiempos de usuarios en la sala
					recibirTiempos(mensaje);
					break;
				case 6:
					// 6: recibe la lista de usuarios en la sala
					recibirListaUsuarios(mensaje);
					break;
				case 9:
					// 9: ya existe la sala privada
					mostrarErrorPorPantalla("Sala privada existente", "Error en creacion de sala privada");
					break;
				case 10:
					// 10: ya existe una sala con ese nombre.
					mostrarErrorPorPantalla("Elija otro nombre de sala", "Error en creacion de sala");
					break;
				case 12:
					// 12: Abre la interfaz grafica
					unirsePartida(mensaje);
					break;
				case 13:
					// 13: procesar un turno jugado
					procesarTurnoJugador(mensaje);
					break;
				case 14:
					// 14: recibe el turno actual
					actualizarTurno(mensaje);
					break;
				case 15:
					// 15: error ya hay 4 usuarios en la sala
					mostrarErrorPorPantalla("Ya hay 4 usuarios en la sala", "Error uniendose a sala");
					break;
				case 16:
					// 16: recepcion lista usuarios para menu de creacion de partida
					recibirListaUsuarios(mensaje);
					break;
				case 17:
					// 17: El servidor le avisa al creador que ingreso o salio un usuario, debe
					// actualizar el menu
					actualizarMenu(mensaje);
					break;
				case 18:
					// 18: termino la partida
					partidaFinalizada(mensaje.getNombreSala());
					break;
				case 19:
					// 19: recibe rendicion de otro jugador
					recibirRendicionDeOtroJugador(mensaje);
					break;
				case 20:
					// 20: no se puede entrar a la sala ya que se esta jugando una partida
					mostrarErrorPorPantalla("No se puede entrar, se esta jugando una partida",
							"Error uniendose a sala");
					break;
				}
				if (tipoMensaje == -2) {
					mostrarErrorPorPantalla("Elija otro nombre de usuario", "Desconexion del servidor");
					socket.close();
				}

			}
		} catch (IOException | ClassNotFoundException e) {
			if (tipoMensaje == -1) {
				System.out.println("Desconexion correcta");
			} else {
				System.out.println("Error en lectura de mensaje en hiloCliente");
				ventana.servidorDejoDeResponder();
				e.printStackTrace();
			}
		}
	}

	private void clienteAceptado() {
		ventana.activarBotones();
	}

	public void cerrar() {
		tipoMensaje = -1;
	}

	private void actualizarSalas(MensajeACliente mensaje) {
		ventana.actualizarSalas(mensaje.getSalas());
	}

	private void unirseASala(MensajeACliente mensaje) {
		ventana.abrirSala(mensaje.getNombreSala());
	}

	private void salirDeSala(MensajeACliente mensaje) {
		ventana.cerrarSala(mensaje.getNombreSala());

	}

	private void recibirMensaje(MensajeACliente mensaje) {
		ventana.recibirMensaje(mensaje);
	}

	private void recibirListaUsuarios(MensajeACliente mensaje) {
		ventana.recibirListaUsuarios(mensaje);
	}

	private void recibirTiempos(MensajeACliente mensaje) {
		ventana.recibirTiempos(mensaje);
	}

	private void mostrarErrorPorPantalla(String descripcion, String titulo) {
		ventana.mostrarErrorPorPantalla(descripcion, titulo);
	}

	private void actualizarMenu(MensajeACliente mensaje) {
		ventana.actualizarMenu(mensaje);
	}

	private void unirsePartida(MensajeACliente mensaje) {
		ventana.unirseAPartida(mensaje);
	}

	private void actualizarTurno(MensajeACliente mensaje) {
		ventana.actualizarTurnoPartida(mensaje);
	}

	private void procesarTurnoJugador(MensajeACliente mensaje) {
		ventana.procesarTurnoPartida(mensaje);
	}

	private void recibirRendicionDeOtroJugador(MensajeACliente mensaje) {
		ventana.recibirRendicionDeOtroJugador(mensaje);
	}

	private void partidaFinalizada(String nombreSala) {
		ventana.partidaFinalizada(nombreSala);
	}

}
