package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayError;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Error_ES.wav
 * @author umansilla
 *
 */
public class PlayError {
	
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	public PlayError(final Call call) {
		this.call = call;
	}
	
	public void audioError() {
		String announcement = null;

		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			//Se ha detectado un error. Favor de comunicarse nuevamente.
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/ES/Error_ES.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/EN/Error_EN.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/PT/Error_PT.wav";
		}
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder()
			.append("http://")
			.append(trafficInterfaceAddress)
			.append("/services/")
			.append(myServiceName)
			.append("/")
			.append(announcement);
		PlayItem playItem = null;
		try{
		playItem = MediaFactory.createPlayItem()
				.setInterruptible(false)
				.setIterateCount(1)
				.setSource(sb.toString());
		}catch (URISyntaxException e) {
			logger.error("PlayError audioError " + e.toString());
		}
		final MediaService mediaServiceError = MediaFactory
				.createMediaService();
		final MediaListenerPlayError mediaListenerPlayError = new MediaListenerPlayError(call);
		mediaServiceError.play(call.getCallingParty(), playItem, mediaListenerPlayError);

	}
}
