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
     * TODO: ListViewにAdViewを差し込む方法を調べて実装する
     *   => 難しいので、複数のListView, Adapterを用意して、その間にAdViewを挟む
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.news_list_view);
        mNewsListAdapter = new NewsListAdapter(getContext());
        mListView.setAdapter(mNewsListAdapter);
        mListView.setOnItemClickListener(onListViewItemClickListener);
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        getLoaderManager().initLoader(sectionNumber, getArguments(), this);
        return rootView;
    }

    @Override
    public void onResume() {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(getClass().getSimpleName(), "onResume is called: " + sectionNumber);
        getLoaderManager().restartLoader(sectionNumber, getArguments(), this);
        super.onResume();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(this.getClass().getSimpleName(), "onCreateLoader.sectionNumber: " + sectionNumber);
        Loader loader = new NewsApiLoader(getContext(), sectionNumber);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(this.getClass().getSimpleName(), "onLoadFinished.sectionNumber: " + sectionNumber);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DbHelper(getContext());
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}