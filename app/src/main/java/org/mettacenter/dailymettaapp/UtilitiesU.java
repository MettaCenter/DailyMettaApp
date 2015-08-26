package org.mettacenter.dailymettaapp;

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
}
