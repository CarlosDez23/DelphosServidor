/*
 * Este hilo es el que se encargará de hacer los accepts
 */
package hilos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Carlos González
 */
public class HiloServidor implements Runnable {

	private ServerSocket servidor;
	private Socket cliente;
	private final static int PUERTO = 49153;
	private Thread hilo;

	public HiloServidor() {
		this.hilo = new Thread(this);
	}

	public void start() {
		this.hilo.start();
	}

	@Override
	public void run() {
		try {
			this.servidor = new ServerSocket(PUERTO);
			while (!this.servidor.isClosed()) {
				System.out.println("Servidor funcionando");
				this.cliente = this.servidor.accept();
				HiloCliente hiloCliente = new HiloCliente(this.cliente);
				hiloCliente.start();
			}
		} catch (IOException e) {
			System.out.println("Ha ocurrido un problema con el servidor " + e.getMessage());
		}
	}
}
