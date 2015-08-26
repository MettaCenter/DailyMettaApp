package org.mettacenter.dailymettaapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * Holds a Daily Metta article
 */
public class ArticleFragmentC
        extends Fragment {

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
}
