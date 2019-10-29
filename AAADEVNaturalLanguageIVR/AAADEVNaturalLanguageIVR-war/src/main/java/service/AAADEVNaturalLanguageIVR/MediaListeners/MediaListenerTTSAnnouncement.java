package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayBeep;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayHelsinki;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordItem;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para obtener los resultados de la reproduccion del audio creado por Text To Speech
 * @author umansilla
 *
 */
public class MediaListenerTTSAnnouncement extends MediaListenerAbstract{
	private final Call call;
	private final Usuario usuario;
	private final int MAX_RECORD_TIME = 60000; // 60 seconds
	private final Logger logger = Logger.getLogger(getClass());
	/*
	 * Constructor
	 */
	public MediaListenerTTSAnnouncement(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}
	
	@Override
	public void playCompleted(final UUID requestId,
			final PlayOperationCause cause) {
		if (cause == PlayOperationCause.COMPLETE) {
			logger.fine("MediaListenerTTSAnnouncement PlayOperationCause.COMPLETE");
			PlayBeep play = new PlayBeep(call);
			play.beep();
			final String storageUrl = formRecordingStoreUrl();
			final RecordItem recordItem = MediaFactory.createRecordItem();

			recordItem.setMaxDuration(MAX_RECORD_TIME).setTerminationKey("#").setFileUri(storageUrl);
			MediaListenerTTSAnnouncement mediaListenerRecord = new MediaListenerTTSAnnouncement(call, usuario);
			MediaService mediaService = MediaFactory.createMediaService();
			mediaService.record(call.getCallingParty(), recordItem, mediaListenerRecord);
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerTTSAnnouncement PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerTTSAnnouncement PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.error("MediaListenerTTSAnnouncement PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.error("MediaListenerTTSAnnouncement PlayOperationCause.STOPPED");
		}
	}

	@Override
	public void recordCompleted(final UUID requestId,
			final RecordOperationCause cause) {

		if (cause == RecordOperationCause.TERMINATION_KEY_PRESSED) {
			logger.fine("MediaListenerTTSAnnouncement RecordOperationCause.TERMINATION_KEY_PRESSED");
			try {
				PlayHelsinki play = new PlayHelsinki(call, usuario);
				play.start();
			} catch (Exception e) {
				PlayError playError = new PlayError(call);
					playError.audioError();
				logger.error("Error MediaListenerTTSAnnouncement: " + e.toString());
				new MyEmailSender().sendErrorByEmail("Error MediaListenerTTSAnnouncement: " + e.toString(), call);
			}
		}
		if(cause == RecordOperationCause.FAILED){
			logger.error("MediaListenerTTSAnnouncement RecordOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerTTSAnnouncement RecordOperationCause.FAILED", call);
		}
		if(cause == RecordOperationCause.PARTICIPANT_DROPPED){
			logger.error("MediaListenerTTSAnnouncement RecordOperationCause.PARTICIPANT_DROPPED");
		}
		if(cause == RecordOperationCause.STOPPED){
			logger.info("MediaListenerTTSAnnouncement RecordOperationCause.STOPPED");
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
