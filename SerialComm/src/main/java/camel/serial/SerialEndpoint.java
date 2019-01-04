package camel.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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

	private CommPortIdentifier portIdentifier;

	private SerialPort serialPort;
	private InputStream in;
	private OutputStream out;

	private SerialConsumer consumer = null;
	private SerialProducer producer = null;

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
		consumer = new SerialConsumer(this, processor, serialPort, in);
		return consumer;
	}

	@Override
	public Producer createProducer() {
		producer = new SerialProducer(this, out);
		return producer;
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

		List<String> serialPorts = new ArrayList<>();
		if (config.getPortName().equalsIgnoreCase("auto")) {
			LOG.info("Attempting to connect to automatically discovered port");
			serialPorts.addAll(getAvailablePorts());
		}
		else {
			serialPorts.add(config.getPortName());
		}

		serialPort = null;

		for (String portName: serialPorts) {

			LOG.info("Attempting to connect to port {}...", portName);

			try {
				portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

				if (portIdentifier.isCurrentlyOwned()) {
					throw new PortInUseException();
				}
				else {
					connect();
				}
			}
			catch(NoSuchPortException e) {
				LOG.error("Serial port {} does not exist, unable to connect", portName);
			}
			catch(PortInUseException pe) {
				LOG.error("Serial port {} is currently in use, unable to connect", portName);
			}
			catch (IllegalArgumentException ie) {
				LOG.error("Port {} is not a serial port!", portName);
			}

			if (serialPort != null) {
				LOG.info("Connection to port {} succeeded", portName);
				break;
			}
		}

		// TODO: close serialPort and streams
	}

	private void connect() throws Exception {
		CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

		if (commPort instanceof SerialPort) {
			serialPort = (SerialPort) commPort;

			LOG.info("Setting serial port parameters: baudrate:{} databits:{} stopbits:{} parity:{}",
					new Object[]{config.getBaudrate(), config.getDatabits(), config.getStopbits(), config.getParity()});

			serialPort.setSerialPortParams(
					config.getBaudrate(), config.getDatabits(), config.getStopbits(), config.getParity());

			in = new DataInputStream(serialPort.getInputStream());
			out = new DataOutputStream(serialPort.getOutputStream());
		} else {
			throw new IllegalArgumentException("Port " + config.getPortName() + " is not a serial port!");
		}
	}

	private void disconnect() throws Exception {
		in.close();
		out.close();
		serialPort.close();
	}

	protected void serialSuspend() throws Exception {
		disconnect();
	}

	protected void serialResume() throws Exception {
		connect();

		if (consumer != null) {
			consumer.setIn(in);
		}

		if (producer != null) {
			consumer.setSerialPort(serialPort);
			producer.setOut(out);
		}
	}

	private void showAvailablePorts() {
		LOG.debug("List of available commPorts...");
		for (@SuppressWarnings("unchecked") Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); ) {
			CommPortIdentifier id = e.nextElement();
			LOG.debug("  serial port: name:" + id.getName() + " type:" + id.getPortType() + " owner:" + id.getCurrentOwner());
		}
		LOG.debug("Listing available commPorts complete.");
	}

	private List<String> getAvailablePorts() {
		List<String> availablePorts = new ArrayList<>();
		for (@SuppressWarnings("unchecked") Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); ) {
			CommPortIdentifier id = e.nextElement();
			availablePorts.add(id.getName());
		}
		Collections.reverse(availablePorts);
		return availablePorts;
	}

}