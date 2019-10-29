package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import service.AAADEVNaturalLanguageIVR.Security.XSSPrevent;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;

/**
 * Clase creada para modificar el nombre de un directorio.
 * @author umansilla
 */
public class ModifyDirectorieName {

    final private HttpServletRequest request;
    private String folder;
    private String newNameFolder;
    private final Logger logger = Logger.getLogger(getClass());
    /**
     * Constructor ModifyDirectorieName
     * @param request Objeto HttpServletRequest
     */
    public ModifyDirectorieName(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Metodo creado para modificar el nombre de un directorio en especifico.
     * @return Respuesta al FrontEnd en formato JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject enterModifyDirectorieName() throws IOException, ServletException {
        folder = new PartToString().getStringValue(request.getPart("folderName"));
        newNameFolder = new PartToString().getStringValue(request.getPart("newNameFolder"));
        newNameFolder = XSSPrevent.stripXSS(newNameFolder);
        File dir = new File(Constants.PATH_TO_AUDIOS +  File.separator + folder);
        if (!dir.isDirectory()) {
            logger.error("ModifyDirectorieName No existe la ruta proporcionada " + dir.getAbsolutePath());
            return new JSONObject().put("status", "error");
        } else {
                File newDir = new File(Constants.PATH_TO_AUDIOS +  File.separator + newNameFolder);
                dir.renameTo(newDir);
            return new JSONObject().put("status", "ok").put("newNameFolder", newNameFolder);
        }
    }
}
