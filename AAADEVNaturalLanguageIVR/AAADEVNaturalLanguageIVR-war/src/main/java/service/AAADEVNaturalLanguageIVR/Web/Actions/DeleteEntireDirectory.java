package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
/**
 * Clase creada para borrar un directorio entero.
 * @author umansilla
 */
public class DeleteEntireDirectory {
	private final Logger logger = Logger.getLogger(getClass());
    final private HttpServletRequest request;
    private String folder;
    /**
     * Contructor DeleteEntireDirectory
     * @param request Objeto HttpServletRequest
     */
    public DeleteEntireDirectory(HttpServletRequest request) {
        this.request = request;
    }
    
    /**
     * Metodo creado para borrar un directorio entero
     * @return Respuseta el FrontEnd en formato JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject enterDeleteEntireDirectory() throws IOException, ServletException {
        folder = new PartToString().getStringValue(request.getPart("folderName"));
        File directoryToDelete = new File(Constants.PATH_TO_AUDIOS + folder);
        if (directoryToDelete.exists()) {
            Boolean statusDelete = deleteDirectory(directoryToDelete);
            if (statusDelete) {
                return new JSONObject().put("status", "ok").put("deletedDirectory", folder);
            } else {
                logger.error("DeleteEntireDirectory Error No se ha podido borrar el Directorio");
                return new JSONObject().put("status", "error");
            }
        } else {
        	logger.error("DeleteEntireDirectory Error El folder no existe");
            return new JSONObject().put("status", "error");
        }
    }
    /**
     * Metodo creado para borrar un archivo en especifico y validar si ha sido borrado correctamente.
     * @param dir Archivo que se debera de borrar.
     * @return Valor en boleano para saber si ha sido borrado o no.
     */
    private static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
