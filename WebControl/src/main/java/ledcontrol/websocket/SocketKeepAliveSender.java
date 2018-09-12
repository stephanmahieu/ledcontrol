package ledcontrol.websocket;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketKeepAliveSender {
	
	private static final Logger LOG = LoggerFactory.getLogger(SocketKeepAliveSender.class);
	
	@EndpointInject(uri = "direct:sendWebsocketData")
	private ProducerTemplate socketProducer;

	public void process(Exchange exchange) {
		LOG.debug("Sending keep-alive over websocket..");
		socketProducer.sendBody("keep-alive");
	}
}
