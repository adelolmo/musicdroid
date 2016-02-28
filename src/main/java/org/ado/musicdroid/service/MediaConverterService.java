package org.ado.musicdroid.service;

import com.avconv4java.core.AVCommand;
import com.avconv4java.core.AVRootOptions;
import com.avconv4java.option.AVAudioOptions;
import com.avconv4java.util.process.ProcessInfo;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.ado.musicdroid.MimeTypeMapping;
import org.ado.musicdroid.Mp3Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.ado.musicdroid.AppConstants.ANDROID_MUSIC_BASE_DIRECTORY;
import static org.ado.musicdroid.AppConstants.EXPORT_DIRECTORY;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Class description here.
 *
 * @author andoni
 * @since 06.09.2014
 */
public class MediaConverterService extends Service<Void> {

    private final Logger LOGGER = LoggerFactory.getLogger(MediaConverterService.class);

    private JadbDevice jadbDevice;
    private File[] songFiles;

    public void setJadbDevice(final JadbDevice jadbDevice) {
        notNull(jadbDevice, "jadbDevice cannot be null");
        this.jadbDevice = jadbDevice;
    }

    public void setSongFiles(final Collection<File> songFiles) {
        notNull(songFiles, "songFiles cannot be null");
        this.songFiles = songFiles.toArray(new File[songFiles.size()]);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            private final List<String> albumCoverProcessedList = new ArrayList<>();

            @Override
            protected Void call() throws Exception {
                for (final File songFile : songFiles) {
                    copyAlbumCoverIfNeeded(songFile);
                    jadbDevice.push(convertSong(songFile), new RemoteFile(getRemoteLocation(songFile)));
                }
                return null;
            }

            private void copyAlbumCoverIfNeeded(File songFile) throws JadbException, IOException {
                final String albumName = getAlbumRelativePath(songFile);
                if (!albumCoverProcessedList.contains(albumName)) {
                    final InputStream inputStream = Mp3Utils.getAlbumCover(songFile);
                    if (inputStream != null) {
                        File tempFile = File.createTempFile("cover", "image");
                        FileUtils.copyInputStreamToFile(inputStream, tempFile);
                        jadbDevice.push(tempFile,
                                new RemoteFile(
                                        getAlbumCoverRemoteLocation(songFile,
                                                MimeTypeMapping.getFileExtension(Mp3Utils.getAlbumCoverMimeType(songFile)))));
                    }
                    albumCoverProcessedList.add(albumName);
                }
            }

            private String getAlbumCoverRemoteLocation(File songFile, String fileExtension) {
                return ANDROID_MUSIC_BASE_DIRECTORY + getAlbumRelativePath(songFile) + "cover." + fileExtension;
            }

            private String getAlbumRelativePath(File songFile) {
                String s = songFile.getAbsolutePath().substring(System.getenv("MUSIC_HOME").length());
                return s.substring(0, s.lastIndexOf("/") + 1);
            }

            private File convertSong(final File songFile) throws Exception {
                final AVRootOptions options =
                        AVRootOptions.create(songFile.getAbsolutePath(), getExportDirectory(songFile))
                                .builders(
                                        AVAudioOptions.create().audioBitRate(128)
                                );
                final AVCommand command = new AVCommand()
                        .setDebug(true)
                        .setTimeout(10 * 60 * 60 * 1000L);

                LOGGER.info("Convert song [" + songFile.getAbsolutePath() + "]");
                final ProcessInfo processInfo = command.run(options);
                final String outputFile = options.getOutputFile();

                LOGGER.info(String.format("Output file: %s, return code: %d", outputFile, processInfo.getStatusCode()));
                if (processInfo.getStatusCode() != 0) {
                    LOGGER.error(processInfo.toString());
                }
                return new File(outputFile);
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
                return exportFile.getAbsolutePath();
            }

            private String getRemoteLocation(File file) {
                return ANDROID_MUSIC_BASE_DIRECTORY + file.getAbsolutePath().substring(System.getenv("MUSIC_HOME").length());
            }
        };
    }
}