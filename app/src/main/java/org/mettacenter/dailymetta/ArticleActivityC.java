package org.mettacenter.dailymetta;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Main activity for the application
 */
public class ArticleActivityC extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu iMenu) {
        //Inflating the menu (which added itemts to the action bar)
        getMenuInflater().inflate(R.menu.menu_article, iMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                new FetchArticlesTaskC(this).execute(); //-NOTE: Only experimental to show what can be done, will not be here in the final version
            case R.id.action_text_search:
                //Displaying the search dialog
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
