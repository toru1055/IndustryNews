package jp.thotta.android.industrynews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thotta on 2016/05/04.
 */
public class News implements Serializable {
    private static final long serialVersionUID = 0L;
    public static final String TABLE_NAME = "news";
    public static final String COL_ID = "id";
    public static final String COL_URL = "url";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_SUBSCRIPTION_NAME = "subscription_name";
    public static final String COL_INDUSTRY_NAME = "industry_name";
    public static final String COL_PUB_DATE = "pub_date";
    public static final String COL_CLICKS = "clicks";
    public static final String COL_IS_STOCKED = "is_stocked";
    Integer id; //primary key
    String url; //not null
    String title; //not null
    String description;
    String subscriptionName;
    String industryName;
    Date pubDate;
    Integer clicks = 0;
    Boolean isStocked = false;

    public News() {}

    public News(Integer id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    public static News find(Integer id, SQLiteDatabase db) {
        String where = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor =
                db.query(TABLE_NAME, null, where, whereArgs, null, null, null);
        if(cursor.getCount() == 1 && cursor.moveToFirst()) {
            News news = new News();
            news.readCursor(cursor);
            return news;
        }
        return null;
    }

    public static List<News> stockList(SQLiteDatabase db) {
        List<News> newsList = new ArrayList<>();
        String where = COL_IS_STOCKED + " = ?";
        String[] whereArgs = {"1"};
        String orderBy = COL_PUB_DATE;
        Cursor cursor =
                db.query(TABLE_NAME, null, where, whereArgs, null, null, orderBy);
        while(cursor.moveToNext()) {
            News news = new News();
            news.readCursor(cursor);
            newsList.add(news);
        }
        return newsList;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public boolean isClicked() {
        return clicks > 0;
    }

    public void incrementClick() {
        clicks += 1;
    }

    public boolean isStocked() {
        return isStocked;
    }

    public void setIsStocked(Boolean isStocked) {
        this.isStocked = isStocked;
    }

    public boolean insertDatabase(SQLiteDatabase db) {
        long t_id = db.insert(TABLE_NAME, null, getContentValues());
        return t_id > 0;
    }

    public boolean updateDatabase(SQLiteDatabase db) {
        String where = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        int rows = db.update(TABLE_NAME, getContentValues(), where, whereArgs);
        return rows == 1;
    }

    void readCursor(Cursor cursor) {
        Log.d(getClass().getSimpleName(), "getCount: " + cursor.getCount());
        Log.d(getClass().getSimpleName(), "getColumnIndex(id): " + cursor.getColumnIndex("id"));
        Log.d(getClass().getSimpleName(), "getInt(1): " + cursor.getInt(1));
        this.id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        this.url = cursor.getString(cursor.getColumnIndex(COL_URL));
        this.title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
        this.description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION));
        this.subscriptionName = cursor.getString(cursor.getColumnIndex(COL_SUBSCRIPTION_NAME));
        this.industryName = cursor.getString(cursor.getColumnIndex(COL_INDUSTRY_NAME));
        this.pubDate = new Date(cursor.getLong(cursor.getColumnIndex(COL_PUB_DATE)));
        this.clicks = cursor.getInt(cursor.getColumnIndex(COL_CLICKS));
        this.isStocked = cursor.getInt(cursor.getColumnIndex(COL_IS_STOCKED)) > 0;
    }

    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_URL, url);
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_SUBSCRIPTION_NAME, subscriptionName);
        values.put(COL_INDUSTRY_NAME, industryName);
        values.put(COL_PUB_DATE,
                pubDate == null ? null : pubDate.getTime());
        values.put(COL_CLICKS, clicks);
        values.put(COL_IS_STOCKED, isStocked ? 1 : 0);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        if (!id.equals(news.id)) return false;
        if (!url.equals(news.url)) return false;
        if (!title.equals(news.title)) return false;
        if (description != null ? !description.equals(news.description) : news.description != null)
            return false;
        if (subscriptionName != null ? !subscriptionName.equals(news.subscriptionName) : news.subscriptionName != null)
            return false;
        if (industryName != null ? !industryName.equals(news.industryName) : news.industryName != null)
            return false;
        if (pubDate != null ? !pubDate.equals(news.pubDate) : news.pubDate != null)
            return false;
        if (clicks != null ? !clicks.equals(news.clicks) : news.clicks != null)
            return false;
        return isStocked != null ? isStocked.equals(news.isStocked) : news.isStocked == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (subscriptionName != null ? subscriptionName.hashCode() : 0);
        result = 31 * result + (industryName != null ? industryName.hashCode() : 0);
        result = 31 * result + (pubDate != null ? pubDate.hashCode() : 0);
        result = 31 * result + (clicks != null ? clicks.hashCode() : 0);
        result = 31 * result + (isStocked != null ? isStocked.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", subscriptionName='" + subscriptionName + '\'' +
                ", industryName='" + industryName + '\'' +
                ", pubDate=" + pubDate +
                ", clicks=" + clicks +
                ", isStocked=" + isStocked +
                '}';
    }

}
