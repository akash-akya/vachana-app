/*
 * vachana. An application for Android users, it contains kannada vachanas
 * Copyright (c) 2016. akash
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.akash.vachana.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HandleShareActivity extends AppCompatActivity {
    private static final String TAG = "HandleShareActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String contentType = intent.getType();

        String ACTION = "com.akash.vachana.WHATSAPP_SHARE_HANDLE";
        if (action.equals(ACTION) && contentType.equals("text/plain")) {
            Log.d(TAG, "onCreate: Got the share!");
            try{
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                sharedText += "\n\n-"+intent.getStringExtra(Intent.EXTRA_SUBJECT);
                Uri uriUrl = Uri.parse("whatsapp://send?text="+sharedText+"");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
            catch(ActivityNotFoundException e){
                Toast.makeText(this, "Whatsapp Not Installed.", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
