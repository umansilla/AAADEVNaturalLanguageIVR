package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
/**
 * Clase creada para modificar la imagen de un diretorio.
 * @author umansilla
 */
public class ModifyImage {
	private final Logger logger = Logger.getLogger(getClass());
    final private HttpServletRequest request;
    private String folder;
    private String imageName;
    /**
     * Constructor ModifyImage
     * @param request Objeto HttpServletRequest
     */
    public ModifyImage(HttpServletRequest request) {
        this.request = request;
    }
    /**
     * Metodo creado para modificar la imagen de un archivo.
     * @return Respuesta al FrontEnd en Formato JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject enterModifyImage() throws IOException, ServletException {
        folder = new PartToString().getStringValue(request.getPart("folderName"));
        imageName = new PartToString().getStringValue(request.getPart("imageName"));
        final Part imagePartOfFile = request.getPart("fileImageBin");
        if (isDirEmpty(Paths.get(Constants.PATH_TO_AUDIOS + folder + "/IMG/"))) {
        	logger.error("ModifyImage Directorio Vacio");
        } else {
            String fileName = getImageFileName(Constants.PATH_TO_AUDIOS + folder + "/IMG/");
            File fileToDelete = new File(Constants.PATH_TO_AUDIOS + folder + "/IMG/" + fileName);
            if (fileToDelete.exists() && fileToDelete.delete()) {
            } else {
            	logger.error("ModifyImage Error al querer Borrar la imagen pasada: " + fileName);
                return new JSONObject().put("status", "error");
            }

        }
        File f = new File(Constants.PATH_TO_AUDIOS + folder + "/IMG/" + imageName);
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        //VALIDAMOS QUE EL ARCHIVO SEA DE IMAGEN, ANTES DE ESCRIBIR
        if (type.equals("image")) {
            //WRITE FILE CON BASE 64 DECODER.
            byte[] imageInBase64 = Base64.getMimeDecoder().decode(new PartToString().getStringValue(imagePartOfFile).trim().split(",")[1]);
            try (OutputStream stream = new FileOutputStream(Constants.PATH_TO_AUDIOS + folder + "/IMG/" + imageName)) {
                stream.write(imageInBase64);
                return new JSONObject().put("status", "ok").put("imageName", imageName);
            } catch (Exception e) {
            	logger.error("ModifyImage No se ha podido escribir en el archivo " + e.toString());
                return new JSONObject().put("status", "error");
            }
        } else {
        	logger.error("ModifyImage El archivo no es un archivo de de imagen");
            return new JSONObject().put("status", "error");
        }

    }
    
    /**
     * Metodo creado para validar si existe un directorio.
     * @param directory Ruta del directorio.
     * @return Valor en boolean.
	 * @throws IOException Se lanza error al no existir contenido.
     */
    private static boolean isDirEmpty(final Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
    /**
     * Metodo para obtener el nombre de la imagen en un directorio.
     * @param path Ruta de almacenamiento del deirectorio.
     * @return NOmbre de la imagen.
	 * @throws IOException Se lanza error al no existir contenido.
     */
    private static String getImageFileName(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.list(new File(path).toPath())
                .forEach(imageFile -> {
                    sb.append(imageFile.getFileName().toString());
                });

        return sb.toString();
    }

}
