package service.AAADEVNaturalLanguageIVR.Verbio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.Google.TTSResponseGoogle;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.IBM.TTSResponseIBM;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.Encoder;
import service.AAADEVNaturalLanguageIVR.Util.RecordingData;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para realizar las peticiones HTTP a Verbio.
 * @author umansilla
 *
 */
public class VerbioClient {
	private transient final Logger logger = Logger
			.getLogger(VerbioClient.class);
	/**
	 * Metodo creado para realizar la peteicion HTTP al servicio de Verbio.
	 * @param call Objeto Call de SDK de Avaya Breeze.
	 * @param usuario Objeto Usuario Bean.
	 * @throws UnsupportedOperationException Se lanza error al no ser soportada la operacion. 
	 * @throws IOException Se lanza error al no exisitir contenido.
	 * @throws SSLUtilityException Se lanza error al no exisiter certificados en una peticion HTTPS.
	 * @throws NoAttributeFoundException Se lanza error al no existir atributo en SMGR.
	 * @throws ServiceNotFoundException Se lanza error al no exisiter servicio en SMGR.
	 * @throws URISyntaxException Se lanza error al no exisiter una ruta HTTP
	 */
	public void verify(Call call, Usuario usuario)
			throws UnsupportedOperationException, IOException,
			SSLUtilityException, NoAttributeFoundException, ServiceNotFoundException, URISyntaxException {
		final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
		final SSLContext sslContextAssistant = SSLUtilityFactory
				.createSSLContext(protocolTypeAssistant);

		final String URI = "https://avaya:DRNUDUsWh5o3uRdQcZ@cloud2.verbio.com/asv/ws/process";

		final HttpClient client = HttpClients.custom()
				.setSSLContext(sslContextAssistant)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		
		final HttpPost postMethodAssistant = new HttpPost(URI);
		postMethodAssistant.addHeader("Content-Type", "application/json");
		String base64 = getBase64();
		final String messageBodyAssistant = "{\n" + "	\"user_data\":\n"
				+ "	{\n" + "		\"filename\":\"" + base64 + "\",\n"
				+ "		\"username\": \"" + usuario.getVerbiouser() + "\",\n"
				+ "		\"action\": \"VERIFY\",\n" + "		\"score\": \"\",\n"
				+ "		\"spoof\": \"0\",\n" + "		\"grammar\": \"\",\n"
				+ "		\"lang\": \"\"\n" + "	}\n" + "}";
		final StringEntity conversationEntityAssistant = new StringEntity(
				messageBodyAssistant);
		postMethodAssistant.setEntity(conversationEntityAssistant);

		final HttpResponse responseAssistant = client
				.execute(postMethodAssistant);

		final BufferedReader inputStreamAssistant = new BufferedReader(
				new InputStreamReader(responseAssistant.getEntity()
						.getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStreamAssistant.readLine()) != null) {
			result.append(line);
		}

		JSONObject json = new JSONObject(result.toString());
		logger.info("VerbioClient response: " + json);

		JSONObject response = json.getJSONObject("response");
		String error = response.getString("error_message");
		String status = response.getString("status");
		if (status.equals("SUCCESS")) {
			logger.info("Verbio Response SUCCESS");
			JSONObject resultVerbio = response.getJSONObject("result");
			JSONObject verbioResult = resultVerbio.getJSONObject("verbio_result");
			String scoreVerbio = verbioResult.getString("score");

			String scoreVoiceRecognition = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.SCORE_VOICE_RECOGNITION);
			// Usuario entrenado
			float scoreNumbre = Float.parseFloat(scoreVerbio);
			float scoreVoiceRecognitionInteger = Float.parseFloat(scoreVoiceRecognition);
			logger.info("Verbio Score: " + scoreNumbre);
			if (scoreNumbre > scoreVoiceRecognitionInteger) {
				// Usuario correcto TTS con el Nombre del Usuario
				logger.info("score > scoreVoiceRecognitionInteger");
					//EL USUARIO HA SIDO VERIFICADO EXITOSAMENTE
					//FAVOR DE INDICAR EN QUE PODEMOS AYUDAR Y AL TERMINAR PRESIONAR #
				if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("Google")){
					TTSResponseGoogle ttsResponse = new TTSResponseGoogle(call, usuario);
					ttsResponse.useridentifiedResponse();
				}
				if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("IBM")){
					TTSResponseIBM ttsResponseIBM = new TTSResponseIBM(call, usuario);
					ttsResponseIBM.useridentifiedResponse();
				}
				
			} else {
				logger.error("Usuario no cumple con score minimo de " + scoreVoiceRecognition);
				//EL USUARIO NO CUMPLE CON EL SCORE MINIMO.
				PlayError noScore = new PlayError(call);
				noScore.audioError();
				new MyEmailSender().sendErrorByEmail("El usuario " + usuario.getVerbiouser() + " No Pasó el Score Mínimo, Score Resultante: " 
							+ scoreVoiceRecognitionInteger , call);
			}

		}else{
			logger.error("Error en la petición a Verbio " + error);
			logger.error(json);
		}

	}
	
	/**
	 * Metodo creado para codificar audio grabado por el usuario en base64
	 * @return Audio codificado en base64
	 */
	public String getBase64() {
		String realPath = getApplcatonPath();
		String[] split = realPath.split("/");
		String base64 = null;
		StringBuilder path = new StringBuilder();
		for (int k = 1; k < split.length - 1; k++) {
			path.append("/");
			path.append(split[k]);
		}
		final String filename = RecordingData.INSTANCE.getRecordingFilename();
		if (filename != null) {
			/*
			 * File(String parent, String child) Creates a new File instance
			 * from a parent pathname string and a child pathname string.
			 */
			final File audioFile = new File(path.toString(), filename);
			if (audioFile.exists()) {
				logger.info(audioFile.getAbsoluteFile());
				base64 = Encoder.encoder(audioFile.getAbsolutePath());
			}
		}
		return base64;
	}

	/**
	 * Metodo creado para obtener la ruta de la aplicacion Web App
	 * @return Ruta de almacenamiento de la Web App
	 */
	public static String getApplcatonPath() {
		CodeSource codeSource = VerbioClient.class.getProtectionDomain()
				.getCodeSource();
		File rootPath = null;
		try {
			rootPath = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return rootPath.getParentFile().getPath();
	}// end of getApplcatonPath()

}
