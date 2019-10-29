package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener los resultados del audio Despedida usuario.
 * @author umansilla
 *
 */
public class MediaListenerPlayThanks extends MediaListenerAbstract{
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
    /*
     * Constructor
     */
    public MediaListenerPlayThanks(final Call call, final boolean dropAfterPlayComplete)
    {
        this.call = call;
    }
    @Override
    public void playCompleted(final UUID requestId, final PlayOperationCause cause)
    {	
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerPlayThanks PlayOperationCause.COMPLETE");
			logger.fine("FIN DE LA LLAMADA SE TRANSFIERE AL AREA");
			call.divertTo(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AGENT_PHONE));
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerPlayThanks PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerPlayThanks PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerPlayThanks PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerPlayThanks PlayOperationCause.STOPPED");
		}

    }
}
