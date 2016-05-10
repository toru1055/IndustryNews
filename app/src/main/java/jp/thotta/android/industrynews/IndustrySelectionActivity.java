package jp.thotta.android.industrynews;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class IndustrySelectionActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Industry>> {
    DbHelper mDbHelper;

    private static void setMyLayoutParams(View view) {
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    View.OnClickListener checkBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            Industry industry = (Industry) checkBox.getTag();
            industry.setReading(checkBox.isChecked());
            industry.updateDatabase(mDbHelper.getWritableDatabase());
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });
        mDbHelper = new DbHelper(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    void generateCheckBoxes() {
        LinearLayout layout =
                (LinearLayout) findViewById(R.id.industry_setting_layout);
        List<Industry> industries =
                Industry.list(mDbHelper.getReadableDatabase());
        for (Industry industry : industries) {
            CheckBox checkBox = new CheckBox(IndustrySelectionActivity.this);
            checkBox.setText(industry.getName());
            checkBox.setChecked(industry.getReading());
            checkBox.setTag(industry);
            checkBox.setOnClickListener(checkBoxClickListener);
            setMyLayoutParams(checkBox);
            layout.addView(checkBox);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            goHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void goHome() {
        Intent intent = new Intent(IndustrySelectionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public Loader<List<Industry>> onCreateLoader(int id, Bundle args) {
        IndustryApiLoader loader =
                new IndustryApiLoader(IndustrySelectionActivity.this);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Industry>> loader, List<Industry> data) {
        Industry.addAll(data, mDbHelper.getWritableDatabase());
        generateCheckBoxes();
    }

    @Override
    public void onLoaderReset(Loader<List<Industry>> loader) {

    }

    public static class IndustryApiLoader extends AsyncTaskLoader<List<Industry>> {
        List<Industry> mIndustries;

        public IndustryApiLoader(Context context) {
            super(context);
        }

        @Override
        public List<Industry> loadInBackground() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            mIndustries = new ArrayList<>();
            mIndustries.add(new Industry(1, "自動車"));
            mIndustries.add(new Industry(2, "広告"));
            mIndustries.add(new Industry(3, "住宅"));
            return mIndustries;
        }
    }

}
