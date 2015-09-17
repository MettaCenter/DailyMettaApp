package org.mettacenter.dailymettaapp;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Random;

/**
 * Fragment for displaying the list of search results
 * The actual search query takes place in the onCreateLoader method
 */
public class SearchResultsFragmentC
        extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;
    private String mSearchStringSg = null;
    private static final int SEARCH_CONTEXT_CHARACTER_PADDING = 50;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent tIntent = getActivity().getIntent();

        if(Intent.ACTION_SEARCH.equals(tIntent.getAction())){
            mSearchStringSg = tIntent.getStringExtra(SearchManager.QUERY);
            //-this data will be used when the loader is initiated

            Log.d(ConstsU.APP_TAG, "mSearchStringSg = " + mSearchStringSg);
        }
        if(mSearchStringSg == null){
            Log.wtf(ConstsU.APP_TAG, "Search string sent to SearchResultsFragmentC is empty");
        }

        getLoaderManager().initLoader(0, null, this);

        //Setting up the adapter..
        String[] tFromColumnsSg = new String[]{ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH, ArticleTableM.COLUMN_TEXT, ArticleTableM.COLUMN_INTERNAL_BOOKMARK};
        int[] tToGuiIt = new int[]{R.id.search_row_title, R.id.search_row_date_month, R.id.search_row_date_dayofmonth, R.id.search_row_text, R.id.search_row_layout}; //-contained in the layout
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.element_search_row, null, tFromColumnsSg, tToGuiIt, 0);
        mAdapter.setViewBinder(new SearchViewBinderM());

        //..add it to the ListView contained within this activity
        setListAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //TODO: Code review needed
    @Override
    public Loader<Cursor> onCreateLoader(int iIdUnused, Bundle iArgumentsUnused) {

        String tSharedSel = "";

        String[] tSearchWordsSgAr = mSearchStringSg.split(ConstsU.SEARCH_DELIMETER);

        for(int i = 0; i < tSearchWordsSgAr.length; i++){
            if(i != 0) {
                tSharedSel = tSharedSel + " AND ";
            }
            tSharedSel = tSharedSel + ArticleTableM.COLUMN_TEXT + " LIKE " + "'" + "%" + tSearchWordsSgAr[i] + "%" + "'" + " "; //-LIKE is case insensitive
            //-TODO: Remove trailing s (plural)
        }



        String[] tSortProj = {BaseColumns._ID, ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TEXT, ArticleTableM.COLUMN_TEMPORARY_ADVANCED_SORT_ORDER};

        Cursor tSortCr = getActivity().getContentResolver().query(ContentProviderM.ARTICLE_CONTENT_URI, tSortProj, tSharedSel, null, null);


        ContentValues tContentValues = new ContentValues();

        long tIdLg;
        String tArticleTextSg;
        String tArticleTitleSg;
        int tRelevanceIt;
        String tLowerCaseSearchStringSg;

        if(tSortCr != null && tSortCr.getCount() > 0){
            tSortCr.moveToFirst();
            do{
                tRelevanceIt = 0;
                tIdLg = tSortCr.getLong(
                        tSortCr.getColumnIndexOrThrow(BaseColumns._ID)
                );
                tArticleTextSg = tSortCr.getString(
                        tSortCr.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT)
                );
                tArticleTitleSg = tSortCr.getString(
                        tSortCr.getColumnIndexOrThrow(ArticleTableM.COLUMN_TITLE)
                );


                for(int i = 0; i < tSearchWordsSgAr.length; i++){
                    tLowerCaseSearchStringSg = tSearchWordsSgAr[i].toLowerCase();

                    tRelevanceIt = tRelevanceIt +
                        org.apache.commons.lang3.StringUtils.countMatches(
                                tArticleTextSg.toLowerCase(), tLowerCaseSearchStringSg)
                        +
                        10 *
                        org.apache.commons.lang3.StringUtils.countMatches(
                            tArticleTitleSg.toLowerCase(), tLowerCaseSearchStringSg);
                    tContentValues.put(ArticleTableM.COLUMN_TEMPORARY_ADVANCED_SORT_ORDER, tRelevanceIt);
                }

                getActivity().getContentResolver().update(ContentProviderM.ARTICLE_CONTENT_URI,tContentValues, BaseColumns._ID + "=" + tIdLg, null);
            }while(tSortCr.moveToNext());
        }


        tSortCr.close();
        tSortCr = null;




        ///updateSortOrderColumn(getActivity(), tSearchWordsSgAr);
        String tSortOrder = ArticleTableM.COLUMN_TEMPORARY_ADVANCED_SORT_ORDER + " DESC";


        String[] tLoaderProj = {BaseColumns._ID, ArticleTableM.COLUMN_TITLE, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH, ArticleTableM.COLUMN_TEXT, ArticleTableM.COLUMN_INTERNAL_BOOKMARK};
        CursorLoader tLoader = new CursorLoader(getActivity(), ContentProviderM.ARTICLE_CONTENT_URI,
                tLoaderProj, tSharedSel, null, tSortOrder);


        return tLoader;
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
    private class SearchViewBinderM
            implements SimpleCursorAdapter.ViewBinder{
        public boolean setViewValue(View iView, Cursor iCursor, int iColIndex){
            int tTitleColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TITLE);
            int tTimeMonthColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_MONTH);
            int tTimeDayOfMonthColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TIME_DAYOFMONTH);
            int tTextColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT);
            int tFavoriteColIndex = iCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_INTERNAL_BOOKMARK);

            if(iColIndex == tTitleColIndex) {
                TextView tTextView = (TextView)iView.findViewById(R.id.search_row_title);
                String tTitleSg = iCursor.getString(tTitleColIndex);
                tTextView.setText(Html.fromHtml(tTitleSg));
                ///tTextView.setTypeface(null, Typeface.BOLD);

                return true;
            }else if(iColIndex == tTimeMonthColIndex){
                TextView tTextView = (TextView)iView.findViewById(R.id.search_row_date_month);

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
            }else if(iColIndex == tTextColIndex){
                TextView tTextView = (TextView)iView.findViewById(R.id.search_row_text);

                String tArticleTextSg = iCursor.getString(tTextColIndex);

                //Finding the first instance of the first searched for string

                String tFirstSearchStringSg;

                if(mSearchStringSg.contains(ConstsU.SEARCH_DELIMETER)){
                    tFirstSearchStringSg = mSearchStringSg.split(ConstsU.SEARCH_DELIMETER)[0];
                }else{
                    tFirstSearchStringSg = mSearchStringSg;
                }

                int tFirstMatchingPosition = tArticleTextSg.toLowerCase().indexOf(
                        tFirstSearchStringSg.toLowerCase(), 0);
                //-TODO: Do we want to change the index where the search is started (now set to 0 which includes some html)?
                //-Please note that we need to use toLowerCase() for both Strings to show the correct position in cases where there is a difference between character cases

                int tCharStart = tFirstMatchingPosition - SEARCH_CONTEXT_CHARACTER_PADDING;
                if(tCharStart < 0){tCharStart = 0;}
                int tCharEnd = tFirstMatchingPosition + tFirstSearchStringSg.length() + SEARCH_CONTEXT_CHARACTER_PADDING;
                if(tCharEnd > tArticleTextSg.length()){tCharEnd = tArticleTextSg.length();}

                String tContextTextSg = iCursor.getString(tTextColIndex).substring(tCharStart, tCharEnd);

                int tFirstSpace = tContextTextSg.indexOf(" ");
                if(tFirstSpace == -1){tFirstSpace = 0;}
                int tLastSpace = tContextTextSg.lastIndexOf(" ");
                if(tLastSpace == -1){tLastSpace = tContextTextSg.length() - 1;}

                String tContextTextFurtherCutToSpaces = tContextTextSg.substring(tFirstSpace + 1, tLastSpace);



                /*
                If we want the searched for word in bold we can use the info here:
                http://stackoverflow.com/questions/14371092/how-to-make-a-specific-text-on-textview-bold
                 */

                tTextView.setText(tContextTextFurtherCutToSpaces);

                return true;
            }else if(iColIndex == tFavoriteColIndex){
                boolean tIsFavorite = iCursor.getLong(tFavoriteColIndex) != ArticleTableM.NOT_BOOKMARKED;

                LinearLayout iLinearLayout = (LinearLayout)iView.getRootView().findViewById(R.id.search_row_layout);

                if(tIsFavorite == true){
                    ((LinearLayout)iView).setBackgroundColor(Color.parseColor("#f2d7b9"));
                }else{
                    ((LinearLayout)iView).setBackgroundColor(Color.parseColor("#ffffff"));
                }

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
