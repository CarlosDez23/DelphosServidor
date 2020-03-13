/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author cgonz
 */
public class BoletinWeb {
	
	private String nombreAlumno;
	private String nota;

	public BoletinWeb() {
	}

	public BoletinWeb(String nombreAlumno, String nota) {
		this.nombreAlumno = nombreAlumno;
		this.nota = nota;
	}

	public String getNombreAlumno() {
		return nombreAlumno;
	}

	public void setNombreAlumno(String nombreAlumno) {
		this.nombreAlumno = nombreAlumno;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	@Override
	public String toString() {
		return "BoletinWeb{" + "nombreAlumno=" + nombreAlumno + ", nota=" + nota + '}';
	}
	
	
	
}
