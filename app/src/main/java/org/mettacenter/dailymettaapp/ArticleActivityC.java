package org.mettacenter.dailymettaapp;

import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main activity for the application
 *
 * AppCompatActivity extends ActionbarActivity
 */
public class ArticleActivityC extends AppCompatActivity {

    //Adapter and pager for side swipe
    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    protected Cursor pCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        //Setting up the data..
        //..clearing the db
        getApplicationContext().deleteDatabase(DbHelperM.DB_NAME);

        //..fetching all the articles
        new FetchArticlesTaskC(this, new MyCallbackClass()).execute();

        //..setup will continue in the class below after a callback from FetchArticlesTaskC
    }

    public class MyCallbackClass{
        public void adapterSetupCallback(){

            try{
                ArticleActivityC.this.pCursor = getContentResolver().query(
                        ContentProviderM.ARTICLE_CONTENT_URI, null, null, null, null);
                mPagerAdapter = new PagerAdapterC(getSupportFragmentManager(), pCursor);
            }catch(Exception e){
                Log.e(UtilitiesU.TAG, e.getMessage());
            }

            mViewPager = (ViewPager)findViewById(R.id.pager);
            mViewPager.setAdapter(mPagerAdapter);

            //Redrawing all fragments
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy(){
        pCursor.close();
        pCursor = null;
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
            case R.id.action_text_search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
