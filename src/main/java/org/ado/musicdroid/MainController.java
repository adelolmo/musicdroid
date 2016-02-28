package org.ado.musicdroid;

import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.ado.musicdroid.service.MediaConverterService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.ado.musicdroid.AppConstants.EXPORT_DIRECTORY;

/**
 * Class description here.
 *
 * @author andoni
 * @since 04.09.2014
 */
public class MainController {

    private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private MediaConverterService mediaConverterService;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private ComboBox devicesComboBox;

    @FXML
    private ListView artistListView;

    @FXML
    private Button exportButton;

    @FXML
    private ImageView albumCoverImageView;

    @FXML
    protected Label conversionProcessLabel;

    private List<File> albumList;

    @FXML
    private void initialize() throws Exception {
        mediaConverterService = new MediaConverterService();
        mediaConverterService.setOnSucceeded(event -> {
            conversionProcessLabel.setText("Conversion finished");
            exportButton.setDisable(false);
        });
        mediaConverterService.setOnRunning(event -> conversionProcessLabel.setText("Converting songs..."));
        mediaConverterService.setOnFailed(event -> {
            conversionProcessLabel.setText("Conversion failed!");
            exportButton.setDisable(false);
            LOGGER.error(event.getSource().exceptionProperty().getValue().toString());
        });

        List<JadbDevice> devices = getJadbDevices();
        LOGGER.info("Devices [" + devices + "]");
        devicesComboBox.setItems(new ObservableListBase<JadbDevice>() {
            @Override
            public JadbDevice get(int index) {
                return devices.get(index);
            }

            @Override
            public int size() {
                return devices.size();
            }
        });
        if (!devicesComboBox.getItems().isEmpty()) {
            devicesComboBox.setValue(devices.get(0));
        }
        albumList = getLocalAlbums();
        artistListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        artistListView.setItems(getAlbumObservableList(null));
    }

    private List<File> getLocalAlbums() {
        Collection<File> files =
                FileUtils.listFilesAndDirs(new File(System.getenv("MUSIC_HOME")),
                        DirectoryFileFilter.DIRECTORY,
                        TrueFileFilter.INSTANCE);
        List<File> fileList = new ArrayList<>();
        fileList.addAll(files);
        fileList.remove(0);
        return fileList;
    }

    private ObservableListBase<AlbumDirectory> getAlbumObservableList(String searchSequence) {


        return new ObservableListBase<AlbumDirectory>() {

            private List<AlbumDirectory> list = getAlbumDirectories();

            private List<AlbumDirectory> getAlbumDirectories() {
                final List<AlbumDirectory> albumDirectories = new ArrayList<>();
                if (StringUtils.isNotBlank(searchSequence)) {

                    return getFilterdAlbumList();

                } else {
                    albumDirectories.addAll(albumList.stream().map(AlbumDirectory::new).collect(Collectors.toList()));
                }
                return albumDirectories;
            }

            private List<AlbumDirectory> getFilterdAlbumList() {
                final List<AlbumDirectory> albumDirectories =
                        albumList.stream()
                                .filter(file -> file.getAbsolutePath().toLowerCase().contains(searchSequence))
                                .map(AlbumDirectory::new)
                                .collect(Collectors.toList());
                albumDirectories
                        .sort((o1, o2) -> o1.getFile().getAbsolutePath()
                                .compareTo(o2.getFile().getAbsolutePath()));
                return albumDirectories;
            }

            @Override
            public AlbumDirectory get(int index) {
                try {
                    return list.get(index);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    private List<JadbDevice> getJadbDevices() {
        List<JadbDevice> deviceList = new ArrayList<>();
        try {
            JadbConnection jadbConnection = new JadbConnection();
            deviceList = jadbConnection.getDevices();
        } catch (IOException | JadbException e) {
            LOGGER.warn(String.format("No Android device found. %s.", e.getMessage()));
        }
        return deviceList;
    }

    public void onMouseClicked() {
        List<File> songList = getSelectedSongs();
        if (!songList.isEmpty()) {
            InputStream albumCoverInputStream = Mp3Utils.getAlbumCover(songList.get(0));
            if (albumCoverInputStream != null) {
                albumCoverImageView.setImage(new Image(albumCoverInputStream));
            } else {
                albumCoverImageView.setImage(null);
            }
        }
    }

    public void onSearch() {
        String searchSequence = textFieldSearch.getCharacters().toString();
        LOGGER.debug(searchSequence);
//        List<File> fileList = getLocalAlbums();
        artistListView.setItems(getAlbumObservableList(searchSequence));

    }

    public void onClick() {
//        Dialogs.showErrorDialog(, "Ooops, there was an error!", "Error Dialog", "title");
        try {
            FileUtils.deleteQuietly(EXPORT_DIRECTORY);
            FileUtils.forceMkdir(EXPORT_DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> songList = getSelectedSongs();

        mediaConverterService.setSongFiles(songList);
        mediaConverterService.setJadbDevice((JadbDevice) devicesComboBox.getSelectionModel().getSelectedItem());
        if (mediaConverterService.getState().equals(Worker.State.SUCCEEDED)) {
            mediaConverterService.restart();
        } else {
            mediaConverterService.start();
        }
        exportButton.setDisable(true);
    }

    private List<File> getSelectedSongs() {
        ObservableList selectedItems = artistListView.getSelectionModel().getSelectedItems();
        List<File> songList = new ArrayList<>();
        for (Object item : selectedItems) {
            File albumDirectory = ((AlbumDirectory) item).getFile();
            songList.addAll(FileUtils.listFiles(albumDirectory, TrueFileFilter.TRUE, TrueFileFilter.TRUE));
        }
        LOGGER.info("selected album(s) " + selectedItems);
        return songList;
    }
}