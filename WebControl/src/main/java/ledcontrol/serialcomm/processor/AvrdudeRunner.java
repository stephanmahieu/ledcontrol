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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AvrdudeRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AvrdudeRunner.class);

    private static final long MAX_TIME_FOR_PROGRAM_TO_FINISH = 30L;

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
            pb.command("cmd.exe", "/c", "dir /ogen");
        } else {
            pb.command("sh", "-c", "ls -ltr");
        }
        pb.directory(new File(System.getProperty("user.home")));

        try {
            websocketLog.sendBody("Running program...");
            LOG.info("Running program...");

            Process process = pb.start();

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute( createOutputThread(process.getInputStream(), OutType.STDOUT));
            executor.execute( createOutputThread(process.getErrorStream(), OutType.STDERR));

            Integer exitValue = awaitProgramFinish(process);
            awaitOutput(executor);

            if (exitValue != null) {
                LOG.info("Program finished with exitcode " + exitValue);
                websocketLog.sendBody("Program finished with exitcode " + exitValue);
            } else {
                LOG.info("Program finished abnormally");
                websocketLog.sendBody("Program finished abnormally");
            }
        } catch (IOException e) {
            LOG.error("Program execution failure", e);
        }
    }

    private Thread createOutputThread(final InputStream inputStream, final OutType type) {
        return new Thread(() -> {
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
    }

    private void awaitOutput(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(MAX_TIME_FOR_PROGRAM_TO_FINISH, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("Output prematurely interrupted", e);
        }
    }

    private Integer awaitProgramFinish(Process process) {
        Integer exitValue = null;
        try {
            if (process.waitFor(MAX_TIME_FOR_PROGRAM_TO_FINISH, TimeUnit.SECONDS)) {
                exitValue = process.exitValue();
            } else {
                LOG.error("Waiting time elapsed before the subprocess has exited");
            }
        }
        catch(InterruptedException e) {
            LOG.error("Program thread interrupted while waiting for subprocess to finish", e);
        }
        return exitValue;
    }
}
