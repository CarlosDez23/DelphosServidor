/*
 * Servidor de la aplicación delphos
 */
package main;

import hilos.HiloServidor;

/**
 *
 * @author Carlos González
 */
public class DelphosServidor {

	public static void main(String[] args) {
		HiloServidor servidor = new HiloServidor();
		servidor.start();
	}	
}
