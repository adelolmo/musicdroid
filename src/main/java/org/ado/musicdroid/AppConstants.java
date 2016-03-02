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

package org.ado.musicdroid;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Class description here.
 *
 * @author andoni
 * @since 05.09.2014
 */
public class AppConstants {
    public static final File EXPORT_DIRECTORY = new File(FileUtils.getTempDirectory(), "export");
    public static final String ANDROID_MUSIC_BASE_DIRECTORY =
            AppProperties.getProperty("android.music.basedir").endsWith("/") ?
                    AppProperties.getProperty("android.music.basedir") :
                    AppProperties.getProperty("android.music.basedir") + "/";
}
