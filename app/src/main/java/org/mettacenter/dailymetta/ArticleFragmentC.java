package org.mettacenter.dailymetta;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view. UNUSED as of this writing
 */
public class ArticleFragmentC extends Fragment {

    public static final String ARG_OBJ = "fragment_object";

    @Override
    public View onCreateView(LayoutInflater iInflater, ViewGroup iContainer,
                             Bundle iSavedInstanceState){

        View rRootView = iInflater.inflate(R.layout.fragment_article, iContainer, false);

        Bundle tArgs = getArguments();

        TextView tTextView = ((TextView) rRootView.findViewById(android.R.id.text1));

/*
        ContentResolver tContentResolver = rRootView.getContext().getContentResolver();
        //Cursor tCur = tContentResolver.query(ContentProviderM.ARTICLE_CONTENT_URI, new String[]{ArticleTableM.COLUMN_LINK}, BaseColumns._ID + " = ?", new String[]{"1"}, null);

        Cursor tCr = tContentResolver.query(ContentProviderM.ARTICLE_CONTENT_URI, null, null, null, null);


        String tArticleText = "empty";
        int tIndex = tCr.getColumnIndexOrThrow(ArticleTableM.COLUMN_LINK);
        try{
            if(tCr != null){
                for(tCr.moveToFirst(); tCr.isAfterLast() == false; tCr.moveToNext()){
                    tArticleText = tCr.getString(tIndex);
                }
            }
        }catch(Exception e){
            Log.e(UtilitiesU.TAG, e.getMessage());
        }finally{
            tCr.close();
        }
*/

        tTextView.setText(tArgs.getString(ARG_OBJ) + " +++ "); // + tArticleText

        return rRootView;
    }
}
