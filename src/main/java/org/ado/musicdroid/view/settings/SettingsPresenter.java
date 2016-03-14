package org.ado.musicdroid.view.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.ado.musicdroid.common.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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

/**
 * @author Andoni del Olmo,
 * @since 20.12.14
 */
public class SettingsPresenter implements Initializable {

    private final Logger LOGGER = LoggerFactory.getLogger(SettingsPresenter.class);

    @FXML
    private TextField textFieldMusicDirectory;

    private Stage stage;
    private SettingsEventListener listener;
    private final DirectoryChooser fileChooser = new DirectoryChooser();

    public void setStage(Stage stage, SettingsEventListener listener) {
        this.stage = stage;
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldMusicDirectory.setText(AppConfiguration.getConfigurationProperty("music.dir"));
    }

    public void save() {
        AppConfiguration.setConfigurationProperty("music.dir", textFieldMusicDirectory.getText());
        listener.configurationChange();
    }

    public void close() {
        stage.close();
    }

    public void browseMusicDirectory() {
        configureDirectoryChooser(fileChooser);
        final File file = fileChooser.showDialog(stage);
        if (file != null) {
            LOGGER.debug("Directory {}", file.getAbsolutePath());
            textFieldMusicDirectory.setText(file.getAbsolutePath());
        }
    }

    private void configureDirectoryChooser(DirectoryChooser fileChooser) {
        fileChooser.setTitle("Music Directory");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public interface SettingsEventListener {
        void configurationChange();
    }
}