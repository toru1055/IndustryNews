package jp.thotta.android.industrynews;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by thotta on 2016/05/07.
 */
public class NewsTest extends AndroidTestCase {
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

    public void testFind() throws Exception {
        News news1 = new News(1L, "http://x.y.z/1", "title1");
        news1.setPubDate(new Date());
        news1.incrementClick();
        assertTrue(news1.insertDatabase(dbHelper.getWritableDatabase()));
        News foundNews = News.find(1L, dbHelper.getReadableDatabase());
        Log.d(getClass().getSimpleName(), "news1: " + news1.pubDate.getTime());
        Log.d(getClass().getSimpleName(), "foundNews: " + foundNews.pubDate.getTime());
        assertEquals(news1, foundNews);
    }

    public void testStockList() throws Exception {
        News news1 = new News(1L, "http://x.y.z/1", "title1");
        news1.setPubDate(new Date());
        news1.incrementClick();
        news1.insertDatabase(dbHelper.getWritableDatabase());
        News news2 = new News(2L, "http://x.y.z/2", "title2");
        news2.setPubDate(new Date());
        news2.incrementClick();
        news2.setIsStocked(true);
        news2.insertDatabase(dbHelper.getWritableDatabase());
        List<News> newsList = News.stockList(dbHelper.getReadableDatabase());
        assertEquals(newsList.size(), 1);
        assertEquals(newsList.get(0), news2);
    }

    public void testInsertDatabase() throws Exception {
        News news1 = new News(1L, "http://x.y.z/1", "title1");
        news1.setPubDate(new Date());
        news1.incrementClick();
        assertTrue(news1.insertDatabase(dbHelper.getWritableDatabase()));
        assertFalse(news1.insertDatabase(dbHelper.getWritableDatabase()));
    }

    public void testUpdateDatabase() throws Exception {
        News news1 = new News(1L, "http://x.y.z/1", "title1");
        news1.setPubDate(new Date());
        news1.incrementClick();
        assertTrue(news1.insertDatabase(dbHelper.getWritableDatabase()));
        News foundNews = News.find(1L, dbHelper.getReadableDatabase());
        foundNews.incrementClick();
        foundNews.setIsStocked(true);
        foundNews.updateDatabase(dbHelper.getWritableDatabase());
        News foundNews2 = News.find(1L, dbHelper.getReadableDatabase());
        assertEquals(foundNews, foundNews2);
        assertNotSame(news1, foundNews2);
    }
}