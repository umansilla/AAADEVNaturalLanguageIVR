/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Servlets;

import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
import service.AAADEVNaturalLanguageIVR.Web.Controllers.VerbioClient;


/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "SaveAudio", urlPatterns = {"/SaveAudio"})
public class SaveAudio extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject json = new JSONObject();
        PrintWriter out = response.getWriter();
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        final Part base64 = request.getPart("base64wav");
        HttpSession session = request.getSession(true);
        Usuario user = (Usuario) session.getAttribute("userActive");

        VerbioClient verbioAddFile = new VerbioClient();
        JSONObject jsonVerbioResult = null;
		try {
			jsonVerbioResult = verbioAddFile.verbioAddFile(new PartToString().getStringValue(request.getPart("base64wav")), user.getVerbiouser());
		} catch (SSLUtilityException e1) {
			logger.info("SaveAudio Error:" + e1.toString());
		}
        
        final String audioNameString = new PartToString().getStringValue(request.getPart("audioName"));
        final FileOutputStream saveAudioFile = new FileOutputStream("home/wsuser/web/VerbioAudios/" + audioNameString.trim());
        final InputStream audioInput = base64.getInputStream();
        final byte audioBytes[] = new byte[(int) base64.getSize()];
        try {
            while ((audioInput.read(audioBytes)) != -1) {
                InputStream byteAudioStream = new ByteArrayInputStream(
                        decode(audioBytes));
                final AudioFormat audioFormat = getAudioFormat();
                AudioInputStream audioInputStream = new AudioInputStream(
                        byteAudioStream, audioFormat, audioBytes.length);

                if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE,
                        audioInputStream)) {
                    AudioSystem.write(audioInputStream,
                            AudioFileFormat.Type.WAVE, saveAudioFile);
                }

            }

            audioInput.close();
            saveAudioFile.flush();
            saveAudioFile.close();
            json.put("verbio", jsonVerbioResult);
            json.put("status", "ok");
            out.println(json);
        } catch (IOException | MessagingException e) {
            audioInput.close();
            saveAudioFile.flush();
            saveAudioFile.close();
            json.put("status", "error");
            out.println(json);
            logger.info("SaveAudio Error: " + e.toString());
        }

    }

    public static byte[] decode(byte[] encodedAudioBytes)
            throws MessagingException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                encodedAudioBytes);
        InputStream b64InputStream = MimeUtility.decode(byteArrayInputStream,
                "base64");

        byte[] tmpAudioBytes = new byte[encodedAudioBytes.length];
        int numberOfBytes = b64InputStream.read(tmpAudioBytes);
        byte[] decodedAudioBytes = new byte[numberOfBytes];

        System.arraycopy(tmpAudioBytes, 0, decodedAudioBytes, 0, numberOfBytes);

        return decodedAudioBytes;
    }


    /**
     * ***************************************************************************
     * return application path
     *
     * @return
     * ***************************************************************************
     */
    public static String getApplcatonPath() {
        CodeSource codeSource = SaveAudio.class.getProtectionDomain().getCodeSource();
        File rootPath = null;
        try {
            rootPath = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rootPath.getParentFile().getPath();
    }//end of getApplcatonPath()

    private AudioFormat getAudioFormat() {
        final float sampleRate = 48000.0F;
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

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
    }
}
