package org.ado.musicdroid.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
 * Class to access application properties and handle user's configuration options.
 * It also contains some application constants.
 *
 * @author andoni
 * @since 22.11.2014
 */
public class AppConfiguration {

    private static final File APP_CONFIG_DIRECTORY = new File(FileUtils.getUserDirectory(), ".musicdroid");
    private static final File CONFIG = new File(APP_CONFIG_DIRECTORY, "musicdroid.properties");

    private static Properties config;

    public static void setConfigurationProperty(String property, String value) {
        init();
        config.put(property, value);
        store();
    }

    public static String getConfigurationProperty(String property) {
        init();
        return config.getProperty(property);
    }

    private static void store() {
        try {
            config.store(FileUtils.openOutputStream(CONFIG), "Music Droid Configuration");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save application configuration file", e);
        }
    }

    private static void init() {
        config = loadFileProperties(CONFIG, true);
        store();
    }

    private static Properties loadFileProperties(File file, boolean createIfNotExist) {
        Properties prop = new Properties();
        try {
            if (!file.exists()) {
                if (createIfNotExist) {
                    FileUtils.touch(file);
                } else {
                    throw new IOException(String.format("Properties file \"%s\" does not exits.", file.getAbsolutePath()));
                }
            }
            prop.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read application configuration file", e);
        }
        return prop;
    }
}
