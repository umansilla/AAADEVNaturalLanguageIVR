package service.AAADEVNaturalLanguageIVR.IBM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.BuscarYRemplazarAcentos;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;


/**
 * Al método ingresa el téxto que se desea analizar (solo en ingles), y regresa un arreglo de emociones cada una con un 
 * rango en porcentaje. 
 * Se usa la variable statica myBeanObj_NLU para inicializar las credenciales necesarias. para inicializar estas credenciales
 * se deben de enviar un POST con las informacion al servlet NLU en formato json
 * @author umansilla
 */
public class NaturalLanguageUnderstanding {
	
	private final Call call;
	private static final Logger logger = Logger.getLogger(NaturalLanguageUnderstanding.class);
	/**
	 * Constructor NaturalLanguageUnderstanding
	 * @param call Objeto Call del SDK de Avaya Breeze.
	 * @param usuario Objeto Ususario Bean.
	 */
	public NaturalLanguageUnderstanding(Call call, Usuario usuario) {
		super();
		this.call = call;
	}

	/**
	 * Metodo creado para realizar peticion a Natural Language Understanding de IBM Cloud.
	 * @param text Texto que se quiere analizar con Natural Language Understanding de IBM Cloud.
	 * @return Retorna la respuesta de NLU como objeteo JSON.
	 */
	public JSONObject request(String text){
		try{	
		if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")){
			//Model Languaje Translator es-en
			BuscarYRemplazarAcentos espanol = new BuscarYRemplazarAcentos();
			text = espanol.Espanol(text);
			text = Languaje_Translator.languageTranslate(text, "es-en", call);
			logger.info("Texto traducido: " + text);
			
		}
		if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")){
			//Model Languaje Translator pt-en
			
			BuscarYRemplazarAcentos portugues = new BuscarYRemplazarAcentos();
			text = portugues.Portugues(text);
			text = Languaje_Translator.languageTranslate(text, "pt-en", call);
			logger.info("Texto traducido: " + text);
		}
			/*
			 * HTTPS
			 */
			String user = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IBM_NATURAL_LANGUAGE_USER_NAME);
			String password = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IBM_NATURAL_LANGUAGE_PASSWORD);
		      final SSLProtocolType protocolType = SSLProtocolType.TLSv1_2;
		      final SSLContext sslContext = SSLUtilityFactory.createSSLContext(protocolType);
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(user, password));
			String encodedMessage = URLEncoder.encode(text, "UTF-8");
			final String URI = "https://gateway.watsonplatform.net/natural-language-understanding/api/v1/analyze?version=2018-11-16&text="
					+encodedMessage+"&features=emotion&return_analyzed_text=false&clean=true&fallback_to_raw=true&concepts.limit=8&emotion.document=true&entities.limit=200&keywords.limit=200&sentiment.document=true";
			
			final HttpClient client = HttpClients.custom()
					.setSSLContext(sslContext)
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			
		      HttpGet getMethod = new HttpGet(URI);
		      getMethod.addHeader("Accept", "application/json");
		      getMethod.addHeader("Content-Type", "application/json");

			final String authString = user + ":" + password;
			final String authEncBytes = DatatypeConverter
					.printBase64Binary(authString.getBytes());
			getMethod.addHeader("Authorization", "Basic " + authEncBytes);


			final HttpResponse response = client.execute(getMethod);

			final BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStream.readLine()) != null) {
				result.append(line);
			}
			inputStream.close();
			logger.info("Respuesta Natural Language Understanding: " + result.toString());
			if(new JSONObject(result.toString()).has("error")){
				logger.error("Error en peticion NLU no hay suficiente texto para detectar emociones " + result.toString() );
				return returnErrorRequest();
			}
			return new JSONObject(result.toString());
		}catch(NoAttributeFoundException | IOException | ServiceNotFoundException | SSLUtilityException  e){
			logger.error("Error en NaturalLanguageUnderstanding " + e);
			new MyEmailSender().sendErrorByEmail("Error en NaturalLanguageUnderstanding " + e, call);
			return returnErrorRequest();
		}
	}
	
	public static JSONObject returnErrorRequest(){
		return new JSONObject("{\n" +
				"  \"usage\": {\n" +
				"    \"text_units\": 1,\n" +
				"    \"text_characters\": 22,\n" +
				"    \"features\": 1\n" +
				"  },\n" +
				"  \"language\": \"en\",\n" +
				"  \"emotion\": {\n" +
				"    \"document\": {\n" +
				"      \"emotion\": {\n" +
				"        \"sadness\": 0.0,\n" +
				"        \"joy\": 0.0,\n" +
				"        \"fear\": 0.0,\n" +
				"        \"disgust\": 0.05,\n" +
				"        \"anger\": 0.0\n" +
				"      }\n" +
				"    }\n" +
				"  }\n" +
				"}");
	}
}