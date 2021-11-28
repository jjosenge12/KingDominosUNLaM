package netcode;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import swingMenu.ServidorGrafico;

public class HiloServidor extends Thread {
	private ServerSocket server;
	private ServidorGrafico servidorGrafico;

	// Nombre de usuario --> socket
	private Map<String, Socket> mapaNombreSocket;
	// Socket --> ObjectOutputStream
	private Map<Socket, ObjectOutputStream> mapaSocketsObjectOuput;
	// Nombre de sala --> Sala
	private Map<String, Sala> mapaSalas;
	// Nombre de partida --> partida
	private Map<String, PartidaEnServidor> mapaPartidas;

	public HiloServidor(ServidorGrafico servidorGrafico, int puerto) {
		this.mapaSalas = new HashMap<String, Sala>();
		this.mapaPartidas = new HashMap<String, PartidaEnServidor>();
		this.mapaNombreSocket = new HashMap<String, Socket>();
		this.mapaSocketsObjectOuput = new HashMap<Socket, ObjectOutputStream>();
		this.servidorGrafico = servidorGrafico;

		try {
			server = new ServerSocket(puerto);
			servidorGrafico.mostrarInfo("Servidor ejecutandose...");
		} catch (IOException e) {
			servidorGrafico.puertoOcupado();
		}
	}

	public void run() {
		try {
			while (server!=null) {
				Socket socket = server.accept();
				new HiloComunicacionClienteServidor(servidorGrafico,socket, mapaSocketsObjectOuput, mapaNombreSocket, mapaSalas,
						mapaPartidas).start();
			}

		} catch (IOException e) {
			servidorGrafico.mostrarInfo("Error en conexion con el cliente");
			e.printStackTrace();
		}

	}


}
