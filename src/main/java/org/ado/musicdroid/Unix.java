package org.ado.musicdroid;

import java.io.File;

/**
 * Class description here.
 *
 * @author andoni
 * @since 05.09.2014
 */
public class Unix {

    public static String escapePath(File file) {
        return escapePath(file.getAbsolutePath());
    }

    public static String escapePath(String path) {
        return path.replace(" ", "\\ ")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("'", "\\'");
    }

    public static String wrapPath(File file) {
        return wrapPath(file.getAbsolutePath());
    }

    public static String wrapPath(String remoteLocation) {
        return "\"" + remoteLocation + "\"";
    }
}
