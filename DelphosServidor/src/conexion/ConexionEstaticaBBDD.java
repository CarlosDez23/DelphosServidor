/*
 * Conexión estática a la base de datos
 */
package conexion;

import constantes.ConstantesConexionBBDD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
				int registrados = sentenciaSQL.executeUpdate(sql);
				if (registrados != 0) {
					registrado = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return registrado;
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

}
