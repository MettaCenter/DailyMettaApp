package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

/**
 * Main activity for the application, shown when the user starts the app. Holds article fragments
 * using a ViewPager
 *
 * Please note: AppCompatActivity extends ActionbarActivity
 */
public class ArticleActivityC
        extends AppCompatActivity
        implements FetchArticlesTaskC.OnAsyncTaskDoneListenerI{

    //Cursor, adapter, pager for side swipe
    private Cursor mCursor = null;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupStart();
    }

    /*
     * Overview of the setup process:
     * 1.
     */
    private void setupStart() {
        switch(UtilitiesU.downloadLogic(this)){
            case DISPLAY_MANUAL_DOWNLOAD_BUTTON:
                findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.pager).setVisibility(View.GONE);
                break;
            case START_DOWNLOAD_OF_ARTICLES:
                downloadArticlesAndFinishSetup();
                break;
            case USE_ALREADY_DOWNLOADED_ARTICLES:
                finishSetup();
                break;
            default:
                Log.wtf(ConstsU.APP_TAG, "Enum case not covered");
        }

        Button tManualDownloadBn = (Button)findViewById(R.id.manual_download_button);
        tManualDownloadBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadArticlesAndFinishSetup();
            }
        });
    }

    private void downloadArticlesAndFinishSetup() {
        findViewById(R.id.empty_layout).setVisibility(View.GONE);
        findViewById(R.id.pager).setVisibility(View.VISIBLE);

        new FetchArticlesTaskC(this, this).execute();
    }

    public void finishSetup(){
        //Setting up the cursor, adapter and pager
        try{
            mCursor = getContentResolver().query(
                    ContentProviderM.ARTICLE_CONTENT_URI, null, null, null, ConstsU.COMMON_SORT_ORDER);
        }catch(Exception e){
            Log.e(ConstsU.APP_TAG, e.getMessage(), e);
        }
        mPagerAdapter = new PagerAdapterC(getSupportFragmentManager(), mCursor);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        //Redrawing all fragments
        mPagerAdapter.notifyDataSetChanged();

        //Setting the position of the viewpager
        Calendar c = Calendar.getInstance();
        long tArticleIdForTodayOrPrevious = getIntent().getLongExtra(ConstsU.EXTRA_ARTICLE_POS_ID, ConstsU.NO_ARTICLE_POS);
        if(tArticleIdForTodayOrPrevious == ConstsU.NO_ARTICLE_POS){
            tArticleIdForTodayOrPrevious = UtilitiesU.getArticleFragmentPositionFromDate(
                    this, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }
        if(tArticleIdForTodayOrPrevious == -1){
            tArticleIdForTodayOrPrevious = 0; //-use the latest article if no article was found for the date
        }
        mViewPager.setCurrentItem((int) tArticleIdForTodayOrPrevious);
        getIntent().removeExtra(ConstsU.EXTRA_ARTICLE_POS_ID);

        //Starting background services
        NotificationServiceC.start(this);
        BackgroundDownloadServiceC.start(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mCursor != null){
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu iMenu) {
        //Inflating the menu (which will add items to the action bar)
        getMenuInflater().inflate(R.menu.menu_article, iMenu);

        //Setting up the SearchView
        SearchManager tSearchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView tSearchView = (SearchView) iMenu.findItem(R.id.action_text_search).getActionView();
        ComponentName tComponentName = this.getComponentName();
        SearchableInfo tSearchableInfo = tSearchManager.getSearchableInfo(tComponentName);
        tSearchView.setSearchableInfo(tSearchableInfo);
        tSearchView.setIconifiedByDefault(false);

        if(BuildConfig.DEBUG == false){
            MenuItem tMenuItem = iMenu.findItem(R.id.action_debug_download);
            tMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem iMenuItem) {
        switch(iMenuItem.getItemId()){
            case R.id.action_donate:
                Intent tBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstsU.METTA_CENTER_DONATE_PAGE));
                startActivity(tBrowserIntent);
                return true;
            case R.id.action_text_search:
                //Intentionally left empty, the search is taken care with the help of the system
                return true;
            case R.id.action_share_app:
                Intent tGooglePlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstsU.GOOGLE_PLAY_APP_LINK));
                startActivity(tGooglePlayIntent);
                return true;
            case R.id.action_choose_date:
                DialogFragment tDatePickerFragment = new DatePickerFragmentC();
                tDatePickerFragment.show(this.getFragmentManager(), DatePickerFragmentC.TAG);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivityC.class));
                return true;
            case R.id.action_bookmarks:
                startActivity(new Intent(this, BookmarksActivityC.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivityC.class));
                return true;
            case R.id.action_debug_download:
                downloadArticlesAndFinishSetup();
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
