package gjcm.kxf.tools;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by kxf on 2017/3/16.
 */
public class PrintSoundProvider extends ContentProvider {
    public static String DBNAME = "gjcmhuifu.db";
    public static String TABLENAME = "printsound";
    public static int DBVERSION = 1;
    private  SQLiteDatabase database;
    private static final int MULTIPLE_LINER = 1;
    private static final int SINGLE_LINER = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PrintSoundPR.AUTHORITY, PrintSoundPR.PATH_MULTIPLE, MULTIPLE_LINER);
        uriMatcher.addURI(PrintSoundPR.AUTHORITY, PrintSoundPR.PATH_SINGLE, SINGLE_LINER);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, DBNAME, null, DBVERSION);
        database = dbOpenHelper.getWritableDatabase();

        if (database == null)
            return false;
        else return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLENAME);
        switch (uriMatcher.match(uri)) {
            case SINGLE_LINER:
                qb.appendWhere(PrintSoundPR.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }
        Cursor cursor = qb.query(database,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_LINER:
                return PrintSoundPR.MIME_TYPE_MULTIPLE;
            case SINGLE_LINER:
                return PrintSoundPR.MIME_TYPE_SINGLE;
            default:
                throw new IllegalArgumentException("Unkown gettype uro:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long code = database.insert(TABLENAME, null, contentValues);
        if (code > 0) {
            Uri newUri = ContentUris.withAppendedId(PrintSoundPR.CONTENT_URI, code);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int code = 0;

        switch (uriMatcher.match(uri)) {
            case MULTIPLE_LINER:
                code = database.delete(TABLENAME, s, strings);
                break;
            case SINGLE_LINER:
                String segment = uri.getPathSegments().get(1);
                code = database.delete(TABLENAME, PrintSoundPR.KEY_ID + "=" + segment, strings);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return code;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_LINER:
                count = database.update(TABLENAME, values, selection, selectionArgs);
                break;
            case SINGLE_LINER:
                String segment = uri.getPathSegments().get(1);
                count = database.update(TABLENAME, values, PrintSoundPR.KEY_ID + "=" + segment, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String PS_TABLE = "create table if not exists " + TABLENAME + "(" + PrintSoundPR.KEY_ID + " integer primary key ," +
                PrintSoundPR.KEY_CONTENT + " boolean ," + PrintSoundPR.KEY_DEL + " integer );";
//        private static final String DB_CREATE = "create table " +
//                TABLENAME + "(" + PrintSoundPR.KEY_ID + " integer primary key autoincrement," +
//                PrintSoundPR.KEY_NAME + " varchar(20)," + PrintSoundPR.KEY_TITLE + " TEXT NOT NULL," +
//                PrintSoundPR.KEY_CONTENT + " integer ," + PrintSoundPR.KEY_DEL + " integer );";

        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PS_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
            onCreate(db);
        }
    }
}
