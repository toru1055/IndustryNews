package jp.thotta.android.industrynews;

/**
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

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
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    AdapterView.OnItemClickListener onListViewItemClickListener =
            new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(), DetailNewsActivity.class);
            News news = (News) view.getTag();
            intent.putExtra("news", news);
            startActivity(intent);
        }
    };
    NewsListAdapter mNewsListAdapter;
    ListView mListView;
    DbHelper dbHelper;
    AdView mAdView;
    SwipeRefreshLayout mSwipeRefreshLayout;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device_id))
                .build();
        mAdView.loadAd(adRequest);

//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            private int mLastFirstVisibleItem;
//            private int mScrollState;
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                // http://stackoverflow.com/questions/16791100/detect-scroll-up-scroll-down-in-listview
//                // TODO: 真似する, toolbarを取得して、hide/showする
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if(view.getId() == mListView.getId()) {
//                    final int currentFirstVisibleItem = mListView.getFirstVisiblePosition();
//                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
//                        Log.d(getClass().getSimpleName(), "Scrolling down");
//                        actionBar.hide();
//                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
//                        Log.d(getClass().getSimpleName(), "Scrolling up");
//                        actionBar.show();
//                    }
//                    mLastFirstVisibleItem = currentFirstVisibleItem;
//                }
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mAdView = new AdView(getActivity());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));


        mListView = (ListView) rootView.findViewById(R.id.news_list_view);
        mListView.addHeaderView(mAdView);
        mNewsListAdapter = new NewsListAdapter(getContext());
        mListView.setAdapter(mNewsListAdapter);
        mListView.setOnItemClickListener(onListViewItemClickListener);
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        getLoaderManager().initLoader(sectionNumber, getArguments(), this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
                getLoaderManager().restartLoader(sectionNumber, getArguments(), PlaceholderFragment.this);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.d(getClass().getSimpleName(), "onResume is called: " + sectionNumber);
        getLoaderManager().restartLoader(sectionNumber, getArguments(), this);
        super.onResume();
        mAdView.resume();
    }

    @Override
    public void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        mSwipeRefreshLayout.setRefreshing(false);
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