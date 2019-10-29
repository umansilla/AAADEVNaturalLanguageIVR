/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Web.Actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.InputIntent;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
/**
 * Clase creada pra Obtener las emociones.
 * @author umansilla
 */
public class GetEmotionsAction {
    
    private final HttpServletRequest request;
    /**
     * Contructor GetEmotionsAction
     * @param request Objeto HttpServletRequest
     */
    public GetEmotionsAction(HttpServletRequest request) {
        this.request = request;
    }
    /**
     * Metodo creado para obtener las emociones de un archivo de texto
     * @return JSONObject
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject getEmotions() throws IOException, ServletException {
        String wavFile = new PartToString().getStringValue(request.getPart("wavFile"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(listFilesForFolder(wavFile)); //convert 
        return new JSONObject(json);
    }
    /**
     * Metodo creado para obtener las emociones de relacionando el archiv de audio con el archivo de texto.
     * @param wavFile Nombre del archivo de audio.
     * @return Objeto InputIntent.
	 * @throws IOException Se lanza error al no existir contenido.
     */
    private static InputIntent listFilesForFolder(final String wavFile) throws IOException {
       
        InputIntent intentObject = null;
        String txtFile = wavFile.replace(".wav", ".txt");
        try (FileReader reader = new FileReader(Constants.ROUTE_INTENTS + txtFile);
                BufferedReader br = new BufferedReader(reader)) {
            // read line by line
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonIntentResults = new JSONObject(sb.toString());
            intentObject = new InputIntent(jsonIntentResults.getString("Anger"),
                    jsonIntentResults.getString("Fear"),
                    jsonIntentResults.getString("Disgust"),
                    jsonIntentResults.getString("Joy"),
                    jsonIntentResults.getString("sadness"));

        } catch (IOException e) {
        	throw new IOException("GetEmotionsAction Error: " + e.toString());
        }
        return intentObject;
    }
    
    
}
