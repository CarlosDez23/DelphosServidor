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
	private String nota;
	private byte [] firma;

	public Nota() {
	}

	public Nota(int idAlumno, int idProfesor, String nota, byte[] firma) {
		this.idAlumno = idAlumno;
		this.idProfesor = idProfesor;
		this.nota = nota;
		this.firma = firma;
	}

	public Nota(int id, int idAlumno, int idProfesor, String nota, byte[] firma) {
		this.id = id;
		this.idAlumno = idAlumno;
		this.idProfesor = idProfesor;
		this.nota = nota;
		this.firma = firma;
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

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public byte[] getFirma() {
		return firma;
	}

	public void setFirma(byte[] firma) {
		this.firma = firma;
	}

	@Override
	public String toString() {
		return "Nota{" + "id=" + id + ", idAlumno=" + idAlumno + ", idProfesor=" + idProfesor + ", nota=" + nota + ", firma=" + firma + '}';
	}
	
}
