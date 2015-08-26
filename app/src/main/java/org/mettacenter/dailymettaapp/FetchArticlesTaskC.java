package org.mettacenter.dailymettaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Calendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Contains the @link doInBackground method which calls other methods for downloading and parsing
 * article data. All this is done on a thread separate from the UI thread
 */
public class FetchArticlesTaskC
        extends AsyncTask<Void, Void, Void> {

    private Context mrContext;
    private ArticleActivityC.AppSetupCallbackClass mArticleActivityCallback;

    public FetchArticlesTaskC(Context irContext, ArticleActivityC.AppSetupCallbackClass iCallback){
        mArticleActivityCallback = iCallback;
        mrContext = irContext;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try{
            SAXParserFactory tSAXParserFactory = SAXParserFactory.newInstance();
            SAXParser tSAXParser = tSAXParserFactory.newSAXParser();

            AtomFeedXmlHandlerM tAtomFeedXmlHandler = new AtomFeedXmlHandlerM(mrContext);
            tSAXParser.parse(ConstsU.ATOM_FEEL_URL, tAtomFeedXmlHandler);

            //Writing the time of this db update to the preferences
            SharedPreferences.Editor tEditor = mrContext.getSharedPreferences(
                    ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
            tEditor.putLong(ConstsU.PREF_LAST_UPDATE_TIME,
                    Calendar.getInstance().getTimeInMillis());
            tEditor.commit();
        }catch (Exception e1){
            Log.e(ConstsU.TAG, e1.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        //Completing the setup
        mArticleActivityCallback.setupCallback();
    }
}
