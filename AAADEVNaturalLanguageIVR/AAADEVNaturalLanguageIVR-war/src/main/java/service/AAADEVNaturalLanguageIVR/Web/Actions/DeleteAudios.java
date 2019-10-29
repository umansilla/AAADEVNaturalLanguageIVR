/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;

/**
 * Clase creada para borrar un directorio en especifico.
 * @author umansilla
 */
public class DeleteAudios {

    private final HttpServletRequest request;
    /**
     * Constructor DeleteAudios
     * @param request Objeto HttpServletRequest
     */
    public DeleteAudios(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Metodo para borrar un directorio desde el Web App.
     * @return Respuesta al ForntEnd en JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject delete() throws IOException, ServletException {
        JSONObject json = new JSONObject();
        JSONArray jsonArrayResponseWav = new JSONArray();
        JSONArray jsonArrayResponseTxt = new JSONArray();
        String arrayString = new PartToString().getStringValue(request.getPart("AudiosArray"));
        JSONArray jsonArray = new JSONArray(arrayString);
        for (int i = 0; i < jsonArray.length(); i++) {
            String wavFile = jsonArray.getString(i);
            
            if (deleteWavFiles(wavFile)) {
                jsonArrayResponseWav.put(wavFile);
            }
            String txtFile = wavFile.replace(".wav", ".txt");
            if (deleteTextFiles(txtFile)) {
                jsonArrayResponseTxt.put(txtFile);
            }
        }
        return json.put("status", "ok").put("deletedAudios", jsonArrayResponseWav).put("deletedTextFile", jsonArrayResponseTxt);
    }

    /**
     * Metodo creado para borrar un archivo en especifico y regresar el valor true si ha sido borrado y false so no ha sido borrado.
     * @param wavFile Nombre del archivo de audio que sera borrado.
     * @return Valor Boleano para saber si ha sido borrado o no.
     */
    private Boolean deleteWavFiles(String wavFile) {
    	HttpSession userSession = (HttpSession) request.getSession();
    	Usuario usuario = (Usuario) userSession.getAttribute("userActive");
    	String routeLanguage = null;
    	if(usuario.getLanguage().equals("es")){
    		routeLanguage = Constants.ROUTE_GRABACIONES_ES;
    	}
    	if(usuario.getLanguage().equals("pt")){
    		routeLanguage = Constants.ROUTE_GRABACIONES_PT;
    	}
    	if(usuario.getLanguage().equals("en")){
    		routeLanguage = Constants.ROUTE_GRABACIONES_EN;
    	}
        final File file = new File(routeLanguage + wavFile);
        if (file.exists() && file.delete()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Metodo creado para borrar archivo de texto
     * @param textFile Nombre del archivo de texto que sera borrado.
     * @return Vlor en boleano para validar si ha sido borrado o no.
     */
    private Boolean deleteTextFiles(String textFile) {
        final File file = new File(Constants.ROUTE_INTENTS + textFile);
        if (file.exists() && file.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
