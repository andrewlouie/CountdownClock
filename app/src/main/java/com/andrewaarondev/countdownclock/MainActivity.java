package com.andrewaarondev.countdownclock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

public class MainActivity extends AppCompatActivity implements
        MyListFragment.Contract {

    static private final String STATE_CHECKED = "com.andrewaarondev.android.countdownclock.STATE_CHECKED";
    private DetailsFragment details = null;
    public static int selectedItem = -1;
    public static final String SHARE_FILE_NAME = "countdown_shared";
    private Intent shareIntent = new Intent(Intent.ACTION_SEND);
    private boolean resuming;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        JodaTimeAndroid.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shareIntent.setType("image/png");
        Uri uri = Uri.fromFile(new File(getExternalCacheDir(), SHARE_FILE_NAME + ".png"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);

        if (details == null && findViewById(R.id.details) != null) {
            details = new DetailsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.details, details).commit();
        }
        MyListFragment mylist = (MyListFragment) getFragmentManager().findFragmentById(R.id.mylist);

        if (mylist == null) {
            mylist = new MyListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.mylist, mylist)
                    .commit();
        }
        if (savedInstanceState == null) {
            if (getResources().getBoolean(R.bool.widescreen)) selectedItem = 0;
        } else {
            selectedItem = savedInstanceState.getInt(STATE_CHECKED);
        }
    }

    @Override
    public void onItemSelected(final Countdown c, int position, boolean fromListLoaded) {
        selectedItem = position;
        if (details != null && getResources().getBoolean(R.bool.widescreen)) {
            if (!resuming || fromListLoaded) details.loadCountdown(c);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                    i.putExtra(DetailsActivity.EXTRA_COUNTDOWN, c);
                    startActivity(i);
                }
            }, 500);
        }
        resuming = false;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(STATE_CHECKED, selectedItem);
    }

    @Override
    public void onResume() {
        resuming = true;
        if (!getResources().getBoolean(R.bool.widescreen)) {
            DatabaseHelper db = DatabaseHelper.getInstance(getBaseContext());
            db.getCountdowns();
        }
        super.onResume();
    }

    @Override
    public boolean isPersistentSelection() {
        return (details != null && getResources().getBoolean(R.bool.widescreen));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        MenuItem item = menu.findItem(R.id.share);

        ShareActionProvider share = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (share != null) {
            share.setShareIntent(shareIntent);
            share.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                    if (details != null) details.saveToShare();
                    return true;
                }
            });
        }
        return (super.onCreateOptionsMenu(menu));
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newitem:
                selectedItem = 0;
                DatabaseHelper db = DatabaseHelper.getInstance(getBaseContext());
                db.newItem();
                return true;
            case R.id.about:
                Intent i = new Intent(this, SimpleContentActivity.class)
                        .putExtra(SimpleContentActivity.EXTRA_FILE,
                                "file:///android_asset/misc/about.html");
                startActivity(i);
                return true;
            case R.id.export:
                details.export();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }
}
