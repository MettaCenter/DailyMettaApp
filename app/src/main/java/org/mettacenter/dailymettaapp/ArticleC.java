package org.mettacenter.dailymettaapp;

import android.util.Log;

/**
 * Created by sunyata on 2015-09-16.
 */
public class ArticleC {

    public static final String REGEX_HTML_TAG_IMG = "<img.+/(img)*>";
    public static final String REGEX_HTML_TAG_A = "<a.+/(a)*>";

    public static final String HTML_NBSP_IN_P = "<p>&nbsp;</p>";

    public static final String HTML_START_EXPERIMENT = "<div id=\"dm-experiment\">";
    public static final String HTML_END_EXPERIMENT = "</div>";

    public static final String HTML_START_QUOTE = "<span id=\"dm-quote\">";///"<h3><b>";
    public static final String HTML_END_QUOTE = "</span>";

    private String mRawArticleSg;

    public ArticleC(String iRawArticleSg){
        mRawArticleSg = iRawArticleSg;
    }


    //TODO: Processing everything at the beginning and simply returning the whole article in case we get an ERROR for any of the parts. ("Soft fail")



    public String getDateAndQuoteAndMainTextHtml(){
        String tDateAndQuoteAndMainTextSg = "ERROR";
        try{
            int tEndIndexIt = mRawArticleSg.indexOf(HTML_START_EXPERIMENT);
            if(tEndIndexIt == -1){
                throw new Exception();
            }

            tDateAndQuoteAndMainTextSg = mRawArticleSg.substring(0, tEndIndexIt);
            tDateAndQuoteAndMainTextSg = tDateAndQuoteAndMainTextSg
                    .replaceAll(ArticleC.REGEX_HTML_TAG_IMG, "")
                    .replaceAll(ArticleC.HTML_NBSP_IN_P, "")
                    .trim();
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tDateAndQuoteAndMainTextSg;
        }
    }

    public String getExperimentHtml(){
        String tExperimentSg = "ERROR";
        try{
            int tStartIndexIt = mRawArticleSg.lastIndexOf(HTML_START_EXPERIMENT);
            int tEndIndexIt = mRawArticleSg.lastIndexOf(HTML_END_EXPERIMENT);
            if(tStartIndexIt == -1 || tEndIndexIt == -1){
                throw new Exception();
            }
            tExperimentSg = mRawArticleSg.substring(
                    tStartIndexIt + HTML_START_EXPERIMENT.length(), tEndIndexIt);
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tExperimentSg;
        }
    }

    public String getBacklinkTextHtml(){
        String tBackLinkTextSg = "ERROR";
        try{
            int tStartIndexIt = mRawArticleSg.lastIndexOf(HTML_END_EXPERIMENT);
            if(tStartIndexIt == -1){
                throw new Exception();
            }
            tBackLinkTextSg = mRawArticleSg.substring(tStartIndexIt + HTML_END_EXPERIMENT.length());
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tBackLinkTextSg;
        }
    }


    //Please note that this is a static method and that it works a bit differently than the other methods above in this class
    public static String extractQuote(String iArticleSg){
        String tQuoteSg = "ERROR";
        try{
            int tStartIndexIt = iArticleSg.indexOf(HTML_START_QUOTE);
            int tEndIndexIt = iArticleSg.indexOf(HTML_END_QUOTE, tStartIndexIt);
            if(tStartIndexIt == -1 || tEndIndexIt == -1){
                throw new Exception();
            }
            tQuoteSg = iArticleSg.substring(tStartIndexIt + HTML_START_QUOTE.length(), tEndIndexIt);
            tQuoteSg = tQuoteSg.replaceAll(REGEX_HTML_TAG_A, "");
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tQuoteSg;
        }
    }

}
