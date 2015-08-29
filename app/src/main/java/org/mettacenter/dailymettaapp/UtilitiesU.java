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


    public static long getArticlePositionFromDate(Context iContext, int monthOfYear, int dayOfMonth){
        long tDataBaseIdLg = -1;
        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};
        String tSel =
                ArticleTableM.COLUMN_TIME_MONTH + " = " + monthOfYear
                        + " AND "
                        + ArticleTableM.COLUMN_TIME_DAYOFMONTH + " = " + dayOfMonth;

        Cursor tCursor = iContext.getContentResolver().query(
                ContentProviderM.ARTICLE_CONTENT_URI, tProj, tSel, null, ConstsU.SORT_ORDER);
        if(tCursor != null && tCursor.getCount() > 0) {
            tCursor.moveToFirst();
            tDataBaseIdLg = tCursor.getLong(tCursor.getColumnIndexOrThrow(BaseColumns._ID));
        }
        return tDataBaseIdLg;
    }

}
