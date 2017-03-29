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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.akash.vachana.R;
import com.akash.vachana.Util.KannadaTransliteration;
import com.akash.vachana.Util.ThemeChangeUtil;
import com.akash.vachana.dbUtil.DatabaseReadAccess;
import com.akash.vachana.dbUtil.KathruDetails;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.KathruDetailsFragment;
import com.akash.vachana.fragment.KathruListFragment;
import com.akash.vachana.fragment.SearchFragment;
import com.akash.vachana.fragment.VachanaFragment;
import com.akash.vachana.fragment.VachanaListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        KathruListFragment.OnKathruListFragmentListener,
        VachanaListFragment.OnVachanaFragmentListListener,
        KathruDetailsFragment.OnKathruDetailsInteractionListener,
        SearchFragment.OnSearchFragmentListener {

    private static final String TAG = "MainActivity";
    private static final long SMOOTH_DRAWER_DELAY = 175;
    private static final String DRAWER_RANDOM_VACHANA_LIST = "random_vachana_list";
    private static final String DRAWER_KATHRU_LIST = "kathru_list";
    private static final String DRAWER_FAVORITE_VACHANA_LIST = "fav_vachana_drawer";
    private static final String DRAWER_FAVORITE_KATHRU_LIST = "kathru_favorite_drawer";
    private static final String DRAWER_SEARCH = "search_view_drawer";
    private static final int SETTINGS_ACTIVITY = 1;

    private static MainDbHelper db;
    private boolean isDarkThemeEnabled;
    private int themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChangeUtil.themeResetGard(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isDarkThemeEnabled = sharedPreferences.getBoolean("theme", false);
        themeColor = sharedPreferences.getInt("themeColor", ContextCompat.getColor(this, R.color.color_set_5_primary));
        AppCompatDelegate.setDefaultNightMode(isDarkThemeEnabled? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        ThemeChangeUtil.onActivityCreateSetTheme(this, 0xFFFFFF & themeColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        * Testing purpose
        * */
        Log.d("Kannada Test", "Start");
        String text = KannadaTransliteration.getUnicodeString("KhaDgadhAri yaccammanAyaka");
        Log.d("Kannada Test", text);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        db = new MainDbHelper(this);
        try {
            db.getDataBase();
            Log.d(TAG, "onCreate: Database is loaded\n");
        } catch (IOException ioe) {
            //throw new Error("Unable to create database");
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount()>0){

                    String name = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
                    int itemId;
                    switch (name){
                        case DRAWER_RANDOM_VACHANA_LIST: itemId = R.id.nav_vachana; break;
                        case DRAWER_KATHRU_LIST: itemId = R.id.nav_kathru; break;
                        case DRAWER_SEARCH: itemId = R.id.nav_search; break;
                        case DRAWER_FAVORITE_VACHANA_LIST: itemId = R.id.nav_favorite; break;
                        case DRAWER_FAVORITE_KATHRU_LIST: itemId = R.id.nav_favorite_kathru; break;
                        default: return;
                    }
                    navigationView.setCheckedItem(itemId);
                }
            }
        });

        if (savedInstanceState == null) {
            selectItem(R.id.nav_vachana);
            navigationView.setCheckedItem(R.id.nav_vachana);
        }
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void doThis(final KathruMini kathruMini){
        new Thread(new Runnable() {
            public void run(){
                if (kathruMini.getFavorite() == 1)
                    MainActivity.db.addKathruToFavorite(kathruMini.getId());
                else
                    MainActivity.db.removeKathruFromFavorite(kathruMini.getId());
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void doThis(final VachanaMini vachanaMini){
        new Thread(new Runnable() {
            public void run(){
                if (vachanaMini.getFavorite() == 1)
                    MainActivity.db.addVachanaToFavorite(vachanaMini.getId());
                else
                    MainActivity.db.removeVachanaFromFavorite(vachanaMini.getId());
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

/*
        switch (id) {
            case R.id.action_settings:
                return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectItem(item.getItemId());
            }
        },SMOOTH_DRAWER_DELAY);

        return true;
    }

    private void selectItem(int itemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;

        switch (itemId){

            case R.id.nav_vachana:
                Random r = new Random();
                ArrayList<KathruMini> k = db.getAllKathruMinis();
                final int id = k.get(r.nextInt(247)).getId();
                final KathruMini kathruMini = db.getKathruMiniById(id);

                fragment = VachanaListFragment.newInstance(kathruMini, kathruMini.getName(), ListType.NORMAL_LIST);

                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, DRAWER_RANDOM_VACHANA_LIST)
                        .addToBackStack(DRAWER_RANDOM_VACHANA_LIST)
                        .commit();
                return;


            case R.id.nav_kathru:
                fragment = KathruListFragment.newInstance("ವಚನಕಾರರು", ListType.NORMAL_LIST);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, DRAWER_KATHRU_LIST)
                        .addToBackStack( DRAWER_KATHRU_LIST )
                        .commit();
                return;


            case R.id.nav_favorite:
                fragment = VachanaListFragment.newInstance(null, "ನೆಚ್ಚಿನ ವಚನಗಳು", ListType.FAVORITE_LIST);
                fragmentManager.popBackStack(DRAWER_FAVORITE_VACHANA_LIST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, DRAWER_FAVORITE_VACHANA_LIST)
                        .addToBackStack( DRAWER_FAVORITE_VACHANA_LIST )
                        .commit();
                return;


            case R.id.nav_favorite_kathru:
                fragment = KathruListFragment.newInstance("ನೆಚ್ಚಿನ ವಚನಕಾರರು", ListType.FAVORITE_LIST);
                fragmentManager.popBackStack(DRAWER_FAVORITE_KATHRU_LIST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, DRAWER_FAVORITE_KATHRU_LIST)
                        .addToBackStack( DRAWER_FAVORITE_KATHRU_LIST )
                        .commit();
                return;


            case R.id.nav_search:
                fragment = SearchFragment.newInstance();
                fragmentManager.popBackStack(DRAWER_SEARCH, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, DRAWER_SEARCH)
                        .addToBackStack( DRAWER_SEARCH )
                        .commit();
                return;

            case R.id.nav_play_link:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException activityNotFoundException) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return;

            case R.id.nav_more_about_vachana:{
                WebView webView = new WebView(this);
                webView.loadUrl("file:///android_res/raw/more_about_vachana.html");
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setView(webView);
                dialog.show();
                return;
            }

            case R.id.nav_settings:{
                Intent intent = new Intent(this, MyPreferencesActivity.class);
                startActivityForResult(intent, SETTINGS_ACTIVITY);
                return;
            }

            case R.id.nav_about_app: {
                WebView webView = new WebView(this);
                webView.loadUrl("file:///android_res/raw/about_app.html");
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setView(webView);
                dialog.show();
                return;
            }

            default:
                Log.e(TAG, "selectItem: Error, Wrong id");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTIVITY){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            boolean newValue = sharedPreferences.getBoolean("theme", false);
            int newColor = sharedPreferences.getInt("themeColor",
                    ContextCompat.getColor(this, R.color.color_set_5_primary));

            if (newValue != isDarkThemeEnabled || newColor != themeColor){
                ThemeChangeUtil.changeToTheme(this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public ArrayList<KathruMini> getAllKathruMinis() {
        return db.getAllKathruMinis();
    }

    @Override
    public void onKathruListItemClick(KathruMini kathruMini) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = VachanaListFragment.newInstance(kathruMini, kathruMini.getName(), ListType.NORMAL_LIST);

        hideKeyboard(this);

        fragmentManager.popBackStack("kathru_list_vertical", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment, "kathru_list_vertical")
                .addToBackStack("kathru_list_vertical")
                .commit();
    }

    @Override
    public ArrayList<KathruMini> getKathruMinis(ListType listType) {
        ArrayList<KathruMini> kathruMinis = null;
        switch (listType){
            case NORMAL_LIST:
                kathruMinis = db.getAllKathruMinis();
                break;
            case FAVORITE_LIST:
                kathruMinis = db.getFavoriteKathruMinis();
                break;
        }
        return kathruMinis;
    }

    @Override
    public void OnVachanaListItemClick(ArrayList<VachanaMini> vachanaMinis, int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VachanaFragment fragment = VachanaFragment.newInstance(position, vachanaMinis);

        hideKeyboard(this);

        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment, "vachana_list")
                .addToBackStack("vachana_list")
                .commit();
    }

    @Override
    public ArrayList<VachanaMini> getVachanaMinis(KathruMini kathruMini, ListType listType) {
        ArrayList<VachanaMini> vachanaMinis = null;
        switch (listType){
            case NORMAL_LIST:
                vachanaMinis = db.getVachanaMinisByKathruId(kathruMini.getId());
                break;
            case FAVORITE_LIST:
                vachanaMinis = db.getFavoriteVachanaMinis();
                break;
        }
        return vachanaMinis;
    }

    @Override
    public KathruDetails getKathruDetails(int kathruId) {
        return db.getKathruDetails(kathruId);
    }

    @Override
    public void onVachanaButtonClick (int kathruId) {
        KathruMini kathruMini = db.getKathruMiniById(kathruId);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        VachanaListFragment fragment = VachanaListFragment.newInstance(kathruMini, kathruMini.getName(), ListType.NORMAL_LIST);

        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment, "vachana_list")
                .addToBackStack( "vachana_list")
                .commit();
    }

    public static DatabaseReadAccess getDatabaseReadAccess(){
        return db;
    }

    public String escapeUnicode(String input) {
        StringBuilder b = new StringBuilder(input.length());
        Formatter f = new Formatter(b);
        for (char c : input.toCharArray()) {
            if (c < 128) {
                b.append(c);
            } else {
                f.format("\\u%04x", (int) c);
            }
        }
        return b.toString();
    }

    @Override
    public void onSearchButtonClick(String text, String kathru, boolean isPartial) {

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final VachanaListFragment fragment = VachanaListFragment.newInstance("ಹುಡುಕು", text, kathru,
                isPartial, ListType.SEARCH);

        hideKeyboard(this);

        fragmentManager.popBackStack("search_button", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack( "search_button")
                .commit();
    }

    @Override
    public ArrayList<VachanaMini> getVachanaMinis(String text, String kathruString, boolean isPartialSearch) {
        return MainActivity.db.searchForVachana( text, kathruString, isPartialSearch);
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
