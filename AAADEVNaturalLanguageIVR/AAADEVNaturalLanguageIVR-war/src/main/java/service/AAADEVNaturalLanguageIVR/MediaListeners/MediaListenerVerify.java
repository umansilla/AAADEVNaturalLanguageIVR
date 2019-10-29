package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayBeep;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;
import service.AAADEVNaturalLanguageIVR.Verbio.VerbioClient;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordItem;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para obtener los resultados del audio Bienvenido_Autenticacion_Verbio_.wav
 * @author umansilla
 *
 */
public class MediaListenerVerify extends MediaListenerAbstract {
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	private final Usuario usuario;

	private final int MAX_RECORD_TIME = 60000; // 60 seconds
	/*
	 * Constructor
	 */
	public MediaListenerVerify(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}

	@Override
	public void playCompleted(final UUID requestId, final PlayOperationCause cause) {
		if (cause == PlayOperationCause.COMPLETE) {
			logger.fine("MediaListenerVerify PlayOperationCause.COMPLETE");
			PlayBeep play = new PlayBeep(call);
			play.beep();
			final String storageUrl = formRecordingStoreUrl();
			final RecordItem recordItem = MediaFactory.createRecordItem();

			recordItem.setMaxDuration(MAX_RECORD_TIME).setTerminationKey("#")
					.setFileUri(storageUrl);
			MediaListenerVerify mediaListenerRecord = new MediaListenerVerify(call, usuario);
			MediaService mediaService = MediaFactory.createMediaService();
			mediaService.record(call.getCallingParty(), recordItem, mediaListenerRecord);
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerVerify PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerVerify PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.error("MediaListenerVerify PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.error("MediaListenerVerify PlayOperationCause.STOPPED");
		}
	}

	@Override
	public void recordCompleted(final UUID requestId,
			final RecordOperationCause cause) {
		if (cause == RecordOperationCause.TERMINATION_KEY_PRESSED) {
			logger.fine("MediaListenerVerify RecordOperationCause.TERMINATION_KEY_PRESSED");
			VerbioClient client = new VerbioClient();
			try {
				client.verify(call, usuario);
			} catch (UnsupportedOperationException | IOException
					| SSLUtilityException | NoAttributeFoundException | ServiceNotFoundException | URISyntaxException e) {
				PlayError play = new PlayError(call);
				play.audioError();
				logger.error("Error MediaListenerVerify: " + e.toString());
				new MyEmailSender().sendErrorByEmail("Error MediaListenerVerify: " + e.toString(), call);
			}
		}
		if(cause == RecordOperationCause.FAILED){
			logger.error("MediaListenerVerify RecordOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerVerify RecordOperationCause.FAILED", call);
		}
		if(cause == RecordOperationCause.PARTICIPANT_DROPPED){
			logger.error("MediaListenerVerify RecordOperationCause.PARTICIPANT_DROPPED");
		}
		if(cause == RecordOperationCause.STOPPED){
			logger.error("MediaListenerVerify RecordOperationCause.STOPPED");
		}
	}
	
	/**
	 * Define la ruta del archivo grabado para ser almacenado (incluye
	 * StoreRecordingServlet) Pribar si hace un POST al Servlet
	 * StoreRecordingServlet
	 * @return Retorna el URL para realizar la peticion HTTPS para grabar el audio del calling party.
	 */
	private String formRecordingStoreUrl() {
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();
		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName)
				.append("/StoreRecordingServlet/").append("recording")
				.append(myServiceName).append(".wav");
		return sb.toString();
	}

}
