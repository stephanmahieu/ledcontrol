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
        LOG.info("JSON Data received, type:[{}]", message.getType());

        switch(message.getType()) {
            case STATUS:
                LOG.info("- Status::Current effect : [{}]", message.getStatus().getEffect());
                LOG.info("- Status::Auto Brightness: [{}]", message.getStatus().isAutoBrightness());
                LOG.info("- Status::Brightness     : [{}]", message.getStatus().getBrightness());
                LOG.info("- Status::No. of LEDs    : [{}]", message.getStatus().getNoOfLeds());
                LOG.info("- Status::FPS            : [{}]", message.getStatus().getFps());
                LOG.info("- Status::Free memory    : [{}]", message.getStatus().getFreeMemory());
                LOG.info("- Status::DebugIsOn      : [{}]", message.getStatus().isDebugOn());
                break;

            case LOG:
                LOG.info("- Info::[{}]", message.getLoggingInfo().getInfo());
                break;

            default:
                LOG.error("Unknown messagetype: {}", message.getType());
        }
    }
}
