package service.AAADEVNaturalLanguageIVR.IBM;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;


/**
 * Clase usada para realizar la traduccion de ES o PT a EN
 * @author umansilla
 */
public class Languaje_Translator {

	/**
	 * Metodo creado para realizar la traduccion de ES o PT a EN
	 * @param texto Texto que se desea traducir.
 	 * @param modelId Idioma al que se traducira.
	 * @param call Objeto Call del SDK del Avaya Breeze.
	 * @return Regresa la traduccion en Ingles del texto.
	 * @throws NoAttributeFoundException Error que se lanza al no encontrar Atributo en SMGR.
	 * @throws ServiceNotFoundException Error que se lanza al no encontrar Servicio en SMGR.
	 */
	public static String languageTranslate(String texto, String modelId, Call call) throws NoAttributeFoundException, ServiceNotFoundException {
		/*
		 * HTTPS
		 */
		String exitCode = null;
		String user = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IBM_LANGUAGE_TRANSLATOR_USER_NAME);
		String password = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IBM_LANGUAGE_TRANSLATOR_PASSWORD);
		try {
		      final SSLProtocolType protocolType = SSLProtocolType.TLSv1_2;
		      final SSLContext sslContext = SSLUtilityFactory.createSSLContext(protocolType);
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(user, password));

			final String URI = "https://gateway.watsonplatform.net/language-translator/api/v3/translate?version=2018-05-01";

			final HttpClient client = HttpClients.custom()
					.setSSLContext(sslContext)
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			
			final HttpPost postMethodTraductor = new HttpPost(URI);
			postMethodTraductor.addHeader("Accept", "application/json");
			postMethodTraductor.addHeader("Content-Type", "application/json");

			final String authStringTraductor = user + ":" + password;
			final String authEncBytesTraductor = DatatypeConverter
					.printBase64Binary(authStringTraductor.getBytes());
			postMethodTraductor.addHeader("Authorization", "Basic " + authEncBytesTraductor);

			final String messageBodyTraductor = "{\"text\":[\""+texto+"\"],\"model_id\":\""+modelId+"\"}";
			final StringEntity conversationEntityTraductor = new StringEntity(messageBodyTraductor);
			postMethodTraductor.setEntity(conversationEntityTraductor);

			final HttpResponse responseTraductor = client.execute(postMethodTraductor);

			final BufferedReader inputStreamTraductor = new BufferedReader(
					new InputStreamReader(responseTraductor.getEntity().getContent()));

			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStreamTraductor.readLine()) != null) {
				result.append(line);
			}

			JSONObject json = new JSONObject(result.toString());
			
			String translate = json.getString("translations");
			JSONArray array = new JSONArray(translate);
			for (int i = 0; i < array.length(); i++) {
			    JSONObject object = array.getJSONObject(i);
			    exitCode = object.get("translation").toString();
			}
			
			inputStreamTraductor.close();
			postMethodTraductor.reset();
		} catch (Exception ex) {
			String Error = "Error " + ex;
			return Error;
		}

		return exitCode;

	}
}