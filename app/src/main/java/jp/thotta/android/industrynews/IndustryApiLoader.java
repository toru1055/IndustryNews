package jp.thotta.android.industrynews;

import android.content.AsyncTaskLoader;
import android.content.Context;
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
import java.util.List;

/**
 * Created by thotta on 2016/05/13.
 */
public class IndustryApiLoader extends AsyncTaskLoader<List<Industry>> {
    private static final String API_URL
            = "http://www7419up.sakura.ne.jp:8080/industry_news/industry_list";

    public IndustryApiLoader(Context context) {
        super(context);
    }

    @Override
    public List<Industry> loadInBackground() {
        List<Industry> industries = new ArrayList<>();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            String jsonLine = "";
            while((line = bufferedReader.readLine()) != null) {
                jsonLine += line;
            }
            in.close();
            JSONArray jsonArray = new JSONArray(jsonLine);
            Log.d(getClass().getSimpleName(), jsonArray.toString());
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                Integer id = json.getInt("id");
                String name = json.getString("name");
                industries.add(new Industry(id, name));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return industries;
    }
}