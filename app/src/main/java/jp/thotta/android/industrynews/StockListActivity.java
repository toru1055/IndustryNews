package jp.thotta.android.industrynews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class StockListActivity extends AppCompatActivity {
    ListView mListView;
    NewsListAdapter mNewsListAdapter;
    DbHelper dbHelper;

    AdapterView.OnItemClickListener onListViewItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(StockListActivity.this, DetailNewsActivity.class);
                    News news = (News) view.getTag();
                    News foundNews = News.find(news.id, dbHelper.getReadableDatabase());
                    if(foundNews != null) {
                        foundNews.incrementClick();
                        foundNews.updateDatabase(dbHelper.getWritableDatabase());
                        intent.putExtra("news", foundNews);
                    } else {
                        news.incrementClick();
                        news.insertDatabase(dbHelper.getWritableDatabase());
                        intent.putExtra("news", news);
                    }
                    startActivity(intent);
                }
            };
    AdapterView.OnItemLongClickListener onListViewItemLongClickListener =
            new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    News news = (News) view.getTag();
                    news.setIsStocked(false);
                    news.updateDatabase(dbHelper.getWritableDatabase());
                    refreshAdapter();
                    Snackbar.make(view, "Remove: " + news.getTitle(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dbHelper = new DbHelper(this);
        mListView = (ListView) findViewById(R.id.news_list_view);
        mNewsListAdapter = new NewsListAdapter(this);
        mListView.setAdapter(mNewsListAdapter);
        mListView.setOnItemClickListener(onListViewItemClickListener);
        mListView.setOnItemLongClickListener(onListViewItemLongClickListener);
        refreshAdapter();
    }

    void refreshAdapter() {
        mNewsListAdapter.clear();
        List<News> newsList = News.stockList(dbHelper.getReadableDatabase());
        mNewsListAdapter.addAll(newsList);
    }

}
