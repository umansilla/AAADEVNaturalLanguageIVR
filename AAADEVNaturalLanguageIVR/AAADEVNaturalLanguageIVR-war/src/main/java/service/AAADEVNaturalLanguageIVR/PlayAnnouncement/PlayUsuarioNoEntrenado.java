package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayError;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir No_Verbio_User_ES.wav
 * @author umansilla
 *
 */
public class PlayUsuarioNoEntrenado {
	private Call call;
	public PlayUsuarioNoEntrenado(final Call call) {
		this.call = call;
	}
	
	public void usuarioNoEntrenado() throws URISyntaxException{
		call.getCallPolicies().setMediaServerInclusion(MediaServerInclusion.AS_NEEDED);
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			//El número de cuenta no  presenta entrenamiento de voz habilitado, favor de habilitar reconocimiento de voz para continuar.
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/ES/No_Verbio_User_ES.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/EN/No_Verbio_User_EN.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/PT/No_Verbio_User_PT.wav";
		}
		
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor().getName();
		final StringBuilder sb = new StringBuilder()
			.append("http://")
			.append(trafficInterfaceAddress)
			.append("/services/")
			.append(myServiceName)
			.append("/")
			.append(announcement);

		PlayItem playItem = MediaFactory.createPlayItem().setInterruptible(false).setIterateCount(1).setSource(sb.toString());
		final MediaService mediaServiceError = MediaFactory.createMediaService();
		final MediaListenerPlayError mediaListenerPlayError = new MediaListenerPlayError(call);
		mediaServiceError.play(call.getCallingParty(), playItem, mediaListenerPlayError);
	}
	
}
