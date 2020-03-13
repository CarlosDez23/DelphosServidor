/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cgonz
 */
public class HiloServidorWeb implements Runnable{

	private Thread hilo;
	private ServerSocket servidor;
	private Socket clienteSocket;
	private final static int PUERTO = 9000;

	public HiloServidorWeb() {
		this.hilo = new Thread(this);
		try {
			this.servidor = new ServerSocket(PUERTO);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void start(){
		this.hilo.start();
	}
	
	@Override
	public void run() {
		try {
			while(!this.servidor.isClosed()){
				System.out.println("Servidor web también funcionando");
				this.clienteSocket = this.servidor.accept();
				new HiloClienteWeb(clienteSocket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
