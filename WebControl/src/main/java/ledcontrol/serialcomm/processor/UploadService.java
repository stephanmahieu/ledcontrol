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
import java.nio.file.StandardCopyOption;

import org.apache.james.mime4j.io.BufferedLineReaderInputStream;
import org.apache.james.mime4j.io.MimeBoundaryInputStream;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;

public class UploadService {
    private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

    private static final int BUFFER_SIZE = 1024 * 16;

    @EndpointInject(uri = "direct:sendWebsocketData")
    private ProducerTemplate websocketLog;


    public String handleUpload(final Exchange exchange) throws Exception {
        LOG.info("Processing uploaded file...");

        final String contentDisposition = (String) exchange.getIn().getHeader("Content-Disposition");

        // TODO get additional submitted fields like device
        //final String device =

        final String filename = getFieldFromContentDisposition(contentDisposition, "filename");
        final String fileSize = (String) exchange.getIn().getHeader("Content-Length");

        final InputStream inputStream = getFileInputStream(exchange);

        LOG.info("Handle upload for file [{}] with size [{}] and contentDisposition [{}] ...", filename, fileSize, contentDisposition);

        Path tempFile = Files.createTempFile(filename + "_", ".tmp");
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        LOG.info("File uploaded as [{}] in directory [{}]", tempFile.getFileName().toString(), tempFile.getParent().toString());

        websocketLog.sendBody("File uploaded successfully: " + tempFile.getParent().toString() + File.separator + tempFile.getFileName().toString());

        return "";
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
