package service.AAADEVNaturalLanguageIVR.TTSAnnouncements.Google;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GoogleRequest;
import service.AAADEVNaturalLanguageIVR.Http.VPSRquest;
import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerTTSAnnouncement;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para realizar audio de saldo actual del usuario usando Google Cloud Plataform.
 * @author umansilla
 *
 */
public class TTSResponseSaldoGoogle {
	private final Call call;
	private final Usuario usuario;
	private static final Logger logger = Logger.getLogger(TTSResponseSaldoGoogle.class);
	/**
	 * Constructor TTSResponseSaldoGoogle
	 * @param call Objeto Call del SDK de Avaya Breeze.
	 * @param usuario Objeto Usuario Bean.
	 */
	public TTSResponseSaldoGoogle(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}
	
	/**
	 * Metodo creado para realizar el texto con los datos de la cuenta del usuario y crear el archivo de audio.
	 * @throws ClientProtocolException Error se lanza al existir error en el protocolo del cliente en una peticion HTTP.
	 * @throws SSLUtilityException Error se lanza al existir un error relacionado con los certificados en una peticion HTTPS.
	 * @throws IOException Error se lanza al no existir contenido en la respuuesta de una peticion HTTPS.
	 * @throws URISyntaxException Error se lanza al crear una ruta HTTP incorrecta o inexistentes.
	 * @throws NoAttributeFoundException Error se lanza al no existir un atributo en SMGR.
	 * @throws ServiceNotFoundException Error se lanza al no existir un servicio en SMGR.
	 */
	public void userIdentifyResponseSaldo() throws ClientProtocolException, SSLUtilityException, IOException, URISyntaxException {
		StringBuilder sb = new StringBuilder();
		String text = null;
		String voice = null;
		String voiceName = null;
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			sb.append("Su saldo actual es de $" + usuario.getSaldo() + ". Favor de indicar en que más podemos apoyar y al finalizar presione #");
			text = sb.toString();
			voice = "es-ES";
			voiceName = "es-ES-Standard-A";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			sb.append("Seu saldo atual é $ " + usuario.getSaldo() + ". Por favor, indique o que mais podemos apoiar e no final pressione #");
			text = sb.toString();
			voice = "pt-BR";
			voiceName = "pt-BR-Standard-A";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			sb.append("Your current balance is $" + usuario.getSaldo() + ". Please indicate what else we can support and at the end press #");
			text = sb.toString();
			voice = "en-US";
			voiceName = "en-US-Wavenet-C";
		}
		try{
			String responseGoogle = null;
			if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.ROUTE_EXECUTION).equals("VPS")){
				VPSRquest vps = new VPSRquest(call);
				responseGoogle = vps.makeVPSRequest(text, voice, voiceName);
			}
			if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.ROUTE_EXECUTION).equals("Breeze")){
				GoogleRequest make = new GoogleRequest(call);
				responseGoogle = make.googleRequestTTS(text, voice, voiceName);
			}
			if(responseGoogle == null || responseGoogle.isEmpty()){
				throw new Exception("responseGoogle es Igual a Null");
			}else{
				makeAudioFile(responseGoogle);
			}
			
			playTTSResponseSaldo();

		}catch(Exception e){
			PlayError play = new PlayError(call);
			play.audioError();
			logger.error("Error TTSResponse: " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error TTSResponse: " + e.toString(), call);
		}

		

	}
	/**
	 * Metodo creado para configurar y reproducir el archivo de audio creado por Text To Speech.
	 * @throws URISyntaxException Error se lanza al crear una ruta HTTP incorrecta o inexistentes.
	 */
	private void playTTSResponseSaldo() throws URISyntaxException{
		/*
		 * Determina la URL del servicio
		 */
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();
		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append("TTSResponseSaldo.wav");
		PlayItem playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());
		final MediaService mediaService = MediaFactory.createMediaService();
		final MediaListenerTTSAnnouncement mediaListenerTTSAnnouncement = new MediaListenerTTSAnnouncement(call, usuario);
		mediaService.play(call.getCallingParty(), playItem, mediaListenerTTSAnnouncement);
	}
	
	/**
	 * Metodo creado para realizar el archivo de audio y ubicarlo dentro de Web App.
	 * @param responseGoogle respuesta de GCP en JSON.
	 * @throws MessagingException La clase base para todas las excepciones lanzadas por la clase Messaging
	 * @throws IOException Señala que se ha producido una excepción de E / S de algún tipo. Esta clase es la clase general de excepciones producidas por operaciones de E / S fallidas o interrumpidas.
	 */
	private void makeAudioFile(String responseGoogle) throws MessagingException, IOException{
		JSONObject json = new JSONObject(responseGoogle);
		 if (json.has("audioContent")) {
			 String realPath = getApplcatonPath();
       		String [] split = realPath.split("/");
       		StringBuilder path = new StringBuilder();
       	       for(int k = 1 ; k < split.length - 1; k++){
       	    	   path.append("/");
       	    	   path.append(split[k]);
       	       }
               String base64String = json.getString("audioContent");
               final FileOutputStream saveAudioFile = new FileOutputStream(path.toString() + "/TTSResponseSaldo.wav");
               InputStream audioInput = new ByteArrayInputStream(base64String.getBytes());
               final byte audioBytes[] = base64String.getBytes("UTF-8");

               while ((audioInput.read(audioBytes)) != -1) {
                   InputStream byteAudioStream = new ByteArrayInputStream(decode(audioBytes));
                   final AudioFormat audioFormat = getAudioFormat();
                   AudioInputStream audioInputStream = new AudioInputStream(byteAudioStream, audioFormat, audioBytes.length);

                   if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE,
                           audioInputStream)) {
                       AudioSystem.write(audioInputStream,
                               AudioFileFormat.Type.WAVE, saveAudioFile);
                   }

               }
               audioInput.close();
               saveAudioFile.flush();
               saveAudioFile.close();
		 }else{
			 logger.error(json);
			 throw new NullPointerException("Error NO existe audioContent");
		 }
	}
	
	/**
	 * Metodo para obtener la ruta de almacenamiento de Web App.
	 * @return Ruta del servicio del almacenamiento de Web App.
	 */
	public static String getApplcatonPath(){
        CodeSource codeSource = TTSResponseGoogle.class.getProtectionDomain().getCodeSource();
        File rootPath = null;
        try {
            rootPath = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
          return e.toString();
        }           
        return rootPath.getParentFile().getPath();
    }//end of getApplcatonPath()


	/**
	 * Decodificar el contenido a Base 64
	 * @param encodedAudioBytes Bytes para decodificar
	 * @return Arreglo en bytes.
	 * @throws MessagingException La clase base para todas las excepciones lanzadas por la clase Messaging
	 * @throws IOException Señala que se ha producido una excepción de E / S de algún tipo. Esta clase es la clase general de excepciones producidas por operaciones de E / S fallidas o interrumpidas.
	 */
	public static byte[] decode(byte[] encodedAudioBytes)
			throws MessagingException, IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				encodedAudioBytes);
		InputStream b64InputStream = MimeUtility.decode(byteArrayInputStream, "base64");

		byte[] tmpAudioBytes = new byte[encodedAudioBytes.length];
		int numberOfBytes = b64InputStream.read(tmpAudioBytes);
		byte[] decodedAudioBytes = new byte[numberOfBytes];

		System.arraycopy(tmpAudioBytes, 0, decodedAudioBytes, 0, numberOfBytes);

		return decodedAudioBytes;
	}
	
	/**
	 * Avaya recommends that audio played by Avaya Aura MS be encoded as 16-bit,
	 * 8 kHz, single channel, PCM files. Codecs other than PCM or using higher
	 * sampling rates for higher quality recordings can also be used, however,
	 * with reduced system performance. Multiple channels, like stereo, are not
	 * supported.
	 * @return Objeto AudioFormat
	 */
    private static AudioFormat getAudioFormat() {
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
