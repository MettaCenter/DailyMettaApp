package org.mettacenter.dailymettaapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;


/**
 * Holds a Daily Metta article
 */
public class ArticleFragmentC
        extends Fragment {

    public static final String ARG_ID = "id";
    public static final String ARG_TITLE = "title";
    public static final String ARG_TEXT = "html_text";
    public static final String ARG_LINK = "link";

    private android.text.Spanned mTitleHtmlFormatted;
    private android.text.Spanned mLinkHtmlFormatted;

    @Override
    public View onCreateView(LayoutInflater iInflater, ViewGroup iContainer,
            Bundle iSavedInstanceState){

        View rRootView = iInflater.inflate(R.layout.fragment_article, iContainer, false);
        Bundle tArgs = getArguments();

        //Title
        TextView tTitleTv = (TextView)rRootView.findViewById(R.id.article_title);
        mTitleHtmlFormatted = Html.fromHtml(tArgs.getString(ARG_TITLE));
        tTitleTv.setText(mTitleHtmlFormatted);

        //Main text
        TextView tArticleTv = (TextView)rRootView.findViewById(R.id.article_text);
        android.text.Spanned tArticleHtmlFormatted = Html.fromHtml(tArgs.getString(ARG_TEXT)
                .replaceAll("<img.+/(img)*>", ""));
        tArticleTv.setText(tArticleHtmlFormatted);

        //Linking to the web
        /*Please note that we need to avoid using android:autoLink="web" here
        which is for plain text links and not links inside <a> tags*/
        tArticleTv.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tLinkTv = ((TextView) rRootView.findViewById(R.id.article_link));
        mLinkHtmlFormatted = Html.fromHtml(tArgs.getString(ARG_LINK));
        tLinkTv.setText(mLinkHtmlFormatted);
        tLinkTv.setMovementMethod(LinkMovementMethod.getInstance());

        //Sharing
        ImageButton tShareImageButton = (ImageButton)rRootView.findViewById(R.id.share_button);
        tShareImageButton.setOnClickListener(new ShareOnClickListener());

        //Favorite
        ImageButton tFavoriteImageButton = (ImageButton)rRootView.findViewById(R.id.favorite_button);
        tFavoriteImageButton.setOnClickListener(new FavoriteOnClickListener());


        return rRootView;
    }

    private class ShareOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ///Bundle tArgs = getArguments();

            Intent tShareIntent = new Intent();
            tShareIntent.setAction(Intent.ACTION_SEND);
            tShareIntent.setType("text/plain");
            tShareIntent.putExtra(Intent.EXTRA_TEXT,
                    mTitleHtmlFormatted.toString()
                    + System.getProperty("line.separator")
                    + mLinkHtmlFormatted.toString());
            startActivity(tShareIntent);
        }
    }

    private class FavoriteOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ContentValues tContentValues = new ContentValues();

            //String[] tProj = {ArticleTableM.COLUMN_INTERNAL_FAVORITE_WITH_TIME};
            String tSel = BaseColumns._ID + "=" + getArguments().getLong(ARG_ID);
            Cursor tCr = getActivity().getContentResolver().query(
                    ContentProviderM.ARTICLE_CONTENT_URI,
                    null, tSel, null, ConstsU.SORT_ORDER);


            if(tCr != null && tCr.getCount() > 0){
                tCr.moveToFirst();

                DatabaseUtils.cursorRowToContentValues(tCr, tContentValues);
                //-ContentValues are updated

                tContentValues.put(ArticleTableM.COLUMN_INTERNAL_FAVORITE_WITH_TIME,
                        Calendar.getInstance().getTimeInMillis());
                getActivity().getContentResolver().update(
                        ContentProviderM.ARTICLE_CONTENT_URI,
                        tContentValues, tSel, null);
                tCr.close();
            }
        }
    }
}
