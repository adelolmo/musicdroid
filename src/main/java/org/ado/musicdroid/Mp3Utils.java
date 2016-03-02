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

import com.mpatric.mp3agic.Mp3File;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Class description here.
 *
 * @author andoni
 * @since 20.09.2014
 */
public class Mp3Utils {

    public static InputStream getAlbumCover(File file) {
        try {
            final Mp3File song = new Mp3File(file.getAbsolutePath());
            if (song.hasId3v2Tag()) {
                return new ByteArrayInputStream(song.getId3v2Tag().getAlbumImage());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getAlbumCoverMimeType(File file) {
        try {
            final Mp3File song = new Mp3File(file.getAbsolutePath());
            if (song.hasId3v2Tag()) {
                return song.getId3v2Tag().getAlbumImageMimeType();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
