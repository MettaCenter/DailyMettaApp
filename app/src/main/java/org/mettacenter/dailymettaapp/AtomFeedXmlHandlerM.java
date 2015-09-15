package org.mettacenter.dailymettaapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;
import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Used by the SAX parser. This is the code that we ourselves write for doing the actual handling
 * of the content in the atom feed
 */
public class AtomFeedXmlHandlerM
        extends DefaultHandler {

    private static final String UPDATED_XML_TAG = "updated";
    private static final String ENTRY_XML_TAG = "entry";
    private static final String CONTENT_XML_TAG = "content"; //-Corresponds to COLUMN_TEXT
    private static final String TITLE_XML_TAG = "title"; //-Corresponds to COLUMN_TITLE
    private static final String ID_XML_TAG = "id"; //-Corresponds to COLUMN_LINK
    private static final String PUBLISHED_XML_TAG = "published";

    private boolean mIsElementParsed = false;
    private boolean mIsContentEntryParsed = false;
    private ContentValues mInsertValues = new ContentValues();
    private Context mrContext;
    private StringBuilder mElementSb = null;

    public AtomFeedXmlHandlerM(Context irContext){
        mrContext = irContext;
    }

    @Override
    public void startElement(String iUriSg, String iLocalNameSg, String iQNameSg,
            org.xml.sax.Attributes iAttributes) throws SAXException {
        mIsElementParsed = true;

        if(ENTRY_XML_TAG.equalsIgnoreCase(iLocalNameSg)) {
            mIsContentEntryParsed = true;
            Log.d(ConstsU.APP_TAG, "========== START ENTRY APP_TAG ==========");
        }

        mElementSb = new StringBuilder();
    }

    /**
     * Surprisingly the characters method can be called several times by the SAX parser, we cannot
     * use "new String(iCharAy, iStartIt, iLengthIt)" and move on, for more info see the topmost
     * answer here:
     * http://stackoverflow.com/questions/2838099/android-sax-parser-not-getting-full-text-from-between-tags
     */
    @Override
    public void characters(char[] iCharAy, int iStartIt, int iLengthIt) throws SAXException{
        if(mIsElementParsed == true){
            ///mElementContent = new String(iCharAy, iStartIt, iLengthIt); //iCharAy.length
            if(mElementSb != null){
                for(int i = iStartIt; i < iStartIt + iLengthIt; i++){
                    mElementSb.append(iCharAy[i]);
                }
            }
        }
    }

    @Override
    public void endElement(String iUriSg, String iLocalNameSg, String iQNameSg) throws SAXException{
        mIsElementParsed = false;

        if(mIsContentEntryParsed == false){ //Parsing the beginning of the xml feed, reading general information
            if(UPDATED_XML_TAG.equalsIgnoreCase(iLocalNameSg)){
                SharedPreferences tSharedPreferences = mrContext.getSharedPreferences(
                        ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                long tLastClientUpdateInMsFeedTzLg = tSharedPreferences.getLong(
                        ConstsU.PREF_LONG_LAST_CLIENT_UPDATE_TIME_IN_MS_FEED_TZ, ConstsU.DB_NEVER_UPDATED);

                if(BuildConfig.DEBUG == false && getLastFeedUpdateTimeInMs() < tLastClientUpdateInMsFeedTzLg){
                    throw new TerminateSAXParsingException();
                }
            }
        }else{
            if(CONTENT_XML_TAG.equalsIgnoreCase(iLocalNameSg)){
                mInsertValues.put(ArticleTableM.COLUMN_TEXT, mElementSb.toString());
            }else if(TITLE_XML_TAG.equalsIgnoreCase(iLocalNameSg)){
                mInsertValues.put(ArticleTableM.COLUMN_TITLE, getPartOfTitleInsideQuotes(mElementSb.toString()));
            }else if(ID_XML_TAG.equalsIgnoreCase(iLocalNameSg)){

                String tWholeIdSg = mElementSb.toString();

                long tId = Long.parseLong(tWholeIdSg.substring(tWholeIdSg.indexOf("=") + 1));
                Log.d(ConstsU.APP_TAG, "Article id: " + tId);
                mInsertValues.put(BaseColumns._ID, tId);

                mInsertValues.put(ArticleTableM.COLUMN_LINK, tWholeIdSg);
            }else if(PUBLISHED_XML_TAG.equalsIgnoreCase(iLocalNameSg)){
                String tArticleTimeFeedTzSg = mElementSb.toString()
                        .replace("T", " ")
                        .replace("Z", "");

                Date tArticleTimeFeedTzDe = null; //-TODO: Handle case when null
                SimpleDateFormat tAtomXmlDateFormatFeedTz = new SimpleDateFormat(ConstsU.FEED_TIME_FORMAT);
                tAtomXmlDateFormatFeedTz.setTimeZone(TimeZone.getTimeZone(ConstsU.FEED_TIME_ZONE));
                try {
                    tArticleTimeFeedTzDe = tAtomXmlDateFormatFeedTz.parse(tArticleTimeFeedTzSg);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar tCalServerTz = Calendar.getInstance();
                tCalServerTz.setTimeZone(TimeZone.getTimeZone(ConstsU.SERVER_TIMEZONE));
                tCalServerTz.setTime(tArticleTimeFeedTzDe);

                int tMonthServerTzIt = tCalServerTz.get(Calendar.MONTH); //-Zero-based
                int tDayOfMonthServerTzIt = tCalServerTz.get(Calendar.DAY_OF_MONTH); //Starts at one
                mInsertValues.put(ArticleTableM.COLUMN_TIME_MONTH, tMonthServerTzIt);
                mInsertValues.put(ArticleTableM.COLUMN_TIME_DAYOFMONTH, tDayOfMonthServerTzIt);
            }else if(ENTRY_XML_TAG.equalsIgnoreCase(iLocalNameSg)) {
                mIsContentEntryParsed = false;

                long tFavoriteWithTimeLg = UtilitiesU.getFavoriteTime(mrContext, mInsertValues.getAsLong(BaseColumns._ID));
                mInsertValues.put(ArticleTableM.COLUMN_INTERNAL_BOOKMARK, tFavoriteWithTimeLg);

                //Write what we have to the db
                mrContext.getContentResolver().insert(
                        ContentProviderM.ARTICLE_CONTENT_URI,
                        mInsertValues);
                //Clear the values so we can start anew on another row
                mInsertValues.clear();

                Log.d(ConstsU.APP_TAG, "========== END ENTRY APP_TAG ==========");
            }
        }
    }

    private long getLastFeedUpdateTimeInMs(){
        long tArticleTimeInMsLg = -1;

        String tArticleTimeSg = mElementSb.toString()
                .replace("T", " ")
                .replace("Z", "");

        Date tDate;
        SimpleDateFormat tAtomXmlDateFormat = new SimpleDateFormat(ConstsU.FEED_TIME_FORMAT);
        tAtomXmlDateFormat.setTimeZone(TimeZone.getTimeZone(ConstsU.FEED_TIME_ZONE));
        try {
            tDate = tAtomXmlDateFormat.parse(tArticleTimeSg);
            tArticleTimeInMsLg = tDate.getTime();
            //-This is a Java long, but there is no problem for SQLite because
            //it's Integer is variable in length and can be up to 8 bytes
        } catch (ParseException e) {
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }

        Log.d(ConstsU.APP_TAG, "tArticleTimeInMsLg = " + tArticleTimeInMsLg);

        if(tArticleTimeInMsLg == -1){
            Log.wtf(ConstsU.APP_TAG, "tArticleTimeInMsLg == -1");
        }

        return tArticleTimeInMsLg;
    }

    private static final String START_QUOTE = "&#8220;";
    private static final String END_QUOTE = "&#8221;";
    private String getPartOfTitleInsideQuotes(String iTitleSg){
        //Example: <title type="html"><![CDATA[&#8220;Love of Humanity&#8221;- Daily Metta]]></title>
        int tFirstQuote = iTitleSg.indexOf(START_QUOTE);
        int tLastQuote = iTitleSg.lastIndexOf(END_QUOTE);

        if(tFirstQuote != -1 && tLastQuote != -1){
            String rPartOfTitleSg = iTitleSg.substring(tFirstQuote + START_QUOTE.length(), tLastQuote);
            return rPartOfTitleSg;
        }else{
            Log.w(ConstsU.APP_TAG, "tFirstQuote = " + tFirstQuote + ", tLastQuote = " + tLastQuote);
            return iTitleSg;
        }
    }
}
