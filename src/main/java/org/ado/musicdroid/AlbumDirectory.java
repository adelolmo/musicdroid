package org.ado.musicdroid;

import java.io.File;

import static org.ado.musicdroid.AppConstants.LOCAL_MUSIC_BASE_DIRECTORY;

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
        return file.getAbsolutePath().substring(LOCAL_MUSIC_BASE_DIRECTORY.length());
    }
}
