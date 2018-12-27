package ledcontrol.serialcomm.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialDataLogger {
    private static final Logger LOG = LoggerFactory.getLogger(SerialDataSender.class);

    public void logMessage(String message) {
        LOG.trace("Sending serial msg [{}]", message);
    }
}
