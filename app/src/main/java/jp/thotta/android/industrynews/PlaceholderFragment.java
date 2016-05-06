package jp.thotta.android.industrynews;

/**
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thotta on 2016/05/06.
 */
public class PlaceholderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<News>> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_API_QUERY = "api_query";
    NewsListAdapter mNewsListAdapter;
    ListView mListView;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, String apiQuery) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_API_QUERY, apiQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.news_list_view);
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        String apiQuery = MainActivity.gPagerItemList.get(sectionNumber).getQuery();
        Log.d(getClass().getSimpleName(), "onCreateView.sectionNumber: " + sectionNumber);
        Log.d(getClass().getSimpleName(), "onCreateView.apiQuery: " + apiQuery);
        mNewsListAdapter = new NewsListAdapter(getContext());
        mListView.setAdapter(mNewsListAdapter);
        getLoaderManager().initLoader(sectionNumber, getArguments(), this);
        getLoaderManager().restartLoader(sectionNumber, getArguments(), this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        String apiQuery = MainActivity.gPagerItemList.get(sectionNumber).getQuery();
        Log.d(this.getClass().getSimpleName(), "onResume.sectionNumber: " + sectionNumber);
        Log.d(this.getClass().getSimpleName(), "onResume.apiQuery: " + apiQuery);
    }


    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(this.getClass().getSimpleName(), "onCreateLoader.id: " + id);
        Log.d(this.getClass().getSimpleName(), "onCreateLoader.sectionNumber: " + sectionNumber);
        Loader loader = new NewsApiLoader(getContext(), sectionNumber);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        mNewsListAdapter.clear();
        for (News news : data) {
            mNewsListAdapter.add(news);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<News> newsList = new ArrayList<>();
            newsList.add(new News(1, "http://www.yahoo.co.jp/1", "ヤフー1"));
            newsList.add(new News(2, "http://www.yahoo.co.jp/2", "ヤフー2"));
            News news3 = new News(3, "http://www.yahoo.co.jp/3", "ヤフー3");
            news3.setDescription(MainActivity.gPagerItemList.get(sectionNumber).getQuery());
            newsList.add(news3);
            return newsList;
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
            return convertView;
        }
    }
}