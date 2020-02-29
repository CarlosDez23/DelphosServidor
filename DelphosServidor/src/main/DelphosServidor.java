/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
