/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

import conexion.ConexionEstaticaBBDD;
import constantes.CodigoOrden;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import modelo.Usuario;

/**
 *
 * @author Carlos González
 */
public class HiloCliente implements Runnable {

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

	public void start() {
		this.hilo.start();
	}

	@Override
	public void run() {
		try {
			while (!this.cliente.isClosed()) {
				try {
					short tipoOrden = this.input.readShort();
					gestionOrden(tipoOrden);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gestionOrden(short orden) {
		try {
			switch (orden) {
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
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("La opción del menú en la que ha fallado es " + orden);

		}

	}

	private void registrarUsuario() throws IOException, ClassNotFoundException {
		Usuario usuario = (Usuario) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.registrarUsuario(usuario));
	}

	private void login() throws ClassNotFoundException, IOException {
		Usuario usuario = (Usuario) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.comprobarUsuario(usuario.getNombreUsuario(), usuario.getPasswordString()));
	}

	private void activarUsuario() {

	}

	private void addCurso() {

	}

	private void editarCurso() {

	}

	private void asignarRol() {

	}

	private void elegirCurso() {

	}

	private void ponerNota() {

	}

	private void elegirProfesor() {

	}

	private void verNota() {

	}
}
