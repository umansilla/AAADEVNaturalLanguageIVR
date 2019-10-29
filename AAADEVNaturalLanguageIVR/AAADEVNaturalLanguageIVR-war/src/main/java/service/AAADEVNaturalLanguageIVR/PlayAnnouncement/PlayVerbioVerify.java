package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerVerify;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Verificar_usuario_ES.wav
 * @author umansilla
 *
 */
public class PlayVerbioVerify {

	private final Call call;
	private final Usuario usuario;
	
	public PlayVerbioVerify(final Call call, final Usuario usuario){
		this.call = call;
		this.usuario = usuario;
	}

	public void verify() throws URISyntaxException {
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			//Para continuar es necesario que repita la siguiente frase, En Avaya mi voz es mi contraseña
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/ES/Verificar_usuario_ES.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/EN/Verificar_usuario_EN.wav";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			announcement = "Audios/"+AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER)+"/PT/Verificar_usuario_PT.wav";
		}
		/*
		 * Determina la URL del servicio
		 */
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor().getName();

		final StringBuilder sb = new StringBuilder()
				.append("http://")
				.append(trafficInterfaceAddress)
				.append("/services/")
				.append(myServiceName)
				.append("/")
				.append(announcement);

		PlayItem playItem = MediaFactory
				.createPlayItem()
				.setInterruptible(false)
				.setIterateCount(1)
				.setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final MediaListenerVerify mediaListenerVerify = new MediaListenerVerify(call, usuario);
		mediaService.play(call.getCallingParty(), playItem, mediaListenerVerify);

	}
}