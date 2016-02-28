package org.ado.musicdroid;

import java.io.File;

/**
 * Class description here.
 *
 * @author andoni
 * @since 20.09.2014
 */
public class AlbumDirectory {
    private File file;

    public AlbumDirectory(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getAbsolutePath().substring(System.getenv("MUSIC_HOME").length() + 1);
    }
}
