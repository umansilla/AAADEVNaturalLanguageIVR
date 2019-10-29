package service.AAADEVNaturalLanguageIVR;

import service.AAADEVNaturalLanguageIVR.MediaListeners.MyEmailListener;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.bus.CollaborationBusException;
import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.email.EmailFactory;
import com.avaya.collaboration.email.EmailRequest;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase Principal Para enviar un Email.
 * @author umansilla
 *
 */
public final class MyEmailSender
{

    private final Logger logger = Logger.getLogger(getClass());
    /**
     * Metodo usado para enviar un email al destinatario que se ha seteado desde los atributos ubicados en SMGR.
     * @param emailTo Destinatario.
     * @param emailSubject Titulo del Correo electronico.
     * @param emailBody	Cuerpo del correo.
     * @param call Objeto Call.
     * @throws NoAttributeFoundException El error se lanza cuando no se encuentra el attributo correcto en AttributeStore.
     * @throws ServiceNotFoundException El error se lanza cuando el conector para enviar eMail no funciona correctamente.
     */
    public void sendEmail(final String emailTo, final String emailSubject, final String emailBody, Call call) throws NoAttributeFoundException, ServiceNotFoundException
    {
        final EmailRequest emailRequest = EmailFactory.createEmailRequest();
        emailRequest.addTo(emailTo);
        emailRequest.setFrom(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL_FROM));
        emailRequest.setSubject(emailSubject);
        emailRequest.setTextBody(emailBody);
        emailRequest.setListener(new MyEmailListener(emailRequest));
        
        try
        {
            emailRequest.send();
        }
        catch (final CollaborationBusException e)
        {
            logger.error("Could not send email request", e);
        }
    }
    
	/**
	 * Metodo creado par enviar el texto de un error cachado.
	 * @param text Texto del error cachado.
	 * @param call Objeto Call del SDK Avaya.
	 */
    public void sendErrorByEmail(String text, Call call){
    	final EmailRequest emailRequest = EmailFactory.createEmailRequest();
        emailRequest.addTo(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL));
        emailRequest.setFrom(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL_FROM));
        emailRequest.setSubject("ERROR AAADEVNaturalLanguageIVR");
        emailRequest.setTextBody(text);
        emailRequest.setListener(new MyEmailListener(emailRequest));
    }
}