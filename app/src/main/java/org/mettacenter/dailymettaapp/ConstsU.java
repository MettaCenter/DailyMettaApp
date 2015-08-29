package org.mettacenter.dailymettaapp;

/**
 * Global constants for the entire application
 */
public interface ConstsU {
    String TAG = "daily_metta_app";
    String EMPTY_STRING = "";
    String EXTRA_ARTICLE_POS_ID = "ARTICLE_POS_ID";
    int LATEST_ARTICLE_VIEWPAGER_POSITION = 0;
    String SORT_ORDER = ArticleTableM.COLUMN_TIME_MONTH + " DESC, "
            + ArticleTableM.COLUMN_TIME_DAYOFMONTH + " DESC";

    //Server
    String SERVER_TIMEZONE = "GMT-7";
    String FEED_TIME_ZONE = "GMT";
    String FEED_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    int MAX_NR_OF_ARTICLES_TO_READ = 400;
    String ATOM_FEEL_URL = "http://mettacenter.org/category/daily-metta/feed/atom/?n=" + MAX_NR_OF_ARTICLES_TO_READ;

    //Preferences
    String GLOBAL_SHARED_PREFERENCES = "preferences";
    String PREF_LAST_UPDATE_TIME_IN_MILLIS = "last_update_time"; //-Local time zone
    long DB_NEVER_UPDATED = -1;
    int UPDATE_INTERVAL_IN_DAYS = 1;
    int UPDATE_INTERVAL_IN_MINUTES = 60;
    String PREF_APP_VERSION_CODE = "app_version_code";
    int APP_NEVER_STARTED = -1;
}
