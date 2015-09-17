package org.mettacenter.dailymettaapp;

import android.util.Log;

/**
 * Created by sunyata on 2015-09-16.
 */
public class ArticleC {
    public static final String REGEX_HTML_TAG_IMG = "<img.+/(img)*>";
    public static final String REGEX_HTML_TAG_A = "<a.+/(a)*>";
    public static final String REGEX_HTML_TAG_SPAN = "<span.+/(span)*>";
    public static final String HTML_START_EXPERIMENT = "<strong>Experiment in Nonviolence:";
    public static final String HTML_END_EXPERIMENT = "</span>";
    public static final String HTML_START_QUOTE = "<h3><b>";
    public static final String HTML_END_QUOTE = "</b></h3>";

    private String mRawArticleSg;

    public ArticleC(String iRawArticleSg){
        mRawArticleSg = iRawArticleSg;
    }


    //TODO: Processing everything at the beginning and simply returning the whole article in case we get an ERROR for any of the parts. ("Soft fail")



    public String getDateAndQuoteAndMainTextHtml(){
        String tDateAndQuoteAndMainTextSg = "ERROR";
        try{
            tDateAndQuoteAndMainTextSg = mRawArticleSg.substring(0, mRawArticleSg.lastIndexOf(HTML_START_EXPERIMENT));
            tDateAndQuoteAndMainTextSg = tDateAndQuoteAndMainTextSg.replaceAll(ArticleC.REGEX_HTML_TAG_IMG, "");
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tDateAndQuoteAndMainTextSg;
        }
    }

    public String getExperimentHtml(){
        String tExperimentSg = "ERROR";
        try{
            tExperimentSg = mRawArticleSg.substring(mRawArticleSg.lastIndexOf(HTML_START_EXPERIMENT), mRawArticleSg.lastIndexOf(HTML_END_EXPERIMENT) + HTML_END_EXPERIMENT.length());
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tExperimentSg;
        }
    }

    public String getBacklinkTextHtml(){
        String tBackLinkTextSg = "ERROR";
        try{
            tBackLinkTextSg = mRawArticleSg.substring(mRawArticleSg.lastIndexOf(HTML_END_EXPERIMENT) + HTML_END_EXPERIMENT.length());
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tBackLinkTextSg;
        }
    }


    public static String extractQuote(String iArticleSg){
        String tQuoteSg = "ERROR";
        try{
            tQuoteSg = iArticleSg.substring(iArticleSg.indexOf(HTML_START_QUOTE) + HTML_START_QUOTE.length(), iArticleSg.indexOf(HTML_END_QUOTE));
            tQuoteSg = tQuoteSg.replaceAll(REGEX_HTML_TAG_A, "");
        }catch (Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }finally {
            return tQuoteSg;
        }
    }




}
