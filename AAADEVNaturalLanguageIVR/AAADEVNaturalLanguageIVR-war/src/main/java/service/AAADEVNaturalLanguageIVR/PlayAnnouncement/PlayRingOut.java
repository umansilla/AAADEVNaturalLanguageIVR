package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerRingOut;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Ring_Out.wav
 * @author umansilla
 *
 */
public class PlayRingOut {
	private final Call call;
	public PlayRingOut(final Call call){
		this.call = call;
	}
	public void ringOut() throws URISyntaxException{
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append("Ring_Out.wav");

		PlayItem playItem = null;

		playItem = MediaFactory.createPlayItem().setInterruptible(false)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaServiceError = MediaFactory
				.createMediaService();
		final MediaListenerRingOut mediaListenerRingOut = new MediaListenerRingOut(call);
		mediaServiceError.play(call.getCallingParty(), playItem, mediaListenerRingOut);
	}
}
