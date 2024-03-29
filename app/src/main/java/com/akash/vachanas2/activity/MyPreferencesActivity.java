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

package com.akash.vachanas2.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.akash.vachanas2.R;
import com.akash.vachanas2.databinding.ActivityMainBinding;
import com.akash.vachanas2.databinding.ActivityPreferenceBinding;
import com.akash.vachanas2.util.ThemeChangeUtil;
import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;

public class MyPreferencesActivity extends AppCompatActivity {
    private static final String TAG = "MyPreferencesActivity";
    private ActivityPreferenceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChangeUtil.themeResetGard(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPreferences.getBoolean("theme", false);
        AppCompatDelegate.setDefaultNightMode(value? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        ThemeChangeUtil.onActivityCreateSetTheme(this, 0xFFFFFF & sharedPreferences.getInt("themeColor",
                ContextCompat.getColor(this, R.color.color_set_5_primary)));

        super.onCreate(savedInstanceState);
        binding = ActivityPreferenceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   onBackPressed();
                }
            });
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.preference_content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        public static final String TAG = "MyPreferenceFragment";

        private SwitchPreferenceCompat darkThemeSwitch;
        private ColorPreferenceCompat themeChooser;
        private Preference license;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);

            Toolbar toolbar = getActivity().findViewById(R.id.search_bar);
            ((AppCompatActivity)getActivity()).setSupportActionBar( toolbar);
/*

            darkThemeSwitch = getPreferenceManager().findPreference("theme");
            darkThemeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    restartActivity();
                    return true;
                }
            });

            themeChooser = getPreferenceManager().findPreference("themeColor");

            themeChooser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    restartActivity();
                    return true;
                }
            });
*/

            license = getPreferenceManager().findPreference("license");
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

        private void restartActivity() {
            ThemeChangeUtil.changeToTheme(getActivity());
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
}
