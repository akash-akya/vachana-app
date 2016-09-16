package com.akash.vachana.activity;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.KathruListFragment;
import com.akash.vachana.fragment.VachanaListFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VachanaListFragment.OnListFragmentInteractionListener,
        KathruListFragment.OnKathruListFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    public static final String DB_NAME = "raw/main.db";

    final String[] fragments ={
            "com.akash.vachana.fragment.VachanaFragment",
            "com.akash.vachana.fragment.VachanaListFragment",
            "com.akash.vachana.fragment.KathruListFragment",
    };

    private final int[] bgColors = {
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color6,
            R.color.color7,
            R.color.color8,
            R.color.color9,
            R.color.color10,
            R.color.color11,
            R.color.color12,
            R.color.color13,
            R.color.color14,
            R.color.color15,
            R.color.color16,
            R.color.color17,
            R.color.color18,
            R.color.color19
    };
    private ActionBar actionBar;
    private ShareActionProvider mShareActionProvider;
    public MainDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar!= null) {
            actionBar.setElevation(0);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // dbHandler class instance
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectItem(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int itemId) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        Bundle bundle = new Bundle();
        switch (itemId){
            case R.id.nav_vachana:
                // For vachanas list view
                Random r = new Random();
                ArrayList<KathruMini> k = db.getAllKathruMinis();
                bundle.putInt("id", k.get(r.nextInt(247)).getId());
                fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
                break;
            case R.id.nav_kathru:
                // For kathru list view
//                bundle.putInt("id", 112);
                fragment = Fragment.instantiate(MainActivity.this, fragments[2]);
                break;
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
    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int posistion) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[0]);

//        ArrayList<Integer> ids = new ArrayList<>();
//        ArrayList<String>  kathrus= new ArrayList<>();
//        for (VachanaMini v: vachanaMinis) {
//            ids.add(v.getId());
//            kathrus.add(v.getKathruName());
//        }

//        bundle.putIntegerArrayList("ids", ids);
//        bundle.putInt("kathru_id", vachanaMinis.getKathruId());
        getIntent().putExtra("vachanas", vachanaMinis);
        getIntent().putExtra("current_position", posistion);

        fragment.setArguments(bundle);
        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack( "vachana_list" )
                .commit();
    }

    private class UpdateVachanaFavorite extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            if ((boolean)objects[1])
                db.addVachanaToFavorite((int)objects[0]);
            else
                db.removeVachanaToFavorite((int)objects[0]);
            return null;
        }
    }

    @Override
    public void onFavoriteButton(int vachanaId, boolean checked) {
        new UpdateVachanaFavorite().execute(vachanaId, checked);
    }

    @Override
    public void onListFragmentInteraction(KathruMini item) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
        bundle.putInt("id", item.getId());

        fragment.setArguments(bundle);
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack( "kathru_list" )
                .commit();
    }

    /*public Kathru getKathruById(int id) {
        Kathru kathru = null;
        try {
            InputStream inputStream = getAssets().open(id+"/details.json");
            kathru = new Kathru(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kathru;
    }

    public Kathru getKathru(KathruMini kathruMini) {
        return getKathruById(kathruMini.getId());
    }

    public Vachana getVachana(int kathruId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = getAssets().open(kathruId+"/"+vachanaId+".json");
            vachana = new Vachana(FileHelper.getFileContent(inputStream), getKathruById(kathruId).getName());
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vachana;
    }

    public Vachana getFirstVachana(int kathruId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = getAssets().open(kathruId+"/"+vachanaId+".json");
            vachana = new Vachana(FileHelper.getFileContent(inputStream), getKathruById(kathruId).getName());
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vachana;
    }

    public ArrayList<KathruMini>  getAllKathru() {
        ArrayList<KathruMini> kathruMap = null;
        try {
            InputStream inputStream = getAssets().open("map.json");
            kathruMap = KathruMap.getKathruMap(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kathruMap;
    }*/
}
