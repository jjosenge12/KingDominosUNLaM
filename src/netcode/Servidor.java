package netcode;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
	public static final int PUERTO = 50000;
	public static final String IP = "localhost";
	private ServerSocket server;

	// Nombre de usuario --> socket
	private Map<String, Socket> mapaNombreSocket;
	// Socket --> ObjectOutputStream
	private Map<Socket, ObjectOutputStream> mapaSocketsObjectOuput;
	// Nombre de sala --> Sala
	private Map<String, Sala> mapaSalas;
	// Nombre de partida --> partida
	private Map<String, PartidaEnServidor> mapaPartidas;

	public Servidor(int puerto) {
		this.mapaSalas = new HashMap<String, Sala>();
		this.mapaPartidas = new HashMap<String, PartidaEnServidor>();
		this.mapaNombreSocket = new HashMap<String, Socket>();
		this.mapaSocketsObjectOuput = new HashMap<Socket, ObjectOutputStream>();

		try {
			server = new ServerSocket(puerto);
		} catch (IOException e) {
			System.out.println("Error en creacion de puertos");
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (true) {
				Socket socket = server.accept();
				System.out.println("Cliente conectado");
				new HiloServidor(socket, mapaSocketsObjectOuput, mapaNombreSocket, mapaSalas, mapaPartidas).start();
			}

		} catch (IOException e) {
			System.out.println("Error en conexion con el cliente");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("Servidor ejecutandose");
		new Servidor(Servidor.PUERTO).run();
	}

}
