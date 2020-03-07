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
import javax.crypto.SecretKey;
import modelo.Alumno;
import modelo.Curso;
import modelo.Nota;
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
				System.out.println("Esperando orden");
				short tipoOrden = (short) this.input.readObject();
				System.out.println(tipoOrden);
				gestionOrden(tipoOrden);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			cerrarConexion();
		}
	}

	public void cerrarConexion() {
		try {
			this.cliente.close();
		} catch (IOException e) {
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
			case CodigoOrden.ADD_CURSO:
				addCurso();
				break;

			case CodigoOrden.EDITAR_CURSO:
				editarCurso();
				break;

			case CodigoOrden.ELEGIR_CURSO:
				elegirCurso();
				break;

			case CodigoOrden.ASIGNAR_PROFESOR:
				asignarProfesor();
				break;
			
			case CodigoOrden.LISTAR_CURSOS_PROFESOR:
				listarCursosProfesor();
				break;
			
			case CodigoOrden.LISTAR_ALUMNOS_CURSO:
				listarAlumnosCurso();
				break;
			
			case CodigoOrden.PONER_NOTA:
				ponerNota();
				break;
				
			case CodigoOrden.LISTAR_PROFESORES_ALUMNO:
				listarProfesoresAlumno();
				break;
			
			case CodigoOrden.VER_NOTA:
				consultarNota();
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

	private void listarUsuarios() throws IOException {
		ArrayList<Usuario> listaUsuarios = ConexionEstaticaBBDD.listarUsuarios();
		this.output.writeObject(listaUsuarios);
	}

	private void activarUsuario() throws IOException, ClassNotFoundException {
		Usuario usuario = (Usuario) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.asignarRol(usuario));
	}

	private void listarCursos() throws IOException {
		ArrayList<Curso> listaCursos = ConexionEstaticaBBDD.listarCursos();
		this.output.writeObject(listaCursos);
	}

	private void addCurso() throws IOException, ClassNotFoundException {
		Curso curso = (Curso) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.insertarCurso(curso));
	}

	private void editarCurso() throws IOException, ClassNotFoundException {
		Curso curso = (Curso) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.actualizarCurso(curso));
	}

	private void elegirCurso() throws IOException, ClassNotFoundException {
		Alumno alumno = (Alumno) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.asignarCurso(alumno));
	}

	private void asignarProfesor() throws IOException, ClassNotFoundException {
		Usuario usuario = (Usuario) this.input.readObject();
		int id = (int) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.asignarProfesor(usuario, id));
	}
	
	private void listarCursosProfesor() throws IOException, ClassNotFoundException{
		int id = (int) this.input.readObject();
		ArrayList<Curso> listaCursos = ConexionEstaticaBBDD.listarCursosProfesor(id);
		this.output.writeObject(listaCursos);
	}

	private void listarAlumnosCurso() throws IOException, ClassNotFoundException {
		int id = (int) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.listarAlumnosCurso(id));
	}

	private void ponerNota() throws IOException, ClassNotFoundException {
		Nota nota = (Nota) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.ponerNota(nota));
	}

	private void listarProfesoresAlumno() throws IOException, ClassNotFoundException {
		int id = (int) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.listarProfesoresAlumno(id));
	}

	private void consultarNota() throws IOException, ClassNotFoundException {
		Object n =  this.input.readObject();
		Nota nota = (Nota) n;
		System.out.println(n);
		this.output.writeObject(ConexionEstaticaBBDD.consultarNota(nota));	
	}

}
