package service.AAADEVNaturalLanguageIVR.Web.Actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 * Clase creada para repetir el tiempo de la sesion
 * @author umansilla
 */
public class RestartTimeSession {
    private final HttpServletRequest request;
    /**
     * Constructor RestartTimeSession
     * @param request Objeto RestartTimeSession
     */
    public RestartTimeSession(HttpServletRequest request) {
        this.request = request;
    }
    /**
     * Metodo creado para repetir el tiempo de sesion.
     * @return Respuesta al FrontEnd en formato JSON.
     */
    public JSONObject restartTime (){
        HttpSession userSession = (HttpSession) request.getSession();
        userSession.setMaxInactiveInterval(15 * 60);
        return new JSONObject().put("status", "ok");
    }
}
