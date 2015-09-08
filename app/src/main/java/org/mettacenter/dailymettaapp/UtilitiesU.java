package org.mettacenter.dailymettaapp;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

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
            tCursor.close();
        }

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
            tCursor.close();
        }

        return tPosLg;
    }


}
