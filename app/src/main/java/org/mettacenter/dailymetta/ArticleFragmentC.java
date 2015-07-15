package org.mettacenter.dailymetta;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Holds a Daily Metta article
 */
public class ArticleFragmentC extends Fragment {

    public static final String ARG_ARTICLE = "article";
    public static final String ARG_LINK = "link";

    @Override
    public View onCreateView(LayoutInflater iInflater, ViewGroup iContainer,
                             Bundle iSavedInstanceState){

        View rRootView = iInflater.inflate(R.layout.fragment_article, iContainer, false);

        Bundle tArgs = getArguments();

        TextView tArticleTv = ((TextView) rRootView.findViewById(R.id.article));
        android.text.Spanned tArticleHtmlFormatted = Html.fromHtml(tArgs.getString(ARG_ARTICLE)
                .replaceAll("<img.+/(img)*>", ""));
        tArticleTv.setText(tArticleHtmlFormatted); // + tArticleText

        /*
        Linking to the web. Please note that we need to avoid using android:autoLink="web" here
        which is for plain text links and not links inside <a> tags
         */
        tArticleTv.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tLinkTv = ((TextView) rRootView.findViewById(R.id.link_to_article));
        android.text.Spanned tLinkHtmlFormatted = Html.fromHtml(tArgs.getString(ARG_LINK));
        tLinkTv.setText(tLinkHtmlFormatted);
        tLinkTv.setMovementMethod(LinkMovementMethod.getInstance());

        return rRootView;
    }
}
