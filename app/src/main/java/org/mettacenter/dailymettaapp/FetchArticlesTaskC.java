package org.mettacenter.dailymettaapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Contains the @link doInBackground method which calls other methods for downloading and parsing
 * article data. All this is done on a thread separate from the UI thread
 */
public class FetchArticlesTaskC extends AsyncTask<Void, Void, Void> {
    private static final int READ_BUFFER_SIZE = 1024;

    private static final String ENTRY_XML_TAG = "entry";
    private static final String CONTENT_XML_TAG = "content"; //-Corresponds to COLUMN_TEXT
    private static final String TITLE_XML_TAG = "title"; //-Corresponds to COLUMN_TITLE
    private static final String ID_XML_TAG = "id"; //-Corresponds to COLUMN_LINK
    private static final String PUBLISHED_XML_TAG = "published"; //-Corresponds to COLUMN_TIME
    //private static final String CATEGORY_XML_TAG = "category"; //TODO: Determine if we are going to use this (if so we will need to add a new table)

    public interface TagPresentlyParsed{
        int NONE = 0;
        int CONTENT = 1;
        int TITLE = 2;
        int ID = 3;
        int PUBLISHED = 4;
    }

    Context mContext;
    ArticleActivityC.AppSetupCallbackClass mCallback;

    public FetchArticlesTaskC(Context iContext, ArticleActivityC.AppSetupCallbackClass iCallback){
        mCallback = iCallback;
        mContext = iContext;
    }

    private static final String ATOM_FEEL_URL = "http://mettacenter.org/category/daily-metta/feed/atom/";

    @Override
    protected Void doInBackground(Void... params) {
        try{
            String tResult = getUrlString(ATOM_FEEL_URL);

            XmlPullParserFactory tParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser tParser = tParserFactory.newPullParser();
            tParser.setInput(new StringReader(tResult));

            parseArticle(tParser, mContext);
        }catch(IOException ioe){
            Log.e(UtilitiesU.TAG, ioe.getMessage());
        }catch(XmlPullParserException xppe){
            Log.e(UtilitiesU.TAG, xppe.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        //Completing the setup
        mCallback.setupCallback();
    }


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
            while( ( tNrOfBytesRead = tIn.read(tReadBuffer) ) > 0 ){
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
        int tEnumTagParsedAtPresent = TagPresentlyParsed.NONE;
        ContentValues tInsertValues = new ContentValues();

        while(iEventType != XmlPullParser.END_DOCUMENT){

            switch(iEventType){
                case XmlPullParser.START_TAG:
                    Log.d(UtilitiesU.TAG, "===== START TAG =====");
                    if(CONTENT_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagPresentlyParsed.CONTENT;
                    }else if(TITLE_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagPresentlyParsed.TITLE;
                    }else if(ID_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagPresentlyParsed.ID;
                    }else if(PUBLISHED_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagPresentlyParsed.PUBLISHED;
                    }
                    break;
                case XmlPullParser.TEXT:
                    Log.d(UtilitiesU.TAG, "===== TEXT =====");
                    switch(tEnumTagParsedAtPresent){

                        case TagPresentlyParsed.CONTENT:
                            String tArticleContentSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleContentSg = " + tArticleContentSg);
                            tInsertValues.put(ArticleTableM.COLUMN_TEXT, tArticleContentSg);
                            break;
                        case TagPresentlyParsed.ID:
                            String tArticleLinkSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleLinkSg = " + tArticleLinkSg);
                            tInsertValues.put(ArticleTableM.COLUMN_LINK, tArticleLinkSg);
                            break;
                        case TagPresentlyParsed.TITLE:
                            String tArticleTitleSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleTitleSg = " + tArticleTitleSg);
                            tInsertValues.put(ArticleTableM.COLUMN_TITLE, tArticleTitleSg);
                            break;
                        case TagPresentlyParsed.PUBLISHED:

                            String tArticleTimeSg = iXmlPullParser.getText()
                                    .replace("T", " ")
                                    .replace("Z", "");
                            //-TODO: Check with Sky if these replacements are a future-safe thing to do
                            Log.d(UtilitiesU.TAG, "tArticleTimeSg = " + tArticleTimeSg);

                            Date tDate;
                            long tTimeInMilliSecondsLg = -1;
                            SimpleDateFormat tAtomXmlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            tAtomXmlDateFormat.setTimeZone(TimeZone.getTimeZone(UtilitiesU.TIMEZONE));
                            try {
                                tDate = tAtomXmlDateFormat.parse(tArticleTimeSg);
                                tTimeInMilliSecondsLg  = tDate.getTime();
                                //-This is a Java long, but there is no problem for SQLite because
                                //it's Integer is variable in length and can be up to 8 bytes
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Log.d(UtilitiesU.TAG, "tTimeInMilliSecondsLg = " + tTimeInMilliSecondsLg);

                            tInsertValues.put(ArticleTableM.COLUMN_TIME, tTimeInMilliSecondsLg);
                            break;
                        default:
                            //intentionally left empty
                    }

                    break;
                case XmlPullParser.END_TAG:
                    Log.d(UtilitiesU.TAG, "===== END TAG =====");

                    if(ENTRY_XML_TAG.equals(iXmlPullParser.getName())){
                        //At the end of each entry we..
                        Log.d(UtilitiesU.TAG, "========== END ENTRY TAG ==========");
                        //..write what we have to the db
                        iContext.getContentResolver().insert(
                                ContentProviderM.ARTICLE_CONTENT_URI,
                                tInsertValues);
                        //..clear the values so we can start anew on another row
                        tInsertValues.clear();
                    }

                    tEnumTagParsedAtPresent = TagPresentlyParsed.NONE;

                    break;
                default:
                    //intentionally left empty
            }
            iEventType = iXmlPullParser.next();
        }
    }
}
