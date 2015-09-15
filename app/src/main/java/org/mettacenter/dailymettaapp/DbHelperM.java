package org.mettacenter.dailymettaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates, opens, and updates the database
 */
public class DbHelperM
        extends SQLiteOpenHelper {

    private static final int DB_VERSION = 5;
    public static final String DB_NAME = "articles.sqlite";

    private static DbHelperM sDbHelper;

    /**
     * Singleton access method, creates a new static instance if one has not already been created
     * @param iContext
     * @return An instance (the instance, since there can be only one) of this class
     */
    public static DbHelperM get(Context iContext){
        if(sDbHelper == null){
            sDbHelper = new DbHelperM(iContext.getApplicationContext());
        }
        return sDbHelper;
    }

    private DbHelperM(Context iContext){
        super(iContext, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase iDb){
        ArticleTableM.createTable(iDb);
    }

    /**
     * One way to upgrade the table is to simply drop it and then download all articles again
     */
    @Override
    public void onUpgrade(SQLiteDatabase iDb, int iOldDbVer, int iNewDbVer) {
        if(false){
            //empty for now
        }else{
            //PLEASE NOTE: When we reach this else clause, we'll simply drop the tables
            ArticleTableM.upgradeTable(iDb, iOldDbVer, iNewDbVer);
        }
    }
}
