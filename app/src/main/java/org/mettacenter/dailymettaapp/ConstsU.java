package org.mettacenter.dailymettaapp;

/**
 * Global constants for the entire application
 */
public interface ConstsU {
    String TAG = "daily_metta_app";
    String EMPTY_STRING = "";
    String EXTRA_ARTICLE_POS_ID = "ARTICLE_POS_ID";
    int LATEST_ARTICLE_VIEWPAGER_POSITION = 0;

    //Server
    String SERVER_TIMEZONE = "GMT-7";
    String ATOM_FEED_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    int MAX_NR_OF_ARTICLES_TO_READ = 400;
    String ATOM_FEEL_URL = "http://mettacenter.org/category/daily-metta/feed/atom/?n=" + MAX_NR_OF_ARTICLES_TO_READ;

    //Preferences
    String GLOBAL_SHARED_PREFERENCES = "preferences";
    String PREF_LAST_UPDATE_TIME = "last_update_time";
    long DB_NEVER_UPDATED = -1;
    int UPDATE_INTERVAL_IN_DAYS = 1;
    int UPDATE_INTERVAL_IN_MINUTES = 180;
}
