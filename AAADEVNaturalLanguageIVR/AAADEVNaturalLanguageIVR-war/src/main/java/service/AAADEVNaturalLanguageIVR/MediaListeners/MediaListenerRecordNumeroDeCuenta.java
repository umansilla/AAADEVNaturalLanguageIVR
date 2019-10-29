package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayBeep;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayHelsinkiRecordNumeroDeCuenta;
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
 * Clase creada para obtener los resultados de la reproduccion de el Numero de Cuenta .wav
 * @author umansilla
 *
 */
public class MediaListenerRecordNumeroDeCuenta extends MediaListenerAbstract{
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	private final int MAX_RECORD_TIME = 60000; // 60 seconds
	public MediaListenerRecordNumeroDeCuenta(Call call) {
		this.call = call;
	}
	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerRecordNumeroDeCuenta PlayOperationCause.COMPLETE");
			new PlayBeep(call).beep();;
			final String storageUrl = formRecordingStoreUrl();
			final RecordItem recordItem = MediaFactory.createRecordItem();

			recordItem.setMaxDuration(MAX_RECORD_TIME)
					.setTerminationKey("#")
					.setFileUri(storageUrl);
			MediaListenerRecordNumeroDeCuenta mediaListenerRecord = new MediaListenerRecordNumeroDeCuenta(call);
			MediaService mediaService = MediaFactory.createMediaService();
			mediaService.record(call.getCallingParty(), recordItem, mediaListenerRecord);
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerRecordNumeroDeCuenta PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerRecordNumeroDeCuenta PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerRecordNumeroDeCuenta PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerRecordNumeroDeCuenta PlayOperationCause.STOPPED");
		}
	}
	@Override
	public void recordCompleted(UUID requestId, RecordOperationCause cause) {
		if(cause == RecordOperationCause.TERMINATION_KEY_PRESSED){
			logger.fine("MediaListenerRecordNumeroDeCuenta RecordOperationCause.TERMINATION_KEY_PRESSED");
			try {
				PlayHelsinkiRecordNumeroDeCuenta play = new PlayHelsinkiRecordNumeroDeCuenta(call);
				play.start();
			} catch (Exception e) {
				PlayError playError = new PlayError(call);
				playError.audioError();
				logger.error("Error MediaListenerRecordNumeroDeCuenta: " + e.toString());
				new MyEmailSender().sendErrorByEmail("Error MediaListenerRecordNumeroDeCuenta: " + e.toString(), call);
			}
		}
		if(cause == RecordOperationCause.FAILED){
			logger.error("MediaListenerRecordNumeroDeCuenta RecordOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerRecordNumeroDeCuenta RecordOperationCause.FAILED", call);
		}
		if(cause == RecordOperationCause.PARTICIPANT_DROPPED){
			logger.error("MediaListenerRecordNumeroDeCuenta RecordOperationCause.PARTICIPANT_DROPPED");
		}
		if(cause == RecordOperationCause.STOPPED){
			logger.info("MediaListenerRecordNumeroDeCuenta RecordOperationCause.STOPPED");
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
