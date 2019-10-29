package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener el resultado de la reproduccion de audio de Beep.wav
 * @author umansilla
 *
 */
public class MediaListenerBeep extends MediaListenerAbstract{
	private final Logger logger = Logger.getLogger(getClass());
	public MediaListenerBeep() {
	}
	
	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerBeep PlayOperationCause.COMPLETE");
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerBeep PlayOperationCause.FAILED");
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerBeep PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerBeep PlayOperationCause.STOPPED");
		}
	}
	
	
}
