package ledcontrol.serialcomm.model;

/**
 * Created by Stephan on 21-12-2015.
 */
public interface ArduinoMessage {
    ArduinoMessageType getType();

    Status getStatus();

    LoggingInfo getLoggingInfo();

    Command getCommand();
    void setCommand(Command command);

    Debug getDebug();
    void setDebug(Debug debug);
}
