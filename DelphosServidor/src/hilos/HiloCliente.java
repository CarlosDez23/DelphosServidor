/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

import constantes.CodigoOrden;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Carlos González
 */
public class HiloCliente implements Runnable{
	
	private Thread hilo;
	private Socket cliente;
	private ObjectOutputStream output;
	private ObjectInputStream input; 
	//private DataOutputStream outputNormal;
	//private DataInputStream inputNormal;
	

	public HiloCliente(Socket cliente) {
		this.hilo = new Thread(this);
		this.cliente = cliente;
		try {
			this.output = new ObjectOutputStream(this.cliente.getOutputStream());
			this.input = new ObjectInputStream(this.cliente.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		this.hilo.start();
	}
	
	

	@Override
	public void run() {
		while(!this.cliente.isClosed()){
			try {
				short tipoOrden = this.input.readShort();
				gestionOrden(tipoOrden);	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void gestionOrden(short orden){
		switch(orden){
			case CodigoOrden.REGISTRAR:
				registrarUsuario();
				break;
			case CodigoOrden.LOGIN:
				login();
				break;
			case CodigoOrden.ACTIVAR_USUARIO:
				activarUsuario();
				break;
			case CodigoOrden.ADD_CURSO:
				addCurso();
				break;
			case CodigoOrden.EDITAR_CURSO:
				editarCurso();
				break;
			case CodigoOrden.ASIGNAR_ROL:
				asignarRol();
				break;
			case CodigoOrden.ELEGIR_CURSO:
				elegirCurso();
				break;
			case CodigoOrden.PONER_NOTA:
				ponerNota();
				break;
			case CodigoOrden.ELEGIR_PROFESOR:
				elegirProfesor();
				break;
			case CodigoOrden.VER_NOTA:
				verNota();
				break;
			default:
				break;
		}
	}
	
	private void registrarUsuario(){
		
		
	}
	
	private void login(){
		
	}
	
	private void activarUsuario(){
		
	}
	
	private void addCurso(){
		
	}
	
	private void editarCurso(){
		
	}
	
	private void asignarRol(){
		
	}
	
	private void elegirCurso(){
		
	}
	
	private void ponerNota(){
		
	}
	
	private void elegirProfesor(){
		
	}
	
	private void verNota(){
		
	}
}
