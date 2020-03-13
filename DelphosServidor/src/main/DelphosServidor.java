/*
 * Servidor de la aplicación delphos
 */
package main;

import hilos.HiloServidor;
import java.security.KeyPair;
import seguridad.Seguridad;
import java.io.*;

/**
 *
 * @author Carlos González
 */
public class DelphosServidor {

	private static final String FICH_PRIVADA = "privada.dat";
	private static final String FICH_PUBLICA = "publica.dat";

	public static void main(String[] args) {
		gestionClaves();
		HiloServidor servidor = new HiloServidor();
		servidor.start();
	}

	private static void gestionClaves() {
		/**
		 * Primero vamos a generar nuestro par de claves para firmar, y lo guardamos en un fichero para enviarlo a los clientes. Si ya existe el fichero, simplemente sacamos las claves de ahí
		 */
		File ficheroPublica = new File(FICH_PUBLICA);
		File ficheroPrivada = new File(FICH_PRIVADA);
		if (!ficheroPrivada.exists() && !ficheroPublica.exists()) {
			try {
				ficheroPrivada.createNewFile();
				ficheroPublica.createNewFile();
				KeyPair par = Seguridad.generarClaves();
				Seguridad.miprivada = par.getPrivate();
				Seguridad.mipublica = par.getPublic();
				Seguridad.archivar_clave_privada(FICH_PRIVADA, Seguridad.miprivada);
				Seguridad.archivar_clave_publica(FICH_PUBLICA, Seguridad.mipublica);
				System.out.println("Claves generadas y almacenadas, comenzando");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Seguridad.mipublica = Seguridad.Recuperar_clave_publica(FICH_PUBLICA);
			Seguridad.miprivada = Seguridad.Recuperar_clave_privada(FICH_PRIVADA);
			System.out.println("Claves recuperadas, comenzando");
		}
	}
}
