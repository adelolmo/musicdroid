package org.ado.musicdroid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.File;
import java.io.IOException;

import static org.ado.musicdroid.AppConstants.EXPORT_DIRECTORY;

/**
 * Class description here.
 *
 * @author andoni
 * @since 06.09.2014
 */
public class AndroidFiles {

    public void copyDirectoryToRemoteDirectory(File directory, JadbDevice jadbDevice, String remoteLocation) throws IOException, JadbException {
        for (File file : FileUtils.listFiles(directory, TrueFileFilter.TRUE, TrueFileFilter.TRUE)) {
            jadbDevice.push(file, getRemoteFile(remoteLocation, file));
        }
    }

    private RemoteFile getRemoteFile(String remoteLocation, File file) {
        return new RemoteFile(remoteLocation + file.getAbsolutePath().substring(EXPORT_DIRECTORY.getAbsolutePath().length() + 1));
    }
}