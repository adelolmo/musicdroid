package org.ado.musicdroid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vidstige.jadb.JadbDevice;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class description here.
 *
 * @author andoni
 * @since 10.09.2014
 */
public class AndroidAdbCommand {

    private final Logger LOGGER = LoggerFactory.getLogger(AndroidAdbCommand.class);
    private Runtime runtime;

    public AndroidAdbCommand() {
        runtime = Runtime.getRuntime();
    }

    public String copyToRemote(File file, JadbDevice jadbDevice, String remoteLocation) {
        return copyToRemote(file.getAbsolutePath(), jadbDevice.getSerial(), remoteLocation);
    }

    public String copyToRemote(String src, String deviceSerial, String dest) {
        String[] cmd = {"push", src, dest};
        return "Copy to Remote from " + src + " to " + dest + " " + customExec(cmd, deviceSerial);
    }

    private String customExec(String[] cmd, String device) {
        String retour = "";
        StringBuilder commandString = getCommandString(cmd);
        String[] commandArray = {"adb", "-s", device};
        try {
            List<String> strings = new ArrayList<>(Arrays.asList(commandArray));
            strings.addAll(Arrays.asList(cmd));
            Process p = runtime.exec(strings.toArray(new String[strings.size()]));
            LOGGER.info("adb -s " + device + " shell " + commandString);

            java.io.BufferedReader standardIn = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            java.io.BufferedReader errorIn = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
            String line = "";
            while ((line = standardIn.readLine()) != null) {
                retour += line + "\n";
            }
            while ((line = errorIn.readLine()) != null) { // When success ADB return success state to std.err !
                retour += line + "\n";
            }
        } catch (java.io.IOException e) {
            LOGGER.error("Unable to execute command \"" + commandString + "\".", e);
        }

        return retour;
    }

    private StringBuilder getCommandString(String[] cmd) {
        StringBuilder commandString = new StringBuilder();
        for (String s : cmd) {
            commandString.append(s).append(" ");
        }
        return commandString;
    }
}
