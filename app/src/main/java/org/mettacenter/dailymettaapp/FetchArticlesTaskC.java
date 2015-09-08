package org.mettacenter.dailymettaapp;

import android.app.ProgressDialog;
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
    private ProgressDialog mProgressDialog;
    private static final String DIALOG_MESSAGE = "Downloading articles";

    public FetchArticlesTaskC(Context irContext, ArticleActivityC.AppSetupCallbackClass iCallback){
        mArticleActivityCallback = iCallback;
        mrContext = irContext;
        mProgressDialog = new ProgressDialog(irContext);
    }

    @Override
    protected void onPreExecute(){
        mProgressDialog.setMessage(DIALOG_MESSAGE);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        boolean tUpdateHasBeenDone = false;
        try{
            SAXParserFactory tSAXParserFactory = SAXParserFactory.newInstance();
            SAXParser tSAXParser = tSAXParserFactory.newSAXParser();

            AtomFeedXmlHandlerM tAtomFeedXmlHandler = new AtomFeedXmlHandlerM(mrContext);
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
            SharedPreferences.Editor tEditor = mrContext.getSharedPreferences(
                    ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
            tEditor.putLong(ConstsU.PREF_LAST_UPDATE_TIME_IN_MILLIS_TZ_FEED,
                    Calendar.getInstance().getTimeInMillis());
            tEditor.commit();

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        if(mProgressDialog.isShowing() == true){
            mProgressDialog.dismiss();
        }

        //Completing the setup
        mArticleActivityCallback.setupCallback();
    }


}
