/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

import conexion.ConexionEstaticaBBDD;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import modelo.BoletinWeb;
import modelo.Nota;

/**
 *
 * @author cgonz
 */
public class HiloClienteWeb implements Runnable {
	
	private Thread hilo;
	private Socket cliente;
	private PrintWriter out;
	private BufferedReader input;

	public HiloClienteWeb(Socket cliente) {
		this.cliente = cliente;
		this.hilo = new Thread(this);
		try {
			
			this.input = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void start(){
		this.hilo.start();
	}

	@Override
	public void run() {
		out = null;
		
		try {
			out = new PrintWriter(new OutputStreamWriter(cliente.getOutputStream(), "8859_1"), true);
			out.println("HTTP/1.1 200 ok");
            out.println("Server: DAM2/1.0");
            out.println("Date: "+"");
            out.println("Content-Type: text/html");
            out.println("");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");

            out.println(" <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            out.println("<title>DAM2 2019 2020</title>");
            out.println("</head>");
			out.print("<body>");
			
			ArrayList<BoletinWeb> boletin = ConexionEstaticaBBDD.consultaNotasWeb();
			for (int i = 0; i < boletin.size(); i++) {
				out.print("Alumno: "+boletin.get(i).getNombreAlumno()+" Nota: "+boletin.get(i).getNota());
			}
			out.print("</body>");
            out.println("</html>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
