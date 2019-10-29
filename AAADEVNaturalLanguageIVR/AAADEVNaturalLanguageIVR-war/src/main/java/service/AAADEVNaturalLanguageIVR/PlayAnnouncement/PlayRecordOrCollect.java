package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;
import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayRecordOrCollect;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitOptions;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.call.media.RecordItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Record_OR_Collect_ES.wav
 * @author umansilla
 *
 */
public class PlayRecordOrCollect {
	private final Call call;
	private final int MAX_RECORD_TIME = 60000; // 60 seconds
	public static MediaService recordMediaService;
	public static UUID recordUUuid;
	public PlayRecordOrCollect(final Call call){
		this.call = call;
	}
	
	public void collect() throws URISyntaxException{
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			//Bienvenido a Banco Avaya. Mencione su número de cuenta y al terminar presionar #. o digite número de cuenta para continuar.
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/ES/Record_OR_Collect_ES.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/EN/Record_OR_Collect_EN.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/PT/Record_OR_Collect_PT.wav";
		}
		
		/*
		 * Determina la URL del servicio
		 */
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever.getTrafficInterfaceAddress();
		final String myServiceName = ServiceUtil.getServiceDescriptor().getName();
		final StringBuilder sb = new StringBuilder()
				.append("http://")
				.append(trafficInterfaceAddress)
				.append("/services/")
				.append(myServiceName)
				.append("/").append(announcement);
		
		final PlayItem playItem = MediaFactory.createPlayItem()
				.setInterruptible(false)
				.setIterateCount(1)
				.setSource(sb.toString());
		
		final DigitOptions digitOptions = MediaFactory.createDigitOptions()
				.setNumberOfDigits(6)
				.setTimeout(60000);
	
		final MediaService mediaService = MediaFactory.createMediaService();
		final MediaListenerPlayRecordOrCollect mediaListener = new MediaListenerPlayRecordOrCollect(call);
		mediaService.promptAndCollect(call.getCallingParty(), playItem, digitOptions, mediaListener);
	}
	
	public void record(){
		final String storageUrl = formRecordingStoreUrl();
		final RecordItem recordItem = MediaFactory.createRecordItem()
				.setMaxDuration(MAX_RECORD_TIME)
				.setTerminationKey("#")
				.setFileUri(storageUrl);
		MediaListenerPlayRecordOrCollect mediaListenerRecord = new MediaListenerPlayRecordOrCollect(call);
		recordMediaService = MediaFactory.createMediaService();
		recordUUuid = recordMediaService.record(call.getCallingParty(), recordItem, mediaListenerRecord);

	}
		
	private String formRecordingStoreUrl() {
		/*
		 * Define la ruta del archivo grabado para ser almacenado (incluye
		 * StoreRecordingServlet) Pribar si hace un POST al Servlet
		 * StoreRecordingServlet
		 */
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
