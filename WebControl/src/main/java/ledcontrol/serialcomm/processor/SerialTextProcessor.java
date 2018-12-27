package ledcontrol.serialcomm.processor;

import camel.serial.model.SerialMessage;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialTextProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SerialTextProcessor.class);

    public void receiveSerialData(Exchange exchange) {
        SerialMessage serialMessage = exchange.getIn(SerialMessage.class);
        String serialData = (String)serialMessage.getBody();

        LOG.debug("TEXT Data received: [{}]", serialData);
    }
}
