package org.mettacenter.dailymetta;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import static org.mettacenter.dailymetta.ArticleActivityC.*;

/**
 * Contains the @link doInBackground method which calls other methods for downloading and parsing
 * article data. All this is done on a thread separate from the UI thread
 */
public class FetchArticlesTaskC extends AsyncTask<Void, Void, ArticleActivityC.MyCallbackClass> {

    Context mContext = null;
    ArticleActivityC.MyCallbackClass mCallback;

    public FetchArticlesTaskC(Context iContext, ArticleActivityC.MyCallbackClass iCallback){
        mCallback = iCallback;
        mContext = iContext;
    }

    private static final String ATOM_FEEL_URL = "http://mettacenter.org/category/daily-metta/feed/atom/";

    @Override
    protected ArticleActivityC.MyCallbackClass doInBackground(Void... params) {
        try{
            String tResult = UtilitiesU.getUrlString(ATOM_FEEL_URL);

            XmlPullParserFactory tParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser tParser = tParserFactory.newPullParser();
            tParser.setInput(new StringReader(tResult));

            UtilitiesU.parseArticle(tParser, mContext);
        }catch(IOException ioe){
            Log.e(UtilitiesU.TAG, ioe.getMessage());
        }catch(XmlPullParserException xppe){
            Log.e(UtilitiesU.TAG, xppe.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArticleActivityC.MyCallbackClass iRefCb){
        //return null;
        mCallback.adapterSetupCallback();
    }
}
