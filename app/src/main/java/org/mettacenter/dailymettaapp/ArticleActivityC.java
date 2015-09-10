package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;

/**
 * Main activity for the application shown when the user starts the app
 *
 * Please note: AppCompatActivity extends ActionbarActivity
 */
public class ArticleActivityC
        extends AppCompatActivity {

    //Adapter, pager and cursor for side swipe
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Cursor mCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);





/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.metta_center_wheel);
        */

        setupStart();
    }


    /**
     *
     * Input:
     *
     * nw access: yes/no
     * tIsConnectedToInternet
     *
     * timer reached: yes/no
     * tIsUpdateIntervalReachedBl
     *
     * articles downloaded: yes/no
     * ConstsU.DB_NEVER_UPDATED
     *
     * app started AND new app version: yes/no
     * ConstsU.APP_NEVER_STARTED, tOldVer, tNewVer
     *
     *
     * Output:
     *
     * Download articles and write to db: yes/no
     *
     * Display blank screen and show message to user: yes/no
     *
     * Update ViewPager Adapter: yes/no
     *
     */
    private void setupStart() {

        UtilitiesU.DownloadActionEnum tDownloadActionEnum = UtilitiesU.downloadLogic(this);

        if(tDownloadActionEnum == UtilitiesU.DownloadActionEnum.DISPLAY_MANUAL_DOWNLOAD_BUTTON){

            findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.pager).setVisibility(View.GONE);

        }else if(tDownloadActionEnum == UtilitiesU.DownloadActionEnum.DOWNLOAD_ARTICLES){

            downloadArticlesAndFinishSetup();

        }else if(tDownloadActionEnum == UtilitiesU.DownloadActionEnum.USE_ALREADY_DOWNLOADED_ARTICLES){

            finishSetup();

        }else{
            Log.wtf(ConstsU.TAG, "Case not covered");
        }




        Button tManualDownloadBn = (Button)findViewById(R.id.manual_download_button);
        tManualDownloadBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadArticlesAndFinishSetup();
            }
        });



        /*..setup continues in AppSetupCallbackClass below after (1) a callback from
        FetchArticlesTaskC (if we update the db) or (2) a direct call (if we don't update the db)
        */
    }

    private void downloadArticlesAndFinishSetup() {

        findViewById(R.id.empty_layout).setVisibility(View.GONE);
        findViewById(R.id.pager).setVisibility(View.VISIBLE);

        //Setting up the data..

        //..clearing the db --- REMOVED, now we instead write over the previous values
        //getApplicationContext().deleteDatabase(DbHelperM.DB_NAME);

        //..fetching all the articles
        new FetchArticlesTaskC(this, new AppSetupCallbackClass())
                .execute();
    }


    public class AppSetupCallbackClass {
        public void setupCallback(){
            ArticleActivityC.this.finishSetup();
        }
    }

    private void finishSetup(){
        try{
            mCursor = getContentResolver().query(
                    ContentProviderM.ARTICLE_CONTENT_URI, null, null, null, ConstsU.SORT_ORDER);
            mPagerAdapter = new PagerAdapterC(getSupportFragmentManager(), mCursor);
        }catch(Exception e){
            Log.e(ConstsU.TAG, e.getMessage());
        }

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        //Redrawing all fragments
        mPagerAdapter.notifyDataSetChanged();


        //Setting the position of the viewpager
        Calendar c = Calendar.getInstance();
        long tArticleIdForTodayOrPrevious = -1;
        tArticleIdForTodayOrPrevious = UtilitiesU.getArticleFragmentPositionFromId(
                this, getIntent().getLongExtra(ConstsU.EXTRA_ARTICLE_POS_ID, -1));
        if(tArticleIdForTodayOrPrevious == -1){
            tArticleIdForTodayOrPrevious = UtilitiesU.getArticleFragmentPositionFromDate(
                    this, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }
        if(tArticleIdForTodayOrPrevious == -1){
            tArticleIdForTodayOrPrevious = 0; //-use the latest article if no article was found for the date
        }
        mViewPager.setCurrentItem((int) tArticleIdForTodayOrPrevious);
        getIntent().removeExtra(ConstsU.EXTRA_ARTICLE_POS_ID);









        NotificationServiceC.setServiceNotification(this);
        BackgroundDownloadServiceC.setService(this);
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        mCursor.close();
        mCursor = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu iMenu) {
        //Inflating the menu (which will add items to the action bar)
        getMenuInflater().inflate(R.menu.menu_article, iMenu);

        SearchManager tSearchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView tSearchView = (SearchView) iMenu.findItem(R.id.action_text_search).getActionView();
        ComponentName tComponentName = this.getComponentName(); //SearchResultsActivityC.class.getComponentType()
        SearchableInfo tSearchableInfo = tSearchManager.getSearchableInfo(tComponentName);
        tSearchView.setSearchableInfo(tSearchableInfo);
        tSearchView.setIconifiedByDefault(false);



        //tSearchView.set

        /*
        ImageView tImageView = (ImageView)findViewById(R.id.search_close_btn);
        tImageView.setVisibility(View.GONE);
*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem iMenuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(iMenuItem.getItemId()){
            case R.id.action_text_search:
                //MenuItemCompat.expandActionView(iMenuItem);
                ///////////////////7onSearchRequested();
                /*
                -Android OS method which will ask the OS to show a search bar positioned over
                the action bar
                */
                return true;
            case R.id.action_choose_date:
                DialogFragment tDatePickerFragment = new DatePickerFragmentC();
                tDatePickerFragment.show(this.getFragmentManager(), "DatePicker");
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivityC.class);
                startActivity(i);
                return true;
            case R.id.action_bookmarks:
                startActivity(new Intent(this, FavoritesActivityC.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivityC.class));
                return true;
            default:
                return super.onOptionsItemSelected(iMenuItem);
        }
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
}
