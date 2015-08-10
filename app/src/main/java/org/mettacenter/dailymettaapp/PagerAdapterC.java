package org.mettacenter.dailymettaapp;

import android.database.Cursor;
import android.os.Bundle;
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
public class PagerAdapterC extends FragmentStatePagerAdapter {

    private Cursor mrefCursor = null;

    public PagerAdapterC(FragmentManager fm, Cursor irefCursor) {
        super(fm);
        mrefCursor = irefCursor;
    }

    @Override
    public Fragment getItem(int iPos) {
        Fragment tFragment = new ArticleFragmentC();
        Bundle tArgs = new Bundle();

        String tTitleSg = "no title found";
        String tTextSg = "no article text found";
        String tLinkSg = "no link found";
        int tArticleTitleIndex = mrefCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TITLE);
        int tArticleIndex = mrefCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT);
        int tLinkIndex = mrefCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_LINK);
        try{

            mrefCursor.moveToPosition(iPos);
            tTitleSg = mrefCursor.getString(tArticleTitleIndex);
            tTextSg = mrefCursor.getString(tArticleIndex);
            tLinkSg = mrefCursor.getString(tLinkIndex);

        }catch(Exception e){
            Log.e(UtilitiesU.TAG, e.getMessage());
        }

        tArgs.putString(ArticleFragmentC.ARG_TITLE, tTitleSg);
        tArgs.putString(ArticleFragmentC.ARG_TEXT, tTextSg);
        tArgs.putString(ArticleFragmentC.ARG_LINK, tLinkSg);

        tFragment.setArguments(tArgs);
        return tFragment;
    }

    @Override
    public int getCount() {
        return mrefCursor.getCount();
    }
}
