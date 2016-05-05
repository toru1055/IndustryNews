package jp.thotta.android.industrynews;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thotta on 2016/05/03.
 */
public class IndustryTest extends AndroidTestCase {
    private DbHelper dbHelper;
    static final String TAG = IndustryTest.class.getSimpleName();

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

    public void testIsEmpty() throws Exception {
        assertTrue(Industry.isEmpty(dbHelper.getReadableDatabase()));
    }

    public void testAddAll() throws Exception {
        List<Industry> industries = new ArrayList<>();
        industries.add(new Industry(1, "自動車"));
        industries.add(new Industry(2, "化学"));
        industries.add(new Industry(3, "建築"));
        industries.add(new Industry(4, "広告"));
        Industry.addAll(industries, dbHelper.getWritableDatabase());
        assertTrue(!Industry.isEmpty(dbHelper.getReadableDatabase()));
    }

    public void testList() throws Exception {
        assertTrue(Industry.isEmpty(dbHelper.getReadableDatabase()));
        List<Industry> industries = new ArrayList<>();
        industries.add(new Industry(1, "自動車"));
        industries.add(new Industry(2, "化学"));
        industries.add(new Industry(3, "建築"));
        industries.add(new Industry(4, "広告"));
        Industry.addAll(industries, dbHelper.getWritableDatabase());
        List<Industry> industriesResult =
                Industry.list(dbHelper.getReadableDatabase());
        assertEquals(industriesResult.size(), 4);
    }

    public void testReadingList() throws Exception {
        Industry industry1 = new Industry(1, "自動車");
        Industry industry2 = new Industry(2, "広告");
        industry1.setReading(true);
        industry2.setReading(false);
        List<Industry> industries = new ArrayList<>();
        industries.add(industry1);
        industries.add(industry2);
        Industry.addAll(industries, dbHelper.getWritableDatabase());
        List<Industry> industriesResult =
                Industry.readingList(dbHelper.getReadableDatabase());
        assertEquals(industriesResult.size(), 1);
        Industry industryResult = industriesResult.get(0);
        assertEquals(industryResult.getName(), "自動車");
        assertEquals(industryResult.getId(), (Integer)1);
        assertTrue(industryResult.getReading());
    }
}