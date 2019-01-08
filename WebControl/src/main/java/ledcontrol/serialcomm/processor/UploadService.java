package ledcontrol.serialcomm.processor;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.james.mime4j.io.BufferedLineReaderInputStream;
import org.apache.james.mime4j.io.MimeBoundaryInputStream;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;

public class UploadService {
    private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

    private static final String SERIAL_ROUTE_ID = "receiveSerial";
    private static final String CONTROL_BUS = "controlbus:route?routeId=" + SERIAL_ROUTE_ID;

    private static final String ACTION_SUSPEND = "suspend";
    private static final String ACTION_RESUME = "resume";
    private static final String ACTION_STATUS = "status";

    private static final int BUFFER_SIZE = 1024 * 16;

    @EndpointInject(uri = "direct:sendWebsocketData")
    private ProducerTemplate websocketLog;

    @EndpointInject(uri = "direct:sendStatusRequest")
    private ProducerTemplate sendStatusRequest;

    @Autowired
    private AvrdudeRunner avrdudeRunner;


    public String handleUpload(final Exchange exchange) throws Exception {
        // get path parameters
        final String comport = ((String) exchange.getIn().getHeader("comport")).replaceAll("_", "/");
        final String device = (String) exchange.getIn().getHeader("device");

        LOG.info("Processing uploaded file for device [{}] on COM port [{}]...", device, comport);

        // save submitted file attachment
        final Path uploadedFile = saveAttachment(exchange);

        websocketLog.sendBody("File uploaded successfully: " + uploadedFile.getParent() + File.separator + uploadedFile.getFileName());

        LOG.info("Stopping serial USB connection...");
        String status = performActionOnRoute(exchange, ACTION_SUSPEND);

        websocketLog.sendBody("Normal serial communication is  " + status);

        if ("Suspended".equals(status)) {

            // TODO run avrdude command to upload the new sketch
            websocketLog.sendBody("Uploading sketch to Arduino ("+ device + ") via USB-port " + comport + "...");

            avrdudeRunner.runAvrdude();

            // wait a few seconds for all output to arrive
            sleep(4000);

            // remove the temporary file
            Files.delete(uploadedFile);
            websocketLog.sendBody("Uploaded File removed");

            status = performActionOnRoute(exchange, ACTION_RESUME);
            websocketLog.sendBody("Normal serial communication is  " + status);

            // restore communication by sending status requests, helps flushing the buffers
            sleep(1000);
            sendStatusRequest();
            sleep(1000);
            sendStatusRequest();
        }

        return "";
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.error("Sleep failure", e);
        }
    }

    private String performActionOnRoute(final Exchange exchange, final String action) {
        final ProducerTemplate producer = exchange.getContext().createProducerTemplate();
        producer.sendBody(CONTROL_BUS + "&action=" + action, null);
        return producer.requestBody(CONTROL_BUS + "&action=" + ACTION_STATUS, null, String.class);
    }

    private void sendStatusRequest() {
        sendStatusRequest.sendBody(null);
    }


    private Path saveAttachment(Exchange exchange) throws MessagingException, IOException {
        final String contentDisposition = (String) exchange.getIn().getHeader("Content-Disposition");

        final String filename = getFieldFromContentDisposition(contentDisposition, "filename");
        final String fileSize = (String) exchange.getIn().getHeader("Content-Length");

        final InputStream inputStream = getFileInputStream(exchange);

        LOG.info("Handle upload for file [{}] with size [{}] and contentDisposition [{}] ...", filename, fileSize, contentDisposition);

        final String tempDir = System.getProperty("java.io.tmpdir");
        final Path binFile = Paths.get(tempDir, filename);
        Files.copy(inputStream, binFile, StandardCopyOption.REPLACE_EXISTING);

        LOG.info("File uploaded as [{}] in directory [{}]", binFile.getFileName().toString(), binFile.getParent().toString());
        return binFile;
    }

    /**
     * Gets the content of a given field from a Content-Disposition string, using the assumptions that for a given
     * fieldname the value is contained in the form of fieldName="value"
     *
     * @param contentDisposition
     *            The Content-Disposition string to search in
     * @param field
     *            The name of the field to return the value for
     *
     * @return The value of the filename field, or null if no such field exists
     */
    private static String getFieldFromContentDisposition(final String contentDisposition, final String field) {
        final String[] entries = contentDisposition.split(";");
        for (String entry : entries) {
            entry = entry.trim();
            if (entry.startsWith(field + '=')) {
                return entry.substring(field.length() + 1).replaceAll("\"", "");
            }
        }
        return null;
    }

    private InputStream getFileInputStream(final Exchange exchange) throws MessagingException, IOException {
        final InputStream bodyInputStream = exchange.getIn().getBody(InputStream.class);
        if (bodyInputStream != null) {
            final MimeBodyPart mimeMessage = new MimeBodyPart(bodyInputStream);
            final DataHandler dh = mimeMessage.getDataHandler();
            if (dh != null) {
                final String contentType = (String) exchange.getIn().getHeader("Content-type");
                final ContentType cType = new ContentType(contentType);
                final String boundary = cType.getParameter("boundary");
                return new MimeBoundaryInputStream(new BufferedLineReaderInputStream(dh.getInputStream(), BUFFER_SIZE), boundary);
            }
        }

        return exchange.getIn().getBody(InputStream.class);
    }
}