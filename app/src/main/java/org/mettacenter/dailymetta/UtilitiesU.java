package org.mettacenter.dailymetta;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Contains various static helper methods for the entire application
 */
public class UtilitiesU {

    public static final String TAG = "daily_metta_app";
    public static final String EMPTY_STRING = "";

    private static final int READ_BUFFER_SIZE = 1024;
    private static final String CONTENT_XML_TAG = "content";

    /**
     * Establishes a connection to the provided url and returns the data available there as a
     * byte array.
     * @param iUrlSg The url to read from
     * @return An array of bytes that have been read
     * @throws IOException
     */
    public static byte[] getUrlBytes(String iUrlSg) throws IOException {
        URL tUrl = new URL(iUrlSg);
        HttpURLConnection tConnection = (HttpURLConnection)tUrl.openConnection();

        try {
            if(tConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            ByteArrayOutputStream tOut = new ByteArrayOutputStream();
            InputStream tIn = tConnection.getInputStream();

            int tNrOfBytesRead = 0;
            byte[] tReadBuffer = new byte[READ_BUFFER_SIZE];
            for(tNrOfBytesRead = tIn.read(tReadBuffer); tNrOfBytesRead > 0;){
                tOut.write(tReadBuffer, 0, tNrOfBytesRead);
            }

            tOut.close();
            return tOut.toByteArray();
        } finally {
            tConnection.disconnect();
        }
    }

    /**
     * Uses @ref getUrlBytes to read data from a URL, then tranforms this byte data to a String
     * @param iUrlSg The URL forwarded to @ref getUrlBytes
     * @return A text String with the data read
     * @throws IOException
     */
    public static String getUrlString(String iUrlSg) throws IOException {
        return new String(getUrlBytes(iUrlSg));
    }

    /**
     * Given an XmlPullParser with multiple articles as the content, parses the XML data and writes
     * to the database
     * @param iXmlPullParser
     * @param iContext
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static void parseArticle(XmlPullParser iXmlPullParser, Context iContext)
            throws XmlPullParserException, IOException {

        int iEventType = iXmlPullParser.next();

        while(iEventType != XmlPullParser.END_DOCUMENT){
            if(iEventType == XmlPullParser.END_TAG){
                Log.d(UtilitiesU.TAG, "===== END TAG =====");
            }else if(iEventType == XmlPullParser.START_TAG){
                Log.d(UtilitiesU.TAG, "===== START TAG =====");
                if(CONTENT_XML_TAG.equals(iXmlPullParser.getName())){
                    //Reading from the XML and Writing to the db..
                    ContentValues tInsertValues = new ContentValues();

                    //..for the article URL
                    String tLinkUrlSg = iXmlPullParser.getAttributeValue(null, "xml:base");
                    Log.d(UtilitiesU.TAG, "tLinkUrlSg = " + tLinkUrlSg);
                    tInsertValues.put(ArticleTableM.COLUMN_LINK, tLinkUrlSg);
                    iContext.getContentResolver().insert(ContentProviderM.ARTICLE_CONTENT_URI,
                            tInsertValues);
                    /*
                     *-using this direct call the insert method for now, perhaps later we will
                     * change to using intents for insertion
                     */

                    tInsertValues.clear();

                    //..for the article text
                    String tArticleContentSg = iXmlPullParser.nextText();
                    Log.d(UtilitiesU.TAG, "tArticleContentSg = " + tArticleContentSg);
                    tInsertValues.put(ArticleTableM.COLUMN_TEXT, tArticleContentSg);
                    iContext.getContentResolver().insert(ContentProviderM.ARTICLE_CONTENT_URI,
                            tInsertValues);
                }

            }else{
                //intentionally left empty
            }

            iXmlPullParser.next();
        }

    }



}
