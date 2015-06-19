package org.mettacenter.dailymetta;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by sunyata on 2015-06-19.
 */
public class FetchArticlesTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        try{
            String tResult = UtilitiesU.getUrlString("http://mettacenter.org/category/daily-metta/feed/atom/");
            Log.i(UtilitiesU.TAG, "Result: " + tResult);
        }catch(IOException ioe){
            Log.e(UtilitiesU.TAG, ioe.getMessage()); //-TODO: Create descriptive tags
        }

        return null;
    }
}
