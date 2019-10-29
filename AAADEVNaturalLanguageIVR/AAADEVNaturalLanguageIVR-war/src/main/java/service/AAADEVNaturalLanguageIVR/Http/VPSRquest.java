package service.AAADEVNaturalLanguageIVR.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Security.AES;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para realizar conversion Voz a texto por medio de VPS
 * @author umansilla
 *
 */
public class VPSRquest {

	private final Call call;

	/**
	 * Constructor VPSRquest
	 * @param call Objeto Call del SDK Avaya Breeze.
	 */
	public VPSRquest(Call call) {
		super();
		this.call = call;
	}

	private transient final Logger logger = Logger.getLogger(getClass());

	/**
	 * Metodo que realiza peticion HTTP a la VPS para conversion de Voz a Texto
	 * @return Retorna el estatus de la peticion junto con el texto.
	 * @throws ClientProtocolException Error que se lanza al existir un error en el Protocolo de la peticion http.
	 * @throws IOException Error que se lanza al no existir contenido en la respuesta de la peticion http.
	 */
	public String[] vpsPOST() throws ClientProtocolException, IOException {
		String idioma = null;
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			logger.info("Se definio el idioma Español");
			idioma = "es-MX";

		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			logger.info("Se definio el idioma Ingles");
			idioma = "en-US";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			logger.info("Se definio el idioma Portugues");
			idioma = "pt-BR";

		}
		
		String[] exitCodes = { null, null };
		

		final String URI = "http://"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.VPS_FQDN)+"/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/Transcript?apikey="
				+ AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_SPEECH_TO_TEXT)
				+ "&idioma=" + idioma 
				+ "&audio=recordingAAADEVNaturalLanguageIVR.wav&breeze=breeze2-132.collaboratory.avaya.com&service=AAADEVNaturalLanguageIVR";

		final HttpClient clientSpeech = HttpClients.createDefault();

		final HttpPost postMethodSpeech = new HttpPost(URI);

		postMethodSpeech.addHeader("Accept", "application/json; charset=UTF-8");
		postMethodSpeech.addHeader("Content-Type", "application/json; charset=UTF-8");

		final String messageBodySpeech = "";
		final StringEntity conversationEntitySpeech = new StringEntity(
				messageBodySpeech);
		postMethodSpeech.setEntity(conversationEntitySpeech);

		final HttpResponse responseSpeech = clientSpeech
				.execute(postMethodSpeech);

		final BufferedReader inputStreamSpeech = new BufferedReader(new InputStreamReader(responseSpeech.getEntity().getContent(), StandardCharsets.ISO_8859_1));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStreamSpeech.readLine()) != null) {
			result.append(line);
		}

		JSONObject json = new JSONObject(result.toString());

		String transcript = json.getString("results");
		JSONArray array = new JSONArray(transcript);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			// exitCode = object.get("alternatives").toString();
			String alternatives = object.getString("alternatives");
			JSONArray array2 = new JSONArray(alternatives);
			for (int j = 0; j < array2.length(); j++) {
				JSONObject object2 = array2.getJSONObject(i);
				exitCodes[0] = object2.get("transcript").toString();
				exitCodes[1] = object2.get("confidence").toString();
			}

		}

		inputStreamSpeech.close();
		postMethodSpeech.reset();

		return exitCodes;

	}
	
	/**
	 * Metodo que se usa para realizar conversion de Voz a texto por medio de la VPS
	 * @param text Texto que se usara para convertir a voz.
	 * @param voice Idioma que se usara para realizar la conversion de voz a texto.
	 * @param voiceName Tipo de voz que se usara para conversion de voz a texto.
	 * @return Retorna el resultado de la peticion HTTP a la VPS.
	 * @throws ClientProtocolException Error que se lanza al existir error en el protocolo de la peticion http.
	 * @throws IOException Error que se lanza al no existir contenido en la respuesta http.
	 * @throws NoAttributeFoundException Error que se lanza al no existir atributo en el SMGR. 
	 * @throws ServiceNotFoundException Error que se lanza al no existir servicio en el SMGR.
	 */
	 public String makeVPSRequest(String text, String voice, String voiceName) throws ClientProtocolException, IOException, NoAttributeFoundException, ServiceNotFoundException{
		 	final HttpClient client = HttpClients.createDefault();
	    	HttpPost postMethod = new HttpPost("http://"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.VPS_FQDN)+"/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/TTS");
	    	MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();  
	    	String apiKey = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_SPEECH_TO_TEXT);
	    	AES aes = new AES();
	    	StringBody apiKeyBody = new StringBody(aes.encrypt(apiKey), ContentType.TEXT_PLAIN);
	    	StringBody textBody = new StringBody(aes.encrypt(text), ContentType.TEXT_PLAIN);
	    	StringBody voiceBody = new StringBody(aes.encrypt(voice), ContentType.TEXT_PLAIN);
	    	StringBody voiceNameBody = new StringBody(aes.encrypt(voiceName), ContentType.TEXT_PLAIN);
	    	
	    	reqEntity.addPart("apiKey", apiKeyBody);
	    	reqEntity.addPart("text", textBody);
	    	reqEntity.addPart("voice", voiceBody);
	    	reqEntity.addPart("voiceName", voiceNameBody);
	    	HttpEntity entity = reqEntity.build();
	    	
	    	postMethod.setEntity(entity);
	    	
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


}