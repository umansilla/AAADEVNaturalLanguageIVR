package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.SpeechToTextIntenciones;
import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayHelsinky;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Helsinki.wav
 * @author umansilla
 *
 */
public class PlayHelsinki extends Thread {
	private final Call call;
	private final Logger logger;
	private final Usuario usuario;
	private MediaService mediaServicehelsinky = null;
	private UUID requestid = null;

	public PlayHelsinki(final Call call,final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
		logger = Logger.getLogger(PlayHelsinki.class);
	}

	@Override
	public void run() {
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
		.append("/services/").append(myServiceName).append("/")
		.append("Helsinki.wav");

		PlayItem playItem = null;
		try {
			playItem = MediaFactory.createPlayItem().setInterruptible(true)
					.setIterateCount(5)
					.setSource(sb.toString());
		} catch (URISyntaxException e) {
			PlayError play = new PlayError(call);
			play.audioError();
			logger.error("Error PlayHelsinki: " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error PlayHelsinki: " + e.toString(), call);
		}

		mediaServicehelsinky = MediaFactory.createMediaService();
		final MediaListenerPlayHelsinky mediaListenerPalyHelsinky = new MediaListenerPlayHelsinky(call);
		requestid = mediaServicehelsinky.play(call.getCallingParty(), playItem,mediaListenerPalyHelsinky);
		
		/*
		 * 
		 */
		SpeechToTextIntenciones peticiones = new SpeechToTextIntenciones(call, usuario);
		try {
			peticiones.peticionesExternas(call.getCallingParty(), requestid, mediaServicehelsinky);
		} catch (NoAttributeFoundException | ServiceNotFoundException | URISyntaxException | SSLUtilityException | IOException | NoUserFoundException | NoServiceProfileFoundException e) {
	    	mediaServicehelsinky.stop(call.getCallingParty(), requestid);
	    	new PlayError(call).audioError();
			logger.error("Error SpeechToTextIntenciones: " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error SpeechToTextIntenciones: " + e.toString(), call);
			
		} 
	}
}
