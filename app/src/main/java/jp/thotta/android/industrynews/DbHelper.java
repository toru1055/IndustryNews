package jp.thotta.android.industrynews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thotta on 2016/05/02.
 */
public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "industry_news.db", null, 1);
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        createIndustry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS industry");
        createIndustry(db);
    }
}
