package org.ado;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Class description here.
 *
 * @author andoni
 * @since 05.09.2014
 */
@Ignore
public class RuntimeTest {

    private static final File EXPORT = new File(FileUtils.getTempDirectory(), "export");

    @Before
    public void setup() throws IOException {
        FileUtils.cleanDirectory(EXPORT);
        FileUtils.forceMkdir(new File(EXPORT, "Faithless/Sunday 8pm"));
    }

    @Test
    public void testRuntime_cat() throws IOException {
        String s = execute(new String[]{"cat"}, "hello, world\n");
        System.out.println(s);
    }

    @Test
    @Ignore
    public void testRuntime_ls() throws IOException {
        String s = execute(new String[]{"ls /tmp/export/"});
        System.out.println(s);
    }

    @Test
    public void testAvconv() throws Exception {
        System.out.println(execute(new String[]{
                "/usr/bin/avconv " +
                        "-i " +
                        "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\" " +
                        "-ab " +
                        "128 " +
                        "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\""}));
    }

    @Test
    public void testAvconv_params() throws Exception {
        System.out.println(execute(new String[]{
                        "/usr/bin/avconv"},
                "-i " +
                        "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\" " +
                        "-ab " +
                        "128 " +
                        "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\""));
    }

    @Test
    public void testAvconv_binSh() throws Exception {
//        System.out.println(executeCommand("/usr/bin/avconv"));
        // new String[]{"/bin/sh", "-c"},
        System.out.println(executeCommand(
                "/usr/bin/avconv " +
                        "-i " +
                        "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\" " +
                        "-ab " +
                        "128 " +
                        "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\""));
    }

    @Test
    public void testAvconv_binSh_c() throws Exception {
        System.out.println(execute(new String[]{"/bin/sh", "-c",
                "/usr/bin/avconv",
                "-i",
                "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"",
                "-ab",
                "128",
                "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\""}));
    }

    @Test
    public void testAvconv_array() throws Exception {
        System.out.println(execute(new String[]{
                "/usr/bin/avconv",
                "-i",
                "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"",
                "-ab",
                "128",
                "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\""}));
    }

    @Test
    public void testAvconv_executeMore() throws IOException {
        executeMore("/usr/bin/avconv " +
                "-i " +
                "'/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3'" +
                "-ab " +
                "128 " +
                "/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3");
    }

    @Test
    public void testAvconv_ex() throws IOException {
        ex("/usr/bin/avconv " +
                "-i " +
                "'/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3'" +
                "-ab " +
                "128 " +
                "/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3");
    }

    @Test
    public void testAvconv_exec() throws IOException {
        exec("/usr/bin/avconv " +
                "-i " +
                "'/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3'" +
                "-ab " +
                "128 " +
                "/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3");
    }

    @Test
    public void testAvconv_processBuilder() throws IOException {
        ProcessBuilder pb =
                new ProcessBuilder("/usr/bin/avconv",
                        "-i \"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"",
                        "-ab 128",
                        "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"");
        pb.start();
    }

    @Test
    public void testAvconv_processBuilder_params() throws IOException {
        ProcessBuilder pb =
                new ProcessBuilder("/usr/bin/avconv",
                        "-i",
                        "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"",
                        "-ab",
                        "128",
                        "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"");
        Process process = pb.start();
        StringBuffer output = new StringBuffer();
        IOUtils.readLines(process.getInputStream(), Charset.forName("UTF-8")).forEach(output::append);
        System.out.println(output);
    }

    @Test
    public void testAvconv_processBuilder_singleParam() throws IOException {
        ProcessBuilder pb =
                new ProcessBuilder("/bin/bash -c /usr/bin/avconv ",
                        "-i " +
                                "\"/media/ich/Dropbox/Musica/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\" " +
                                "-ab " +
                                "128 " +
                                "\"/tmp/export/Faithless/Sunday 8pm/03 - Faithless - Hour Of Need.mp3\"");
        Process process = pb.start();
        StringBuffer output = new StringBuffer();
        IOUtils.readLines(process.getInputStream(), Charset.forName("UTF-8")).forEach(output::append);
        System.out.println(output);
    }

    @Test
    public void testLastChance() throws InterruptedException, IOException {

        Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "ls /tmp/export"});

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        int exitVal = p.waitFor();
        System.out.println("Exited with error code " + exitVal);

    }

    @Test
    public void testAvconv_LastChance() throws InterruptedException, IOException {

        Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c",
                "/usr/bin/avconv -i /tmp/song.mp3 -ab 128 /tmp/export/song_converted.mp3"
        });

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        int exitVal = p.waitFor();
        System.out.println("Exited with error code " + exitVal);

    }

    @Test
    public void testAvconv_LastChance_songWithSpaces() throws InterruptedException, IOException {

        Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c",
                "/usr/bin/avconv -i /tmp/song\\ good.mp3 -ab 128 /tmp/export/song\\ converted.mp3"
        });

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        int exitVal = p.waitFor();
        System.out.println("Exited with error code " + exitVal);

    }

    private String execute(String[] cmdarray) throws IOException {
        return execute(cmdarray, null);
    }

    private String execute(String[] cmdarray, String parameters) throws IOException {
        Process tr = Runtime.getRuntime().exec(cmdarray);
        Writer wr = new OutputStreamWriter(tr.getOutputStream());
        BufferedReader rd = new BufferedReader(new InputStreamReader(tr.getInputStream()));
//        StringBuffer output = new StringBuffer();
//        IOUtils.readLines(tr.getErrorStream(), Charset.forName("UTF-8")).forEach(output::append);
//        System.out.println("errors: " + output);
        if (parameters != null) {
            wr.write(parameters);
            wr.flush();
        }
        return rd.readLine();
    }

    private String executeCommand(String command) throws Exception {

        System.out.println("command line [" + command + "]");
        StringBuffer output = new StringBuffer();

        Runtime.getRuntime().exec(command);
        IOUtils.readLines(Runtime.getRuntime().exec(command).getInputStream(), Charset.forName("UTF-8")).forEach(output::append);
        return output.toString();
    }


    private void executeMore(String command) throws IOException {
                    /* Create process */
        Process p = Runtime.getRuntime().exec(command);

            /* Get OuputStream */
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(p.getOutputStream())), true);

            /* Read the output of command prompt */
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
            /* Read upto end of execution */
        while (line != null) {
                /* Pass the value to command prompt/user input */
//            writer.println("08-08-2014");
            System.out.println(line);
            line = reader.readLine();
        }
            /* The stream obtains data from the standard output stream of the process represented by this Process object. */
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            /* The stream obtains data from the error output stream of the process represented by this Process object. */
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String Input;
        while ((Input = stdInput.readLine()) != null) {
            System.out.println(Input);
        }

        String Error;
        while ((Error = stdError.readLine()) != null) {
            System.out.println(Error);
        }
    }

    private String ex(String command) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    private void exec(String command) throws IOException {
        Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", command});
    }
}
