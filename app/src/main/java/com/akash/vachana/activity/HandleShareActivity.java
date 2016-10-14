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
