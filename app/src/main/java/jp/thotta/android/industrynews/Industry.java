package jp.thotta.android.industrynews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thotta on 2016/05/03.
 */
public class Industry {
    public static final String TABLE_NAME = "industry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_IS_READING = "is_reading";
    public static final String COL_LAST_UPDATED = "last_updated";
    public static final String COL_SORT_MODE = "sort_mode";
    Integer id;
    String name;
    Boolean isReading = false;
    Date lastUpdated;
    String sortMode = "recent";

    public Industry() {
    }

    public Industry(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static boolean isEmpty(SQLiteDatabase db) {
        Cursor cursor = db.query("industry", null, null, null, null, null, null);
        int tableSize = cursor.getCount();
        cursor.close();
        return (tableSize == 0);
    }

    public static void addAll(List<Industry> industries, SQLiteDatabase db) {
        for (Industry industry : industries) {
            db.insertWithOnConflict(TABLE_NAME, null,
                    industry.getContentsValues(),
                    SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public static List<Industry> list(SQLiteDatabase db) {
        List<Industry> industries = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Industry industry = new Industry();
            industry.readCursor(cursor);
            industries.add(industry);
        }
        return industries;
    }

    public static List<Industry> readingList(SQLiteDatabase db) {
        List<Industry> industries = new ArrayList<>();
        String where = COL_IS_READING + " = ?";
        String[] whereArgs = {"1"};
        Cursor cursor =
                db.query(TABLE_NAME, null, where, whereArgs, null, null, null);
        while (cursor.moveToNext()) {
            Industry industry = new Industry();
            industry.readCursor(cursor);
            industries.add(industry);
        }
        return industries;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getReading() {
        return isReading;
    }

    public void setReading(Boolean isReading) {
        this.isReading = isReading;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void updateLastUpdated() {
        this.lastUpdated = new Date();
    }

    public String getSortMode() {
        return sortMode;
    }

    public void setSortMode(String sortMode) {
        if ("click".equals(sortMode)) {
            this.sortMode = "click";
        } else {
            this.sortMode = "recent";
        }
    }

    public void readCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        this.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        this.isReading =
                (cursor.getInt(cursor.getColumnIndex(COL_IS_READING)) == 1);
        this.sortMode = cursor.getString(cursor.getColumnIndex(COL_SORT_MODE));
        this.lastUpdated = new Date(
                cursor.getInt(cursor.getColumnIndex(COL_LAST_UPDATED))
        );
    }

    public ContentValues getContentsValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_NAME, name);
        values.put(COL_IS_READING, isReading ? 1 : 0);
        values.put(COL_LAST_UPDATED,
                lastUpdated == null ? null : lastUpdated.getTime());
        values.put(COL_SORT_MODE, sortMode);
        return values;
    }

    public void updateDatabase(SQLiteDatabase db) {
        String where = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        db.update(TABLE_NAME, getContentsValues(), where, whereArgs);
    }
}
