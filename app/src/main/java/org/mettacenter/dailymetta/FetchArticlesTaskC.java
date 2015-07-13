package org.mettacenter.dailymetta;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
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
    private static final String PUBLISHED_XML_TAG = "id"; //-Corresponds to COLUMN_TIME
    ///private static final String PUBLISHED_XML_TAG = "published";
    //private static final String CATEGORY_XML_TAG = "category"; //TODO: Determine if we are going to use this (if so we will need to add a new table)

    public interface TagParsedAtPresent{
        int NONE = 0;
        int CONTENT = 1;
        int TITLE = 2;
        int ID = 3;
        int PUBLISHED = 4;
    }

    Context mContext;
    ArticleActivityC.MyCallbackClass mCallback;

    public FetchArticlesTaskC(Context iContext, ArticleActivityC.MyCallbackClass iCallback){
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
        mCallback.adapterSetupCallback();
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
        int tEnumTagParsedAtPresent = TagParsedAtPresent.NONE;
        ContentValues tInsertValues = new ContentValues();

        while(iEventType != XmlPullParser.END_DOCUMENT){

            switch(iEventType){
                case XmlPullParser.START_TAG:
                    Log.d(UtilitiesU.TAG, "===== START TAG =====");
                    if(CONTENT_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagParsedAtPresent.CONTENT;
                    }else if(TITLE_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagParsedAtPresent.TITLE;
                    }else if(ID_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagParsedAtPresent.ID;
                    }else if(PUBLISHED_XML_TAG.equals(iXmlPullParser.getName())){
                        tEnumTagParsedAtPresent = TagParsedAtPresent.PUBLISHED;
                    }
                    break;
                case XmlPullParser.TEXT:
                    Log.d(UtilitiesU.TAG, "===== TEXT =====");
                    switch(tEnumTagParsedAtPresent){

                        case TagParsedAtPresent.CONTENT:
                            String tArticleContentSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleContentSg = " + tArticleContentSg);
                            tInsertValues.put(ArticleTableM.COLUMN_TEXT, tArticleContentSg);
                            break;
                        case TagParsedAtPresent.ID:
                            String tArticleLinkSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleLinkSg = " + tArticleLinkSg);
                            tInsertValues.put(ArticleTableM.COLUMN_LINK, tArticleLinkSg);
                            break;
                        case TagParsedAtPresent.TITLE:
                            String tArticleTitleSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleTitleSg = " + tArticleTitleSg);
                            tInsertValues.put(ArticleTableM.COLUMN_TITLE, tArticleTitleSg);
                            break;
                        case TagParsedAtPresent.PUBLISHED:
                            String tArticleTimeSg = iXmlPullParser.getText();
                            Log.d(UtilitiesU.TAG, "tArticleTimeSg = " + tArticleTimeSg);
                            //long tDate = ___;
                            //tInsertValues.put(ArticleTableM.COLUMN_TITLE, tArticleTimeSg);
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

                    tEnumTagParsedAtPresent = TagParsedAtPresent.NONE;

                    break;
                default:
                    //intentionally left empty
            }

            iEventType = iXmlPullParser.next();
        }
    }
}
