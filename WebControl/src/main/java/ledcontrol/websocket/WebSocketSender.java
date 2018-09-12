package ledcontrol.websocket;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketSender
{

  private static final Logger LOG = LoggerFactory.getLogger( WebSocketSender.class );

  @EndpointInject( uri = "direct:sendWebsocketData" )
  private ProducerTemplate socketProducer;

  private static final SimpleDateFormat sdf = new SimpleDateFormat( "HH':'mm':'ss.SSS" );

  public void send(String message) {
    LOG.debug( "Sending data over websocket.." );

    //		String msg = DataUtil.safePrint(message.getBytes());
    //		
    //		if (Command.DEBUG.equals(message.getCommand())) {
    //			msg = "DBG " + msg;
    //		}
    //		else {
    //			if (UnitID.RPI.equals(message.getToUnitId())) {
    //				msg = "<<< " + msg;
    //			}
    //			else {
    //				msg = ">>> " + msg;
    //			}
    //		}
    String msg = sdf.format(new Date()) + " " + message;

    socketProducer.sendBody(msg);
  }
}
