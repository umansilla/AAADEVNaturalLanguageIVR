package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;

/**
 * Clase creada para cambiar el idioma del Objeto Usuario.
 * @author umansilla
 */
public class ChangeLanguage {
	private final HttpServletRequest request;
	/**
	 * Constructor ChangeLanguage
	 * @param request Objeto HttpServletRequest
	 */
	public ChangeLanguage(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Metodo creado para cambiar el idioma en un objeto Usuario creado por session en Web App
	 * @return Retorno de la respuesta JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
	 */
	public JSONObject changeLanguage() throws IOException, ServletException {
		String language = new PartToString().getStringValue(request
				.getPart("language"));
		HttpSession userSession = (HttpSession) request.getSession();
		Usuario usuario = (Usuario) userSession.getAttribute("userActive");
		usuario.setLanguage(language);
		return new JSONObject().put("status", "ok");
	}
}
