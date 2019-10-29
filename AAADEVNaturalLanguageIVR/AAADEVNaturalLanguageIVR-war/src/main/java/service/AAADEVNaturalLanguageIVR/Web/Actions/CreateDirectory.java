package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
/**
 * Clase creada para crear un directorio.
 * @author umansilla
 */
public class CreateDirectory {
    final private HttpServletRequest request;
    private String folder;

    /**
     * Constructor CreateDirectory
     * @param request Objeto HttpServletRequest
     */
    public CreateDirectory(HttpServletRequest request) {
        this.request = request;
    }
    
    /**
     * Metodo para crear un directorio desde Web App
     * @return Respuesta en formato JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject enterCreateDirectory() throws IOException, ServletException {
        folder = new PartToString().getStringValue(request.getPart("directoryName"));
        final File file = new File(Constants.PATH_TO_AUDIOS + folder);
        if (file.exists()) {
            return new JSONObject().put("status", "the file already exists");
        } else {
            new File(Constants.PATH_TO_AUDIOS + folder).mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "EN").mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "ES").mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "PT").mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "IMG").mkdirs();
            File copied = new File(Constants.PATH_TO_WEB_APP + "/img/avaya.png");
            File original = new File(Constants.PATH_TO_AUDIOS + folder + "/IMG/" + "avayaFirst.png");
            copy(copied, original);            
            return new JSONObject().put("status", "ok");
        }
        
    }
    
    /**
     * Metodo creado para cambiar un directorio de una fuente a un destino.
     * @param src Fuente del directrio.
     * @param dest Destino del directrio.
	 * @throws IOException Se lanza error al no existir contenido.
     */
    private static void copy(File src, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dest);

            // buffer size 1K
            byte[] buf = new byte[1024];

            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        } finally {
            is.close();
            os.close();
        }
    }
}