package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener los call backs de la reproduccion de Error.wav
 * @author umansilla
 *
 */
public class MediaListenerPlayError extends MediaListenerAbstract{
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	public MediaListenerPlayError(final Call call) {
		this.call = call;
	}

	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerPlayError PlayOperationCause.COMPLETE");
			call.drop();
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerPlayError PlayOperationCause.FAILED");
			call.drop();
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerPlayError PlayOperationCause.INTERRUPTED");
			call.drop();
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerPlayError PlayOperationCause.STOPPED");
			call.drop();
		}
	}
}
