package service.AAADEVNaturalLanguageIVR.PlayAnnouncement;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Http.SpeechToTextNumeroDeCuenta;
import service.AAADEVNaturalLanguageIVR.MediaListeners.MediaListenerPlayHelsinky;
import service.AAADEVNaturalLanguageIVR.Util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para configurar y reproducir Helsinki.wav"
 * @author umansilla
 *
 */
public class PlayHelsinkiRecordNumeroDeCuenta extends Thread{
	private final Call call;
	private final Logger logger;
	private MediaService mediaServicehelsinky = null;
	private UUID requestid = null;
	
	public PlayHelsinkiRecordNumeroDeCuenta(final Call call){
		this.call = call;
		logger = Logger.getLogger(PlayHelsinkiRecordNumeroDeCuenta.class);
	}
	@Override
	public void run() {
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever.getTrafficInterfaceAddress();
		final String myServiceName = ServiceUtil.getServiceDescriptor().getName();
		final StringBuilder sb = new StringBuilder()
			.append("http://")
			.append(trafficInterfaceAddress)
			.append("/services/")
			.append(myServiceName)
			.append("/")
			.append("Helsinki.wav");

		PlayItem playItem = null;
		try {
			playItem = MediaFactory.createPlayItem().setInterruptible(true)
					.setIterateCount(5)
					.setSource(sb.toString());
		} catch (URISyntaxException e){
			logger.error("Error PlayHelsinkiRecordNumeroDeCuenta : " + e.toString());
	    	PlayError playError = new PlayError(call);
			playError.audioError();
			new MyEmailSender().sendErrorByEmail("Error PlayHelsinkiRecordNumeroDeCuenta : " + e.toString(), call);
			
		}

		mediaServicehelsinky = MediaFactory.createMediaService();
		final MediaListenerPlayHelsinky mediaListenerPalyHelsinky = new MediaListenerPlayHelsinky(call);
		requestid = mediaServicehelsinky.play(call.getCallingParty(), playItem ,mediaListenerPalyHelsinky);
		
		
		
		SpeechToTextNumeroDeCuenta peticiones = new SpeechToTextNumeroDeCuenta(call);
		try {
			peticiones.speechToText(call.getCallingParty(), requestid, mediaServicehelsinky);
		} catch (IOException | SSLUtilityException e) {
	    	mediaServicehelsinky.stop(call.getCallingParty(), requestid);
	    	PlayError playError = new PlayError(call);
			playError.audioError();
			logger.error("Error SpeechToTextNumeroDeCuenta " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error SpeechToTextNumeroDeCuenta " + e.toString(), call);
		}
	}
	
}
