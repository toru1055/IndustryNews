package jp.thotta.android.industrynews;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Industry.addAll(data, db);
        Industry.updateNames(data, db);
        generateCheckBoxes();
    }

    @Override
    public void onLoaderReset(Loader<List<Industry>> loader) {

    }

}
