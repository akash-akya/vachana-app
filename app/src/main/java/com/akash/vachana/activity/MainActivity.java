package com.akash.vachana.activity;

import android.content.Context;
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

import com.akash.vachana.R;
import com.akash.vachana.ListViewHelper.VachanaList;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.KathruMap;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.KathruListFragment;
import com.akash.vachana.fragment.VachanaListFragment;
import com.akash.vachana.util.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VachanaListFragment.OnListFragmentInteractionListener,
        KathruListFragment.OnKathruListFragmentInteractionListener {

    private static final String TAG = "MainActivity";

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
                ArrayList<KathruMini> k = getAllKathru();
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
    public void onListFragmentInteraction(VachanaMini item) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[0]);

        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(item.getId());
        bundle.putIntegerArrayList("ids", ids);
        bundle.putInt("kathru_id", item.getKathruId());

        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(KathruMini item) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[1]);
        bundle.putInt("id", item.getId());

        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }


    public Kathru getKathruById(int id) {
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
    }

}
