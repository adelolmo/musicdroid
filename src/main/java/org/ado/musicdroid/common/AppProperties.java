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

package org.ado.musicdroid.common;

import java.io.IOException;
import java.util.Properties;

/**
 * Class description here.
 *
 * @author andoni
 * @since 05.09.2014
 */
public class AppProperties {

    private static Properties properties = new Properties();

    public static String getProperty(String key) {
        try {
            init();
            return (String) properties.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void init() throws IOException {
        if (properties.isEmpty()) {
            properties.load(AppProperties.class.getResourceAsStream("musicdroid.properties"));
        }
    }
}
