package ledcontrol.serialcomm.processor;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class AvrdudeRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AvrdudeRunner.class);

    @EndpointInject(uri = "direct:sendWebsocketData")
    private ProducerTemplate websocketLog;

    private enum OutType {STDOUT, STDERR}


    public AvrdudeRunner() {
    }

    // TODO actually run avrdude
    public void runAvrdude() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        ProcessBuilder pb = new ProcessBuilder();
        if (isWindows) {
            pb.command("cmd.exe", "/c", "dir");
        } else {
            pb.command("sh", "-c", "ls");
        }
        pb.directory(new File(System.getProperty("user.home")));

        try {
            websocketLog.sendBody("Running program...");
            LOG.info("Running program...");
            Process process = pb.start();

            captureOutput(process.getInputStream(), OutType.STDOUT);
            captureOutput(process.getErrorStream(), OutType.STDERR);

            // Wait for the exit value
            try {
                if (process.waitFor(30L, TimeUnit.SECONDS)) {
                    int exitValue = process.exitValue();
                    websocketLog.sendBody("Program finished with exitcode " + exitValue);

                    // TODO handle exitcode
                } else {
                    // timeout
                    websocketLog.sendBody("Program timeout!");
                }
            }
            finally {
                process.destroy();
            }
        } catch (IOException|InterruptedException e) {
            LOG.error("Program execution failure", e);
        }
    }

    private void captureOutput(final InputStream inputStream, final OutType type) {
        Thread outputThread = new Thread(() -> {
            try (BufferedReader brOut = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = brOut.readLine()) != null) {
                    final String msg = type.name() + ": " + line;
                    if (OutType.STDERR.equals(type)) {
                        LOG.error(msg);
                    } else {
                        LOG.info(msg);
                    }
                    websocketLog.sendBody(msg);
                }
            } catch (IOException e) {
                LOG.error("Error capturing " + type.name(), e);
            }
        });
        outputThread.start();
    }

}
