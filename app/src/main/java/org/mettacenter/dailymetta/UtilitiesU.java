package org.mettacenter.dailymetta;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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
            int READ_BUFFER_SIZE = 1024;
            byte[] tReadBuffer = new byte[READ_BUFFER_SIZE];
            while((tBytesRead = tIn.read(tReadBuffer)) > 0){
                tOut.write(tReadBuffer, 0, tBytesRead);
            }

            tOut.close();
            return tOut.toByteArray();

        } finally {
            tConnection.disconnect();
        }

    }

    public static void parseArticle(XmlPullParser iXmlPullParser)
            throws XmlPullParserException, IOException{

        int iEventType = iXmlPullParser.next();

        String CONTENT_TAG = "content";

        boolean tIsInsideContentTag = false;

        while(iEventType != XmlPullParser.END_DOCUMENT){
            if(iEventType == XmlPullParser.END_TAG){
                tIsInsideContentTag = false;
                Log.i(UtilitiesU.TAG, "===== END TAG =====");
            }else if(iEventType == XmlPullParser.START_TAG
                    && CONTENT_TAG.equals(iXmlPullParser.getName())){

                tIsInsideContentTag = true;

                // xml:base
                String tLinkUrlSg = iXmlPullParser.getAttributeValue(null, "xml:base");
                Log.i(UtilitiesU.TAG, "tLinkUrlSg = " + tLinkUrlSg);

                // text
                String tArticleContentSg = iXmlPullParser.nextText();
                //String tArticleContentSg = iXmlPullParser.getText();
                Log.i(UtilitiesU.TAG, "tArticleContentSg = " + tArticleContentSg);

            }else if(iEventType == XmlPullParser.TEXT){
                /*
                // text
                String tArticleContentSg = iXmlPullParser.getText();
                Log.i(UtilitiesU.TAG, "tArticleContentSg = " + tArticleContentSg);
                */
            }

            iXmlPullParser.next();
        }

    }

}
