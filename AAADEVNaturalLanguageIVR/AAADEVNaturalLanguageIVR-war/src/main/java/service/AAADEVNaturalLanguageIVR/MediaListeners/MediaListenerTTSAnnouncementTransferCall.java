package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.util.UUID;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

public class MediaListenerTTSAnnouncementTransferCall extends MediaListenerAbstract{
	private final Call call;
	private final Usuario usuario;

	private final Logger logger = Logger.getLogger(getClass());
	/*
	 * Constructor
	 */
	public MediaListenerTTSAnnouncementTransferCall(final Call call, final Usuario usuario) {
		this.call = call;
		this.usuario = usuario;
	}
	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if (cause == PlayOperationCause.COMPLETE) {
			logger.fine("MediaListenerTTSAnnouncementTransferCall PlayOperationCause.COMPLETE");
			logger.info("Se transfiere la llamada a: " + usuario.getEntity() + " Al teléfono: " + usuario.getCallDivertTo());
			call.divertTo(usuario.getCallDivertTo());
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerTTSAnnouncementTransferCall PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerTTSAnnouncementTransferCall PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.error("MediaListenerTTSAnnouncementTransferCall PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.error("MediaListenerTTSAnnouncementTransferCall PlayOperationCause.STOPPED");
		}
	}
	
	

}
