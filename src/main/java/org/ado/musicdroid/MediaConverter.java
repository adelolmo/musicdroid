package org.ado.musicdroid;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.ado.musicdroid.AppConstants.EXPORT_DIRECTORY;

/**
 * Class description here.
 *
 * @author andoni
 * @since 05.09.2014
 */
public class MediaConverter {

    private final Logger LOGGER = LoggerFactory.getLogger(MediaConverter.class);
    private static final String SHELL = "/bin/sh";
    private static final String AVCONV_COMMAND = "/usr/bin/avconv -i %s -ab %s %s";

    private static List<File> convertionQueue;
    private final Object lock = new Object();
    private MainController mainController;

    public MediaConverter(MainController mainController) {
        this.mainController = mainController;
        convertionQueue = new ArrayList<>();
    }

    private MediaConverterListener mediaConverterListener;

    public void setMediaConverterListener(MediaConverterListener mediaConverterListener) {
        this.mediaConverterListener = mediaConverterListener;
    }

    public void convertSong(File songFile) {

        // avconv -i "$MUSIC_HOME"path -ab $BIT_RATE "$TMP_MUSIC_DIR"path

//        executeCommand(songFile);
        executeRuntimeCommand(songFile);
    }

    private void executeRuntimeCommand(File songFile) {
        String export = String.format(AVCONV_COMMAND,
                Unix.escapePath(songFile),
                "128",
                getExportDirectory(songFile));
        try {
//            Process tr = Runtime.getRuntime().exec(new String[]{export});
            String s = executeCommand(export);
            LOGGER.info(s);
        } catch (IOException e) {
            LOGGER.error("Unable to convert song file \"" + songFile.getAbsolutePath() + "\".", e);
        }

    }

    private String executeCommand(String command) throws IOException {

        LOGGER.info("command line [" + command + "]");
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            IOUtils.readLines(p.getInputStream(), Charset.forName("UTF-8")).forEach(output::append);

        } catch (InterruptedException e) {
            LOGGER.error("Cannot execute command", e);
        }

        return output.toString();

    }

    private void executeCommand(File songFile) {
        CommandLine commandLine = new CommandLine(SHELL);
        commandLine.addArguments(new String[]{"-c",
                String.format(AVCONV_COMMAND, Unix.escapePath(songFile), "128", getExportDirectory(songFile))}
                , false);

        LOGGER.info("command line [" + commandLine + "]");

        DefaultExecutor defaultExecutor = new DefaultExecutor();
        try {
            defaultExecutor.execute(commandLine, new SongConverterResultHandler(songFile));
        } catch (IOException e) {
            LOGGER.error("Unable to convert song file \"" + songFile.getAbsolutePath() + "\".", e);
        }
    }

    private String getExportDirectory(File songFile) {
        File exportFile = new File(EXPORT_DIRECTORY, songFile.getAbsolutePath().substring(System.getenv("MUSIC_HOME").length()));
        File exportDirectory = new File(FilenameUtils.getFullPath(exportFile.getAbsolutePath()));
        if (!exportDirectory.exists()) {
            try {
                FileUtils.forceMkdir(exportDirectory);
            } catch (IOException e) {
                // ignore
            }
        }
        return Unix.escapePath(exportFile);
    }

    private void exec(String command) throws IOException {
        Process tr = Runtime.getRuntime().exec(new String[]{"cat"});
        Writer wr = new OutputStreamWriter(tr.getOutputStream());
        BufferedReader rd = new BufferedReader(new InputStreamReader(tr.getInputStream()));
        wr.write(command + "\n");
        wr.flush();
        String s = rd.readLine();
        System.out.println(s);
    }

    private class SongConverterResultHandler implements ExecuteResultHandler {

        private File songFile;

        public SongConverterResultHandler(File songFile) {
            this.songFile = songFile;
            synchronized (lock) {
                convertionQueue.add(songFile);
            }
        }

        @Override
        public void onProcessComplete(int exitValue) {
            LOGGER.info("Conversion of song \"" + songFile.getAbsolutePath() + "\" -> " + exitValue);
            notifyProcessFinishedIfNeeded();
        }

        @Override
        public void onProcessFailed(ExecuteException e) {
            LOGGER.error("Conversion of song \"" + songFile.getAbsolutePath() + "\" failed.", e);
            notifyProcessFinishedIfNeeded();
        }

        private void notifyProcessFinishedIfNeeded() {
            synchronized (lock) {
                convertionQueue.remove(songFile);
                if (convertionQueue.isEmpty()) {
//                    mediaConverterListener.processFinished();
                    mainController.conversionProcessLabel.setText("Conversion finished");
                }
            }
        }
    }

    public interface MediaConverterListener {
        void processFinished();
    }
}