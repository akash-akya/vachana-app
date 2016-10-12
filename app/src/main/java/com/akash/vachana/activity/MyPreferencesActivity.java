package com.akash.vachana.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.akash.vachana.R;
import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;

/**
 * Created by akash on 8/10/16.
 */

public class MyPreferencesActivity extends FragmentActivity {

    private static boolean needAppRestart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if (needAppRestart){
            assert getCallingActivity() != null;
            startActivity(IntentCompat.makeRestartActivityTask(getCallingActivity()));
        }
        super.onBackPressed();
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        public static final String TAG = "MyPreferenceFragment";

        private SwitchPreferenceCompat darkThemeSwitch;
        private ColorPreferenceCompat themeChooser;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
            darkThemeSwitch = (SwitchPreferenceCompat) getPreferenceManager().findPreference("theme");
            darkThemeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((boolean) newValue)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    needAppRestart = true;
                    return true;
                }
            });

            themeChooser = (ColorPreferenceCompat) getPreferenceManager().findPreference("themeColor");

            themeChooser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (darkThemeSwitch.isEnabled())
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    needAppRestart = true;
                    return true;
                }
            });
        }
    }
}
