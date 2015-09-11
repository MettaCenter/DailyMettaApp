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
    private OnAsyncTaskDoneListenerI mArticleActivityCallback = null;
    private ProgressDialog mProgressDialog;
    private static final String DIALOG_MESSAGE = "Downloading articles";

    public FetchArticlesTaskC(Context irContext, OnAsyncTaskDoneListenerI iOnAsyncTaskDoneListener){
        mArticleActivityCallback = iOnAsyncTaskDoneListener;
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

        UtilitiesU.downloadArticles(mrContext);

        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        if(mProgressDialog.isShowing() == true){
            mProgressDialog.dismiss();
        }

        //Completing the setup
        //-(only works when FetchArticles has been called from ArticleActivityC)
        if(mArticleActivityCallback != null){
            mArticleActivityCallback.finishSetup();
        }
    }

    public interface OnAsyncTaskDoneListenerI{
        void finishSetup();
    }
}
