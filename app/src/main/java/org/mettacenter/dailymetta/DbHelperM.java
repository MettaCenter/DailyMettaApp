package org.mettacenter.dailymetta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates, opens, and updates the database
 */
public class DbHelperM extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "articles.sqlite";

    private static DbHelperM sDbHelper;

    public static DbHelperM get(Context iContext){
        if(sDbHelper == null){
            sDbHelper = new DbHelperM(iContext.getApplicationContext());
        }
        return sDbHelper;
    }

    public DbHelperM(Context iContext) {
        super(iContext, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase iDb) {
        ArticleTableM.createTable(iDb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase iDb, int iOldVer, int iNewVer) {
        if(false){
            //empty for now
        }else{
            //PLEASE NOTE: When we reach this else clause, we'll simply drop the tables
            ArticleTableM.upgradeTable(iDb, iOldVer, iNewVer);
        }
    }
}
