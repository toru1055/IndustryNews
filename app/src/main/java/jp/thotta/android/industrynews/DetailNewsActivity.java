package jp.thotta.android.industrynews;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailNewsActivity extends AppCompatActivity {
    WebView mWebView;
    News mNews;
    DbHelper dbHelper;
    FloatingActionButton mFabStock;
    FloatingActionButton mFabUnStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        dbHelper = new DbHelper(this);
        Intent intent = getIntent();
        mNews = (News) intent.getSerializableExtra("news");

        News foundNews = News.find(mNews.id, dbHelper.getReadableDatabase());
        if(foundNews == null) {
            mNews.incrementClick();
            mNews.insertDatabase(dbHelper.getWritableDatabase());
            new ClickAsyncTask().execute(mNews);
        } else {
            mNews.setClicks(foundNews.getClicks());
            mNews.setIsStocked(foundNews.isStocked());
            mNews.incrementClick();
            mNews.updateDatabase(dbHelper.getWritableDatabase());
        }

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mNews.getUrl());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mNews.getTitle());
        toolbar.setLogoDescription(mNews.getDescription());
        setSupportActionBar(toolbar);
        mFabStock = (FloatingActionButton) findViewById(R.id.fabStock);
        mFabStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNews.setIsStocked(true);
                mNews.updateDatabase(dbHelper.getWritableDatabase());
                Snackbar.make(view, "Stocked: " + mNews.getTitle(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mFabStock.hide();
                mFabUnStock.show();
            }
        });
        mFabUnStock = (FloatingActionButton) findViewById(R.id.fabUnStock);
        mFabUnStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNews.setIsStocked(false);
                mNews.updateDatabase(dbHelper.getWritableDatabase());
                Snackbar.make(view, "UnStocked: " + mNews.getTitle(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mFabUnStock.hide();
                mFabStock.show();
            }
        });
        if(mNews.isStocked()) {
            mFabStock.hide();
            mFabUnStock.show();
        } else {
            mFabUnStock.hide();
            mFabStock.show();
        }

        FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailNewsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        FloatingActionButton fabBrowser = (FloatingActionButton) findViewById(R.id.fabBrowser);
        fabBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mNews.getUrl());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class ClickAsyncTask extends AsyncTask<News, Void, Void> {
        private static final String BASE_API_URL =
                "http://www7419up.sakura.ne.jp:8080/industry_news/click_news?id=";

        @Override
        protected Void doInBackground(News... params) {
            try {
                URL api_url = new URL(BASE_API_URL + params[0].getId());
                HttpURLConnection urlConnection = (HttpURLConnection) api_url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
