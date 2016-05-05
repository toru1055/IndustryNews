package jp.thotta.android.industrynews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.thotta.android.industrynews.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    // 1. AsyncTaskLoaderをMainActivityに付けることを検討する
    // 1-1. その前にgitに登録する
    // 2. SlidingTabLayout.onPageChangeListener的なので、loaderを動かす
    // 2-1. 現在のPagePositionを記憶しておく
    // 2-2. AsyncTaskLoaderのforceLoadをキック（initLoader）
    // 3. onLoadFinishedでNewsListをListViewに反映
    // 3-1. もしくはmSlidingTabLayout.getRootView()でFragmentのrootViewにアクセスできるので、
    // adapterをMainActivityに持ってきてMainActivityでListViewと紐付ける

    public static class PagerItem {
        String pageTitle;
        String sortMode;
        List<Industry> industries;
        int indicatorColor;
        int dividerColor;

        public PagerItem(String pageTitle, String sortMode,
                         List<Industry> industries,
                         int indicatorColor, int dividerColor) {
            this.pageTitle = pageTitle;
            this.sortMode = sortMode;
            this.industries = industries;
            this.indicatorColor = indicatorColor;
            this.dividerColor = dividerColor;
        }

        public String getPageTitle() {
            return pageTitle;
        }

        public String getSortMode() {
            return sortMode;
        }

        public List<Industry> getIndustries() {
            return industries;
        }

        public Industry getIndustry() {
            return industries.get(0);
        }

        public int getIndicatorColor() {
            return indicatorColor;
        }

        public int getDividerColor() {
            return dividerColor;
        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DbHelper mDbHelper;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private List<PagerItem> mPagerItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonIndustrySelection = (Button) findViewById(R.id.button_industry_selection);
        buttonIndustrySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, IndustrySelectionActivity.class));
            }
        });
        mDbHelper = new DbHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Industry.isEmpty(mDbHelper.getReadableDatabase())) {
            startActivity(new Intent(this, IndustrySelectionActivity.class));
            return;
        }
        List<Industry> industries =
                Industry.readingList(mDbHelper.getReadableDatabase());
        if (industries.size() == 0) {
            startActivity(new Intent(this, IndustrySelectionActivity.class));
            return;
        }
        mPagerItemList = new ArrayList<>();
        mPagerItemList.add(new PagerItem("新着", "recent", industries,
                Color.BLUE, Color.GRAY));
        mPagerItemList.add(new PagerItem("人気", "click", industries,
                Color.GREEN, Color.GRAY));
        for (Industry industry : industries) {
            List<Industry> l = new ArrayList<>();
            l.add(industry);
            mPagerItemList.add(new PagerItem(industry.getName(),
                    industry.getSortMode(), l, Color.YELLOW, Color.GRAY));
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, IndustrySelectionActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        NewsListAdapter mNewsListAdapter;

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
            ListView listView =
                    (ListView) rootView.findViewById(R.id.news_list_view);
            mNewsListAdapter = new NewsListAdapter(getContext());
            listView.setAdapter(mNewsListAdapter);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNewsListAdapter.add(new News(1, "http://www.yahoo.co.jp/1", "ヤフー1"));
            mNewsListAdapter.add(new News(2, "http://www.yahoo.co.jp/2", "ヤフー2"));
            mNewsListAdapter.add(new News(3, "http://www.yahoo.co.jp/3", "ヤフー3"));
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mPagerItemList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerItemList.get(position).getPageTitle();
        }
    }
}
