package org.mettacenter.dailymettaapp;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Html;
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
public class BookmarksFragmentC
        extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setEmptyView(getActivity().findViewById(R.id.empty_bookmarks_layout));

        getLoaderManager().initLoader(0, null, this);

        //Setting up the adapter..
        String[] tFromColumnsSg = new String[]{ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TEXT, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};
        int[] tToGuiIt = new int[]{R.id.favorite_row_title, R.id.favorite_row_quote, R.id.favorite_row_date_month, R.id.favorite_row_date_dayofmonth}; //-contained in the layout
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.element_favorite_row, null, tFromColumnsSg, tToGuiIt, 0);
        mAdapter.setViewBinder(new FavoriteViewBinderM());

        //..adding it to the ListView contained within this activity
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem iItem) {
        int iId = iItem.getItemId();

        return super.onOptionsItemSelected(iItem);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int iIdUnused, Bundle iArgumentsUnused) {

        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TEXT, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};
        String tSel = ArticleTableM.COLUMN_INTERNAL_BOOKMARK + " != " + ArticleTableM.NOT_BOOKMARKED;

        CursorLoader rLoader = new CursorLoader(getActivity(),
                ContentProviderM.ARTICLE_CONTENT_URI,
                tProj, tSel, null, ConstsU.BOOKMARK_SORT_ORDER);

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
            int tTextColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT);
            int tTimeMonthColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_MONTH);
            int tTimeDayOfMonthColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_DAYOFMONTH);


            if(iColIndex == tTitleColIndex) {
                TextView tTextView = (TextView)iView.findViewById(R.id.favorite_row_title);
                String tTitleSg = iCursor.getString(tTitleColIndex);
                tTextView.setText(Html.fromHtml(tTitleSg));
                ///tTextView.setTypeface(null, Typeface.BOLD);

                return true;

            }else if(iColIndex == tTextColIndex){

                TextView tTextView = (TextView)iView.findViewById(R.id.favorite_row_quote);

                tTextView.setText(ArticleC.extractQuote(
                        iCursor.getString(tTextColIndex)));

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

        //Starting a new article activity with the fragment for the chosen article
        Intent tIntent = new Intent(this.getActivity(), ArticleActivityC.class);
        ///tIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        tIntent.putExtra(ConstsU.EXTRA_ARTICLE_POS_ID, UtilitiesU.getArticleFragmentPositionFromId(getActivity(), iId));
        this.getActivity().startActivityForResult(tIntent, 0);
    }

}
