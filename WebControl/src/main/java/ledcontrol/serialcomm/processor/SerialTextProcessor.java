package ledcontrol.serialcomm.processor;

import camel.serial.model.SerialMessage;
import ledcontrol.websocket.WebSocketSender;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Stephan on 22-12-2015.
 */
public class SerialTextProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SerialTextProcessor.class);

    @Autowired
    WebSocketSender webSocketSender;

    public void receiveSerialData(Exchange exchange) {
        SerialMessage serialMessage = exchange.getIn(SerialMessage.class);
        String serialData = (String)serialMessage.getBody();

        webSocketSender.send(serialData);
        LOG.info("TEXT Data received: [{}]", serialData);
    }
}
