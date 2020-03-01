/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author cgonz
 */
public class Curso implements Serializable {
	
	private int idCurso;
	private String codigoCurso;
	private String nombre;

	public Curso() {
	}

	public Curso(String codigoCurso, String nombre) {
		this.codigoCurso = codigoCurso;
		this.nombre = nombre;
	}

	public int getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	public String getCodigoCurso() {
		return codigoCurso;
	}

	public void setCodigoCurso(String codigoCurso) {
		this.codigoCurso = codigoCurso;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Curso{" + "idCurso=" + idCurso + ", codigoCurso=" + codigoCurso + ", nombre=" + nombre + '}';
	}
	
}
