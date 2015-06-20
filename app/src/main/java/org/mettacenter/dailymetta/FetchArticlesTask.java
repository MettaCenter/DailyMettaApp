package org.mettacenter.dailymetta;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by sunyata on 2015-06-19.
 */
public class FetchArticlesTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

        try{

            String tResult = UtilitiesU.getUrlString("http://mettacenter.org/category/daily-metta/feed/atom/");
            //Log.i(UtilitiesU.TAG, "Result: " + tResult);

            XmlPullParserFactory tParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser tParser = tParserFactory.newPullParser();
            tParser.setInput(new StringReader(tResult));

            UtilitiesU.parseArticle(tParser);

        }catch(IOException ioe){
            Log.e(UtilitiesU.TAG, ioe.getMessage()); //-TODO: Create descriptive tags
        }catch(XmlPullParserException xppe){
            Log.e(UtilitiesU.TAG, xppe.getMessage()); //-TODO: Create descriptive tags
        }

        return null;
    }
}
