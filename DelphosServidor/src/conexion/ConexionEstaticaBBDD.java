/*
 * Conexión estática a la base de datos
 */
package conexion;

import constantes.ConstantesConexionBBDD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import modelo.Usuario;

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
		String sql = "INSERT INTO " + ConstantesConexionBBDD.TABLAUSUARIOS + "(NOMBRE, PASSWORD,TELEFONO,DIRECCION,EDAD) VALUES ('" + usuario.getNombreUsuario() + "', '" + usuario.getPasswordString() + "', '"+ usuario.getTelefono()+ "','"
				+ usuario.getDireccion()
				+ "', " + usuario.getEdad()+")";
		
		System.out.println(sql);
		try {
			if (!existeUsuario(usuario.getNombreUsuario())) {
				if (sentenciaSQL.executeUpdate(sql) == 1 && registrarRol(idUltimoRegistrado(usuario.getNombreUsuario()), 0)) {
					registrado = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return registrado;
	}
	
	private synchronized static boolean registrarRol(int id, int rol){
		boolean correcto = false;
		String sql = "INSERT INTO "+ConstantesConexionBBDD.TABLAROLESASIGNADOS+"(ID_ROL, ID_USUARIO) VALUES ("+rol+","+id+")";
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				correcto = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return correcto;
	}
	
	private static int idUltimoRegistrado(String nombre){
		int id = -1;
		String sql = "SELECT ID FROM "+ConstantesConexionBBDD.TABLAUSUARIOS+" WHERE NOMBRE = '"+nombre+"'";
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
	 * @param nombre
	 * @param password
	 * @return 
	 */
	public static Usuario comprobarUsuario(String nombre, String password){
		Usuario usuario = null;
		String sql = "SELECT U.ID, U.NOMBRE, R.ID_ROL FROM "+ConstantesConexionBBDD.TABLAUSUARIOS+" U, "+ConstantesConexionBBDD.TABLAROLESASIGNADOS+
				" R WHERE NOMBRE= '"+nombre+"' AND PASSWORD = '"+password+"'"
				+ "AND U.ID = R.ID_USUARIO";
		System.out.println(sql);
		try {
			registros = sentenciaSQL.executeQuery(sql);
			if (registros.next()) {
				usuario = new Usuario();
				usuario.setIdUsuario(registros.getInt(1));
				usuario.setNombreUsuario(registros.getString(2));
				usuario.setRol((byte)registros.getInt(3));
				System.out.println(usuario.toString());
			}		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return usuario; 
	}
	
	public static ArrayList<Usuario> listarUsuarios(){
		System.out.println("Entrando a listar usuarios");
		ArrayList<Usuario> listaUsuarios = new ArrayList<>();
		String sql = "SELECT U.ID, U.NOMBRE, U.PASSWORD, U.TELEFONO, U.DIRECCION, U.EDAD, R.ID_ROL FROM "+ConstantesConexionBBDD.TABLAUSUARIOS+" U, "+ConstantesConexionBBDD.TABLAROLESASIGNADOS+
				" R WHERE U.ID = R.ID_USUARIO";
		System.out.println(sql);
		try {
			registros = sentenciaSQL.executeQuery(sql);
			while(registros.next()){
				Usuario usuario = new Usuario();
				usuario.setIdUsuario(registros.getInt(1));
				usuario.setNombreUsuario(registros.getString(2));
				usuario.setPasswordString(registros.getString(3));
				usuario.setTelefono(registros.getString(4));
				usuario.setDireccion(registros.getString(5));
				usuario.setEdad(registros.getInt(6));
				usuario.setRol((byte)registros.getInt(7));
				listaUsuarios.add(usuario);		
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaUsuarios; 
	}
	
	public synchronized static boolean asignarRol (Usuario usuario){
		boolean activado = false; 
		int rol = (int) usuario.getRol();
		String sql = "UPDATE "+ConstantesConexionBBDD.TABLAROLESASIGNADOS+" SET ID_ROL = "+rol+" WHERE ID_USUARIO = "+usuario.getIdUsuario();
		try {
			if (sentenciaSQL.executeUpdate(sql) == 1) {
				activado = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return activado; 
	}
}
