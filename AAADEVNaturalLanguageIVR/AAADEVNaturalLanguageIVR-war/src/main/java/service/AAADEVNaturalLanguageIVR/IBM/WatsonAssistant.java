package service.AAADEVNaturalLanguageIVR.IBM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para analizar el texto con la herramienta de Watson Assitant de
 * IBM Cloud.
 * 
 * @author umansilla
 */
public class WatsonAssistant {
	private final Call call;
	private final Usuario usuario;

	/**
	 * Constructor WatsonAssistant
	 * 
	 * @param call
	 *            Objeto Call del SDK de Avaya Breeze.
	 * @param usuario
	 *            Objeto Usuario Bean.
	 */
	public WatsonAssistant(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}

	private static final Logger logger = Logger
			.getLogger(WatsonAssistant.class);

	/**
	 * Metodo creado para realizar peticion a Watson Assistant de IBM Cloud, con
	 * el fin de obtener la intencion del texto.
	 * 
	 * @param text
	 *            Texto que se desea analizar.
	 * @return Regresa la intencion y confianza, resultado del analisis de
	 *         Watson Assitant IBM Cloud.
	 * @throws NoAttributeFoundException
	 *             Error que se lanza al no existir atributo en SMGR.
	 * @throws ServiceNotFoundException
	 *             Error que se lanza al no encontrar servicio en SMGR.
	 */
	public JSONObject request(String text) throws NoAttributeFoundException,
			ServiceNotFoundException {

		String intent2 = null;
		String personEntity = "Empty";
		try {
			/*
			 * HTTP
			 */
			String userNameAssistant = AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IBM_WATSON_ASSISTANT_USER_NAME);
			String passwordAssistant = AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IBM_WATSON_ASSISTANT_PASSWORD);

			final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
			final SSLContext sslContextAssistant = SSLUtilityFactory
					.createSSLContext(protocolTypeAssistant);
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(userNameAssistant,
							passwordAssistant));

			final String URI = "https://gateway.watsonplatform.net/assistant/api/v1/workspaces/"
					+ AttributeStore.INSTANCE.getServiceProfilesAttributeValue(
							call.getCalledParty(),
							Constants.IBM_WATSON_ASSISTANT_WORK_SPACE_ID)
					+ "/message?version=2018-07-10";

			final HttpClient client = HttpClients.custom()
					.setSSLContext(sslContextAssistant)
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
					.build();

			final HttpPost postMethodAssistant = new HttpPost(URI);
			postMethodAssistant.addHeader("Accept", "application/json");
			postMethodAssistant.addHeader("Content-Type", "application/json");

			final String authStringAssistant = userNameAssistant + ":"
					+ passwordAssistant;
			final String authEncBytesAssistant = DatatypeConverter
					.printBase64Binary(authStringAssistant.getBytes());
			postMethodAssistant.addHeader("Authorization", "Basic "
					+ authEncBytesAssistant);
			final String messageBodyAssistant = "{\"input\": {\"text\": \""
					+ text + "\"}}";
			final StringEntity conversationEntityAssistant = new StringEntity(
					messageBodyAssistant,
					ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
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
			String intent = json.getString("intents");
			JSONArray array = new JSONArray(intent);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				intent2 = (object.has("intent")) ? (object.getString("intent"))
						: ("Empty");
			}
			String entity = json.getString("entities");
			JSONArray arrayEntity = new JSONArray(entity);
				for (int j = 0; j < arrayEntity.length(); j++) {
					JSONObject entityObject = arrayEntity.getJSONObject(j);
					String entityName = (entityObject.has("entity")) ? (entityObject
							.getString("entity")) : ("Empty");
					if (entityName.equals("Person")) {
						personEntity = entityObject.getString("value");
					}
				}

		} catch (NullPointerException | IOException | SSLUtilityException | JSONException e) {
			logger.error("Error en WatsonAssistant " + e);
			String Error = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Servicio a Clientes")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Customer Service")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Atendimento ao Cliente")
									: ("Error"))));
			new MyEmailSender().sendErrorByEmail("Error en WatsonAssistant "
					+ e, call);
			return new JSONObject().put("Intent", Error).put("Entity",
					personEntity);
		}

		return new JSONObject().put("Intent", definirIntent(intent2)).put(
				"Entity", personEntity);
	}

	/**
	 * Metodo creado para formatear con el idioma correcto la intencion
	 * retornada por IBM Cloud.
	 * 
	 * @param intent
	 *            Intencion de entrada.
	 * @return Regresa la intencion formateada de acuerdo al idioma seleccionado
	 *         en SMGR.
	 * @throws NoAttributeFoundException
	 *             Error al no existir no encontrar atributo en el SMGR.
	 * @throws ServiceNotFoundException
	 *             Error al no encontrar servicio en SMGR.
	 */
	public String definirIntent(String intent)
			throws NoAttributeFoundException, ServiceNotFoundException {
		String intencion = null;
		if (intent.equals("ConsultaMovimientos")
				&& usuario.getUserRegistered().equals(true)) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Consulta De Movimientos")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Check Movements")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Verificar Movimentos")
									: ("Error"))));
			return intencion;
		}
		if (intent.equals("ConsultaSaldo")
				&& usuario.getUserRegistered().equals(true)) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Consulta De Saldo")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Balance Inquiry")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Verificar Saldo")
									: ("Error"))));
			return intencion;
		}
		if (intent.equals("Despedida")) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Despedida")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Farewell")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Despedida") : ("Error"))));
			return intencion;
		}
		if (intent.equals("CANCELACIONES")) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Cancelaciones")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Cancellations")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Cancelamentos")
									: ("Error"))));
			return intencion;
		}

		if (intent.equals("FACTURACION")) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Facturacion")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Billing")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Faturamento")
									: ("Error"))));
			return intencion;
		}
		if (intent.equals("HELP_DESK")) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Servicio Técnico")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Help Desk")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Serviço técnico")
									: ("Error"))));
			return intencion;
		}
		if (intent.equals("VENTAS")) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Ventas")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Sales")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Vendas") : ("Error"))));
			return intencion;
		}
		if (intent.equals("ASESOR")&& usuario.getUserRegistered().equals(true)) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Asesor De Cuenta")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Account Advisor")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Consultor De Contas")
									: ("Error"))));
			return intencion;
		}
		if (intent.equals("Dial")&& usuario.getUserRegistered().equals(true)) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Transferencia De Llamada")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Call Transfer")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Transferência De Chamadas")
									: ("Error"))));
			return intencion;
		}
		if (intent.equals("SERVICIO_A_CLIENTES") || intent.equals(null)
				|| intent.equals("")
				|| usuario.getUserRegistered().equals(false)) {
			intencion = (AttributeStore.INSTANCE
					.getServiceProfilesAttributeValue(call.getCalledParty(),
							Constants.IDIOMA_OPCION).equals("es")) ? ("Servicio a clientes")
					: ((AttributeStore.INSTANCE
							.getServiceProfilesAttributeValue(
									call.getCalledParty(),
									Constants.IDIOMA_OPCION).equals("en") ? ("Customer service")
							: ((AttributeStore.INSTANCE
									.getServiceProfilesAttributeValue(
											call.getCalledParty(),
											Constants.IDIOMA_OPCION)
									.equals("pt")) ? ("Atendimento ao cliente")
									: ("Error"))));
			return intencion;
		}

		return intencion;
	}

}