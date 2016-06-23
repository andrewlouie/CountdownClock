/***
 * Copyright (c) 2008-2012 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain	a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p/>
 * From _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package com.andrewaarondev.countdownclock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_COUNTDOWN =
            "com.andrewaarondev.android.countdownclock.EXTRA_COUNTDOWN";
    private DetailsFragment details = null;
    private static Countdown cd = null;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND);


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        MenuItem item = menu.findItem(R.id.share);

        ShareActionProvider share = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Uri uri = Uri.fromFile(new File(getExternalCacheDir(), MainActivity.SHARE_FILE_NAME + ".png"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        share.setShareIntent(shareIntent);
        share.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                if (details != null) details.saveToShare();
                return true;
            }
        });
        return (super.onCreateOptionsMenu(menu));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                details.export();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareIntent.setType("image/png");
        if (getResources().getBoolean(R.bool.widescreen)) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        } else {
            details = (DetailsFragment) getFragmentManager()
                    .findFragmentById(android.R.id.content);
            cd = getIntent().getParcelableExtra(EXTRA_COUNTDOWN);

            if (details == null) {
                details = new DetailsFragment();
                getFragmentManager().beginTransaction()
                        .add(android.R.id.content, details)
                        .commit();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (details != null && cd != null) details.loadCountdown(cd);
    }

    @Override
    public void onBackPressed() {
        MainActivity.selectedItem = -1;
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        //super.onBackPressed();
    }
}
