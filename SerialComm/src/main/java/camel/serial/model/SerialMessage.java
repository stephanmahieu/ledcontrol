package camel.serial.model;

import org.apache.camel.impl.DefaultMessage;

public class SerialMessage extends DefaultMessage {

	public SerialMessage(String payload) {
		super();
		setBody(payload);
	}
	
}
