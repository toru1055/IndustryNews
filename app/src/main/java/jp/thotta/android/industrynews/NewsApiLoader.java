package jp.thotta.android.industrynews;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thotta on 2016/05/13.
 */
public class NewsApiLoader extends AsyncTaskLoader<List<News>> {
    private static final String API_URL_BASE
            = "http://www7419up.sakura.ne.jp:8080/industry_news/news_list";
    int sectionNumber;

    public NewsApiLoader(Context context, int sectionNumber) {
        super(context);
        this.sectionNumber = sectionNumber;
    }

    @Override
    public List<News> loadInBackground() {
        List<News> newsList = new ArrayList<>();
        HttpURLConnection urlConnection = null;
        String query = MainActivity.gPagerItemList.get(sectionNumber).getQuery();
        try {
            URL api_url = new URL(API_URL_BASE + query);
            Log.d(this.getClass().getSimpleName(), "loadInBackground.api_url: " + api_url.toString());

            urlConnection = (HttpURLConnection) api_url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            String jsonLine = "";
            while((line = bufferedReader.readLine()) != null) {
                jsonLine += line;
            }
            in.close();
            JSONArray jsonArray = new JSONArray(jsonLine);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Long id = json.getLong("id");
                String url = json.getString("url");
                String title = json.getString("title");
                News news = new News(id, url, title);
                news.setDescription(json.getString("description"));
                news.setSubscriptionName(json.getString("subscriptionName"));
                news.setIndustryName(json.getString("industryName"));
                news.setPubDate(new Date(json.getLong("pubDate")));
                newsList.add(news);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }
}
