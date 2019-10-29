package service.AAADEVNaturalLanguageIVR.Util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import service.AAADEVNaturalLanguageIVR.MyCallListener;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceData;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.dal.factory.CollaborationDataFactory;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceDescriptor;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/**
 * Clase creada para obtener los atributos desde el SMGR.
 * @author umansilla
 *
 */
public enum AttributeStore
{
    INSTANCE;
    private final Map<String, String> attributeMap = new HashMap<String, String>();
    private final ServiceData serviceData;
    private static Logger logger = Logger.getLogger(MyCallListener.class);
    /**
     * Constructor AttributeStores
     */
    private AttributeStore()
    {	

        final ServiceDescriptor serviceDescriptor = ServiceUtil.getServiceDescriptor();
        if (serviceDescriptor == null)
        {
            throw new IllegalStateException("Couldn't get service descriptor");
        }
        

        serviceData = CollaborationDataFactory.getServiceData(serviceDescriptor.getName(), serviceDescriptor.getVersion());
    }
    
    public void addAttribute(final String name, final String value)
    {
        attributeMap.put(name, value);
    }
    
    /**
     * Obtener el valor de un atributo a nivel global y cluster
     * @param attributeName Nombre del atributo.
     * @return Valor del atributo.
     * @throws NoAttributeFoundException Se lanza error al no existir el nombre del atributo SMGR.
     * @throws ServiceNotFoundException Se lanza error al no exisitir servicio en el SMGR.
     */
    public String getAttributeValue(final String attributeName) throws NoAttributeFoundException, ServiceNotFoundException
    {
        String attributeValue = attributeMap.get(attributeName);
        if (StringUtils.isEmpty(attributeValue))
        {	
 
            attributeValue = serviceData.getServiceAttribute(attributeName);
        }
        return attributeValue;
    }
    
    /**
     * Metodo usado para obetner el atribubto desde el SMGR a nivel service profile.
     * @param participantCalled Participante, calling party
     * @param attributeName Nombre del atributo que se desea recuperar.
     * @return Valor del atributo a nivel service profile.
     */
    public String getServiceProfilesAttributeValue(final Participant participantCalled, final String attributeName)
    {
        String attributeValue = attributeMap.get(attributeName);
        if (StringUtils.isEmpty(attributeValue))
        {	
        	
            try {
				attributeValue = serviceData.getServiceAttribute(participantCalled.getAddress(), attributeName);
			} catch (NoUserFoundException | NoAttributeFoundException
					| ServiceNotFoundException | NoServiceProfileFoundException e) {
				logger.error("Error al recuperar Atributo por Service Profle " + e.toString());
			}
        }
        
        return attributeValue;
    }
}