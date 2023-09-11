package com.akash.vachanas2.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.widget.Toolbar;
import android.webkit.WebView;

import com.akash.vachanas2.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        addPreferencesFromResource(R.xml.preferences);
        setPreferencesFromResource(R.xml.preferences, rootKey);

//        Toolbar toolbar = getActivity().findViewById(R.id.search_bar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar( toolbar);

        Preference license = getPreferenceManager().findPreference("license");
        license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                WebView webView = new WebView(getContext());
                webView.loadUrl("file:///android_res/raw/copyrights.html");

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setView(webView);
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public void setDivider(Drawable divider) {
        super.setDivider(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void setDividerHeight(int height) {
        super.setDividerHeight(0);
    }
}
