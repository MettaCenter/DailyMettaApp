package org.mettacenter.dailymettaapp;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Contains various static helper methods for the entire application
 */
public class UtilitiesU {

    public static final String TAG = "daily_metta_app";

    public static final String EMPTY_STRING = "";


    public static final String TIMEZONE = "GMT-7";

    public static final String EXTRA_ARTICLE_POS_ID = "ARTICLE_POS_ID";
}
