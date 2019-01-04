package camel.serial;

import camel.serial.model.SerialMessage;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.TooManyListenersException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Suspendable;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Consumer starting a serial consumer which forwards data to the ledcontrol.processor.
 * 
 */
public class SerialConsumer extends DefaultConsumer implements SerialPortEventListener, Suspendable {

    private static final Logger LOG = LoggerFactory.getLogger(SerialConsumer.class);
    
    private InputStream in;
    private SerialPort serialPort;
    private SerialEndpoint endpoint;

    
    public SerialConsumer(SerialEndpoint endpoint, Processor processor, SerialPort serialPort, InputStream in) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.serialPort = serialPort;
        this.in = in;
    }

    @Override
    protected void doStart() {
        LOG.info("Start SerialConsumer");
        
        if (null == serialPort) {
            LOG.error("No serialPort (null), can not start consumer!");
            return;
        }
        
        try {
            super.doStart();
        } catch (Exception e) {
            LOG.error("Error starting SerialConsumer", e);
        }
        
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            LOG.error("Error: too many eventlisteners added", e);;
        }
    }

//    @Override
//    protected void doStop() {
//        LOG.debug("Stop SerialConsumer...");
//        try {
//            super.doStop();
//		} catch (Exception e) {
//			LOG.error("Error stopping SerialConsumer", e);
//		}
//        
//        if (serialPort != null) {
//	        serialPort.notifyOnDataAvailable(false);
//	        serialPort.removeEventListener();
//        }
//
//        if (in != null) {
//	        try {
//				LOG.debug("Close inputstream for {}", URISupport.sanitizeUri(endpoint.getEndpointUri()));
//				in.close();
//			} catch (IOException e) {
//				LOG.warn("Error closing input stream", e);
//			} finally {
//				in = null;
//			}
//        }
//        
//        try {
//			endpoint.stop();
//		} catch (Exception e) {
//			LOG.error("Error stopping endpoint", e);
//		}
//    }

    @Override
    protected void doSuspend() {
        LOG.debug("Suspend SerialConsumer...");
        try {
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();

            endpoint.serialSuspend();
            super.doSuspend();
        } catch (Exception e) {
            LOG.error("Error suspending SerialConsumer", e);
        }
    }

    @Override
    protected void doResume() {
        LOG.debug("Resume SerialConsumer...");
        try {
            endpoint.serialResume();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            super.doResume();
        } catch (Exception e) {
            LOG.error("Error resuming SerialConsumer", e);
        }
    }

    protected void setIn(InputStream in) {
        this.in = in;
    }

    protected void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    /* (non-Javadoc)
     * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        String dataMessage = null;
        byte[] buffer = new byte[1024];
        int data;
        try {
            int len = 0;

            // String based, read up to the first LF
            while ((data = in.read()) > -1) {
                buffer[len++] = (byte) data;
                if (data == 10) {
                    break;
                }
            }

            // discard single CR + LF
            if (len == 2 && buffer[0] == 13 && buffer[1] == 10) {
                len = 0;
            }

            if (len > 0) {
                try {
                    // copy len-2 bytes (discard CR+LF)
                    byte[] dataReceived = Arrays.copyOfRange(buffer, 0, len-2);
                    dataMessage = new String(dataReceived);
                    LOG.debug("Received message! [{}]", dataMessage);
                }
                catch (Exception ex) {
                    LOG.warn("Error interpreting data (out of sync?)");
                }
            }

            if (dataMessage != null) {
                Exchange exchange = getEndpoint().createExchange();
                exchange.setIn(new SerialMessage(dataMessage));
                try {
                    getProcessor().process(exchange);
                } catch (Exception e) {
                    LOG.error("Could not delegate message to Camel ledcontrol.processor", e);
                }
            }

        } catch (IOException e) {
            LOG.error("Error receiving data", e);
        }
    }

    @Override
    public String toString() {
        return "SerialConsumer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
    }

}
