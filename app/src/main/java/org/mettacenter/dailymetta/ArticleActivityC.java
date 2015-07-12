package org.mettacenter.dailymetta;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Main activity for the application
 */
public class ArticleActivityC extends AppCompatActivity {

    //Adapter and pager for side swipe
    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);


        //Setting up the data..
        //..clearing the db
        getApplicationContext().deleteDatabase(DbHelperM.DB_NAME);


        //..fetching all the articles
        new FetchArticlesTaskC(this, new MyCallbackClass()).execute();



    }

    public class MyCallbackClass{
        public void adapterSetupCallback(){

            Cursor tCr = null;
            try{
                tCr = getContentResolver().query(ContentProviderM.ARTICLE_CONTENT_URI, null, null, null, null);
                mPagerAdapter = new PagerAdapterC(getSupportFragmentManager(), tCr);
            }catch(Exception e){
                Log.e(UtilitiesU.TAG, e.getMessage());
            }finally{
                //tCr.close();
            }


            mViewPager = (ViewPager)findViewById(R.id.pager);
            mViewPager.setAdapter(mPagerAdapter);



            //Redrawing the views in all the fragments
            ///ArticleActivityC.this.getWindow().getDecorView().findViewById(android.R.id.content);
            mPagerAdapter.notifyDataSetChanged();
            //mViewPager.noti
        }
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
                ///new FetchArticlesTaskC(this).execute(); //-NOTE: Only experimental to show what can be done, will not be here in the final version
            case R.id.action_text_search:
                //Displaying the search dialog
                ///new FetchArticlesTaskC(this).execute(); //-NOTE: Only experimental to show what can be done, will not be here in the final version
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

