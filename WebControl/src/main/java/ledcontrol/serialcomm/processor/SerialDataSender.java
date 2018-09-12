package ledcontrol.serialcomm.processor;

import ledcontrol.serialcomm.model.*;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SerialDataSender {

    private static final Logger LOG = LoggerFactory.getLogger(SerialDataSender.class);

    @EndpointInject(uri = "direct:sendSerialData")
    private ProducerTemplate sendSerial;


    public void sendStatusRequest() {
        ArduinoMessage jsonMessage = new ArduinoMessageImpl(ArduinoMessageType.STATUS);
        sendSerial.sendBody(jsonMessage);
    }

    public void sendDebugState(boolean debugState) {
        ArduinoMessage jsonMessage = new ArduinoMessageImpl(ArduinoMessageType.DEBUG);
        jsonMessage.setDebug(new Debug(debugState));
        sendSerial.sendBody(jsonMessage);
    }

    public void sendReset() {
        sendSerial.sendBody("RESET");
    }

    public void sendEffect(String effect) {
        ArduinoMessage jsonMessage = new ArduinoMessageImpl(ArduinoMessageType.CMD);
        Command command = new Command();
        command.setName("effect");
        command.setParam(new ArrayList<String>());
        command.getParam().add(effect);
        jsonMessage.setCommand(command);
        sendSerial.sendBody(jsonMessage);
    }

    public void sendCommand(String commandName, List<String> parameters) {
        ArduinoMessage jsonMessage = new ArduinoMessageImpl(ArduinoMessageType.CMD);
        Command command = new Command();
        command.setName(commandName);
        command.setParam(parameters);
        //command.setParam(new ArrayList<String>());
        //command.getParam().add("255");
        //command.getParam().add("dummy");
        jsonMessage.setCommand(command);
        sendSerial.sendBody(jsonMessage);
    }
}
