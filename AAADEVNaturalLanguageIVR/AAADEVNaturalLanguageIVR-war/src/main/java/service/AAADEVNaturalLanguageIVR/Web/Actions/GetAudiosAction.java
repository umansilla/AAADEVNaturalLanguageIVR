/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.InputIntent;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
/**
 * Clase creada para obtener la ruta de los audios y el valor del archivo de texto.
 * @author umansilla
 */
public class GetAudiosAction {
	/**
	 * Obtener la ruta de audios de acuerdo al idioma especificado en el Objeto Usuarios.
	 * @param language Idioma en el objeto Usuarios
	 * @return List<InputIntent>
	 * @throws IOException Se lanza error al no existir contenido.
	 */
    public List<InputIntent> getAudios(String language) throws IOException {
    	String routeLanguage = null;
    	if(language.equals("es")){
    		routeLanguage = Constants.ROUTE_GRABACIONES_ES;
    	}
    	if(language.equals("pt")){
    		routeLanguage = Constants.ROUTE_GRABACIONES_PT;
    	}
    	if(language.equals("en")){
    		routeLanguage = Constants.ROUTE_GRABACIONES_EN;
    	}
    	
    	
        final File folder = new File(routeLanguage);
        List<InputIntent> listIntents = listFilesForFolder(folder);
        return listIntents;
    }
    /**
     * Metodo creado para obtener los datos del archivo de texto por grabacion.
     * @param folder Folder de acuerdo al idioma.
     * @return List<InputIntent>
	 * @throws IOException Se lanza error al no existir contenido.
     */
    public static List<InputIntent> listFilesForFolder(final File folder) throws IOException {
         List<InputIntent> listInputIntents = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                String wavFile = fileEntry.getName();
                String txtFile = wavFile.replace(".wav", ".txt");
                try (FileReader reader = new FileReader(Constants.ROUTE_INTENTS +txtFile);
                        BufferedReader br = new BufferedReader(reader)) {
                    // read line by line
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    
                    JSONObject jsonIntentResults = new JSONObject(sb.toString());
                    InputIntent intentObject = new InputIntent(jsonIntentResults.getString("Anger")
                            , jsonIntentResults.getString("Fear")
                            , jsonIntentResults.getString("Transcript")
                            , jsonIntentResults.getString("fechayHora")
                            , jsonIntentResults.getString("Disgust")
                            , jsonIntentResults.getString("Destino")
                            , jsonIntentResults.getString("Joy")
                            , jsonIntentResults.getString("sadness")
                            , jsonIntentResults.getJSONObject("Intent").getString("Intent")
                            , jsonIntentResults.getString("COnfidence")
                            , jsonIntentResults.getString("Origen"), fileEntry.getName());
                    listInputIntents.add(intentObject);
                } catch (IOException e) {
                   throw new IOException("GetAudiosAction Error: " + e.toString());
                }

            }
        }
        return listInputIntents;
    }
}
