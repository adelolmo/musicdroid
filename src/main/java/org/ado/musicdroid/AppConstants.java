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
