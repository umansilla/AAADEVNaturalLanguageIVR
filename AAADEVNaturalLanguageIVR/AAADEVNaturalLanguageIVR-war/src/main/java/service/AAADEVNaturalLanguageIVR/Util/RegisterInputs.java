package service.AAADEVNaturalLanguageIVR.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;

/**
 * Clase creada para registrar el ingreso de usuarios a la Web App y Al IVR
 * @author umansilla
 *
 */
public class RegisterInputs {
	

	/**
	 * Metodo creado para registrar el ingreso de un usuario al Web App
	 * @param usuario Objeto Usuario Bean.
	 * @param cliente Cliente escrto desde Log In
	 * @param pais Pais escrito desde Log In. 
	 * @throws IOException Se lanza error al no exisitr uno de los parametros de ingreso.
	 */
	public void registerWebApp(Usuario usuario, String cliente, String pais) throws IOException{
		FileWriter fichero = new FileWriter(Constants.ROUTE_REGISTER_FILE, true);
		PrintWriter pw = new PrintWriter(fichero);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        pw.println(df.format(date) +"_WEB-APP:_AAADEVNaturalLanguageIVR_USUARIO:_"+usuario.getUsername()+"_ClIENTE:_"+cliente+"_PAIS:_"+pais);
		pw.close();
		fichero.close();
	}
	
	/**
	 * Metodo creado para registrar a un usuario identificado al ingresar al IVR.
	 * @param usuario Objeto Usuario Bean.
	 * @param callingParty Registro del numero de ingreso.
	 * @throws IOException Se lanza error al no exisitr uno de los parametros de ingreso.
	 */
	public void registerUserIVR(Usuario usuario, String callingParty) throws IOException{
		FileWriter fichero = new FileWriter(Constants.ROUTE_REGISTER_FILE, true);
		PrintWriter pw = new PrintWriter(fichero);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        pw.println(df.format(date) +"_SnapIn-IVR:_AAADEVNaturalLanguageIVR_Usuario:_"+usuario.getUsername()+"_CALLING-PARTY:_"+callingParty);
		pw.close();
		fichero.close();
	}
	
	/**
	 * Metodo creado para registrar usuario al ingresar al IVR.
	 * @param callingParty Registro del numero de ingreso.
	 * @throws IOException Se lanza error al no exisitr uno de los parametros de ingreso.
	 */
	public void registerIVR(String callingParty) throws IOException{
		FileWriter fichero = new FileWriter(Constants.ROUTE_REGISTER_FILE, true);
		PrintWriter pw = new PrintWriter(fichero);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        pw.println(df.format(date) +"_SnapIn-IVR:_AAADEVNaturalLanguageIVR_CALLING-PARTY:_"+callingParty);
		pw.close();
		fichero.close();
	}
}
