package org.ado.musicdroid.view;

/*
 * Copyright (c) 2016 Andoni del Olmo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javafx.collections.ObservableListBase;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.ado.musicdroid.AlbumDirectory;
import org.ado.musicdroid.common.AppConfiguration;
import org.ado.musicdroid.common.Mp3Utils;
import org.ado.musicdroid.service.MediaConverterService;
import org.ado.musicdroid.view.about.AboutPresenter;
import org.ado.musicdroid.view.about.AboutView;
import org.ado.musicdroid.view.settings.SettingsPresenter;
import org.ado.musicdroid.view.settings.SettingsView;
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
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.ado.musicdroid.common.AppConstants.EXPORT_DIRECTORY;

/**
 * @author Andoni del Olmo
 * @since 04.03.16
 */
public class AppPresenter implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppPresenter.class);
    private Stage stage;

    private MediaConverterService mediaConverterService;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private ComboBox<JadbDevice> devicesComboBox;

    @FXML
    private ListView<AlbumDirectory> artistListView;

    @FXML
    private Button exportButton;

    @FXML
    private ImageView albumCoverImageView;

    @FXML
    protected Label conversionProcessLabel;

    private List<File> albumList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mediaConverterService = new MediaConverterService();
        mediaConverterService.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                conversionProcessLabel.setText(String.format("Processing \"%s\" ...", newValue.getName()));
            }
        });
        mediaConverterService.setOnSucceeded(event -> {
            conversionProcessLabel.setText("Conversion finished");
            exportButton.setDisable(false);
        });
        mediaConverterService.setOnFailed(event -> {
            conversionProcessLabel.setText("Conversion failed!");
            exportButton.setDisable(false);
            LOGGER.error(event.getSource().exceptionProperty().getValue().toString());
        });

        final List<JadbDevice> devices = getJadbDevices();
        LOGGER.info("Devices [{}]", devices);
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
        devicesComboBox.setConverter(new StringConverter<JadbDevice>() {
            @Override
            public String toString(JadbDevice device) {
                return device.getSerial();
            }

            @Override
            public JadbDevice fromString(String string) {
                return null;
            }
        });
        if (!devicesComboBox.getItems().isEmpty()) {
            devicesComboBox.setValue(devices.get(0));
            exportButton.setDisable(false);
        }
        loadAlbumList();
    }

    private void loadAlbumList() {
        albumList = getLocalAlbums();
        artistListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        artistListView.setItems(getAlbumObservableList(null));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void settings() {
        final Stage stage = new Stage();
        final SettingsView settingsView = new SettingsView();
        final SettingsPresenter presenter = (SettingsPresenter) settingsView.getPresenter();
        presenter.setStage(stage, this::loadAlbumList);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(settingsView.getView()));
        stage.setTitle("Settings");
        stage.show();
    }

    public void about() {
        final Stage stage = new Stage();
        final AboutView aboutView = new AboutView();
        final AboutPresenter presenter = (AboutPresenter) aboutView.getPresenter();
        presenter.setStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(aboutView.getView()));
        stage.setTitle("About");
        stage.show();
    }

    private List<File> getLocalAlbums() {
        final String musicDirectory = AppConfiguration.getConfigurationProperty("music.dir");

        final File directory = new File(musicDirectory);
        if (!directory.exists()) {
            return Collections.emptyList();
        }

        return FileUtils.listFilesAndDirs(directory,
                DirectoryFileFilter.DIRECTORY,
                TrueFileFilter.INSTANCE)
                .stream()
                .filter(file -> !file.getAbsolutePath().equals(musicDirectory))
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .collect(Collectors.toList());
    }

    private ObservableListBase<AlbumDirectory> getAlbumObservableList(String searchSequence) {
        return new ObservableListBase<AlbumDirectory>() {

            private List<AlbumDirectory> list = getAlbumDirectories();

            private List<AlbumDirectory> getAlbumDirectories() {
                if (StringUtils.isNotBlank(searchSequence)) {
                    return getFilteredAlbumList();
                } else {
                    return albumList.stream().map(AlbumDirectory::new).collect(Collectors.toList());
                }
            }

            private List<AlbumDirectory> getFilteredAlbumList() {
                return albumList.stream()
                        .filter(file -> file.getAbsolutePath().toLowerCase().contains(searchSequence))
                        .map(AlbumDirectory::new)
                        .sorted((o1, o2) -> o1.getFile().getAbsolutePath()
                                .compareTo(o2.getFile().getAbsolutePath()))
                        .collect(Collectors.toList());
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
        try {
            return new JadbConnection().getDevices();
        } catch (IOException | JadbException e) {
            LOGGER.warn(String.format("No Android device found. %s.", e.getMessage()));
        }
        return Collections.emptyList();
    }

    public void onMouseClicked() {
        final List<File> songList = getSelectedSongs();
        if (!songList.isEmpty()) {
            final InputStream albumCoverInputStream = Mp3Utils.getAlbumCover(songList.get(0));
            if (albumCoverInputStream != null) {
                albumCoverImageView.setImage(new Image(albumCoverInputStream));
            } else {
                albumCoverImageView.setImage(null);
            }
        }
    }

    public void onSearch() {
        final String searchSequence = textFieldSearch.getCharacters().toString();
        LOGGER.debug(searchSequence);
        artistListView.setItems(getAlbumObservableList(searchSequence));
    }

    public void onClick() {
        try {
            FileUtils.deleteQuietly(EXPORT_DIRECTORY);
            FileUtils.forceMkdir(EXPORT_DIRECTORY);
        } catch (IOException e) {
            LOGGER.error(String.format("Unable to clear export directory %s", EXPORT_DIRECTORY), e);
        }

        mediaConverterService.setSongFiles(getSelectedSongs());
        mediaConverterService.setJadbDevice(devicesComboBox.getSelectionModel().getSelectedItem());
        if (mediaConverterService.getState().equals(Worker.State.SUCCEEDED)) {
            mediaConverterService.restart();
        } else {
            mediaConverterService.start();
        }
        exportButton.setDisable(true);
    }

    private List<File> getSelectedSongs() {
        final List<File> songList = new ArrayList<>();
        for (Object item : artistListView.getSelectionModel().getSelectedItems().stream()
                .sorted((i1, i2) -> i1.getFile().getName().compareTo(i2.getFile().getName()))
                .collect(Collectors.toList())) {
            songList.addAll(FileUtils.listFiles(((AlbumDirectory) item).getFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
                    .stream()
                    .sorted((i1, i2) -> i1.getName().compareTo(i2.getName()))
                    .collect(Collectors.toList()));
        }
        return songList;
    }
}