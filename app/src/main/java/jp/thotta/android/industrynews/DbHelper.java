package jp.thotta.android.industrynews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thotta on 2016/05/02.
 */
public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "industry_news.db", null, 2);
    }

    private static void createIndustry(SQLiteDatabase db) {
        String sql = "CREATE TABLE industry(" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL UNIQUE, " +
                "is_reading INTEGER NOT NULL DEFAULT 0, " +
                "last_updated INTEGER, " +
                "sort_mode TEXT NOT NULL DEFAULT 'recent')";
        db.execSQL(sql);
    }

    private static void createNews(SQLiteDatabase db) {
        String sql = "CREATE TABLE " +
                News.TABLE_NAME + "(" +
                News.COL_ID + " INTEGER PRIMARY KEY, " +
                News.COL_URL + " TEXT NOT NULL UNIQUE, " +
                News.COL_TITLE + " TEXT NOT NULL, " +
                News.COL_DESCRIPTION + " TEXT, " +
                News.COL_SUBSCRIPTION_NAME + " TEXT, " +
                News.COL_INDUSTRY_NAME + " TEXT, " +
                News.COL_PUB_DATE + " INTEGER, " +
                News.COL_CLICKS + " INTEGER, " +
                News.COL_IS_STOCKED + " INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createIndustry(db);
        createNews(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS industry");
        db.execSQL("DROP TABLE IF EXISTS news");
        createIndustry(db);
        createNews(db);
    }
}
