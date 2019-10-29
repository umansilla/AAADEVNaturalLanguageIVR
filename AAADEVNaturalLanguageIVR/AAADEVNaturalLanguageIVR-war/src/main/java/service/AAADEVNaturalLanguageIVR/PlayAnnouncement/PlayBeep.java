package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerBeep;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Beep.wav
 * @author umansilla
 *
 */
public class PlayBeep {
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	public PlayBeep(final Call call){
		this.call = call;
	}
	public void beep(){

		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append("Beep.wav");

		PlayItem playItem = null;
		try{
		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());
		}catch(URISyntaxException e){
			logger.error("Error PlayBeep: " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error PlayBeep: " + e.toString(), call);
		}
		final MediaService mediaServiceError = MediaFactory
				.createMediaService();
		final Participant participant = call.getCallingParty();
		final MediaListenerBeep mediaListenerPlayError = new MediaListenerBeep();

		mediaServiceError.play(participant, playItem, mediaListenerPlayError);
	}
}
