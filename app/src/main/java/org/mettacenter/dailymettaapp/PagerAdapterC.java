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

    private Cursor mCursor = null;

    public PagerAdapterC(FragmentManager fm, Cursor iCursor) {
        super(fm);
        mCursor = iCursor;
    }

    @Override
    public Fragment getItem(int iPos) {
        Fragment tFragment = new ArticleFragmentC();
        Bundle tArgs = new Bundle();

        String tLinkText = "no link found";
        String tArticleText = "no article text found";
        int tLinkIndex = mCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_LINK);
        int tArticleIndex = mCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT);
        try{

            mCursor.moveToPosition(iPos);
            tLinkText = mCursor.getString(tLinkIndex);
            tArticleText = mCursor.getString(tArticleIndex);

        }catch(Exception e){
            Log.e(UtilitiesU.TAG, e.getMessage());
        }

        tArgs.putString(ArticleFragmentC.ARG_ARTICLE, tArticleText);
        tArgs.putString(ArticleFragmentC.ARG_LINK, tLinkText);

        tFragment.setArguments(tArgs);
        return tFragment;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }
}
