package ledcontrol.serialcomm.processor;

import ledcontrol.serialcomm.model.ArduinoMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Stephan on 20-12-2015.
 */
public class SerialJSONProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SerialJSONProcessor.class);

    public void receiveJSONData(ArduinoMessageImpl message) {
        LOG.debug("JSON Data received, type:[{}]", message.getType());

        switch(message.getType()) {
            case STATUS:
                LOG.trace("- Status::Current effect : [{}]", message.getStatus().getEffect());
                LOG.trace("- Status::Auto Brightness: [{}]", message.getStatus().isAutoBrightness());
                LOG.trace("- Status::Brightness     : [{}]", message.getStatus().getBrightness());
                LOG.trace("- Status::No. of LEDs    : [{}]", message.getStatus().getNoOfLeds());
                LOG.trace("- Status::FPS            : [{}]", message.getStatus().getFps());
                LOG.trace("- Status::Free memory    : [{}]", message.getStatus().getFreeMemory());
                LOG.trace("- Status::DebugIsOn      : [{}]", message.getStatus().isDebugOn());
                break;

            case LOG:
                LOG.trace("- Info::[{}]", message.getLoggingInfo().getInfo());
                break;

            default:
                LOG.error("Unknown messagetype: {}", message.getType());
        }
    }
}
