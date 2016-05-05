package jp.thotta.android.industrynews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

/**
 * Created by thotta on 2016/05/02.
 */
public class DbHelperTest extends AndroidTestCase {
    private DbHelper dbHelper;

    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context =
                new RenamingDelegatingContext(getContext(), "test_");
        dbHelper = new DbHelper(context);
    }

    public void tearDown() throws Exception {
        dbHelper.close();
        super.tearDown();
    }

    public void testBasicUsage() throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("name", "自動車");
        db.insert("industry", null, values);
        Cursor cursor = db.query("industry", null, null, null, null, null, null);
        assertTrue(cursor.moveToNext());
        int apiId = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        assertEquals(apiId, 1);
        assertEquals(name, "自動車");
        cursor.close();
    }
}