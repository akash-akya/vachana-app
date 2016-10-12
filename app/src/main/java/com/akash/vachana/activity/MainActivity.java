package com.akash.vachana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.akash.vachana.R;
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
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        KathruListFragment.OnKathruListFragmentListener,
        VachanaListFragment.OnVachanaFragmentListListener,
        KathruDetailsFragment.OnKathruDetailsInteractionListener,
        SearchFragment.OnSearchFragmentListener {

    private static final String TAG = "MainActivity";
    private static final long SMOOTH_DRAWER_DELAY = 175;

    public static MainDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPreferences.getBoolean("theme", false);
        matchColor(0xFFFFFF & sharedPreferences.getInt("themeColor", 0));
        AppCompatDelegate.setDefaultNightMode(value? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Log.d(TAG, "onCreate: Database Created\n");
        } catch (IOException ioe) {
            //throw new Error("Unable to create database");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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

        selectItem(R.id.nav_vachana);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            super.onBackPressed();
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
        Fragment fragment = null;

        Bundle bundle = new Bundle();
        switch (itemId){

            case R.id.nav_vachana:
                Random r = new Random();
                ArrayList<KathruMini> k = db.getAllKathruMinis();
                final int id = k.get(r.nextInt(247)).getId();
                final KathruMini kathruMini = db.getKathruMiniById(id);

                fragment = VachanaListFragment.newInstance(kathruMini, kathruMini.getName(), ListType.NORMAL_LIST);

                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, "vachana_list")
                        .commit();
                return;


            case R.id.nav_kathru:
                fragment = KathruListFragment.newInstance("ವಚನಕಾರರು", ListType.NORMAL_LIST);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, "kathru_list")
                        .addToBackStack( "kathru_list" )
                        .commit();
                return;


            case R.id.nav_favorite:
                fragment = VachanaListFragment.newInstance(null, "ನೆಚ್ಚಿನ ವಚನಗಳು", ListType.FAVORITE_LIST);
                fragmentManager.popBackStack("fav_vachana_drawer", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, "fav_vachana_drawer")
                        .addToBackStack( "fav_vachana_drawer" )
                        .commit();
                return;


            case R.id.nav_favorite_kathru:
                fragment = KathruListFragment.newInstance("ನೆಚ್ಚಿನ ವಚನಕಾರರು", ListType.FAVORITE_LIST);
                fragmentManager.popBackStack("kathru_favorite_drawer", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, "kathru_favorite_drawer")
                        .addToBackStack( "kathru_favorite_drawer" )
                        .commit();
                return;


            case R.id.nav_search:
                fragment = SearchFragment.newInstance();
/*                try {
                    fragment.setArguments(bundle);
                } catch (NullPointerException e){
                    Log.d(TAG, "selectItem: Fragment is null!!");
                }*/
                fragmentManager.popBackStack("search_view_drawer", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, "search_view_drawer")
                        .addToBackStack( "search_view_drawer" )
                        .commit();
                return;


            case R.id.nav_settings:
                Intent intent = new Intent(this, MyPreferencesActivity.class);
                startActivityForResult(intent, 0);
                return;


            default:
                Log.e(TAG, "selectItem: Error, Wrong id");
        }
    }

    public ArrayList<KathruMini> getAllKathruMinis() {
        return db.getAllKathruMinis();
    }

    void matchColor(int id){
        switch (id) {
            case 0x4fc3f7: setTheme(R.style.theme1); break;
            case 0x42bd41: setTheme(R.style.theme2); break;
            case 0xffb74d: setTheme(R.style.theme3); break;
            case 0xff8a65: setTheme(R.style.theme4); break;
            case 0x3F51B5: setTheme(R.style.theme5); break;
            default: Log.d(TAG, "Color: unknown theme");
        }
    }

    @Override
    public void onKathruListItemClick(KathruMini kathruMini) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = VachanaListFragment.newInstance(kathruMini, kathruMini.getName(), ListType.NORMAL_LIST);

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
                vachanaMinis = db.getVachanaMinisByKathruId(kathruMini.getId(), kathruMini.getName());
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

    @Override
    public void onSearchButtonClick(String text, String kathru, boolean isPartial) {

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final VachanaListFragment fragment = VachanaListFragment.newInstance("ಹುಡುಕು", text, kathru,
                isPartial, ListType.SEARCH);

        fragmentManager.popBackStack("search_button", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack( "search_button")
                .commit();
    }

    @Override
    public ArrayList<VachanaMini> getVachanaMinis(String text, String kathruString, boolean isPartialSearch) {
        String query_text = "SELECT " +
                MainDbHelper.KEY_VACHANA_ID + ", "+
                MainDbHelper.KEY_TITLE + ", "+
                MainDbHelper.FOREIGN_KEY_KATHRU_ID + ", "+
                MainDbHelper.KEY_FAVORITE;
        String[] parameters;

        query_text += " FROM " + MainDbHelper.TABLE_VACHANA;
        query_text += " WHERE " +
                MainDbHelper.KEY_TITLE + " LIKE ? "; // + "%"+query+"%";

        String query_text_parameter = isPartialSearch? "%"+text+"%" : text;
        if (!kathruString.isEmpty()) {
            query_text += " AND " +
                    MainDbHelper.FOREIGN_KEY_KATHRU_ID + " IN " +
                    " ( SELECT " + MainDbHelper.KEY_KATHRU_ID +
                    " FROM " + MainDbHelper.TABLE_KATHRU +
                    " WHERE " + MainDbHelper.KEY_NAME + " LIKE ? )"; // + "%"+kathruString+"% ) ";
            parameters = new  String[]{query_text_parameter, "%"+kathruString+"%"};
        } else {
            parameters = new  String[]{query_text_parameter};
        }

        return MainActivity.db.query( query_text, parameters);
    }
}
