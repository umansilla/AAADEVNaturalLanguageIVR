package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayNoUserProfile;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir No_User_Profile_ES.wav
 * @author umansilla
 *
 */
public class PlayNoUserProfile {
	private final Call call;

	public PlayNoUserProfile(Call call) {
		super();
		this.call = call;
	}
	
	public void noUser() throws URISyntaxException{
		String announcement = null;

		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			//Bienvenido a Avaya Developers. Favor de indicar en que podemos ayudar y al finalizar presionar #
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/ES/No_User_Profile_ES.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/EN/No_User_Profile_EN.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/PT/No_User_Profile_PT.wav";
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
		PlayItem playItem = MediaFactory.createPlayItem()
				.setInterruptible(false)
				.setIterateCount(1)
				.setSource(sb.toString());
		final MediaService mediaServiceError = MediaFactory
				.createMediaService();
		final MediaListenerPlayNoUserProfile mediaListenerPlayError = new MediaListenerPlayNoUserProfile(call);
		mediaServiceError.play(call.getCallingParty(), playItem, mediaListenerPlayError);

	}
}
