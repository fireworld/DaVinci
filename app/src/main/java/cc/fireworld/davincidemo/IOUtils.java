package cc.fireworld.davincidemo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by cxx on 15/12/1.
 * email: xx.ch@outlook.com
 */
public class IOUtils {

    public static String readStream(String httpUrl, String charsetName) throws IOException {
        InputStream is = new URL(httpUrl).openStream();
        InputStreamReader isr = new InputStreamReader(is, charsetName);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder builder = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            builder.append(line);
        }
        is.close();
        isr.close();
        br.close();
        return builder.toString();
    }
}
