package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener el resultado de la reproduccion de Helsinky.wav
 * @author umansilla
 *
 */
public class MediaListenerPlayHelsinky extends MediaListenerAbstract{
	private final Logger logger = Logger.getLogger(getClass());
    /*
     * Constructor
     */
	private final Call call;
    public MediaListenerPlayHelsinky(final Call call)
    {
    	this.call = call;
    }
	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerPlayHelsinky PlayOperationCause.COMPLETE");
			logger.error("PLAY HELSINKY TERMINO DE REPRODUCIR, NO HAY RESPUESTA POR STT");
			PlayError play = new PlayError(call);
			play.audioError();
			new MyEmailSender().sendErrorByEmail("PLAY HELSINKY TERMINO DE REPRODUCIR, NO HAY RESPUESTA POR STT", call);
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerPlayHelsinky PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerPlayHelsinky PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerPlayHelsinky PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerPlayHelsinky PlayOperationCause.STOPPED");
		}
	}
    
}
