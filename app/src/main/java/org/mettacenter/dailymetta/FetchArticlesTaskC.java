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
 * Created by sunyata on 2015-06-19.
 */
public class FetchArticlesTaskC extends AsyncTask<Void, Void, Void> {

    Context mContext = null;
    public FetchArticlesTaskC(Context iContext){
        mContext = iContext;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try{

            String tResult = UtilitiesU.getUrlString("http://mettacenter.org/category/daily-metta/feed/atom/");
            //Log.i(UtilitiesU.TAG, "Result: " + tResult);

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
}
