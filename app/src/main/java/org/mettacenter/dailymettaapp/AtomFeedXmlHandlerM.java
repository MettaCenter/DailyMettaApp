package org.mettacenter.dailymettaapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    private static final String ENTRY_XML_TAG = "entry";
    private static final String CONTENT_XML_TAG = "content"; //-Corresponds to COLUMN_TEXT
    private static final String TITLE_XML_TAG = "title"; //-Corresponds to COLUMN_TITLE
    private static final String ID_XML_TAG = "id"; //-Corresponds to COLUMN_LINK
    private static final String PUBLISHED_XML_TAG = "published"; //-Corresponds to COLUMN_TIME
    private static final String UPDATED_XML_TAG = "updated"; //-Corresponds to COLUMN_TIME

    private boolean mIsElementParsed = false;
    private boolean mIsContentEntryParsed = false;
    //private String mElementContent = null;
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
            Log.d(ConstsU.TAG, "========== START ENTRY TAG ==========");

        }




        mElementSb = new StringBuilder();
    }

    //public void characters(char[] ch, int start, int length) throws SAXException {

    /**
     * Surprisingly the characters method can be called several times by the SAX parser, we cannot
     * use "new String(iCharAy, iStartIt, iLengthIt)" and move on, for more info see the topmost
     * answer here:
     * http://stackoverflow.com/questions/2838099/android-sax-parser-not-getting-full-text-from-between-tags
     */
    @Override
    public void characters(char[] iCharAy, int iStartIt, int iLengthIt) throws SAXException{
        if(mIsElementParsed == true){
            //mElementContent = new String(iCharAy, iStartIt, iLengthIt); //iCharAy.length
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

        if(mIsContentEntryParsed == true){

            if(CONTENT_XML_TAG.equalsIgnoreCase(iLocalNameSg)){
                mInsertValues.put(ArticleTableM.COLUMN_TEXT, mElementSb.toString());
            }else if(TITLE_XML_TAG.equalsIgnoreCase(iLocalNameSg)){
                mInsertValues.put(ArticleTableM.COLUMN_TITLE, UtilitiesU.getPartOfTitleInsideQuotes(mElementSb.toString()));
            }else if(ID_XML_TAG.equalsIgnoreCase(iLocalNameSg)){

                String tWholeIdSg = mElementSb.toString();

                long tId = Long.parseLong(tWholeIdSg.substring(tWholeIdSg.indexOf("=") + 1));
                Log.d(ConstsU.TAG, "Article id: " + tId);
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

                int tMonthServerTzIt = tCalServerTz.get(Calendar.MONTH);
                int tDayOfMonthServerTzIt = tCalServerTz.get(Calendar.DAY_OF_MONTH);
                mInsertValues.put(ArticleTableM.COLUMN_TIME_MONTH, tMonthServerTzIt);
                mInsertValues.put(ArticleTableM.COLUMN_TIME_DAYOFMONTH, tDayOfMonthServerTzIt);
            }else if(ENTRY_XML_TAG.equalsIgnoreCase(iLocalNameSg)) {

                mIsContentEntryParsed = false;
                Log.d(ConstsU.TAG, "========== END ENTRY TAG ==========");

                //Extract the favorite status and write it to the entry with the same id
                String[] tProj = {ArticleTableM.COLUMN_INTERNAL_FAVORITE_WITH_TIME};
                String tSel = BaseColumns._ID + "=" + mInsertValues.getAsLong(BaseColumns._ID);
                Cursor tCr = mrContext.getContentResolver().query(
                        ContentProviderM.ARTICLE_CONTENT_URI,
                        tProj, tSel, null, ConstsU.SORT_ORDER);
                tCr.moveToFirst();
                if(tCr != null && tCr.getCount() > 0){
                    long tFavoriteWithTimeLg = tCr.getColumnIndexOrThrow(ArticleTableM.COLUMN_INTERNAL_FAVORITE_WITH_TIME);
                    mInsertValues.put(ArticleTableM.COLUMN_INTERNAL_FAVORITE_WITH_TIME, tFavoriteWithTimeLg);
                }

                //Write what we have to the db
                mrContext.getContentResolver().insert(
                        ContentProviderM.ARTICLE_CONTENT_URI,
                        mInsertValues);
                //Clear the values so we can start anew on another row
                mInsertValues.clear();
            }

        }else{ //Parsing the beginning of the xml feed, reading general information (mIsContentEntryParsed == false)

            if(UPDATED_XML_TAG.equalsIgnoreCase(iLocalNameSg)){

                SharedPreferences tSharedPreferences = mrContext.getSharedPreferences(
                        ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                long tLastFeedUpdateInMsFeedTzLg = tSharedPreferences.getLong(
                        ConstsU.PREF_LAST_UPDATE_TIME_IN_MILLIS_TZ_FEED, ConstsU.DB_NEVER_UPDATED);

                long tLastClientUpdateInMsFeedTzLg = getArticleTimeInMilliSeconds(iLocalNameSg);

                if(tLastClientUpdateInMsFeedTzLg > tLastFeedUpdateInMsFeedTzLg){
                    //-Important to use > and not >= here because of the case when both are -1
                    throw new TerminateSAXParsingException();
                }

            }

        }

    }


    private long getArticleTimeInMilliSeconds(String iRawArticleTimeSg){
        long tArticleTimeInMilliSecondsLg = -1;

        String tArticleTimeSg = iRawArticleTimeSg
                .replace("T", " ")
                .replace("Z", "");

        Date tDate;
        SimpleDateFormat tAtomXmlDateFormat = new SimpleDateFormat(ConstsU.FEED_TIME_FORMAT);
        tAtomXmlDateFormat.setTimeZone(TimeZone.getTimeZone(ConstsU.FEED_TIME_ZONE));
        try {
            tDate = tAtomXmlDateFormat.parse(tArticleTimeSg);
            tArticleTimeInMilliSecondsLg = tDate.getTime();
            //-This is a Java long, but there is no problem for SQLite because
            //it's Integer is variable in length and can be up to 8 bytes
        } catch (ParseException e) {
            Log.e(ConstsU.TAG, e.getMessage());
        }

        Log.d(ConstsU.TAG, "tTimeInMilliSecondsLg = " + tArticleTimeInMilliSecondsLg);

        return tArticleTimeInMilliSecondsLg;
    }

}
