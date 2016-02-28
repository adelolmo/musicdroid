package org.ado.musicdroid;

import java.util.HashMap;
import java.util.Map;

/**
 * Class description here.
 *
 * @author andoni
 * @since 20.09.2014
 */
public class MimeTypeMapping {

    private static Map<String, String> mapping = getMapping();

    private static Map<String, String> getMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put("image/jpeg", "jpg");
        map.put("image/png", "png");
        map.put("image/gif", "gif");
        map.put("image/tiff", "tif");
        map.put("image/svg+xml", "svg");
        map.put("image/svg-xml", "svg");
        return map;
    }

    public static String getFileExtension(String mimeType) {
        return mapping.get(mimeType);
    }
}
