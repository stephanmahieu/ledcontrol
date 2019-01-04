package camel.serial;

import java.io.OutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Suspendable;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SerialProducer extends DefaultProducer implements Suspendable {

    private static final Logger LOG = LoggerFactory.getLogger(SerialProducer.class);

    private static final boolean DO_TIMESLOT_REQUEST = true;

    private static final byte[] TIMESLOT_REQUEST = {0x1};

    private OutputStream out;
    private SerialEndpoint endpoint;
    

    public SerialProducer(SerialEndpoint endpoint, OutputStream out) {
        super(endpoint);
        this.endpoint = endpoint;
        this.out = out;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (out == null) {
            LOG.error("Can not send serial data, no OutputStream!");
            return;
        }

        String dataMessage;
        if (exchange.hasOut()) {
            dataMessage = exchange.getOut().getBody(String.class);
        } else {
            dataMessage = exchange.getIn().getBody(String.class);
        }

        if (!dataMessage.endsWith("\n")) {
            dataMessage += "\n";
        }

        if (DO_TIMESLOT_REQUEST) {
            out.write(TIMESLOT_REQUEST);
            out.flush();
            delayMillis(15);
        }
        out.write(dataMessage.getBytes());

        LOG.debug("Serial data sent: [{}]", dataMessage.toString());
    }

    private void delayMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.warn("Sleep failure", e);
        }
    }

//	@Override
//	protected void doStop() throws Exception {
//		super.doStop();
//
//		if (out != null) {
//	        try {
//				LOG.debug("Close outputstream for {}", URISupport.sanitizeUri(endpoint.getEndpointUri()));
//				out.close();
//			} catch (IOException e) {
//				LOG.warn("Error closing input stream", e);
//			} finally {
//				out = null;
//			}
//        }
//        
//        try {
//			endpoint.stop();
//		} catch (Exception e) {
//			LOG.error("Error stopping endpoint", e);
//		}
//	}

    @Override
    protected void doSuspend() {
        LOG.debug("Suspend SerialProducer...");
        try {
            endpoint.serialSuspend();
            super.doSuspend();
        } catch (Exception e) {
            LOG.error("Error suspending SerialProducer", e);
        }
    }

    @Override
    protected void doResume() {
        LOG.debug("Resume SerialProducer...");
        try {
            endpoint.serialResume();
            super.doResume();
        } catch (Exception e) {
            LOG.error("Error resuming SerialProducer", e);
        }
    }

    protected void setOut(OutputStream out) {
        this.out = out;
    }

    @Override
    public String toString() {
        return "SerialProducer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
    }

}
