package org.mettacenter.dailymetta;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashSet;

/**
 * Created by sunyata on 2015-06-23.
 */
public class ContentProviderM extends ContentProvider{

    static final String AUTHORITY = "org.mettacenter.dailymetta";
    public static final Uri ARTICLE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + ArticleTableM.TABLE_ARTICLE);

    private DbHelperM mDbHelper;

    private static final int ARTICLE = 11;
    private static final int ARTICLE_SINGLE_ROW = 12;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(AUTHORITY, ArticleTableM.TABLE_ARTICLE, ARTICLE);
        sUriMatcher.addURI(AUTHORITY, ArticleTableM.TABLE_ARTICLE + "/#", ARTICLE_SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = DbHelperM.get(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri iUri, String[] iProjection, String iSelection,
            String[] iSelectionArgs, String iSortOrder) {

        verifyColumns(iUri, iProjection);


        SQLiteQueryBuilder tQueryBuilder = new SQLiteQueryBuilder();

        SQLiteDatabase tDb = mDbHelper.getWritableDatabase();

        String tTable = UtilitiesU.EMPTY_STRING;
        String tKeyColumn = UtilitiesU.EMPTY_STRING;

        switch(sUriMatcher.match(iUri)){

            case ARTICLE_SINGLE_ROW:
                tKeyColumn = BaseColumns._ID;
            case ARTICLE:
                tTable = ArticleTableM.TABLE_ARTICLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + iUri);
        }

        /*
        if(tTable.equals(UtilitiesU.EMPTY_STRING) == true){
            throw new Exception
            return null;
        }
        */

        Cursor rCr = null;

        tQueryBuilder.setTables(tTable);
        if(tKeyColumn.equals(UtilitiesU.EMPTY_STRING) == false){
            tQueryBuilder.appendWhere(tKeyColumn + "=" + iUri.getLastPathSegment());
        }
        rCr = tQueryBuilder.query(tDb, iProjection, iSelection, iSelectionArgs, null, null, null);

        rCr.setNotificationUri(getContext().getContentResolver(), iUri);

        return rCr;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri iUri, ContentValues iContentValues) {

        long tInsertRowId = 0;
        String rUriSg = "";

        SQLiteDatabase tDb = mDbHelper.getWritableDatabase();

        String tTable = UtilitiesU.EMPTY_STRING;

        Uri tContectUri = null;

        switch(sUriMatcher.match(iUri)){
            case ARTICLE:
                tTable = ArticleTableM.TABLE_ARTICLE;
                tContectUri = ARTICLE_CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + iUri);
        }

        if(tTable.equals(UtilitiesU.EMPTY_STRING) == false && tContectUri != null){
            tInsertRowId = tDb.insert(tTable, null, iContentValues);
            rUriSg = tContectUri + "/" + tInsertRowId;
        }


        getContext().getContentResolver().notifyChange(iUri, null);


        return Uri.parse(rUriSg);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO
        return 0;
    }

    private void verifyColumns(Uri iUri, String[] iProjection){

        HashSet<String> tAvailableColumns = new HashSet<String>();

        switch(sUriMatcher.match(iUri)){
            case ARTICLE:
            case ARTICLE_SINGLE_ROW:
                tAvailableColumns.add(BaseColumns._ID);
                tAvailableColumns.add(ArticleTableM.COLUMN_TIME);
                tAvailableColumns.add(ArticleTableM.COLUMN_TEXT);
                tAvailableColumns.add(ArticleTableM.COLUMN_LINK);
                tAvailableColumns.add(ArticleTableM.COLUMN_CATEGORY);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + iUri);
        }



    }
}
