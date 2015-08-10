package org.mettacenter.dailymettaapp;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Activity for displaying the list of search results. UNUSED as of this writing
 */
public class SearchResultsActivityC extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter = null;
    private String mSearchStringSg = null;
    private static final String SEARCH_COL = ArticleTableM.COLUMN_TEXT;
    private static final int TEXT_CONTEXT_PADDING = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);



        Intent tIntent = getIntent();

        if(Intent.ACTION_SEARCH.equals(tIntent.getAction())){
            mSearchStringSg = tIntent.getStringExtra(SearchManager.QUERY);
            //-this data will be used when the loader is initiated

            Log.i(UtilitiesU.TAG, "mSearchStringSg = " + mSearchStringSg);

            //doMySearch(tQuery);
            //-it seems we don't need this since the query takes place in the onCreateLoader method
        }



        getLoaderManager().initLoader(0, null, this);

        //Setting up the adapter..

        String[] tFromColumnsSg = new String[]{SEARCH_COL};
        int[] tToGuiIt = new int[]{R.id.search_row_text}; //-contained in the layout

        mAdapter = new SimpleCursorAdapter(this, R.layout.element_search_row, null, tFromColumnsSg, tToGuiIt, 0);

        mAdapter.setViewBinder(new SearchViewBinderM());

        //..add it to the ListView contained within this activity
        setListAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Please note that the id for the list items starts at 1, while the ViewPager positions
     * start at 0, therefore we subtract 1 from the id that we receive in this method
     */
    @Override
    public void onListItemClick(ListView iListView, View iView, int iPos, long iId){
        super.onListItemClick(iListView, iView, iPos, iId);
        //-TODO: Experiment with removing this

        Log.i(UtilitiesU.TAG, "onListItemClick, iId = " + iId);

        //Starting a new article activity with the fragment for the chosen article
        /////Uri tUri = Uri.parse(ContentProviderM.ARTICLE_CONTENT_URI + "/" + iId);
        Intent tIntent = new Intent(SearchResultsActivityC.this, ArticleActivityC.class);
        tIntent.putExtra(UtilitiesU.EXTRA_ARTICLE_POS_ID, iId - 1); /////iId temporarily used, removed "tUri.toString()"
        SearchResultsActivityC.this.startActivityForResult(tIntent, 0);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int iIdUnused, Bundle iArgumentsUnused) {

        String[] tProj = {BaseColumns._ID, SEARCH_COL};
        String tSel = SEARCH_COL + " LIKE ?"; //-"like" is case insensitive
        String[] tSelArgs = {"%"+ mSearchStringSg +"%"};
        String tSortOrderSg = "DESC"; //-starting with the latest

        CursorLoader rLoader = new CursorLoader(
                this, ContentProviderM.ARTICLE_CONTENT_URI, tProj, tSel, tSelArgs, tSortOrderSg);

        return rLoader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> iCursorLoader, Cursor iCursor) {
        mAdapter.swapCursor(iCursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> iCursorUnused) {
        mAdapter.swapCursor(null);
    }


    /**
     * ViewBinder for showing the textual context (or preview) for each search hit
     */
    private class SearchViewBinderM implements SimpleCursorAdapter.ViewBinder{
        public boolean setViewValue(View iView, Cursor iCursor, int iColIndex){
            int tSearchColIndex = iCursor.getColumnIndexOrThrow(SEARCH_COL);

            if(iColIndex == tSearchColIndex){

                TextView tTextView = (TextView)iView.findViewById(R.id.search_row_text);

                String tArticleTextSg = iCursor.getString(tSearchColIndex);

                ///Log.i(UtilitiesU.TAG, "tArticleTextSg.length() = " + tArticleTextSg.length());

                //Finding the first instance of the searched for string

                int tFirstMatchingPosition = tArticleTextSg.indexOf(mSearchStringSg, 0);
                //-TODO: Do we want to change the index where the search is started (now set to 0 which includes some html)?

                ///Log.i(UtilitiesU.TAG, "tFirstMatchingPosition = " + tFirstMatchingPosition);

                int tStart = tFirstMatchingPosition - TEXT_CONTEXT_PADDING;
                if(tStart < 0){tStart = 0;}
                int tEnd = tFirstMatchingPosition + mSearchStringSg.length() + TEXT_CONTEXT_PADDING;
                if(tEnd > tArticleTextSg.length()){tEnd = tArticleTextSg.length();}

                String tContextTextSubString = iCursor.getString(tSearchColIndex).substring(tStart, tEnd);

                /*
                If we want the searched for word in bold we can check this link:
                http://stackoverflow.com/questions/14371092/how-to-make-a-specific-text-on-textview-bold
                 */

                tTextView.setText(tContextTextSubString);

                return true;
            }
            return false; //-default
        }
    }
}
