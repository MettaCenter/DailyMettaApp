package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
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
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private Cursor mCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        //Setting up the data..
        //..clearing the db
        getApplicationContext().deleteDatabase(DbHelperM.DB_NAME); //TODO: Remove

        //..fetching all the articles
        new FetchArticlesTaskC(this, new AppSetupCallbackClass()).execute();

        //..setup continues in AppSetupCallbackClass below after a callback from FetchArticlesTaskC
    }


    /*
    The override below does not seem to be needed, but please keep in mind that "getIntent" does
    not get the latest intent, but rather the intent that was used to start the activity

    @Override
    public void onNewIntent(Intent iIntent){
        super.onNewIntent(iIntent);
        setIntent(iIntent);
    }
    */


    /**
     * Used for setting up after data has been fetched from the server
     */
    public class AppSetupCallbackClass {
        public void setupCallback(){
            try{
                mCursor = getContentResolver().query(
                        ContentProviderM.ARTICLE_CONTENT_URI, null, null, null, null);
                mPagerAdapter = new PagerAdapterC(getSupportFragmentManager(), mCursor);
            }catch(Exception e){
                Log.e(UtilitiesU.TAG, e.getMessage());
            }

            mViewPager = (ViewPager)findViewById(R.id.pager);
            mViewPager.setAdapter(mPagerAdapter);

            //Redrawing all fragments
            mPagerAdapter.notifyDataSetChanged();



            ///if(getIntent().hasExtra(SearchResultsActivityC.EXTRA_ARTICLE_POS_ID) == true){
                //Choosing the fragment to display. 0 (the latest article) is the default
                //Casting is done from long to int
                int tPositionIt = (int)getIntent().getLongExtra(
                        UtilitiesU.EXTRA_ARTICLE_POS_ID, 0);
                mViewPager.setCurrentItem(tPositionIt);
                getIntent().removeExtra(UtilitiesU.EXTRA_ARTICLE_POS_ID);
            ///}else if(getIntent().hasExtra(DatePickerFragmentC.EXTRA_ARTICLE_TIME) == true){


            ///}else{
                //intentionally left empty
            ///}
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mCursor.close();
        mCursor = null;
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
            case R.id.action_text_search:
                onSearchRequested();
                return true;
            case R.id.action_choose_date:


                DialogFragment tDatePickerFragment = new DatePickerFragmentC();
                tDatePickerFragment.show(this.getFragmentManager(), "DatePicker");


                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
