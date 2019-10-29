package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;
import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayError;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Despedida_usuario_ES.wav
 * @author umansilla
 *
 */
public class PlayDespedida {
	private final Call call;
	public PlayDespedida(final Call call){
		this.call = call;
	}
	public void playDespedida() throws URISyntaxException{
		String anuncio = null;
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			anuncio = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/ES/Despedida_usuario_ES.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			anuncio = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/EN/Despedida_usuario_EN.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			anuncio = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/PT/Despedida_usuario_PT.wav";
		}
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append(anuncio);

		PlayItem playItem = null;

		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaServiceError = MediaFactory
				.createMediaService();
		final Participant participant = call.getCallingParty();
		final MediaListenerPlayError mediaListenerPlayError = new MediaListenerPlayError(call);

		mediaServiceError.play(participant, playItem, mediaListenerPlayError);
	}
}