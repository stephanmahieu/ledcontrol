package camel.serial;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component which create new SerialComponent instance
 *
 */
public class SerialComponent extends DefaultComponent{
	
	private static final Logger LOG = LoggerFactory.getLogger(SerialComponent.class);
	
    private SerialConfiguration config = new SerialConfiguration();


	@Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
    	LOG.info("Creating endpoint, uri={}", uri);

        try {
	        setProperties(config, parameters);
		} catch (Exception e) {
			throw new SerialException("Error setting serial properties", e);
		}
        
		LOG.debug("SerialComponent configuration port:{} baudrate:{} databits:{} stopbits:{} parity:{}",
				new Object[] { config.getPortName(), config.getBaudrate(), config.getDatabits(), config.getStopbits(), config.getParity() });
        
        return new SerialEndpoint(uri, this, config);
    }
    
}
