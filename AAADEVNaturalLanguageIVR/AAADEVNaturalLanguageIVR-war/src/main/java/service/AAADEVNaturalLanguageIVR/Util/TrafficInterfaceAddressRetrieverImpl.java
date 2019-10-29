package service.AAADEVNaturalLanguageIVR.Util;

import com.avaya.asm.datamgr.AssetDM;
import com.avaya.asm.datamgr.DMFactory;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener la direccion IP del Cluster y nombre del servicio.
 * @author umansilla
 *
 */
public final class TrafficInterfaceAddressRetrieverImpl implements TrafficInterfaceAddressRetriever
{
    private final AssetDM assetDM;

    private final Logger logger;
    /**
     * Constructor TrafficInterfaceAddressRetrieverImpl
     */
    public TrafficInterfaceAddressRetrieverImpl()
    {
        this((AssetDM) DMFactory.getInstance().getDataMgr(AssetDM.class),
                Logger.getLogger(TrafficInterfaceAddressRetriever.class));
    }
    /**
     * Constructor TrafficInterfaceAddressRetrieverImpl
     */
    TrafficInterfaceAddressRetrieverImpl(final AssetDM assetDM, final Logger logger)
    {
        this.assetDM = assetDM;

        this.logger = logger;
    }

    @Override
    public String getTrafficInterfaceAddress()
    {
    	logger.info("getTrafficInterfaceAddress()");
        final String localAsset = assetDM.getMyAssetIp();
        
        return localAsset;
    }
}