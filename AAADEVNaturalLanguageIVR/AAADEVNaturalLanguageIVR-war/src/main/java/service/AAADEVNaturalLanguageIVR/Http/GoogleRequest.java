package service.AAADEVNaturalLanguageIVR.Http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.Encoder;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;

/**
 * Clase que obtiene los metodos para consumir la API de GCP
 * @author umansilla
 *
 */
public class GoogleRequest {

	private final Call call;
	
	/**
	 * Constructor.
	 * @param call Objeto Call del SDK de Avaya Breeze.
	 */
	public GoogleRequest(Call call) {
		super();
		this.call = call;
	}

	/**
	 * Metodo creado para consumir la API de Google Text To Speech.
	 * @param text Texto que se envia para convertir a voz.
	 * @param voice	El idioma que se usuara para la conversion de texto a voz..
	 * @param voiceName voz que se usuara para la conversion de texto a voz.
	 * @return Se obtiene la respuesta en formato json, donde se encuentra el contenido del audio en base64.
	 * @throws SSLUtilityException Se lanza el siguiente error cuando no se puede realizar la peticion por HTTPS.
	 * @throws ClientProtocolException Error que se lanza al haber un error en la peticion HTTPs.
	 * @throws IOException Error que se lanza cuando no existe contenido en la respuesta HTTPS.
	 * @throws NoAttributeFoundException El siguiente error se lanza cuando no se encuentra un attributo en SMGR.
	 * @throws ServiceNotFoundException El siguiente error se lanza cuando no se encuentra el servicio en SMGR.
	 */
	public String googleRequestTTS(String text, String voice, String voiceName)
			throws SSLUtilityException, ClientProtocolException, IOException,
			NoAttributeFoundException, ServiceNotFoundException {

		final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
		final SSLContext sslContextAssistant = SSLUtilityFactory
				.createSSLContext(protocolTypeAssistant);

		String apiKey = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_SPEECH_TO_TEXT);
		final String URI = "https://texttospeech.googleapis.com/v1/text:synthesize?key="
				+ apiKey;

		final HttpClient client = HttpClients.custom()
				.setSSLContext(sslContextAssistant)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		final HttpPost postMethod = new HttpPost(URI);
		postMethod.addHeader("Content-Type", "application/json");

		final StringEntity ttsEntity = new StringEntity(
				createJsonPayLoadRequest(text, voice, voiceName));
		postMethod.setEntity(ttsEntity);
		final HttpResponse response = client.execute(postMethod);

		final BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStream.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}

	/**
	 * Metodo creado para consumir el API de GCP y realizar la transcipcion de un audio. Speech To Text.
	 * @return Regresa el texto transcrito por GCP.
	 * @throws SSLUtilityException Se lanza el siguiente error cuando no se puede realizar la peticion por HTTPS.
	 * @throws ClientProtocolException Error que se lanza al haber un error en la peticion HTTPs.
	 * @throws IOException Error que se lanza cuando no existe contenido en la respuesta HTTPS.
	 */
	public String googleRequestSTT() throws SSLUtilityException, ClientProtocolException, IOException {
		String voice = null;
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			voice = "es-MX";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			voice = "en-US";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			voice = "pt-BR";
		}

		final SSLProtocolType protocolType = SSLProtocolType.TLSv1_2;
		final SSLContext sslContext = SSLUtilityFactory
				.createSSLContext(protocolType);

		String apiKey = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_SPEECH_TO_TEXT);
		final String URI = "https://speech.googleapis.com/v1/speech:recognize?key"+ apiKey;

		final HttpClient client = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		final HttpPost postMethod = new HttpPost(URI);
		postMethod.addHeader("Content-Type", "application/json");
		final String filename = "recordingAAADEVBancoIVR.wav";
		
		String realPath = getApplcatonPath();
   		String [] split = realPath.split("/");
   		StringBuilder path = new StringBuilder();
   	       for(int k = 1 ; k < split.length - 1; k++){
   	    	   path.append("/");
   	    	   path.append(split[k]);
   	       }	
   	    String base64 = null;
		final File audioFile = new File(path.toString(), filename);
			if (audioFile.exists()) {
				base64 = Encoder.encoder(audioFile.getAbsolutePath());
			} else {
				throw new IOException("EL archivo de audio recordingAAADEVBancoIVR.wav No Existe");
			}
			
		if(base64 == null || base64.isEmpty()){
			throw new NullPointerException("Base 64 es = null");
		}
		
		final StringEntity ttsEntity = new StringEntity(
				createJsonPayLoadRequest(voice, base64));
		postMethod.setEntity(ttsEntity);
		final HttpResponse response = client.execute(postMethod);

		final BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStream.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
	
	/**
	 * Pay load que se envia en el body de la peticion HTTPS para realizar la transcripcion de un Audio.
	 * @param voice Se envia el idioma de la voz para transcripcion.
	 * @param base64Content contenido del audio en base64.
	 * @return Regresa el objeto JSON como String.
	 */
	private String createJsonPayLoadRequest(String voice, String base64Content) {
		JSONObject json = new JSONObject();

		json.put("audio", new JSONObject().put("content", base64Content.trim()));

		json.put(
				"config",
				new JSONObject().put("sampleRateHertz", 8000)
						.put("encoding", "LINEAR16")
						.put("audioChannelCount", 1).put("languageCode", voice));

		return json.toString();
	}
	
	/**
	 * Pay load que se envia en el body de la peticion HTTPS para realizar la conversion de texto a voz.
	 * @param text Texto que se usuara para convertir de texto a voz.
	 * @param voice El idioma que se usara para la conversion de texto a voz.
	 * @param voiceName El tipo de voz que se usara para la conversion de texto a voz.
	 * @return Retorna un objeto JSON en formato String.
	 */
	private String createJsonPayLoadRequest(String text, String voice,
			String voiceName) {
		JSONObject json = new JSONObject();

		JSONObject jsonAudioConfig = new JSONObject()
				.put("audioEncoding", "LINEAR16")
				.put("sampleRateHertz", 8000)
				.put("effectsProfileId",
						new JSONArray().put("telephony-class-application"));
		JSONObject jsonInput = new JSONObject().put("text", text);
		JSONObject jsonVoice = new JSONObject().put("languageCode", voice).put(
				"name", voiceName);

		json.put("audioConfig", jsonAudioConfig);
		json.put("input", jsonInput);
		json.put("voice", jsonVoice);

		return json.toString();
	}
	
	/**
	 * Metodo creado para obtener la ruta de almacenamiento.
	 * @return Ruta donde se almacena el audio para la conversion voz a texto.
	 */
	public static String getApplcatonPath(){
        CodeSource codeSource = GoogleRequest.class.getProtectionDomain().getCodeSource();
        File rootPath = null;
        try {
            rootPath = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
          return e.toString();
        }           
        return rootPath.getParentFile().getPath();
    }//end of getApplcatonPath()
}
