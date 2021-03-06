package jp.thotta.android.industrynews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.thotta.android.industrynews.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
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
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("意識高い系ニュース");
//        setSupportActionBar(toolbar);
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
                    industry.getSortMode(), l,
                    industry.getColor(), Color.GRAY));
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.d(getClass().getSimpleName(), "onResume.pagerAdapeterCount: " + mSectionsPagerAdapter.getCount());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return gPagerItemList.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return gPagerItemList.get(position).getDividerColor();
            }
        });
        FloatingActionButton fabSetting = (FloatingActionButton) findViewById(R.id.fabSetting);
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IndustrySelectionActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton fabStock = (FloatingActionButton) findViewById(R.id.fabStock);
        fabStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StockListActivity.class);
                startActivity(intent);
            }
        });
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
            Log.d(this.getClass().getSimpleName(), "getItem.gPagerItemList.size(): " + gPagerItemList.size());
            Log.d(this.getClass().getSimpleName(), "getItem.position: " + position);
            return PlaceholderFragment.newInstance(position);
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
