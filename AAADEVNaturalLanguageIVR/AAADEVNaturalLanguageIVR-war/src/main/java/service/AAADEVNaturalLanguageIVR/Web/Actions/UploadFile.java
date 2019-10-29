package service.AAADEVNaturalLanguageIVR.Web.Actions;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;

import com.avaya.collaboration.util.logger.Logger;
/**
 * Clase creada para subir un nuevo directorio.
 * @author umansilla
 */
public class UploadFile {
	private final Logger logger = Logger.getLogger(getClass());
    final private HttpServletRequest request;
    private String folder;
    private String language;
    private String fileName;
    /**
     * Constructor UploadFile
     * @param request Objeto HttpServletRequest
     */
    public UploadFile(HttpServletRequest request) {
        this.request = request;
    }
    /**
     * Metodo creado para crear crear el archivo de audio recibido desde el FronEnd en Base64
     * @return Respueta al FrontEnd en formado JSON.
	 * @throws IOException Se lanza error al no existir contenido.
	 * @throws ServletException Define una excepción general que el servlet puede lanzar cuando encuentra dificultades.
     */
    public JSONObject enterUpload() throws IOException, ServletException {
        fileName = new PartToString().getStringValue(request.getPart("File_Name"));
        String folder_Name = new PartToString().getStringValue(request.getPart("File_Folder_Name"));
        String[] arrOfStr = folder_Name.split(",");
        language = arrOfStr[0];
        folder = arrOfStr[1];
        final Part audioPartOfFile = request.getPart("File_bin");
        language = (language.equals("English")) ? ("EN") : (language.equals("Español")) ? ("ES") : (language.equals("Portugues")) ? ("PT") : ("Error");

        final FileOutputStream saveAudioFile = new FileOutputStream(Constants.PATH_TO_AUDIOS + folder + "/" + language + "/" + fileName);

        final InputStream audioInput = audioPartOfFile.getInputStream();
        final byte audioBytes[] = new byte[(int) audioPartOfFile.getSize()];
        try {
            //WRITE FILE CON BASE 64 DECODER.
            while ((audioInput.read(audioBytes)) != -1) {
                InputStream byteAudioStream = new ByteArrayInputStream(
                        Base64.getMimeDecoder().decode(new PartToString().getStringValue(audioPartOfFile).trim().split(",")[1]));
                final AudioFormat audioFormat = getAudioFormat();
                AudioInputStream audioInputStream = new AudioInputStream(byteAudioStream, audioFormat, audioBytes.length);

                if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, audioInputStream)) {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, saveAudioFile);
                }

            }

            audioInput.close();
            saveAudioFile.flush();
            saveAudioFile.close();
        } catch (IOException e) {
            logger.error("UploadFile Error" + e.toString());
            return new JSONObject().put("status", "error");
        }

        return new JSONObject().put("status", "ok").put("language", language).put("fileName", fileName).put("folderName", folder);
    }

    /**
     * Avaya recommends that audio played by Avaya Aura MS be encoded as 16-bit,
	 * 8 kHz, single channel, PCM files. Codecs other than PCM or using higher
	 * sampling rates for higher quality recordings can also be used, however,
	 * with reduced system performance. Multiple channels, like stereo, are not
	 * supported.
	 * 
	 * @see Using Web Services on Avaya Aura Media Server Release 7.7, Issue 1,
	 * August 2015 on support.avaya.com
     * @return Objeto AudioFormat
     */
    private AudioFormat getAudioFormat() {
        final float sampleRate = 8000.0F;
        // 8000,11025,16000,22050,44100
        final int sampleSizeInBits = 16;
        // 8,16
        final int channels = 1;
        // 1,2
        final boolean signed = true;
        // true,false
        final boolean bigEndian = false;
        // true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
                bigEndian);
    }
}
