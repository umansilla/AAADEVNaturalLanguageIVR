package service.AAADEVNaturalLanguageIVR.Web.Actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 * Clase creada para cerrar la session de un usuario.
 * @author umansilla
 */
public class CloseSession {
    private final HttpServletRequest request;
    
    /**
     * Constructor CloseSession
     * @param request Objeto HttpServletRequest
     */
    public CloseSession(HttpServletRequest request) {
        this.request = request;
    }
    
    /**
     * Metodo creado para cerrar la session de un usuario.
     * @return Respuesta a Web App en JSON.
     */
    public JSONObject closeSession(){
        
         HttpSession userSession = (HttpSession) request.getSession();
         userSession.removeAttribute("userActive");
         userSession.removeAttribute("Registros");
         userSession.removeAttribute("Usuario");
         
         return new JSONObject().put("status", "ok");
    }
}
