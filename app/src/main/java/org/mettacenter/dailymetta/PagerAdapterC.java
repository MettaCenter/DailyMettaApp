package org.mettacenter.dailymetta;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by sunyata on 2015-07-11.
 *
 * Inspiration: http://tumble.mlcastle.net/post/25875136857/bridging-cursorloaders-and-viewpagers-on-android
 *
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





        String tLinkText = "empty_link";
        String tArticleText = "empty_text";
        int tLinkIndex = mCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_LINK);
        int tArticleIndex = mCursor.getColumnIndexOrThrow(ArticleTableM.COLUMN_TEXT);
        try{

            mCursor.moveToPosition(iPos);
            tLinkText = mCursor.getString(tLinkIndex);
            tArticleText = mCursor.getString(tArticleIndex);

            /*
            if(mCursor != null){
                for(mCursor.moveToFirst(); mCursor.isAfterLast() == false; mCursor.moveToNext()){
                    tArticleText = mCursor.getString(tIndex);
                }
            }
            */
        }catch(Exception e){
            //Log.e(UtilitiesU.TAG, e.getMessage());
        }finally{
            //mCursor.close();
            //TODO: Close the cursor in the ondestroy method
        }





        ///String tText =  "" + (iPos + 1);


        tArgs.putString(ArticleFragmentC.ARG_ARTICLE, tArticleText);
        tArgs.putString(ArticleFragmentC.ARG_LINK, tLinkText);


        tFragment.setArguments(tArgs);
        return tFragment;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public CharSequence getPageTitle(int iPos) {
        return "Fragment object nr " + (iPos + 1);
    }
}
