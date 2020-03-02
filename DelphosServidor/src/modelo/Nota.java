/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author Carlos Gonzalez
 */
public class Nota implements Serializable {
	
	private int id;
	private int idAlumno;
	private int idProfesor;
	private float nota;

	public Nota() {
	}

	public Nota(int idAlumno, int idProfesor, float nota) {
		this.idAlumno = idAlumno;
		this.idProfesor = idProfesor;
		this.nota = nota;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdAlumno() {
		return idAlumno;
	}

	public void setIdAlumno(int idAlumno) {
		this.idAlumno = idAlumno;
	}

	public int getIdProfesor() {
		return idProfesor;
	}

	public void setIdProfesor(int idProfesor) {
		this.idProfesor = idProfesor;
	}

	public float getNota() {
		return nota;
	}

	public void setNota(float nota) {
		this.nota = nota;
	}

	@Override
	public String toString() {
		return "Nota{" + "id=" + id + ", idAlumno=" + idAlumno + ", idProfesor=" + idProfesor + ", nota=" + nota + '}';
	}
}
