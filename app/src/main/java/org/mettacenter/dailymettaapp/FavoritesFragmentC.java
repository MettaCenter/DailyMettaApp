package org.mettacenter.dailymettaapp;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;

/**
 * Fragment for displaying the list of search results
 * The actual search query takes place in the onCreateLoader method
 */
public class FavoritesFragmentC
        extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);

        //Setting up the adapter..
        String[] tFromColumnsSg = new String[]{ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};
        int[] tToGuiIt = new int[]{R.id.favorite_row_title, R.id.favorite_row_date_month, R.id.favorite_row_date_dayofmonth}; //-contained in the layout
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.element_favorite_row, null, tFromColumnsSg, tToGuiIt, 0);
        mAdapter.setViewBinder(new FavoriteViewBinderM());

        //..add it to the ListView contained within this activity
        setListAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int iIdUnused, Bundle iArgumentsUnused) {

        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};
        String tSel = ArticleTableM.COLUMN_INTERNAL_FAVORITE_WITH_TIME + " != " + ArticleTableM.NOT_FAVORITE;

        CursorLoader rLoader = new CursorLoader(getActivity(),
                ContentProviderM.ARTICLE_CONTENT_URI,
                tProj, tSel, null, ConstsU.SORT_ORDER);

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
    private class FavoriteViewBinderM
            implements SimpleCursorAdapter.ViewBinder{
        public boolean setViewValue(View iView, Cursor iCursor, int iColIndex){
            int tTitleColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TITLE);
            int tTimeMonthColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_MONTH);
            int tTimeDayOfMonthColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_DAYOFMONTH);

            if(iColIndex == tTitleColIndex) {
                TextView tTextView = (TextView)iView.findViewById(R.id.favorite_row_title);
                String tTitleSg = UtilitiesU.getPartOfTitleInsideQuotes(iCursor.getString(tTitleColIndex));
                tTextView.setText(Html.fromHtml(tTitleSg));
                ///tTextView.setTypeface(null, Typeface.BOLD);

                return true;
            }else if(iColIndex == tTimeMonthColIndex){
                TextView tTextView = (TextView)iView.findViewById(R.id.favorite_row_date_month);

                tTextView.setText(new DateFormatSymbols().getMonths()[iCursor.getInt(tTimeMonthColIndex)] + " ");
                ///tTextView.setTypeface(null, Typeface.ITALIC);

                /*
                long tArticleTimeInMillisLg = iCursor.getLong(tTimeColIndex);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(tArticleTimeInMillisLg);

                SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat("MMMM d");

                String tMonthAndDaySg = tSimpleDateFormat.format(c.getTime());
                tTextView.setText(tMonthAndDaySg);
                ///tTextView.setTypeface(null, Typeface.ITALIC);
                */

                return true;
            }
            return false; //-default
        }
    }


    /**
     * Please note that the id for the list items starts at 1, while the ViewPager positions
     * start at 0, therefore we subtract 1 from the id that we receive in this method
     */
    @Override
    public void onListItemClick(ListView iListView, View iView, int iPos, long iId){
        super.onListItemClick(iListView, iView, iPos, iId);

        Log.d(ConstsU.TAG, "onListItemClick, iId = " + iId);

        //Starting a new article activity with the fragment for the chosen article
        /////Uri tUri = Uri.parse(ContentProviderM.ARTICLE_CONTENT_URI + "/" + iId);
        Intent tIntent = new Intent(this.getActivity(), ArticleActivityC.class);
        tIntent.putExtra(ConstsU.EXTRA_ARTICLE_POS_ID, iId); /////iId temporarily used, removed "tUri.toString()"
        this.getActivity().startActivityForResult(tIntent, 0);
    }

}
