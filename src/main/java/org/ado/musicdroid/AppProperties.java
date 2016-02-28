package org.ado.musicdroid;

import java.io.IOException;
import java.util.Properties;

/**
 * Class description here.
 *
 * @author andoni
 * @since 05.09.2014
 */
public class AppProperties {

    private static Properties properties = new Properties();

    public static String getProperty(String key) {
        try {
            init();
            return (String) properties.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void init() throws IOException {
        if (properties.isEmpty()) {
            properties.load(AppProperties.class.getResourceAsStream("musicdroid.properties"));
        }
    }
}
