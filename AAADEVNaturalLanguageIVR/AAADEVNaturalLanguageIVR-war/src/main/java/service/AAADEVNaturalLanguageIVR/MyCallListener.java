package service.AAADEVNaturalLanguageIVR;

import java.net.URISyntaxException;

import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayRingOut;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.CallListenerAbstract;
import com.avaya.collaboration.call.TheCallListener;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.util.logger.Logger;

/**
 * This class is needed if an application with call features is written.
 * If you have an application which is doing only HTTP related operations, remove this class from the project.
 * 
 * For HTTP only application, also remove the sip.xml from src/main/java/webapp/WEB-INF and blank out details from
 * CARRule.xml. Look at the files for more details.
 * 
 */
@TheCallListener
public class MyCallListener extends CallListenerAbstract 
{
    private static Logger logger = Logger.getLogger(MyCallListener.class);
   
    /**
     * Constructor vacio.
     */
	public MyCallListener() 
	{
	}
	/*
	 * (non-Javadoc)
	 * @see com.avaya.collaboration.call.CallListener#callIntercepted(com.avaya.collaboration.call.Call)
	 */
	@Override
	public final void callIntercepted(final Call call) {
		call.getCallPolicies().setMediaServerInclusion(MediaServerInclusion.AS_NEEDED);
		PlayRingOut play = new PlayRingOut(call);
		try {
			play.ringOut();
		} catch (URISyntaxException e) {
			logger.error("Error MyCallListener: " + e.toString());
			PlayError playerror = new PlayError(call);
			playerror.audioError();
			new MyEmailSender().sendErrorByEmail("Error MyCallListener: " + e.toString(), call);
		}
	}
	
}
