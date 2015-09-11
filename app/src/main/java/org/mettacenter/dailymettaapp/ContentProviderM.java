package org.mettacenter.dailymettaapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * ContentProvider for our application, works as an interface/connection between the SQLite
 * database and the UI with methods for inserting and reading data etc
 */
public class ContentProviderM
        extends ContentProvider{

    static final String AUTHORITY = "org.mettacenter.dailymettaapp";
    public static final Uri ARTICLE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + ArticleTableM.TABLE_ARTICLE);
    ///public static final Uri FAVORITE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FavoriteTableM.TABLE_FAVORITE);

    /**
     * When the first two arguments that is seen in the "addURI" method  below matches the input
     * the "match" method will produce the number in the third argument given to "addURI".
     * The dash "#" is a special case which is matched against any number
     */
    private static final int ARTICLE = 11;
    private static final int ARTICLE_SINGLE_ROW = 12;
    private static final int FAVORITE = 21;
    private static final int FAVORITE_SINGLE_ROW = 22;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(AUTHORITY, ArticleTableM.TABLE_ARTICLE, ARTICLE);
        sUriMatcher.addURI(AUTHORITY, ArticleTableM.TABLE_ARTICLE + "/#", ARTICLE_SINGLE_ROW);
    }

    private DbHelperM mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = DbHelperM.get(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri iUri, String[] iProjectionAy, String iSelectionSg,
            String[] iSelectionArgsAy, String iSortOrderSg) {
        //verifyColumns(iUri, iProjectionAy);
        SQLiteQueryBuilder tQueryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase tDb = mDbHelper.getWritableDatabase();
        String tTable = ConstsU.EMPTY_STRING;
        String tKeyColumn = ConstsU.EMPTY_STRING;

        switch(sUriMatcher.match(iUri)){
            case ARTICLE_SINGLE_ROW:
                tKeyColumn = BaseColumns._ID;
            case ARTICLE:
                tTable = ArticleTableM.TABLE_ARTICLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + iUri);
        }

        Cursor rCr = null;
        tQueryBuilder.setTables(tTable);
        if(tKeyColumn.equals(ConstsU.EMPTY_STRING) == false){
            tQueryBuilder.appendWhere(tKeyColumn + "=" + iUri.getLastPathSegment());
        }
        rCr = tQueryBuilder.query(tDb, iProjectionAy, iSelectionSg, iSelectionArgsAy,
                null, null, iSortOrderSg);
        rCr.setNotificationUri(getContext().getContentResolver(), iUri);

        return rCr;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri iUri, ContentValues iContentValues) {
        long tInsertRowIdLg = 0; //-the row numer where the inserted data is placed
        Uri rUriSg = null;
        SQLiteDatabase tDb = mDbHelper.getWritableDatabase();
        String tTable = ConstsU.EMPTY_STRING;

        Uri tContentUri = null;

        switch(sUriMatcher.match(iUri)){
            case ARTICLE:
                tTable = ArticleTableM.TABLE_ARTICLE;
                tContentUri = ARTICLE_CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + iUri);
        }

        if(tTable.equals(ConstsU.EMPTY_STRING) == false && tContentUri != null){
            tInsertRowIdLg = tDb.insert(tTable, null, iContentValues);
            rUriSg = Uri.parse(tContentUri + "/" + tInsertRowIdLg);
        }

        getContext().getContentResolver().notifyChange(iUri, null);

        return rUriSg;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO
        return 0;
    }

    @Override
    public int update(Uri iUri, ContentValues iContentValues, String iSelection, String[] iSelectionArgs) {
        int tNumberOfRowsUpdated = 0;
        SQLiteDatabase tDb = mDbHelper.getWritableDatabase();

        Uri tContentUri = null;
        String tTable = ConstsU.EMPTY_STRING;

        switch(sUriMatcher.match(iUri)){
            case ARTICLE_SINGLE_ROW:
            case ARTICLE:
                tTable = ArticleTableM.TABLE_ARTICLE;
                tContentUri = ARTICLE_CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + iUri);
        }

        if(tTable.equals(ConstsU.EMPTY_STRING) == false && tContentUri != null){
            tNumberOfRowsUpdated = tDb.update(tTable, iContentValues, iSelection, iSelectionArgs);
        }

        getContext().getContentResolver().notifyChange(iUri, null);

        return tNumberOfRowsUpdated;
    }
}
