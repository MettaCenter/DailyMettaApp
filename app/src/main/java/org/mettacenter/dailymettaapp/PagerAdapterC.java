package org.mettacenter.dailymettaapp;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Maps the data in the db to the Fragments in the ArticleActivityC
 *
 * Documentation:
 * * http://tumble.mlcastle.net/post/25875136857/bridging-cursorloaders-and-viewpagers-on-android
 * * http://developer.android.com/training/implementing-navigation/lateral.html
 * * http://developer.android.com/reference/android/support/v4/app/FragmentStatePagerAdapter.html
 */
public class PagerAdapterC
        extends FragmentStatePagerAdapter {

    private Cursor mrCursor = null;

    public PagerAdapterC(FragmentManager fm, Cursor irCursor) {
        super(fm);
        mrCursor = irCursor;
    }

    @Override
    public Fragment getItem(int iPos) {
        Fragment tFragment = new ArticleFragmentC();
        Bundle tArgs = new Bundle();

        long tIdLg = -1;
        String tTitleSg = "no title found";
        String tTextSg = "no article text found";
        String tLinkSg = "no link found";
        int tArticleIdIndex = mrCursor.getColumnIndexOrThrow(BaseColumns._ID);
        int tArticleTitleIndex = mrCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TITLE);
        int tArticleIndex = mrCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT);
        int tLinkIndex = mrCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_LINK);
        try{

            mrCursor.moveToPosition(iPos);
            tIdLg = mrCursor.getLong(tArticleIdIndex);
            tTitleSg = mrCursor.getString(tArticleTitleIndex);
            tTextSg = mrCursor.getString(tArticleIndex);
            tLinkSg = mrCursor.getString(tLinkIndex);

        }catch(Exception e){
            Log.e(ConstsU.TAG, e.getMessage());
        }

        tArgs.putLong(ArticleFragmentC.ARG_ID, tIdLg);
        tArgs.putString(ArticleFragmentC.ARG_TITLE, tTitleSg);
        tArgs.putString(ArticleFragmentC.ARG_TEXT, tTextSg);
        tArgs.putString(ArticleFragmentC.ARG_LINK, tLinkSg);

        tFragment.setArguments(tArgs);
        return tFragment;
    }

    @Override
    public int getCount() {
        return mrCursor.getCount();
    }
}
