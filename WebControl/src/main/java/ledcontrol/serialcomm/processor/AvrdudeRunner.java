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
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AvrdudeRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AvrdudeRunner.class);

    private static final long MAX_TIME_FOR_PROGRAM_TO_FINISH = 30L;
    private static final String[] PATHNAMES = {"Path", "PATH", "path"};
    private static final String[] AVRDUDE_NAMES = {"avrdude.exe", "avrdude"};
    private static final String[] ARDUINO_NAMES = {"arduino.exe", "arduino"};

    @EndpointInject(uri = "direct:sendWebsocketData")
    private ProducerTemplate websocketLog;

    private enum OutType {STDOUT, STDERR}


    public AvrdudeRunner() {
    }

    // TODO actually run avrdude
    public void runAvrdude() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        ProcessBuilder pb = new ProcessBuilder();
        String avrdudeApp = findAvrdudeApp(pb.environment());

        if (avrdudeApp != null) {
            pb.command(avrdudeApp, "-?");
        } else {
            if (isWindows) {
                pb.command("cmd.exe", "/c", "dir /ogen");
            } else {
                pb.command("sh", "-c", "ls -ltr");
            }
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


    private String findAvrdudeApp(Map<String, String> env) {
        List<String> paths = getEnvironmentPaths(env);

        // find avrdude app in the environment paths
        Path avrdudeApp = findAppPath(paths, AVRDUDE_NAMES);

        if (avrdudeApp == null) {
            // did not find avrdude, try to find arduino instead
            Path arduinoApp = findAppPath(paths, ARDUINO_NAMES);

            if (arduinoApp != null) {
                // found arduino, now try to locate avrdude in its subdirectory
                avrdudeApp = findAvrDudeInArduinoDir(arduinoApp);
            }
        }

        if (avrdudeApp != null) {
            websocketLog.sendBody("Found avrdude location: " + avrdudeApp);
        }

        return (avrdudeApp == null) ? null : avrdudeApp.toString();
    }

    private List<String> getEnvironmentPaths(Map<String, String> env) {
        List<String> paths = new ArrayList<>();
        for (String key : PATHNAMES) {
            if (env.containsKey(key)) {
                String path = env.get(key);
                String[] splitpaths = path.split(File.pathSeparator);
                paths.addAll(Arrays.asList(splitpaths));
            }
        }

        // add the homedir
        File homedir = new File(System.getProperty("user.home"));
        paths.add(homedir.getAbsolutePath());

        // add scoop directory if present
        if (env.containsKey("SCOOP")) {
            paths.add(env.get("SCOOP") + "/apps/arduino/current/hardware/tools/avr/bin");
            paths.add(env.get("SCOOP") + "/apps/arduino/current");
        }

        return paths;
    }

    private Path findAvrDudeInArduinoDir(Path arduinoApp) {
        String avrdudeDir = (new File(arduinoApp.getParent().toFile(), "hardware/tools/avr/bin/")).toString();
        return findAppPath(avrdudeDir, AVRDUDE_NAMES);
    }

    private Path findAppPath(final String path, String[] appNames) {
        return findAppPath(new ArrayList<>(Arrays.asList(path)), appNames);
    }

    private Path findAppPath(final List<String> paths, String[] appNames) {
        Path foundApp = null;
        for (String directory : paths) {

            // try to find the app in each dir
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
                for (Path filepath : directoryStream) {
                    //websocketLog.sendBody("  - file: " + filepath.toString());
                    if (foundApp == null) {
                        foundApp = findApp(filepath, appNames);
                    }
                }
            } catch  (NoSuchFileException nsf) {
                LOG.warn("Error finding file in subdir", nsf.getMessage());
            } catch (IOException e) {
                LOG.error("Error finding file in subdir", e.getMessage());
            }
        }
        return foundApp;
    }

    private Path findApp(final Path filepath, final String[] appNames) {
        String filename = filepath.getFileName().toString();
        Path foundApp = null;
        for (String appName : appNames) {
            if (appName.equals(filename) && !filepath.toString().contains("shims")) {
                foundApp = filepath.toAbsolutePath();
            }
        }
        return foundApp;
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
