/*
 * Clase alumno
 */
package modelo;

/**
 *
 * @author Carlos González
 */
public class Alumno extends Usuario{
	
	private int idCurso;

	public Alumno() {
	}

	public Alumno(int idCurso, int idUsuario, String nombreUsuario, String passwordString, String telefono, String direccion, int edad, byte rol) {
		super(idUsuario, nombreUsuario, passwordString, telefono, direccion, edad, rol);
		this.idCurso = idCurso;
	}

	public int getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}
}
