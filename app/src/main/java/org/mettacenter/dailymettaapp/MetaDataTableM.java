package org.mettacenter.dailymettaapp;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * At the time of writing contains favorite column.
 *
 * The other columns are for referring back to the articles table (using two columns). The id
 * column is kept since this is adventegeos in Android for things like CursorLoader (?)
 * Can be used for storing more meta-data in the future like tags etc.
 *
 * The reason that this table is kept separate from the data itself is that the data is often
 * recreated.
 */
public class MetaDataTableM {

    public static final String TABLE_ARTICLE = "meta_data";
    public static final String COLUMN_TIME_MONTH = "time_month";
    public static final String COLUMN_TIME_DAYOFMONTH = "time_dayofmonth";
    public static final String COLUMN_FAVORITE = "favorite"; //-for the future <--- Better to put this in another table

    public static final String NO_TEXT = "";
    public static final int TIME_NOT_SET = -1;
    public static final int BOOL_AS_INT_FALSE = 0;
    public static final int BOOL_AS_INT_TRUE = 1;

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_ARTICLE
            + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
            + ", " + COLUMN_TIME_MONTH + " INTEGER NOT NULL DEFAULT '" + TIME_NOT_SET + "'"
            + ", " + COLUMN_TIME_DAYOFMONTH + " INTEGER NOT NULL DEFAULT '" + TIME_NOT_SET + "'"
            + ", " + COLUMN_FAVORITE + " INTEGER NOT NULL DEFAULT '" + BOOL_AS_INT_FALSE + "'"
            + ", " + " UNIQUE(" + COLUMN_TIME_MONTH + ", " + COLUMN_TIME_DAYOFMONTH + ")"
            + ");";

    public static void createTable(SQLiteDatabase iDb){
        iDb.execSQL(CREATE_TABLE);
        Log.i(ConstsU.TAG, "Table " + TABLE_ARTICLE + " created! Database version = "
                + iDb.getVersion());
    }

    public static void upgradeTable(SQLiteDatabase iDb, int iOldVer, int iNewVer){
        if(false){
            //empty for now
        }else{
            Log.w(ConstsU.TAG, "Upgrade removed the old table and created a new one, "
                    + "all data in the old table was deleted");
            iDb.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
            createTable(iDb);
        }
    }

}
