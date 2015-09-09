package org.mettacenter.dailymettaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by sunyata on 2015-08-26.
 */
public class UtilitiesU {

    private static final String START_QUOTE = "&#8220;";
    private static final String END_QUOTE = "&#8221;";

    public static String getPartOfTitleInsideQuotes(String iTitleSg){
        //Example: <title type="html"><![CDATA[&#8220;Love of Humanity&#8221;- Daily Metta]]></title>
        int tFirstQuote = iTitleSg.indexOf(START_QUOTE);
        int tLastQuote = iTitleSg.lastIndexOf(END_QUOTE);

        if(tFirstQuote != -1 && tLastQuote != -1){
            String rPartOfTitleSg = iTitleSg.substring(tFirstQuote + START_QUOTE.length(), tLastQuote);
            return rPartOfTitleSg;
        }else{
            Log.w(ConstsU.TAG, "tFirstQuote = " + tFirstQuote + ", tLastQuote = " + tLastQuote);
            return iTitleSg;
        }
    }

    public static long getArticleFragmentPositionFromDate(Context iContext, int iMonthOfYear, int iDayOfMonth){
        long tPosLg = -1;
        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};

        Cursor tCursor = iContext.getContentResolver().query(
                ContentProviderM.ARTICLE_CONTENT_URI, tProj, null, null, ConstsU.SORT_ORDER);
        if(tCursor != null && tCursor.getCount() > 0) {
            tCursor.moveToFirst();
            int i=0;
            long tMonthLg;
            long tDayOfMonthLg;
            do{
                tMonthLg = tCursor.getLong(tCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_MONTH));
                tDayOfMonthLg = tCursor.getLong(tCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_DAYOFMONTH));
                if(tMonthLg == iMonthOfYear && tDayOfMonthLg == iDayOfMonth){
                    tPosLg = i;
                }
                i++;
            }while(tCursor.moveToNext() == true);
        }

        tCursor.close();
        tCursor = null;

        return tPosLg;
    }



    public static long getArticleFragmentPositionFromId(Context iContext, long iIdLg){
        long tPosLg = -1;
        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};

        Cursor tCursor = iContext.getContentResolver().query(
                ContentProviderM.ARTICLE_CONTENT_URI, tProj, null, null, ConstsU.SORT_ORDER);
        if(tCursor != null && tCursor.getCount() > 0) {
            tCursor.moveToFirst();
            int i=0;
            long tIdLg;
            do{
                tIdLg = tCursor.getLong(tCursor.getColumnIndexOrThrow(BaseColumns._ID));
                if(tIdLg == iIdLg){
                    tPosLg = i;
                }
                i++;
            }while(tCursor.moveToNext() == true);
        }

        tCursor.close();
        tCursor = null;

        return tPosLg;
    }


    public enum DownloadActionEnum {
        DOWNLOAD_ARTICLES,
        USE_ALREADY_DOWNLOADED_ARTICLES,
        DISPLAY_MANUAL_DOWNLOAD_BUTTON;
    }

    public static DownloadActionEnum downloadLogic(Context iContext){


        ConnectivityManager cm = (ConnectivityManager)iContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo tActiveNetworkInfo = cm.getActiveNetworkInfo();
        boolean tIsConnectedToInternet = tActiveNetworkInfo != null
                && tActiveNetworkInfo.isConnectedOrConnecting();


        //Checking if this is the first time the app is started or if we are running a new version
        int tOldVer = PreferenceManager.getDefaultSharedPreferences(iContext).getInt(
                ConstsU.PREF_APP_VERSION_CODE, ConstsU.APP_NEVER_STARTED);
        int tNewVer = 0;
        try {
            tNewVer = iContext.getPackageManager().getPackageInfo(iContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf(ConstsU.TAG, e.getMessage());
            e.printStackTrace();
        }
        if(tNewVer > tOldVer){
            //Writing the new version into the shared preferences
            PreferenceManager.getDefaultSharedPreferences(iContext)
                    .edit()
                    .putInt(ConstsU.PREF_APP_VERSION_CODE, tNewVer)
                    .commit();
        }

        SharedPreferences tSharedPreferences = iContext.getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        long tLastUpdateInMsFeedTzLg = tSharedPreferences.getLong(
                ConstsU.PREF_LAST_UPDATE_TIME_IN_MILLIS_FEED_TZ, ConstsU.DB_NEVER_UPDATED);
        long tUpdateIntervalInMillisLg = TimeUnit.HOURS.toMillis(ConstsU.UPDATE_INTERVAL_IN_HOURS);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone(ConstsU.FEED_TIME_ZONE));
        boolean tIsUpdateIntervalReachedBl =
                c.getTimeInMillis() - tLastUpdateInMsFeedTzLg
                        >= tUpdateIntervalInMillisLg;
        Log.d(ConstsU.TAG, "tIsUpdateIntervalReachedBl = " + tIsUpdateIntervalReachedBl);
        Log.d(ConstsU.TAG, "tLastUpdateInMsFeedTzLg = " + tLastUpdateInMsFeedTzLg);
        Log.d(ConstsU.TAG, "Calendar.getInstance().getTimeInMillis() = " + Calendar.getInstance().getTimeInMillis());
        Log.d(ConstsU.TAG, "tUpdateIntervalInMillisLg = " + tUpdateIntervalInMillisLg);


        //TODO: Do we want to do the update when a new app version is launched?


        if(tIsConnectedToInternet == false
                && (tLastUpdateInMsFeedTzLg == ConstsU.DB_NEVER_UPDATED || tNewVer > tOldVer)){

            return DownloadActionEnum.DISPLAY_MANUAL_DOWNLOAD_BUTTON;

        }else if(tIsUpdateIntervalReachedBl == true
                || tLastUpdateInMsFeedTzLg == ConstsU.DB_NEVER_UPDATED
                || tNewVer > tOldVer){

            return DownloadActionEnum.DOWNLOAD_ARTICLES;

        }else{

            return DownloadActionEnum.USE_ALREADY_DOWNLOADED_ARTICLES;

        }
    }


    public static void downloadArticles(Context iContext){

        boolean tUpdateHasBeenDone = false;
        try{
            SAXParserFactory tSAXParserFactory = SAXParserFactory.newInstance();
            SAXParser tSAXParser = tSAXParserFactory.newSAXParser();

            AtomFeedXmlHandlerM tAtomFeedXmlHandler = new AtomFeedXmlHandlerM(iContext);
            tSAXParser.parse(ConstsU.ATOM_FEEL_URL, tAtomFeedXmlHandler);

            tUpdateHasBeenDone = true;

        }catch (TerminateSAXParsingException e1){
            //Continuing, this exception does not indicate an error
            tUpdateHasBeenDone = false;
        }catch (Exception e2){
            Log.e(ConstsU.TAG, e2.getMessage());
        }

        if(tUpdateHasBeenDone == true) {

            //Writing the time of this db update to the preferences
            SharedPreferences.Editor tEditor = iContext.getSharedPreferences(
                    ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
            tEditor.putLong(ConstsU.PREF_LAST_UPDATE_TIME_IN_MILLIS_FEED_TZ,
                    Calendar.getInstance().getTimeInMillis());
            tEditor.commit();

        }
    }

}
