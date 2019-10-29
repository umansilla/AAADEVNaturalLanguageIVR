package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;

/**
 * Clase creada para borrar un archivo en especifico.
 * @author umansilla
 */
public class DeleteFile {
	final private HttpServletRequest request;
	private String language;
	private String folder;
	private String audioName;
	/**
	 * Constructor DeleteFile
	 * @param request
	 */
	public DeleteFile(HttpServletRequest request) {
		this.request = request;
	}
    /**
     * Metodo creado para borrar un archivo.
     * @return Respuseta el FrontEnd en formato JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
	public JSONObject enterDeleteFile() throws IOException, ServletException {
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonArray = new JSONArray(
				new PartToString().getStringValue(request
						.getPart("files_Array")));
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			String arr = json.keys().next();
			String[] arrOfStr = arr.split(",");
			language = arrOfStr[0];
			folder = arrOfStr[1];
			audioName = json.getString(arr);
			Boolean valueDeleted = deleteFiles();
			if (valueDeleted) {
				jsonResponse.put(audioName, "Deleted");
			} else {
				jsonResponse.put(audioName, "No Deleted");
			}
		}
		return new JSONObject().put("status", "ok").put("audios", jsonResponse);
	}

    /**
     * Metodo creado para borrar un archivo en especifico y validar si ha sido borrado correctamente.
     * @param dir Archivo que se debera de borrar.
     * @return Valor en boleano para saber si ha sido borrado o no.
     */
	private Boolean deleteFiles() {
		final File file = new File(Constants.PATH_TO_AUDIOS + folder + "/"
				+ language + "/" + audioName);
		if (file.exists() && file.delete()) {
			return true;
		} else {
			return false;
		}
	}
}
