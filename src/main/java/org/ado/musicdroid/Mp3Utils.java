package org.ado.musicdroid;

import com.mpatric.mp3agic.ID3v2;
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
            Mp3File song = new Mp3File(file.getAbsolutePath());
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2tag = song.getId3v2Tag();
                return new ByteArrayInputStream(id3v2tag.getAlbumImage());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getAlbumCoverMimeType(File file) {
        try {
            Mp3File song = new Mp3File(file.getAbsolutePath());
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2tag = song.getId3v2Tag();
                return id3v2tag.getAlbumImageMimeType();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
