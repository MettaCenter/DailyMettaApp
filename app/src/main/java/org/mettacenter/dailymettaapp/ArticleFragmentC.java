package org.mettacenter.dailymettaapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Holds a Daily Metta article
 */
public class ArticleFragmentC
        extends Fragment {

    public static final String ARG_ID = "id";
    public static final String ARG_TITLE = "title";
    public static final String ARG_TEXT = "text";
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
        android.text.Spanned tArticleHtmlFormatted = Html.fromHtml(
                tArgs.getString(ARG_TEXT).replaceAll("<img.+/(img)*>", ""));
        tArticleTv.setText(tArticleHtmlFormatted);
        tArticleTv.setMovementMethod(LinkMovementMethod.getInstance());
        /*-Please note that we need to avoid using android:autoLink="web" here
        which is for plain text links and not links inside <a> tags*/

        //Linking to the web
        TextView tLinkTv = ((TextView) rRootView.findViewById(R.id.article_link));
        mLinkHtmlFormatted = Html.fromHtml(tArgs.getString(ARG_LINK));
        tLinkTv.setText(mLinkHtmlFormatted);
        tLinkTv.setMovementMethod(LinkMovementMethod.getInstance());

        //Sharing
        ImageButton tShareImageButton = (ImageButton)rRootView.findViewById(R.id.share_button);
        tShareImageButton.setOnClickListener(new ShareOnClickListener());

        //Bookmark
        RelativeLayout tBookmark = (RelativeLayout)rRootView.findViewById(R.id.article_bookmark_layout);
        tBookmark.setOnClickListener(new FavoriteOnClickListener());
        updateGuiBookmark(tBookmark);

        return rRootView;
    }

    private class ShareOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
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

            String[] tProj = {ArticleTableM.COLUMN_INTERNAL_BOOKMARK};
            String tSel = BaseColumns._ID + "=" + getArguments().getLong(ARG_ID);
            Cursor tCr = getActivity().getContentResolver().query(
                    ContentProviderM.ARTICLE_CONTENT_URI,
                    null, tSel, null, ConstsU.COMMON_SORT_ORDER);

            if(tCr != null && tCr.getCount() > 0){
                tCr.moveToFirst();

                DatabaseUtils.cursorRowToContentValues(tCr, tContentValues);
                //-ContentValues are updated

                long tPrevFavoriteWithTimeLg = UtilitiesU.getFavoriteTime(getActivity(), getArguments().getLong(ARG_ID));
                long tNewFavoriteWithTimeLg;

                if(tPrevFavoriteWithTimeLg == ArticleTableM.NOT_BOOKMARKED){
                    tNewFavoriteWithTimeLg = Calendar.getInstance().getTimeInMillis();
                }else{
                    tNewFavoriteWithTimeLg = ArticleTableM.NOT_BOOKMARKED;
                }

                tContentValues.put(ArticleTableM.COLUMN_INTERNAL_BOOKMARK,
                        tNewFavoriteWithTimeLg);
                getActivity().getContentResolver().update(
                        ContentProviderM.ARTICLE_CONTENT_URI,
                        tContentValues, tSel, null);
                tCr.close();
            }
            updateGuiBookmark((RelativeLayout)v);
        }
    }

    private void updateGuiBookmark(RelativeLayout iView){
        long tFavoriteWithTimeLg = UtilitiesU.getFavoriteTime(getActivity(), getArguments().getLong(ARG_ID));
        ImageView iBookmarkButtonIv = (ImageView)iView.findViewById(R.id.article_bookmark_button);
        if(tFavoriteWithTimeLg != ArticleTableM.NOT_BOOKMARKED){
            iView.setBackgroundResource(R.color.light_orange);
            iBookmarkButtonIv.setImageResource(R.mipmap.ic_favorite_black_24dp);
            iBookmarkButtonIv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.orange));
        }else{
            iView.setBackgroundResource(R.color.light_gray);
            iBookmarkButtonIv.setImageResource(R.mipmap.ic_favorite_border_black_24dp);
            iBookmarkButtonIv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white));
        }
        iView.invalidate();
    }
}
