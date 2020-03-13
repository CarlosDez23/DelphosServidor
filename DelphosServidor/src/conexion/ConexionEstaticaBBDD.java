/*
 * Conexión estática a la base de datos
 */
package conexion;

import constantes.ConstantesConexionBBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import modelo.Alumno;
import modelo.BoletinWeb;
import modelo.Curso;
import modelo.Nota;
import modelo.Usuario;
import seguridad.Seguridad;

/**
 *
 * @author Carlos González
 */
public class ConexionEstaticaBBDD {

	private static Connection conex;
	private static Statement sentenciaSQL;
	private static ResultSet registros;

	static {
		try {
			try {
				String controlador = "com.mysql.jdbc.Driver";
				Class.forName(controlador);
				String URL_BD = "jdbc:mysql://localhost/" + ConstantesConexionBBDD.BD;
				conex = java.sql.DriverManager.getConnection(URL_BD, ConstantesConexionBBDD.USUARIO, ConstantesConexionBBDD.PASSWD);
				sentenciaSQL = conex.createStatement();
				System.out.println("Conexión realizada con éxito");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para registrar un usuario, lo debemos sincronizar para que no haya pérdida de datos si varios clientes están registrándose al mismo tiempo. Devolerá true si el registro ha sido correcto
	 *
	 * @param usuario
	 * @return registrado
	 */
	public synchronized static boolean registrarUsuario(Usuario usuario) {
		boolean registrado = false;
		
		String sql2 = "INSERT INTO "+ConstantesConexionBBDD.TABLAUSUARIOS + "(NOMBRE, PASSWORD,TELEFONO,DIRECCION,EDAD,CLAVE) VALUES (?,?,?,?,?,?)";

		System.out.println(sql2);
		try {
			PreparedStatement preparada = conex.prepareStatement(sql2);
			preparada.setString(1, usuario.getNombreUsuario());
			preparada.setString(2, usuario.getPasswordString());
			preparada.setString(3, usuario.getTelefono());
			preparada.setString(4, usuario.getDireccion());
			preparada.setInt(5, usuario.getEdad());
			preparada.setBytes(6, usuario.getClaveKey().getEncoded());
			if (!existeUsuario(usuario.getNombreUsuario())) {
				if (preparada.executeUpdate() == 1 && registrarRol(idUltimoRegistrado(usuario.getNombreUsuario()), 0)) {
					registrado = true;
				}
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return registrado;
	}

	private synchronized static boolean registrarRol(int id, int rol) {
		boolean correcto = false;
		String sql = "INSERT INTO " + ConstantesConexionBBDD.TABLAROLESASIGNADOS + "(ID_ROL, ID_USUARIO) VALUES (" + rol + "," + id + ")";
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				correcto = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return correcto;
	}

	private static int idUltimoRegistrado(String nombre) {
		int id = -1;
		String sql = "SELECT ID FROM " + ConstantesConexionBBDD.TABLAUSUARIOS + " WHERE NOMBRE = '" + nombre + "'";
		try {
			registros = sentenciaSQL.executeQuery(sql);
			if (registros.next()) {
				id = registros.getInt(1);
				System.out.println(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Método para comprobar que no existe un usuario con el mismo nombre en la BBDD
	 *
	 * @param nombre
	 * @return
	 */
	private static boolean existeUsuario(String nombre) {
		boolean existe = false;
		String sql = "SELECT * FROM " + ConstantesConexionBBDD.TABLAUSUARIOS + " WHERE NOMBRE = '" + nombre + "'";
		try {
			registros = sentenciaSQL.executeQuery(sql);
			if (registros.next()) {
				existe = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return existe;
	}

	/**
	 * Método para comprobar que existe un usuario en la BBDD, se le llamará a la hora del login
	 *
	 * @param nombre
	 * @param password
	 * @return
	 */
	public static Usuario comprobarUsuario(String nombre, String password) {
		Usuario usuario = null;
		String sql = "SELECT U.ID, U.NOMBRE, R.ID_ROL, U.CLAVE FROM " + ConstantesConexionBBDD.TABLAUSUARIOS + " U, " + ConstantesConexionBBDD.TABLAROLESASIGNADOS
				+ " R WHERE NOMBRE= '" + nombre + "' AND PASSWORD = '" + password + "'"
				+ "AND U.ID = R.ID_USUARIO";
		System.out.println(sql);
		try {
			registros = sentenciaSQL.executeQuery(sql);
			System.out.println("Antes de next");
			if (registros.next()) {
				System.out.println("Entrando a registros");
				usuario = new Usuario();
				usuario.setIdUsuario(registros.getInt(1));
				usuario.setNombreUsuario(registros.getString(2));
				usuario.setRol((byte) registros.getInt(3));
				usuario.setClaveKey(Seguridad.recomponerClaveSimetrica(registros.getBytes(4)));
				Seguridad.claveCifrado = Seguridad.recomponerClaveSimetrica(registros.getBytes(4));
				System.out.println(usuario.toString());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return usuario;
	}

	public static ArrayList<Usuario> listarUsuarios() {
		System.out.println("Entrando a listar usuarios");
		ArrayList<Usuario> listaUsuarios = new ArrayList<>();
		String sql = "SELECT U.ID, U.NOMBRE, U.PASSWORD, U.TELEFONO, U.DIRECCION, U.EDAD, R.ID_ROL FROM " + ConstantesConexionBBDD.TABLAUSUARIOS + " U, " + ConstantesConexionBBDD.TABLAROLESASIGNADOS
				+ " R WHERE U.ID = R.ID_USUARIO";
		System.out.println(sql);
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while (registros.next()) {
				Usuario usuario = new Usuario();
				usuario.setIdUsuario(registros.getInt(1));
				usuario.setNombreUsuario(registros.getString(2));
				usuario.setPasswordString(registros.getString(3));
				usuario.setTelefono(registros.getString(4));
				usuario.setDireccion(registros.getString(5));
				usuario.setEdad(registros.getInt(6));
				usuario.setRol((byte) registros.getInt(7));
				listaUsuarios.add(usuario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaUsuarios;
	}

	public synchronized static boolean asignarRol(Usuario usuario) {
		boolean activado = false;
		int rol = (int) usuario.getRol();
		String sql = "UPDATE " + ConstantesConexionBBDD.TABLAROLESASIGNADOS + " SET ID_ROL = " + rol + " WHERE ID_USUARIO = " + usuario.getIdUsuario();
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				activado = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return activado;
	}

	public static ArrayList<Curso> listarCursos() {
		ArrayList<Curso> listaCursos = new ArrayList<>();
		String sql = "SELECT * FROM " + ConstantesConexionBBDD.TABLACURSO;
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while (registros.next()) {
				Curso curso = new Curso();
				curso.setIdCurso(registros.getInt(1));
				curso.setCodigoCurso(registros.getString(2));
				curso.setNombre(registros.getString(3));
				listaCursos.add(curso);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaCursos;
	}

	public static synchronized boolean insertarCurso(Curso curso) {
		boolean registrado = false;
		String sql = "INSERT INTO " + ConstantesConexionBBDD.TABLACURSO + " (CODIGO, NOMBRE) VALUES ( '"
				+ curso.getCodigoCurso() + "', '" + curso.getNombre() + "')";
		System.out.println(sql);
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				registrado = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return registrado;
	}

	public static synchronized boolean actualizarCurso(Curso curso) {
		boolean actualizado = false;
		String sql = "UPDATE " + ConstantesConexionBBDD.TABLACURSO + " SET CODIGO = '" + curso.getCodigoCurso()
				+ "', NOMBRE = '" + curso.getNombre() + "' WHERE IDCURSO = " + curso.getIdCurso();
		System.out.println(sql);
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				actualizado = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actualizado;
	}

	public static synchronized boolean asignarCurso(Alumno alumno) {
		boolean registrado = false;
		String sql = "INSERT INTO " + ConstantesConexionBBDD.TABLAALUMNO + " VALUES ( "
				+ alumno.getIdUsuario() + ", " + alumno.getIdCurso() + ")";
		System.out.println(sql);
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				registrado = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return registrado;
	}
	
	public static synchronized boolean asignarProfesor(Usuario usuario, int idCurso){
		boolean registrado = false;
		String sql = "INSERT INTO " + ConstantesConexionBBDD.TABLAIMPARTE + " (COD_PROFESOR, COD_CURSO) VALUES ( "
				+ usuario.getIdUsuario()+ ", " + idCurso + ")";
		System.out.println(sql);
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				registrado = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return registrado;
	}
	
	public static ArrayList<Curso> listarCursosProfesor(int idProfesor){
		ArrayList<Curso> listaCursos = new ArrayList<>();
		String sql = "SELECT C.IDCURSO, C.CODIGO, C.NOMBRE FROM "+ConstantesConexionBBDD.TABLACURSO+" C, "+ConstantesConexionBBDD.TABLAIMPARTE+" I" 
				+" WHERE C.IDCURSO = I.COD_CURSO AND I.COD_PROFESOR = "+idProfesor;
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while (registros.next()) {
				Curso curso = new Curso();
				curso.setIdCurso(registros.getInt(1));
				curso.setCodigoCurso(registros.getString(2));
				curso.setNombre(registros.getString(3));
				listaCursos.add(curso);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaCursos;
	}
	
	public static ArrayList<Alumno> listarAlumnosCurso(int idCurso){
		ArrayList<Alumno> listaAlumnos = new ArrayList<>();
		String sql = "SELECT U.ID, U.NOMBRE FROM "+ConstantesConexionBBDD.TABLAUSUARIOS+" U, "+ConstantesConexionBBDD.TABLAALUMNO+" A "+
				" WHERE A.IDCURSO = "+idCurso+" AND  A.ID = U.ID";
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while(registros.next()){
				Alumno aux = new Alumno();
				aux.setIdUsuario(registros.getInt(1));
				aux.setNombreUsuario(registros.getString(2));
				listaAlumnos.add(aux);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaAlumnos;
	}
	
	public static synchronized boolean ponerNota(Nota nota){
		boolean puesta = false;
		String sql = "INSERT INTO "+ConstantesConexionBBDD.TABLANOTAS+" (COD_ALUMNO, COD_PROFESOR, NOTA) VALUES ( "+
				nota.getIdAlumno()+", "+nota.getIdProfesor()+", "+nota.getNota()+")";
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				puesta = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return puesta;
	}
	
	public static ArrayList<Usuario> listarProfesoresAlumno(int id){
		ArrayList<Usuario> listaUsuarios = new ArrayList<>();
		String sql = "SELECT * FROM "+ConstantesConexionBBDD.TABLAUSUARIOS+" U, "+ ConstantesConexionBBDD.TABLAIMPARTE +" I WHERE I.COD_CURSO = (SELECT IDCURSO FROM ALUMNO WHERE ID = "+id+" ) AND U.ID = I.COD_PROFESOR ";
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while (registros.next()) {				
				Usuario usuario = new Usuario();
				usuario.setIdUsuario(registros.getInt(1));
				usuario.setNombreUsuario(registros.getString(2));
				usuario.setPasswordString(registros.getString(3));
				usuario.setTelefono(registros.getString(4));
				usuario.setDireccion(registros.getString(5));
				usuario.setEdad(registros.getInt(6));
				listaUsuarios.add(usuario);
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaUsuarios;
	}
	
	public static Nota consultarNota(Nota nota){
		Nota aux = null;
		String sql = "SELECT * FROM "+ConstantesConexionBBDD.TABLANOTAS+" WHERE COD_ALUMNO = "+nota.getIdAlumno()+" AND COD_PROFESOR = "+nota.getIdProfesor();
		try {
			registros = sentenciaSQL.executeQuery(sql);
			if (registros.next()) {
				aux = new Nota();
				aux.setId(registros.getInt(1));
				aux.setIdAlumno(registros.getInt(2));
				aux.setIdProfesor(registros.getInt(3));
				float valor = registros.getFloat(4);
				aux.setNota(Float.toString(valor));
				System.out.println("Nota consultada "+aux.toString());
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return aux;
	}
	
	public static ArrayList<BoletinWeb> consultaNotasWeb(){
		ArrayList<BoletinWeb> listNotas = new ArrayList<>();
		String sql = "SELECT N.NOTA, U.NOMBRE FROM "+ConstantesConexionBBDD.TABLANOTAS+" N, "+
				ConstantesConexionBBDD.TABLAUSUARIOS+" U WHERE N.COD_ALUMNO = U.ID";
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while(registros.next()){
				BoletinWeb boletin = new BoletinWeb(registros.getString(2), registros.getString(1));
				listNotas.add(boletin);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listNotas;
	}
}
