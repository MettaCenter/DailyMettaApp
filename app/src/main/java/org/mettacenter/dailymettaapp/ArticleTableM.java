package org.mettacenter.dailymettaapp;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * SQLite table for storing article data
 */
public class ArticleTableM {
    public static final String TABLE_ARTICLE = "article";
    //public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_TIME = "time"; //-saved as an integer in unix time
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_CATEGORY = "category";

    public static final String NO_TEXT = "";
    public static final int TIME_NOT_SET = -1;

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_ARTICLE
            + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
            + ", " + COLUMN_TIME + " INTEGER NOT NULL DEFAULT '" + TIME_NOT_SET + "'"
            + ", " + COLUMN_TEXT + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ", " + COLUMN_TITLE + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ", " + COLUMN_LINK + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ", " + COLUMN_CATEGORY + " TEXT NOT NULL DEFAULT '" + NO_TEXT + "'"
            + ");";

    public static void createTable(SQLiteDatabase iDb){
        iDb.execSQL(CREATE_TABLE);
        Log.i(UtilitiesU.TAG, "Table " + TABLE_ARTICLE + " created! Database version = "
                + iDb.getVersion());
    }

    public static void upgradeTable(SQLiteDatabase iDb, int iOldVer, int iNewVer){
        if(false){
            //empty for now
        }else{
            Log.w(UtilitiesU.TAG, "Upgrade removed the old table and created a new one, "
                    + "all data in the old table was deleted");
            iDb.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
            createTable(iDb);
        }
    }
}
