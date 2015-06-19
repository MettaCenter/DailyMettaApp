package org.mettacenter.dailymetta;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sunyata on 2015-06-19.
 */
public class UtilitiesU {

    static final String TAG = "TAG";

    public static String getUrlString(String iUrlSg) throws IOException {

        return new String(getUrlBytes(iUrlSg));

    }

    public static byte[] getUrlBytes(String iUrlSg) throws IOException {
        URL tUrl = new URL(iUrlSg);
        HttpURLConnection tConnection = (HttpURLConnection)tUrl.openConnection();

        try {



            ByteArrayOutputStream tOut = new ByteArrayOutputStream();
            InputStream tIn = tConnection.getInputStream();

            if(tConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int tBytesRead = 0;
            byte[] tReadBuffer = new byte[1024];
            while((tBytesRead = tIn.read(tReadBuffer)) > 0){
                tOut.write(tReadBuffer, 0, tBytesRead);
            }

            tOut.close();
            return tOut.toByteArray();

        } finally {
            tConnection.disconnect();
        }

    }

}
