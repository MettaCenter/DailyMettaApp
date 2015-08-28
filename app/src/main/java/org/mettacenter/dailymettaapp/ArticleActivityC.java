package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Main activity for the application shown when the user starts the app
 *
 * Please note: AppCompatActivity extends ActionbarActivity
 */
public class ArticleActivityC
        extends AppCompatActivity {

    //Adapter and pager for side swipe
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private Cursor mCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        SharedPreferences tSharedPreferences = getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        long tLastUpdateInMillisLg = tSharedPreferences.getLong(
                ConstsU.PREF_LAST_UPDATE_TIME, ConstsU.DB_NEVER_UPDATED);
        long tUpdateIntervalInMillisLg = TimeUnit.MINUTES.toMillis(ConstsU.UPDATE_INTERVAL_IN_MINUTES); //-TODO: Change to days
        boolean tIsUpdateIntervalReachedBl =
                Calendar.getInstance().getTimeInMillis() - tLastUpdateInMillisLg
                >= tUpdateIntervalInMillisLg;
        Log.d(ConstsU.TAG, "tIsUpdateIntervalReachedBl = " + tIsUpdateIntervalReachedBl);
        Log.d(ConstsU.TAG, "tLastUpdateInMillisLg = " + tLastUpdateInMillisLg);
        Log.d(ConstsU.TAG, "Calendar.getInstance().getTimeInMillis() = " + Calendar.getInstance().getTimeInMillis());
        Log.d(ConstsU.TAG, "tUpdateIntervalInMillisLg = " + tUpdateIntervalInMillisLg);




        //Checking if this is the first time the app is started or if we are running a new version
        int tOldVer = PreferenceManager.getDefaultSharedPreferences(this).getInt(
                ConstsU.PREF_APP_VERSION_CODE, ConstsU.APP_NEVER_STARTED);
        int tNewVer = 0;
        try {
            tNewVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf(ConstsU.TAG, e.getMessage());
            e.printStackTrace();
            finish();
        }




        //TODO: Do we want to do the update when a new app version is launched?

        //Setting up the data..
        if(tIsUpdateIntervalReachedBl == true
                || tLastUpdateInMillisLg == ConstsU.DB_NEVER_UPDATED
                || tNewVer > tOldVer){
            //..clearing the db
            getApplicationContext().deleteDatabase(DbHelperM.DB_NAME);

            //..fetching all the articles
            new FetchArticlesTaskC(this, new AppSetupCallbackClass()).execute();

            if(tNewVer > tOldVer){
                //Writing the new version into the shared preferences
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putInt(ConstsU.PREF_APP_VERSION_CODE, tNewVer)
                        .commit();
            }

        }else{
            finishSetup();
        }

        /*..setup continues in AppSetupCallbackClass below after (1) a callback from
        FetchArticlesTaskC (if we update the db) or (2) a direct call (if we don't update the db)
        */
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

        int tPositionIt = (int)getIntent().getLongExtra(ConstsU.EXTRA_ARTICLE_POS_ID,
                ConstsU.LATEST_ARTICLE_VIEWPAGER_POSITION);
        mViewPager.setCurrentItem(tPositionIt);
        getIntent().removeExtra(ConstsU.EXTRA_ARTICLE_POS_ID);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem iMenuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(iMenuItem.getItemId()){
            case R.id.action_text_search:
                onSearchRequested();
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
