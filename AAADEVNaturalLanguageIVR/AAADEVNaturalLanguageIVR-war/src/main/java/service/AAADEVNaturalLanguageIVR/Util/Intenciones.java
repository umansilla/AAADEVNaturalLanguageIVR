package service.AAADEVNaturalLanguageIVR.Util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayDespedida;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayThanks;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.Google.TTSResponseMovimientosGoogle;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.Google.TTSResponseSaldoGoogle;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.Google.TTSResponseTransferenciaGoogle;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.IBM.TTSResponseMovimientosIBM;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.IBM.TTSResponseSaldoIBM;
import service.AAADEVNaturalLanguageIVR.TTSAnnouncements.IBM.TTSResponseTransferenciaIBM;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para identificar el intencion que debera ser reproducida.
 * @author umansilla
 *
 */
public class Intenciones {

	private final Call call;
	private final Usuario usuario;
	private static final Logger logger = Logger.getLogger(Intenciones.class);
	/**
	 * Constructor Intenciones
	 * @param call Objeto Call del SDK de Avaya Breeze.
	 * @param usuario Objeto Ususario Bean.
	 */
	public Intenciones(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}

	/**
	 * Metodo creado para identificar el idioma y establecer el mensaje que se reproducira al usuario.
	 * @throws URISyntaxException Se lanza el error al no existir una ruta HTTP.
	 * @throws NoAttributeFoundException Se lanza el error al no existir el atributo en SMGR. 
	 * @throws ServiceNotFoundException Se lanza el error al no exisitir el servicio en SMGR.
	 * @throws ClientProtocolException Se lanza error al no existir protocolo en una peticion HTTP.
	 * @throws SSLUtilityException Se lanza error al no exisitir certificados validos en una peticion HTTP.
	 * @throws IOException Se lanza error al no exisistir contenido en la respuesta de una peticion HTTP.
	 * @throws NoUserFoundException Se lanza error al no exisitir usuario en el SMGR.
	 * @throws NoServiceProfileFoundException Se lanza error al no exisitr Service Profile en SMGR.
	 */
	public void definirIntencion() throws URISyntaxException,
			NoAttributeFoundException, ServiceNotFoundException,
			ClientProtocolException, SSLUtilityException, IOException,
			NoUserFoundException, NoServiceProfileFoundException {
		if (usuario.getIntent().equals("Consulta De Movimientos")
				|| usuario.getIntent().equals("Check Movements")
				|| usuario.getIntent().equals("Verificar Movimentos")) {
			// TTS PARA CONSULTA DE MOVIMIENTOS
			logger.fine("INTENT = Consulta de Movimientos");
			if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("Google")) {
				TTSResponseMovimientosGoogle ttsResponseMovimientosGoogle = new TTSResponseMovimientosGoogle(
						call, usuario);
				ttsResponseMovimientosGoogle.userIdentifyResponseMovimientos();
			}
			if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("IBM")) {
				TTSResponseMovimientosIBM responseMovimientosIBM = new TTSResponseMovimientosIBM(
						call, usuario);
				responseMovimientosIBM.useridentifiedResponse();
			}

		}
		if (usuario.getIntent().equals("Consulta De Saldo")
				|| usuario.getIntent().equals("Balance Inquiry")
				|| usuario.getIntent().equals("Verificar Saldo")) {
			// TTS PARA CONSULTA DE SALDO
			logger.fine("INTENT = Consulta de Saldo");
			if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("Google")) {
				TTSResponseSaldoGoogle responseSaldo = new TTSResponseSaldoGoogle(
						call, usuario);
				responseSaldo.userIdentifyResponseSaldo();
			}
			if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("IBM")) {
				TTSResponseSaldoIBM ttsResponseSaldoIBM = new TTSResponseSaldoIBM(
						call, usuario);
				ttsResponseSaldoIBM.useridentifiedResponse();
			}

		}
		
		if (usuario.getIntent().equals("Transferencia De Llamada")
				|| usuario.getIntent().equals("Call Transfer")
				|| usuario.getIntent().equals("Transferência De Chamadas")) {
			// TTS PARA CONSULTA DE SALDO
			logger.fine("INTENT = Transferencia De Llamada");
			if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("Google")) {
				new TTSResponseTransferenciaGoogle(call, usuario).transferCall();
			}
			if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.CLOUD_POVIDER).equals("IBM")) {
				new TTSResponseTransferenciaIBM(call, usuario).transferCall();
			}

		}
		
		if (usuario.getIntent().equals("Cancelaciones")
				|| usuario.getIntent().equals("Cancellations")
				|| usuario.getIntent().equals("Cancelamentos")) {
			// TTS PARA CONSULTA DE SALDO
			logger.fine("INTENT = Cancelaciones");
			PlayThanks playThanks = new PlayThanks();
			String wav = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("Gracias_ES_Cancelaciones.wav")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("Gracias_EN_Cancelaciones.wav")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("Gracias_PT_Cancelaciones.wav")
									: ("Error"))));
			String currentFolder = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("ES")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("EN")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("PT")
									: ("Error"))));
			String announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/"+currentFolder+"/"+wav;
			playThanks.playThanks(call, announcement);
		}
		if (usuario.getIntent().equals("Facturacion")
				|| usuario.getIntent().equals("Billing")
				|| usuario.getIntent().equals("Faturamento")) {
			logger.fine("INTENT = Facturacion");
			PlayThanks playThanks = new PlayThanks();
			String wav = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("Gracias_ES_Facturacion.wav")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("Gracias_EN_Facturacion.wav")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("Gracias_PT_Facturacion.wav")
									: ("Error"))));
			String currentFolder = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("ES")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("EN")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("PT")
									: ("Error"))));
			String announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/"+currentFolder+"/"+wav;
			playThanks.playThanks(call, announcement);
		}
		if (usuario.getIntent().equals("Servicio Técnico")
				|| usuario.getIntent().equals("Help Desk")
				|| usuario.getIntent().equals("Serviço técnico")) {
			logger.fine("INTENT = Servicio Técnico");
			PlayThanks playThanks = new PlayThanks();
			String wav = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("Gracias_ES_Soporte_Tecnico.wav")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("Gracias_EN_Soporte_Tecnico.wav")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("Gracias_PT_Soporte_Tecnico.wav")
									: ("Error"))));
			String currentFolder = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("ES")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("EN")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("PT")
									: ("Error"))));
			String announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/"+currentFolder+"/"+wav;
			playThanks.playThanks(call, announcement);
		}
		if (usuario.getIntent().equals("Ventas")
				|| usuario.getIntent().equals("Sales")
				|| usuario.getIntent().equals("Vendas")) {
			logger.fine("INTENT = Ventas");
			PlayThanks playThanks = new PlayThanks();
			String wav = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("Gracias_ES_Ventas.wav")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("Gracias_EN_Ventas.wav")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("Gracias_PT_Ventas.wav")
									: ("Error"))));
			String currentFolder = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("ES")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("EN")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("PT")
									: ("Error"))));
			String announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/"+currentFolder+"/"+wav;
			playThanks.playThanks(call, announcement);
		}
		if (usuario.getIntent().equals("Asesor De Cuenta")
				|| usuario.getIntent().equals("Account Advisor")
				|| usuario.getIntent().equals("Consultor De Contas")) {
			logger.fine("INTENT = Asesor de cuenta");
			PlayThanks playThanks = new PlayThanks();
			String wav = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("Gracias_ES_Asesor.wav")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("Gracias_EN_Asesor.wav")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("Gracias_PT_Asesor.wav")
									: ("Error"))));
			String currentFolder = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("ES")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("EN")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("PT")
									: ("Error"))));
			String announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/"+currentFolder+"/"+wav;
			playThanks.playThanks(call, announcement);
		}
		if (usuario.getIntent().equals("Servicio a clientes")
				|| usuario.getIntent().equals("Customer service")
				|| usuario.getIntent().equals("Atendimento ao cliente")) {
			logger.fine("INTENT = Servicio a clientes");
			PlayThanks playThanks = new PlayThanks();
			String wav = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("Gracias_ES_Servicio_A_Clientes.wav")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("Gracias_EN_Servicio_A_Clientes.wav")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("Gracias_PT_Servicio_A_Clientes.wav")
									: ("Error"))));
			String currentFolder = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es") ? ("ES")
					: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) ? ("EN")
							: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? ("PT")
									: ("Error"))));
			String announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/"+currentFolder+"/"+wav;
			playThanks.playThanks(call, announcement);
		}
		if (usuario.getIntent().equals("Despedida")
				|| usuario.getIntent().equals("Farewell")) {
			// DESPEDIDA
			logger.fine("INTENT = Despedida");
			PlayDespedida despedida = new PlayDespedida(call);
			despedida.playDespedida();
		}
	}
}