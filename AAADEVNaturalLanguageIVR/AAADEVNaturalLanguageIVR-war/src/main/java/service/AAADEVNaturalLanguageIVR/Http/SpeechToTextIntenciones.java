package service.AAADEVNaturalLanguageIVR.Http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.IBM.NaturalLanguageUnderstanding;
import service.AAADEVNaturalLanguageIVR.IBM.WatsonAssistant;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.Intenciones;
import service.AAADEVNaturalLanguageIVR.Util.WriteOnDisk;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para realizar las diferentes peticiones a servicios externos.
 * @author umansilla
 *
 */
public class SpeechToTextIntenciones {
	private static final Logger logger = Logger.getLogger(SpeechToTextIntenciones.class);
	private final Call call;
	private final Usuario usuario;
	
	/**
	 * Constructor
	 * @param call Objeto Call del SDK de Avaya Breeze.
	 * @param usuario Objeto Bean Usuario.
	 */
	public SpeechToTextIntenciones(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}
	
	/**
	 * Metodo creado para realizar peticiones a servicios externos.
	 * Speech To Text.
	 * Watson Assistant.
	 * Natural Language Understanding.
	 * Creacion de archivo .wav y archivo .txt para consumo de la web App.
	 * @param participant Participante que ha realizado la llamada.s
	 * @param requestid Request Id al reproducir el audio de Helsinky.wav
	 * @param mediaServicehelsinky Media Service de la reproduccion del audio Herlsnky.wav
	 * @throws ClientProtocolException Error que se lanza al ocurrir un error en una paticion HTTPS.
	 * @throws IOException	Error que se lanza al no existir contenido en una respuesta HTTPS.
	 * @throws NoAttributeFoundException Error se lanza cuando un attributo no se ha encontrado en SMGR.
	 * @throws ServiceNotFoundException Error se lanza cuando no se encuentra un servicio en SMGR.
	 * @throws URISyntaxException Error se lanza cuando el URI esta mal formado.
	 * @throws SSLUtilityException Error se lanza cuando la peticion HTTPS no se ha podido realizar.
	 * @throws NoUserFoundException Error se lanza cuando no existe el usuario.
	 * @throws NoServiceProfileFoundException Error que se lanza cuando no se encuentra service profile en SMGR.
	 */
	public void peticionesExternas(Participant participant, UUID requestid, MediaService mediaServicehelsinky) throws ClientProtocolException, IOException, NoAttributeFoundException, ServiceNotFoundException, URISyntaxException, SSLUtilityException, NoUserFoundException, NoServiceProfileFoundException
			 {
		/*
		 * Petición a Google Cloud
		 */
		
		if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.ROUTE_EXECUTION).equals("VPS")){
			VPSRquest vps = new VPSRquest(call);
			String[] arregloGoogle = vps.vpsPOST();
			usuario.setTranscript(arregloGoogle[0]);
			usuario.setConfianzaTranscript(arregloGoogle[1]);
		}
		if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.ROUTE_EXECUTION).equals("Breeze")){
			GoogleRequest request = new GoogleRequest(call);
			String responseGoogle = request.googleRequestSTT();
	        JSONObject jsonResponse = new JSONObject(responseGoogle);
	        
	        JSONArray jsonArray =  jsonResponse.getJSONArray("results");
	        JSONObject jsonObject = jsonArray.getJSONObject(0);
	        JSONArray jsonArrayAlternatives = jsonObject.getJSONArray("alternatives");
	        String transcript = jsonArrayAlternatives.getJSONObject(0).getString("transcript");
	        float confidenceFloat = jsonArrayAlternatives.getJSONObject(0).getFloat("confidence");
	        
	        usuario.setTranscript(transcript);
	        usuario.setConfianzaTranscript(Float.toString(confidenceFloat));
		}
		logger.info("Texto : " + usuario.getTranscript());
		logger.info("Confidence " + usuario.getConfianzaTranscript());


		/*
		 * Petición Watson Assistant
		 */

		logger.info("Petición Watson Assitant, Intenciones");
		WatsonAssistant watsoAssistant = new WatsonAssistant(call, usuario);
		JSONObject jsonResponseWatsonAssitant = watsoAssistant.request(usuario.getTranscript());
		
		usuario.setIntent(jsonResponseWatsonAssitant.getString("Intent"));
		usuario.setEntity(jsonResponseWatsonAssitant.getString("Entity"));
		
		logger.info("Petición Natural Language Understanding, Emociones");
		NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding(call, usuario);
		usuario.setJsonToWrite(naturalLanguageUnderstanding.request(usuario.getTranscript()));
		
		logger.info("Creación de archivos wav y txt");
		WriteOnDisk write = new WriteOnDisk(call, usuario);
		write.createFiles();
		
		Intenciones intentOut = new Intenciones(call, usuario);
		intentOut.definirIntencion();

		/*
		 * Detener musica en epera
		 */
		mediaServicehelsinky.stop(participant, requestid);

		final MyEmailSender myEmailSender = new MyEmailSender();
		Date now = new Date(System.currentTimeMillis());
		String horaFecha = (now.toString().replaceAll("[^\\dA-Za-z]", ""));
		StringBuilder emailBody = new StringBuilder();
		emailBody.append("Número de origen: " + call.getCallingParty().getHandle()
				+ "\n Hora y fecha: " + horaFecha + "\n Transcripción: "
				+ usuario.getTranscript() + "\n Intención: " + usuario.getIntent());
		myEmailSender.sendEmail(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL) , "AAADEVNaturalLanguageIVR", emailBody.toString(), call);

	}
}
