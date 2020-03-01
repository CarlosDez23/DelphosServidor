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
import java.util.ArrayList;
import modelo.Curso;
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
					System.out.println("Esperando orden");
					short tipoOrden = (short) this.input.readObject();
					System.out.println(tipoOrden);
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
			case CodigoOrden.LISTAR_USUARIOS:
				listarUsuarios();
				break;	
			case CodigoOrden.ACTIVAR_USUARIO:
				activarUsuario();
				break;
			case CodigoOrden.LISTAR_CURSOS:
				listarCursos();
				break;
			default:
				break;
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
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
	
	private void listarUsuarios() throws IOException{
		ArrayList<Usuario> listaUsuarios = ConexionEstaticaBBDD.listarUsuarios();
		this.output.writeObject(listaUsuarios);
	}

	private void activarUsuario() throws IOException, ClassNotFoundException {
		Usuario usuario = (Usuario) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.asignarRol(usuario));
		
	}
	
	private void listarCursos() throws IOException{
		ArrayList<Curso> listaCursos = ConexionEstaticaBBDD.listarCursos();
		this.output.writeObject(listaCursos);
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
