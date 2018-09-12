package camel.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SerialEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(SerialEndpoint.class);

    private SerialConfiguration config;

    private SerialPort serialPort;
	private InputStream in;
	private OutputStream out;
    
    
    public SerialEndpoint(String endPointUri, Component component, SerialConfiguration config) {
        super(endPointUri, component);
        this.config = config;
        try {
			serialConnect();
		} catch (Exception e) {
			//throw new SerialException("Error connecting to serial port " + config.getPortName(), e);
			LOG.error("Error connecting to serial port " + config.getPortName(), e);
		}
    }
    
    @Override
    public Consumer createConsumer(Processor processor) {
        return new SerialConsumer(this, processor, serialPort, in);
    }
    
    @Override
    public Producer createProducer() {
        return new SerialProducer(this, out);
    }

    @Override
    public boolean isSingleton() {
    	// TODO check if SerialEndpoint is singleton yes/no
        return true;
    }

    @Override
	public String toString() {
		return String.format("SerialEndpoint[%s]", URISupport.sanitizeUri(getEndpointUri()));
	}
  
//	@Override
//	public void stop() throws Exception {
//    	// in/out closed by producer/consumer
//		if (in != null || out != null) {
//	    	LOG.debug("Not closing serial port yet because of open streams: in:{}, out:{}", in, out);
//		}
//		else if (serialPort == null) {
//	    	LOG.debug("Serial port not open, no need to close");
//		}
//		else {
//			LOG.info("Closing serial port {}", config.getPortName());
//			serialPort.close();
//			serialPort = null;
//		}
//		super.stop();
//	}

	private void serialConnect() throws Exception {

    	if (LOG.isDebugEnabled()) {
    		showAvailablePorts();
    	}
    	
    	LOG.info("Connecting to port {}...", config.getPortName());
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(config.getPortName());
        
		if (portIdentifier.isCurrentlyOwned()) {
			LOG.error("Serial port {} is currently in use, unable to connect", config.getPortName());
		}
		else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				
				LOG.info("Setting serial port parameters: baudrate:{} databits:{} stopbits:{} parity:{}",
						new Object[] { config.getBaudrate(), config.getDatabits(), config.getStopbits(), config.getParity() });
				
				serialPort.setSerialPortParams(
						config.getBaudrate(), config.getDatabits(), config.getStopbits(), config.getParity());

				in = new DataInputStream(serialPort.getInputStream());
				out = new DataOutputStream(serialPort.getOutputStream());

			}
			else {
				throw new IllegalArgumentException("Port " + config.getPortName() + " is not a serial port!");
			}
		}
		
		// TODO: close serialPort en streams
    }
    
    private void showAvailablePorts() {
    	LOG.debug("List of available commPorts...");
    	for (@SuppressWarnings("unchecked")Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements();) {
			CommPortIdentifier id = e.nextElement();
			LOG.debug("  serial port: name:" + id.getName() + " type:" + id.getPortType() + " owner:" + id.getCurrentOwner());
		}
    	LOG.debug("Listing available commPorts complete.");
    }
    
}
