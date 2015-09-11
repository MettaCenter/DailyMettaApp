package org.mettacenter.dailymettaapp;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * SQLite table for storing article data
 */
public class ArticleTableM {
    public static final String TABLE_ARTICLE = "article";

    public static final String COLUMN_TIME_MONTH = "time_month";
    public static final String COLUMN_TIME_DAYOFMONTH = "time_dayofmonth";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_INTERNAL_BOOKMARK = "bookmark";

    public static final String NO_TEXT = "";
    public static final int TIME_NOT_SET = -1;
    public static final int NOT_BOOKMARKED = -1;

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_ARTICLE
            + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY"
            + ", " + COLUMN_TIME_MONTH + " INTEGER NOT NULL DEFAULT '" + TIME_NOT_SET + "'"
            + ", " + COLUMN_TIME_DAYOFMONTH + " INTEGER NOT NULL DEFAULT '" + TIME_NOT_SET + "'"
            + ", " + COLUMN_TEXT + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ", " + COLUMN_TITLE + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ", " + COLUMN_LINK + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ", " + COLUMN_INTERNAL_BOOKMARK + " INTEGER NOT NULL DEFAULT '" + NOT_BOOKMARKED + "'"
            + ");";
    /*
    -Please note that AUTOINCREMENT is not used and this is not needed for CursorAdapter, see
    this page for details:
    http://stackoverflow.com/questions/27681391/what-is-the-use-of-basecolumnss-id-primary-key-in-android
     */

    public static void createTable(SQLiteDatabase iDb){
        iDb.execSQL(CREATE_TABLE);
        Log.i(ConstsU.APP_TAG, "Table " + TABLE_ARTICLE + " created! Database version = "
                + iDb.getVersion());
    }

    public static void upgradeTable(SQLiteDatabase iDb, int iOldVer, int iNewVer){
        if(false){
            //empty for now
        }else{
            Log.w(ConstsU.APP_TAG, "Upgrade removed the old table and created a new one, "
                    + "all data in the old table was deleted");
            iDb.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
            createTable(iDb);
        }
    }
}
