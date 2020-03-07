/*
 * Clase usuario
 */
package modelo;

import java.io.Serializable;
import javax.crypto.SecretKey;

/**
 *
 * @author Carlos González
 */
public class Usuario implements Serializable {
	
	private int idUsuario;
	private String nombreUsuario;
	private String passwordString;
	private String telefono; 
	private String direccion;
	private int edad;
	private byte rol;
	private SecretKey claveKey;
	
	public Usuario() {
	}

	public Usuario(int idUsuario, String nombreUsuario, String passwordString, String telefono, String direccion, int edad, byte rol) {
		this.idUsuario = idUsuario;
		this.nombreUsuario = nombreUsuario;
		this.passwordString = passwordString;
		this.telefono = telefono;
		this.direccion = direccion;
		this.edad = edad;
		this.rol = rol;
	}

	public Usuario(String nombreUsuario, String passwordString, String telefono, String direccion, int edad) {
		this.nombreUsuario = nombreUsuario;
		this.passwordString = passwordString;
		this.telefono = telefono;
		this.direccion = direccion;
		this.edad = edad;
	}

	public Usuario(int idUsuario, String nombreUsuario, String passwordString, String telefono, String direccion, int edad, byte rol, SecretKey claveKey) {
		this.idUsuario = idUsuario;
		this.nombreUsuario = nombreUsuario;
		this.passwordString = passwordString;
		this.telefono = telefono;
		this.direccion = direccion;
		this.edad = edad;
		this.rol = rol;
		this.claveKey = claveKey;
	}

	public Usuario(String nombreUsuario, String passwordString, String telefono, String direccion, int edad, SecretKey claveKey) {
		this.nombreUsuario = nombreUsuario;
		this.passwordString = passwordString;
		this.telefono = telefono;
		this.direccion = direccion;
		this.edad = edad;
		this.claveKey = claveKey;
	}
	
	

	
	
	
	
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getPasswordString() {
		return passwordString;
	}

	public void setPasswordString(String passwordString) {
		this.passwordString = passwordString;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public byte getRol() {
		return rol;
	}

	public void setRol(byte rol) {
		this.rol = rol;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public SecretKey getClaveKey() {
		return claveKey;
	}

	public void setClaveKey(SecretKey claveKey) {
		this.claveKey = claveKey;
	}
	
	

	@Override
	public String toString() {
		return "Usuario{" + "idUsuario=" + idUsuario + ", nombreUsuario=" + nombreUsuario + ", passwordString=" + passwordString + ", telefono=" + telefono + ", direccion=" + direccion + ", edad=" + edad + ", rol=" + rol + '}';
	}
	
}
