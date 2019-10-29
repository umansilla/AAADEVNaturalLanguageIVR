package service.AAADEVNaturalLanguageIVR.TTSAnnouncements.IBM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.net.ssl.SSLContext;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerTTSAnnouncement;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.BuscarYRemplazarAcentos;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para realizar archivo de audio usando Text To Speech con IBM Cloud.
 * @author umansilla
 *
 */
public class TTSResponseSaldoIBM {
	private static int filesize;
	private final Logger logger = Logger.getLogger(getClass());
	private final Call call;
	private final Usuario usuario;

	/**
	 * Constructor TTSResponseSaldoIBM
	 * @param call Call Objeto en SDK del Avaya Breeze.
	 * @param usuario Objeto Usuario Bean.
	 */
	public TTSResponseSaldoIBM(final Call call, final Usuario usuario){
		this.call = call;
		this.usuario = usuario;
	}
	
	/**
	 * Metodo creado para realizar la respuesta TTS con saldos del usuario.
	 */
	public void useridentifiedResponse(){
		StringBuilder sb = new StringBuilder();
		
		String announcement = null;
		String voice = null;
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			sb.append("Su saldo actual es de $" + usuario.getSaldo() + ". Favor de indicar en que más podemos apoyar y al finalizar presione #");
			BuscarYRemplazarAcentos espanol = new BuscarYRemplazarAcentos();
			announcement = espanol.Espanol(sb.toString());
			voice = "es-ES_LauraVoice";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			sb.append("Seu saldo atual é $ " + usuario.getSaldo() + ". Por favor, indique o que mais podemos apoiar e no final pressione #");
			BuscarYRemplazarAcentos portugues = new BuscarYRemplazarAcentos();
			announcement = portugues.Portugues(sb.toString());
			voice = "pt-BR_IsabelaVoice";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			sb.append("Your current balance is $" + usuario.getSaldo() + ". Please indicate what else we can support and at the end press #");
			announcement = sb.toString();
			voice = "en-US_AllisonVoice";
		}
		
		
		try {
			TTSWatsonGenerateAudioFile(announcement, voice, call);
		} catch (SSLUtilityException | IOException | NoAttributeFoundException | ServiceNotFoundException | NoUserFoundException | NoServiceProfileFoundException e) {
			logger.error("TTSWatsonGenerateAudioFile Error: " + e.toString());
			PlayError play = new PlayError(call);
			play.audioError();
			new MyEmailSender().sendErrorByEmail("TTSWatsonGenerateAudioFile Error: " + e.toString(), call);
		}
		
	}
	/**
	 * Metodo creado para generar el archivo de audio con IBM Cloud Text To Speech.
	 * @param announcement Nombre del archivo que se genera.
	 * @param voice Idioma del texto que se desea crear a voz.
	 * @param call Call Objeto del SDK de Avaya Breeze.
	 * @throws SSLUtilityException Se lanza al exisitir un error en los certificados en una peticion HTTPS.
	 * @throws ClientProtocolException Se lanza al exisitir un error en el protocolo del cliente en una peticion HTTPS.
	 * @throws IOException Se lanza el error an lo exisitir contenido en la respuesta de una peticion HTTPS.
	 * @throws NoAttributeFoundException Se lanza el error al no exisitir atributo en SMGR.
	 * @throws ServiceNotFoundException Se lanza el error al no exisitir servicio en SMGR.
	 * @throws NoUserFoundException Se lanza error al no existir user en SMGR.
	 * @throws NoServiceProfileFoundException Se lanza el error an lo existir service profile en SMGR.
	 */
	private void TTSWatsonGenerateAudioFile(String announcement, String voice, Call call) throws SSLUtilityException, ClientProtocolException, IOException, NoAttributeFoundException, ServiceNotFoundException, NoUserFoundException, NoServiceProfileFoundException{
		String user = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IBM_TTS_USER_NAME);
		String password = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IBM_TTS_PASSWORD);
		final SSLProtocolType protocolType = SSLProtocolType.TLSv1_2;
		final SSLContext sslContext = SSLUtilityFactory
				.createSSLContext(protocolType);
		final CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(user, password));
		
		final String URI = "https://stream.watsonplatform.net/text-to-speech/api/v1/synthesize?voice="
				+ voice;
		
		final HttpClient client = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		final HttpPost postTTSpeech = new HttpPost(URI);
		postTTSpeech.addHeader("Accept", "audio/l16;rate=8000");
		postTTSpeech.addHeader("Content-Type", "application/json");

		final String authStringTTSpecch = user + ":" + password;
		final String authEncBytesTTSpeech = DatatypeConverter
				.printBase64Binary(authStringTTSpecch.getBytes());
		postTTSpeech.addHeader("Authorization", "Basic "
				+ authEncBytesTTSpeech);

		final String messageBodyTTSpeech = "{\"text\":\""
				+ announcement + "\"}";
		
		final StringEntity conversationEntityTTSpeech = new StringEntity(
				messageBodyTTSpeech);
		postTTSpeech.setEntity(conversationEntityTTSpeech);

		final HttpResponse responseTTSpeech = client
				.execute(postTTSpeech);

		InputStream in = reWriteWaveHeader(responseTTSpeech.getEntity()
				.getContent());
		
		/*
		 * Determinar el path de almacenamiento
		 */
		String realPath = getApplcatonPath();
		String [] split = realPath.split("/");
		StringBuilder path = new StringBuilder();
	       for(int k = 1 ; k < split.length - 1; k++){
	    	   path.append("/");
	    	   path.append(split[k]);
	       }
		OutputStream out = new FileOutputStream(path.toString() + "/TTSResponseSaldoIBM.wav");
		
		byte[] buffer = new byte[filesize + 8];
		while ((in.read(buffer)) > 0) {

			InputStream byteAudioStream = new ByteArrayInputStream(
					buffer);
			AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1,
					false, false);
			AudioInputStream audioInputStream = new AudioInputStream(
					byteAudioStream, audioFormat, buffer.length);
			if (AudioSystem.isFileTypeSupported(
					AudioFileFormat.Type.WAVE, audioInputStream)) {
				AudioSystem.write(audioInputStream,
						AudioFileFormat.Type.WAVE, out);
			}

		}

		out.close();
		in.close();
		
		
		try {
			playAnnouncement(call);
		} catch (URISyntaxException e) {
			logger.info("Error TTSResponseIBM playAnnouncement " + e.toString());
			PlayError play = new PlayError(call);
			play.audioError();
			new MyEmailSender().sendErrorByEmail("Error TTSResponseIBM playAnnouncement " + e.toString(), call);
		}
	}
	/**
	 * Metodo creado para configurar y reproducir el archivo de audio creado con IBM Cloud.
	 * @param call Objeto Call del SDK de Avaya Breeze.
	 * @throws URISyntaxException Se lanza el error al existir un error en una ruta HTTP.
	 */
	private void playAnnouncement(Call call) throws URISyntaxException{
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
				.append("TTSResponseSaldoIBM.wav");
		/*
		 * PlayWelcome == null reproduce el mensaje de bienvenida
		 * "Bienvenido.wav" es el resultado del stringBuilder
		 */
		PlayItem playItem = MediaFactory
				.createPlayItem()
				.setInterruptible(false)
				.setIterateCount(1).setSource(sb.toString());
		final MediaService mediaService = MediaFactory.createMediaService();
		final MediaListenerTTSAnnouncement mediaListenerTTSAnnouncement = new MediaListenerTTSAnnouncement(call, usuario);
		mediaService.play(call.getCallingParty(), playItem, mediaListenerTTSAnnouncement);
	}
	
	/*
	 * TextToSpeech Methods
	 */
	/**
	 * Metodo creado para reescribir el header del archivo de audio creado por IBM Cloud
	 * @param is Input Stream 
	 * @return InputStream
	 * @throws IOException Erro al no existir el archivo de audio.
	 */
	private static InputStream reWriteWaveHeader(InputStream is)
			throws IOException {
		byte[] audioBytes = toByteArray(is);
		filesize = audioBytes.length - 8;

		writeInt(filesize, audioBytes, 4);
		writeInt(filesize - 8, audioBytes, 74);

		return new ByteArrayInputStream(audioBytes);
	}
	

	private static void writeInt(int value, byte[] array, int offset) {
		for (int i = 0; i < 4; i++) {
			array[offset + i] = (byte) (value >>> (8 * i));
		}
	}

	private static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384]; // 4 kb

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}

	/*
	 * End TextToSpeech Methods
	 */
	/**
	 * Metodo para obtener la ruta de almacenamiento de Web App.
	 * @return Ruta del servicio del almacenamiento de Web App.
	 */
    private static String getApplcatonPath(){
        CodeSource codeSource = TTSResponseIBM.class.getProtectionDomain().getCodeSource();
        File rootPath = null;
        try {
            rootPath = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
          return e.toString();
        }           
        return rootPath.getParentFile().getPath();
    }//end of getApplcatonPath()
}
