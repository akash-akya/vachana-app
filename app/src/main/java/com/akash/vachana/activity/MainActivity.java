package com.akash.vachana.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.KathruListFragment;
import com.akash.vachana.fragment.SearchFragment;
import com.akash.vachana.fragment.VachanaListFragment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchFragment.OnSearchFragmentInteractionListener, Serializable {

    private static final String TAG = "MainActivity";

    public static final String[] fragments ={
            "com.akash.vachana.fragment.VachanaFragment",
            "com.akash.vachana.fragment.VachanaListFragment",
            "com.akash.vachana.fragment.KathruListFragment",
            "com.akash.vachana.fragment.SearchFragment"
    };

    public MainDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            db.createDataBase();
            Log.d(TAG, "onCreate: Database Created\n");
        } catch (IOException ioe) {
            //throw new Error("Unable to create database");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        selectItem(R.id.nav_vachana);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
/*
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        selectItem(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
                final KathruMini kathru = db.getKathruMiniById(id);
                bundle.putString("title", kathru.getName());
                bundle.putSerializable("listener", new VachanaListFragment.OnListFragmentInteractionListener() {
                    @Override
                    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[0]);

                        getIntent().putExtra("vachanas", vachanaMinis);
                        getIntent().putExtra("current_position", position);
                        fragment.setArguments(getIntent().getExtras());

                        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content, fragment)
                                .addToBackStack( "vachana_list" )
                                .commit();
                    }

                    @Override
                    public void onFavoriteButton(int vachanaId, boolean checked) {
                        new UpdateVachanaFavorite().execute(vachanaId, checked);
                    }

                    @Override
                    public ArrayList<VachanaMini> getVachanaMinis() {
                        return db.getVachanaMinisByKathruId(id);
                    }
                });
                fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
                fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case R.id.nav_kathru:
                fragment = Fragment.instantiate(MainActivity.this, fragments[2]);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                bundle.putSerializable("listener", allKathruListListener);
                try {
                    fragment.setArguments(bundle);
                } catch (NullPointerException e){
                    Log.d(TAG, "selectItem: Fragment is null!!");
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
//                        .addToBackStack( "kathru_list" )
                        .commit();
                return;
            case R.id.nav_favorite:
                bundle.putString("title", "ನೆಚ್ಚಿನ ವಚನಗಳು");
                bundle.putSerializable("listener", new VachanaListFragment.OnListFragmentInteractionListener() {
                    @Override
                    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[0]);

                        Log.d(TAG, "onListFragmentInteraction: "+position);
                        getIntent().putExtra("vachanas", vachanaMinis);
                        getIntent().putExtra("current_position", position);
                        fragment.setArguments(getIntent().getExtras());

                        fragmentManager.popBackStack("favorite_vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content, fragment)
                                .addToBackStack( "favorite_vachana_list" )
                                .commit();
                    }

                    @Override
                    public void onFavoriteButton(int vachanaId, boolean checked) {
                        new UpdateVachanaFavorite().execute(vachanaId, checked);
                    }

                    @Override
                    public ArrayList<VachanaMini> getVachanaMinis() {
                        return db.getFavoriteVachanaMinis();
                    }
                });
                fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
                fragment.setArguments(bundle);
                fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack( "vachana_list" )
                        .commit();
                return;
            case R.id.nav_favorite_kathru:
                fragment = Fragment.instantiate(MainActivity.this, fragments[2]);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                bundle.putSerializable("listener", favoriteKathruListListener);
                try {
                    fragment.setArguments(bundle);
                } catch (NullPointerException e){
                    Log.d(TAG, "selectItem: Fragment is null!!");
                }
                fragmentManager.popBackStack("kathru_favorite_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack( "kathru_favorite_list" )
                        .commit();
                return;
            case R.id.nav_search:
                fragment = Fragment.instantiate(MainActivity.this, fragments[3]);
                try {
                    fragment.setArguments(bundle);
                } catch (NullPointerException e){
                    Log.d(TAG, "selectItem: Fragment is null!!");
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .commit();
                return;
            case R.id.nav_settings:
                return;
            default:
                Log.e(TAG, "selectItem: Error, Wrong id");
        }

        try {
            fragment.setArguments(bundle);
        } catch (NullPointerException e){
            Log.d(TAG, "selectItem: Fragment is null!!");
        }

        fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction: Hi dude!!");
    }

    public class UpdateVachanaFavorite extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            if ((boolean)objects[1])
                db.addVachanaToFavorite((int)objects[0]);
            else
                db.removeVachanaFromFavorite((int)objects[0]);
            return null;
        }
    }

    public class UpdateKathruFavorite extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            if ((boolean)objects[1])
                db.addKathruToFavorite((int)objects[0]);
            else
                db.removeKathruFromFavorite((int)objects[0]);
            return null;
        }
    }

    private KathruListFragment.OnKathruListFragmentInteractionListener allKathruListListener = new KathruListFragment.OnKathruListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(final KathruMini item) {
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
            bundle.putString("title", item.getName());
            bundle.putSerializable("listener", new VachanaListFragment.OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[0]);

                    getIntent().putExtra("vachanas", vachanaMinis);
                    getIntent().putExtra("current_position", position);
                    fragment.setArguments(getIntent().getExtras());

                    fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack( "vachana_list" )
                            .commit();
                }

                @Override
                public void onFavoriteButton(int vachanaId, boolean checked) {
                    new UpdateVachanaFavorite().execute(vachanaId, checked);
                }

                @Override
                public ArrayList<VachanaMini> getVachanaMinis() {
                    return db.getVachanaMinisByKathruId(item.getId());
                }
            });

            fragment.setArguments(bundle);
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack( "kathru_list" )
                    .commit();
        }

        @Override
        public void onFavoriteButton(int kathruId, boolean checked) {
            new UpdateKathruFavorite().execute(kathruId, checked);
        }

        @Override
        public ArrayList<KathruMini> getKathruMinis() {
            return db.getAllKathruMinis();
        }
    };

    private KathruListFragment.OnKathruListFragmentInteractionListener favoriteKathruListListener = new KathruListFragment.OnKathruListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(final KathruMini item) {
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
            bundle.putString("title", item.getName());
            bundle.putSerializable("listener", new VachanaListFragment.OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[0]);

                    getIntent().putExtra("vachanas", vachanaMinis);
                    getIntent().putExtra("current_position", position);
                    fragment.setArguments(getIntent().getExtras());

                    fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack( "vachana_list" )
                            .commit();
                }

                @Override
                public void onFavoriteButton(int vachanaId, boolean checked) {
                    new UpdateVachanaFavorite().execute(vachanaId, checked);
                }

                @Override
                public ArrayList<VachanaMini> getVachanaMinis() {
                    return db.getVachanaMinisByKathruId(item.getId());
                }
            });

            fragment.setArguments(bundle);
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack( "kathru_list" )
                    .commit();
        }

        @Override
        public void onFavoriteButton(int kathruId, boolean checked) {
            new UpdateKathruFavorite().execute(kathruId, checked);
        }

        @Override
        public ArrayList<KathruMini> getKathruMinis() {
            return db.getFavoriteKathruMinis();
        }
    };
}
