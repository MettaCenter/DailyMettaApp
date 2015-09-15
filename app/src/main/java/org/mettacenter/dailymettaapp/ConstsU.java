package org.mettacenter.dailymettaapp;

/**
 * Global constants for the entire application
 */
public interface ConstsU {
    String APP_TAG = "dailymettaapp";
    String EMPTY_STRING = "";
    String SEARCH_DELIMETER = " ";
    String EXTRA_ARTICLE_POS_ID = "ARTICLE_POS_ID";
    int LATEST_ARTICLE_VIEWPAGER_POSITION = 0;
    String COMMON_SORT_ORDER =
            ArticleTableM.COLUMN_TIME_MONTH + " DESC, "
            + ArticleTableM.COLUMN_TIME_DAYOFMONTH + " DESC";
    String BOOKMARK_SORT_ORDER = ArticleTableM.COLUMN_INTERNAL_BOOKMARK + " DESC";
    //////String SEARCH_SORT_ORDER = ArticleTableM.COLUMN_INTERNAL_BOOKMARK + " DESC";
    ////////String RESULTS_SORT_ORDER = ArticleTableM.COLUMN_INTERNAL_BOOKMARK + " DESC";
    long NO_ARTICLE_POS = -1;
    String METTA_CENTER_DONATE_PAGE = "http://mettacenter.org/get-involved/donate/donation-form/";
    String GOOGLE_PLAY_APP_LINK = "market://details?id=org.mettacenter.dailymettaapp";

    //Server
    String SERVER_TIMEZONE = "GMT-7";
    String FEED_TIME_ZONE = "GMT";
    String FEED_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    int MAX_NR_OF_ARTICLES_TO_READ = 400;
    String ATOM_FEEL_URL = "http://mettacenter.org/category/daily-metta/feed/atom/?n=" + MAX_NR_OF_ARTICLES_TO_READ;

    //Preferences
    String GLOBAL_SHARED_PREFERENCES = "preferences";
    String PREF_LONG_LAST_CLIENT_UPDATE_TIME_IN_MS_FEED_TZ = "last_update_time"; //-Local time zone
    long DB_NEVER_UPDATED = -1;
    int UPDATE_INTERVAL_IN_HOURS = 12;
    String PREF_APP_VERSION_CODE = "app_version_code";
    String PREF_INT_NOTIFICATION_HOUR = "notification_hour";
    String PREF_INT_NOTIFICATION_MINUTE = "notification_minute";
    int NOTIFICATION_NOT_SET = -1;
    int APP_NEVER_STARTED = -1;
}
