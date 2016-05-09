package jp.thotta.android.industrynews;

/**
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thotta on 2016/05/06.
 */
public class PlaceholderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<News>> {
    /**
     * TODO: WebViewにStock機能を作る。StockListActivityを作る
     * TODO: ListViewにAdViewを差し込む方法を調べて実装する
     * TODO: 本当はスワイプだけでリロードしたくない。うまく制御したい
     * TODO: test書く
     * TODO: API作る. Android StudioかIntelliJで作る. Spring 使ってみる
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    AdapterView.OnItemClickListener onListViewItemClickListener =
            new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(), DetailNewsActivity.class);
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
    NewsListAdapter mNewsListAdapter;
    ListView mListView;
    DbHelper dbHelper;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DbHelper(getContext());
//        getLoaderManager().initLoader(0, getArguments(), this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.news_list_view);
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(getClass().getSimpleName(), "onCreateView.sectionNumber: " + sectionNumber);
        mNewsListAdapter = new NewsListAdapter(getContext());
        mListView.setAdapter(mNewsListAdapter);
        mListView.setOnItemClickListener(onListViewItemClickListener);
        getLoaderManager().initLoader(0, getArguments(), this).forceLoad();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(this.getClass().getSimpleName(), "onResume.sectionNumber: " + sectionNumber);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(this.getClass().getSimpleName(), "onCreateLoader.id: " + id);
        Log.d(this.getClass().getSimpleName(), "onCreateLoader.sectionNumber: " + sectionNumber);
        Loader loader = new NewsApiLoader(getContext(), sectionNumber);
        //loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        if (data != null) {
            mNewsListAdapter.clear();
            for (News news : data) {
                mNewsListAdapter.add(news);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    public static class NewsApiLoader extends AsyncTaskLoader<List<News>> {
        int sectionNumber;

        public NewsApiLoader(Context context, int sectionNumber) {
            super(context);
            this.sectionNumber = sectionNumber;
        }

        @Override
        public List<News> loadInBackground() {

            try {
                Thread.sleep(1000);
                List<News> newsList = new ArrayList<>();
                newsList.add(new News(1, "http://m.yahoo.co.jp/#1", "ヤフー1"));
                News news2 = new News(2, "http://m.yahoo.co.jp/#2", "ヤフー2");
                news2.setDescription("あああああああああああああああああああああああああああ" +
                        "あああああああああああああああああああああああああああああああああああ" +
                        "ああああああああ");
                News news3 = new News(3, "http://www.yahoo.co.jp/#3", "ヤフー3");
                news3.setDescription(MainActivity.gPagerItemList.get(sectionNumber).getQuery());
                newsList.add(news2);
                newsList.add(news3);
                return newsList;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class NewsListAdapter extends ArrayAdapter<News> {

        public NewsListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getContext().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.news_row, null);
            }
            News news = getItem(position);
            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.textViewTitle);
            TextView descriptionTextView =
                    (TextView) convertView.findViewById(R.id.textViewDescription);
            titleTextView.setText(news.getTitle());
            descriptionTextView.setText(news.getDescription());
            convertView.setTag(news);
            return convertView;
        }
    }
}