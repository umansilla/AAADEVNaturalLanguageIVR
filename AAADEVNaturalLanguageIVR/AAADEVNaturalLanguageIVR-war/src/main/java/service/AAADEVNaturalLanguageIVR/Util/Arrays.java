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

import org.json.JSONObject;

/**
 * Clase creada para obtener un listado de los audios de un folder.
 * @author umansilla
 */
public class Arrays {

	/**
	 * Metodo creado para crear un objeto JSON de los archivos de audio existentes en un folder.
	 * @param storeLocation Path del folder de audios.
	 * @return Objeto JSON que se usara para la Web App.
	 */
    public static JSONObject arrayFiles(String storeLocation) {
        JSONObject json = new JSONObject();
        int index = 0;
        String dirPath = storeLocation;
        File dir = new File(dirPath);
        String[] files = dir.list();
        if (files.length == 0) {
            json.put("status", "empty");
        } else {
            for (int contador = files.length - 1; contador >= 0; contador--) {
                json.put("Index" + index, files[contador]);
                index++;
            }
        }

        return json;
    }

}
