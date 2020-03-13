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
import seguridad.Seguridad;

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
		//De igual manera vamos a crear el par de claves para este usuario
		this.output.writeObject(ConexionEstaticaBBDD.registrarUsuario(usuario));
		
	}

	private void login() throws ClassNotFoundException, IOException {
		Usuario usuario = (Usuario) this.input.readObject();
		this.output.writeObject(ConexionEstaticaBBDD.comprobarUsuario(usuario.getNombreUsuario(), usuario.getPasswordString()));
		//Le mandamos nuestra clave pública al usuario logueado
		this.output.writeObject(Seguridad.mipublica);
		
	}

	private void listarUsuarios() throws IOException {
		ArrayList<Usuario> listaUsuarios = ConexionEstaticaBBDD.listarUsuarios();
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(listaUsuarios, Seguridad.claveCifrado));
	}

	private void activarUsuario() throws IOException, ClassNotFoundException {
		Usuario usuario = (Usuario) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.asignarRol(usuario), Seguridad.claveCifrado));
	}

	private void listarCursos() throws IOException {
		ArrayList<Curso> listaCursos = ConexionEstaticaBBDD.listarCursos();
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(listaCursos, Seguridad.claveCifrado));
	}

	private void addCurso() throws IOException, ClassNotFoundException {
		Curso curso = (Curso) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject()) ;
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.insertarCurso(curso), Seguridad.claveCifrado));
	}

	private void editarCurso() throws IOException, ClassNotFoundException {
		Curso curso = (Curso) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject()) ;
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.actualizarCurso(curso), Seguridad.claveCifrado));
	}

	private void elegirCurso() throws IOException, ClassNotFoundException {
		Alumno alumno = (Alumno) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.asignarCurso(alumno), Seguridad.claveCifrado));
	}

	private void asignarProfesor() throws IOException, ClassNotFoundException {
		Usuario usuario = (Usuario) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		int id = (int)  Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.asignarProfesor(usuario,id), Seguridad.claveCifrado));
	}
	
	private void listarCursosProfesor() throws IOException, ClassNotFoundException{
		int id = (int)  Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		ArrayList<Curso> listaCursos = ConexionEstaticaBBDD.listarCursosProfesor(id);
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(listaCursos, Seguridad.claveCifrado));
	}

	private void listarAlumnosCurso() throws IOException, ClassNotFoundException {
		int id = (int)  Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.listarAlumnosCurso(id), Seguridad.claveCifrado));
	}

	private void ponerNota() throws IOException, ClassNotFoundException {
		Nota nota = (Nota) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.ponerNota(nota), Seguridad.claveCifrado));
	}

	private void listarProfesoresAlumno() throws IOException, ClassNotFoundException {
		int id = (int)  Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(ConexionEstaticaBBDD.listarProfesoresAlumno(id), Seguridad.claveCifrado));
	}

	private void consultarNota() throws IOException, ClassNotFoundException {
		Nota nota = (Nota) Seguridad.descifrar(Seguridad.claveCifrado, this.input.readObject());
		Nota enviar = ConexionEstaticaBBDD.consultarNota(nota);
		if (enviar != null) {
			System.out.println("Nota rescatada "+enviar.getNota());
			enviar.setFirma(Seguridad.firmar(enviar.getNota(), Seguridad.miprivada));
		}
		this.output.writeObject(Seguridad.cifrarConClaveSimetrica(enviar, Seguridad.claveCifrado));
	}

}
