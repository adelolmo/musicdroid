package org.ado.musicdroid;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.RemoteFile;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.assertEquals;

@Ignore
public class AndroidFilesTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private AndroidFiles unitUnderTest;

    private File exportDirectory;
    private JadbDevice device;

    @Before
    public void setUp() throws Exception {
        unitUnderTest = new AndroidFiles();

        exportDirectory = temporaryFolder.newFolder("export");
        new File(exportDirectory, "song_one.mp3");
        new File(exportDirectory, "song_two.mp3");
//        FileUtils.write(file, "test data content");

        JadbConnection jadbConnection = new JadbConnection();
        device = jadbConnection.getAnyDevice();

    }

    @Test
    public void testCopyDirectoryToRemoteDirectory() throws Exception {
        unitUnderTest.copyDirectoryToRemoteDirectory(exportDirectory, device, "/sdcard/");

        ByteArrayOutputStream destination = new ByteArrayOutputStream();
        device.pull(new RemoteFile("/sdcard/song_one.mp3"), destination);
        assertEquals("song_one.mp3 content", "song one content", destination.toString());
    }
}