package ledcontrol.serialcomm.model;

public class ArduinoMessageImpl implements ArduinoMessage {

    private ArduinoMessageType type;

    private Status status;
    private LoggingInfo loggingInfo;
    private Debug debug;
    private Command command;

    public ArduinoMessageImpl() {
    }

    public ArduinoMessageImpl(ArduinoMessageType type) {
        this.type = type;
    }

    @Override
    public ArduinoMessageType getType() {
        return type;
    }

    public void setType(ArduinoMessageType type) {
        this.type = type;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public LoggingInfo getLoggingInfo() {
        return loggingInfo;
    }

    public void setLoggingInfo(LoggingInfo loggingInfo) {
        this.loggingInfo = loggingInfo;
    }

    @Override
    public Debug getDebug() {
        return debug;
    }

    @Override
    public void setDebug(Debug debug) {
        this.debug = debug;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }
}
