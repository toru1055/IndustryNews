package jp.thotta.android.industrynews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import jp.thotta.android.industrynews.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    /*
     */

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
    public static List<PagerItem> gPagerItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonIndustrySelection = (Button) findViewById(R.id.button_industry_selection);
        buttonIndustrySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                Intent intent = new Intent(MainActivity.this, IndustrySelectionActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new DbHelper(this);
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
        gPagerItemList = new ArrayList<>();
        gPagerItemList.add(new PagerItem("新着", "recent", industries,
                Color.BLUE, Color.GRAY));
        gPagerItemList.add(new PagerItem("人気", "click", industries,
                Color.GREEN, Color.GRAY));
        for (Industry industry : industries) {
            List<Industry> l = new ArrayList<>();
            l.add(industry);
            gPagerItemList.add(new PagerItem(industry.getName(),
                    industry.getSortMode(), l, Color.YELLOW, Color.GRAY));
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.d(getClass().getSimpleName(), "onResume.pagerAdapeterCount: " + mSectionsPagerAdapter.getCount());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        recreate();
        super.onRestart();
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
        mDbHelper.close();
        super.onDestroy();
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
            String apiQuery = gPagerItemList.get(position).getQuery();
            Log.d(this.getClass().getSimpleName(), "getItem.position: " + position);
            Log.d(this.getClass().getSimpleName(), "getItem.getQuery: " + apiQuery);
            return PlaceholderFragment.newInstance(position, apiQuery);
        }

        @Override
        public int getCount() {
            return gPagerItemList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(this.getClass().getSimpleName(), "getPageTitle.position: " + position);
            return gPagerItemList.get(position).getPageTitle();
        }
    }
}
