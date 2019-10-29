package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
import service.AAADEVNaturalLanguageIVR.Util.ZipUtils;

/**
 * Clase creada para crear un archivo .zip desde Web App
 * @author umansilla
 */
public class CreateZip {
	private final Logger logger = Logger.getLogger(getClass());
    final private HttpServletRequest request;
    private String folder;
    /**
     * Constructor CreateZip
     * @param request
     */
    public CreateZip(HttpServletRequest request) {
        this.request = request;
    }
    /**
     * Metodo creado para crear un archivo .zip de acuerdo a la peticion desde Web App.
     * @return Respuesta al Web App en JSON.
     */ 
    public JSONObject enterCreateZip() {
        try {
            folder = new PartToString().getStringValue(request.getPart("folderName"));
            ZipUtils zipDirectory = new ZipUtils(Constants.PATH_TO_AUDIOS + folder);
            zipDirectory.generateFileList(new File(Constants.PATH_TO_AUDIOS + folder));
            Boolean zipStatus = zipDirectory.zipIt(Constants.PATH_TO_WEB_APP + "/" + folder + ".zip");
            if (zipStatus) {
                return new JSONObject().put("status", "ok").put("zipFolder", folder + ".zip");
            } else {
                logger.error("CreateZip zipStatus = False");
                return new JSONObject().put("status", "error");
            }
        } catch (IOException | ServletException | JSONException e) {
            logger.error("CreateZip Error enterCreateZip: " + e.toString());
            return new JSONObject().put("status", "error");
        }
    }
}
