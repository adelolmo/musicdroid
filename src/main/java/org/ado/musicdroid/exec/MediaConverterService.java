package org.ado.musicdroid.exec;

import com.avconv4java.core.AVCommand;
import com.avconv4java.core.AVRootOptions;
import com.avconv4java.option.AVAudioOptions;
import com.avconv4java.util.process.ProcessInfo;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.ado.musicdroid.AndroidAdbCommand;
import org.ado.musicdroid.MimeTypeMapping;
import org.ado.musicdroid.Mp3Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vidstige.jadb.JadbDevice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.ado.musicdroid.AppConstants.*;
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
    private AndroidAdbCommand androidAdbCommand;

    public void setJadbDevice(final JadbDevice jadbDevice) {
        notNull(jadbDevice, "jadbDevice cannot be null");
        this.jadbDevice = jadbDevice;
        androidAdbCommand = new AndroidAdbCommand();
    }

    public void setSongFiles(final Collection<File> songFiles) {
        notNull(songFiles, "songFiles cannot be null");
        this.songFiles = songFiles.toArray(new File[songFiles.size()]);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<String> albumCoverProcessedList = new ArrayList<>();
                for (File songFile : songFiles) {
                    try {
                        copyAlbumCoverIfNeeded(albumCoverProcessedList, songFile);
                        androidAdbCommand.copyToRemote(convertSong(songFile), jadbDevice, getRemoteLocation(songFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            private void copyAlbumCoverIfNeeded(List<String> albumCoverProcessedList, File songFile) {
                String albumName = getAlbumRelativePath(songFile);
                if (!albumCoverProcessedList.contains(albumName)) {
                    InputStream inputStream = Mp3Utils.getAlbumCover(songFile);
                    if (inputStream != null) {
                        String mimeType = Mp3Utils.getAlbumCoverMimeType(songFile);
                        String fileExtension = MimeTypeMapping.getFileExtension(mimeType);
                        try {
                            File tempFile = File.createTempFile("cover", "image");
                            FileUtils.copyInputStreamToFile(inputStream, tempFile);
                            androidAdbCommand.copyToRemote(tempFile, jadbDevice, getAlbumCoverRemoteLocation(songFile, fileExtension));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    albumCoverProcessedList.add(albumName);
                }
            }

            private String getAlbumCoverRemoteLocation(File songFile, String fileExtension) {
                return ANDROID_MUSIC_BASE_DIRECTORY + getAlbumRelativePath(songFile) + "cover." + fileExtension;
            }

            private String getAlbumRelativePath(File songFile) {
                String s = songFile.getAbsolutePath().substring(LOCAL_MUSIC_BASE_DIRECTORY.length());
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
                ProcessInfo processInfo = command.run(options);
                final String outputFile = options.getOutputFile();

                LOGGER.info(String.format("Output file: %s, return code: %d", outputFile, processInfo.getStatusCode()));
                if (processInfo.getStatusCode() != 0) {
                    LOGGER.error(processInfo.toString());
                }
                return new File(outputFile);
            }

            private String getExportDirectory(File songFile) {
                File exportFile = new File(EXPORT_DIRECTORY, songFile.getAbsolutePath().substring(LOCAL_MUSIC_BASE_DIRECTORY.length()));
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
                return ANDROID_MUSIC_BASE_DIRECTORY + file.getAbsolutePath().substring(LOCAL_MUSIC_BASE_DIRECTORY.length());
            }
        };
    }
}