package jp.thotta.android.industrynews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    /*
    1. SettingActivityを起動する場合は、MainActivityをfinishするか、フラグを立てる
        http://stackoverflow.com/questions/11347161/oncreate-always-called-if-navigating-back-with-intent
    2. Fragmentに業種情報を持たせる時は、メンバ変数ではなくBundleに持たせる
    2-1. PagerItemにgetQueryメソッドを作成し、APに必要のクエリな情報をStringに変換する
    2-2. もしくはPagerItemをSerializable実装にして、Bundleに持たせる
    3. コードを綺麗にする。static classとかの意味をしっかり理解して使う.
    3-1. FragmentとPagerItemは別ファイルにする
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
    private List<PagerItem> mPagerItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonIndustrySelection = (Button) findViewById(R.id.button_industry_selection);
        buttonIndustrySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        Log.d(getClass().getSimpleName(), "onResume.pagerAdapeterCount: " + mSectionsPagerAdapter.getCount());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    protected void onPause() {
        getSupportFragmentManager().popBackStackImmediate();
        super.onPause();
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
            String apiQuery = mPagerItemList.get(position).getQuery();
            Log.d(this.getClass().getSimpleName(), "getItem.position: " + position);
            Log.d(this.getClass().getSimpleName(), "getItem.getQuery: " + apiQuery);
            return PlaceholderFragment.newInstance(position, apiQuery);
        }

        @Override
        public int getCount() {
            return mPagerItemList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(this.getClass().getSimpleName(), "getPageTitle.position: " + position);
            return mPagerItemList.get(position).getPageTitle();
        }
    }
}
