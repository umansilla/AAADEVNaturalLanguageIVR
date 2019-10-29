/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Util;

/**
*
* @author umansilla
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

/**
 * Clase creada para codificar a base64 un archivo de audio.
 * @author umansilla
 *
 */
public class Encoder {

	/**
	 * Metodo creado para ingresar el nombre de un archivo de audio y codificarlo a base64.
	 * @param fileName
	 * @return
	 */
	public static String encoder(String fileName) {
		String base64 = "";
		File file = new File(fileName);
		try (FileInputStream datain = new FileInputStream(file)) {
			byte Data[] = new byte[(int) file.length()];
			datain.read(Data);
			base64 = DatatypeConverter.printBase64Binary(Data);
		} catch (FileNotFoundException e) {
			System.out.println("Error" + e);
		} catch (IOException ioe) {
			System.out.println("Ex " + ioe);
		}
		return base64;
	}
	
}