package com.akash.vachana.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akash on 9/2/16.
 */
public class VachanaFragment extends Fragment {

    private static final String TAG = "VachanaFragment";
//    private static final int VACHANA_KEY = 1;

    //    private Kathru currentKathru;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ArrayList<VachanaMini> vachanaMinis;
    private static Context sContext;
    private MainActivity mainActivity;
    private Menu menu;
    private HashMap<Integer, Vachana> vachanas =  new HashMap<>();
    private int current_position;

    public VachanaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        sContext = getActivity();
        mainActivity = (MainActivity) getActivity();
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.vachana_pager_layout, null);

        Bundle extra = getArguments();
        vachanaMinis = (ArrayList<VachanaMini>) extra.getSerializable("vachanas");

        Log.d(TAG, "onCreateView: "+vachanaMinis.size());
        viewPager = (ViewPager) root.findViewById(R.id.vachana_view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        current_position = (int) extra.getSerializable("current_position");
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.vachana, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Vachana vachana = (Vachana) myViewPagerAdapter.vachanaHashMap.get(viewPager.getCurrentItem());
        if (vachana != null) {
            switch (id) {
                case R.id.action_share:
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody =  null;
                    try {
                        shareBody = vachana.getText();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                        Log.d(TAG, "onOptionsItemSelected: TextView not found") ;
                    }
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, vachana.getKathru());
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    return true;

                case R.id.action_favorite:
                    boolean new_state = !vachana.getFavorite();
                    vachana.setFavorite(new_state);
                    if (new_state)
                        item.setIcon(R.drawable.ic_star_20dp);
                    else
                        item.setIcon(R.drawable.ic_star_outline_20dp);
                    new UpdateVachanaFavorite().execute(vachana.getId(), new_state);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class UpdateVachanaFavorite extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            if ((boolean)objects[1])
                mainActivity.db.addVachanaToFavorite((int)objects[0]);
            else
                mainActivity.db.removeVachanaToFavorite((int)objects[0]);
            return null;
        }
    }

    /**
     * viewpager change listener
     */
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) { }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        @Override
        public void onPageScrollStateChanged(int arg0) { }
    };

    @Override
    public void onResume() {
        super.onResume();

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
        Vachana vachana = (Vachana) myViewPagerAdapter.vachanaHashMap.get(viewPager.getCurrentItem());
        if (vachana!= null) {
            try {
                mainActivity.getSupportActionBar().setTitle(vachana.getKathru());
            } catch (NullPointerException e){
                Log.d(  TAG, "onCreate: Actionbar not found");
            }
        }
        viewPager.setCurrentItem(current_position, true);
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        public HashMap<Integer, Vachana> vachanaHashMap = new HashMap<>();

        public MyViewPagerAdapter() {        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position)  {
            layoutInflater = (LayoutInflater) sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.vachana_text_view, container, false);
            new GetVachanaFromDb(view, position).execute();
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return vachanaMinis.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            vachanaHashMap.remove(position);
        }

        private class GetVachanaFromDb extends AsyncTask {
            private ProgressBar progressBar;
            private TextView tv;
            private int position;

            public GetVachanaFromDb(View view, int position) {
                tv = (TextView) view.findViewById(R.id.vachana_text);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                this.position = position;
            }

            @Override
            protected Vachana doInBackground(Object[] objects) {
                Vachana vachana = mainActivity.db.getVachana(vachanaMinis.get(position).getId());
                return vachana;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Vachana vachana = (Vachana)o;
                MenuItem item = menu.findItem(R.id.action_favorite);
                if (item != null){
                    if (vachana.getFavorite()) {
                        menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_20dp);
                    }
                    else {
                        menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_outline_20dp);
                    }
                }
                progressBar.setVisibility(View.GONE);
                tv.setText(vachana.getText());
                tv.setVisibility(View.VISIBLE);
                vachanaHashMap.put(position, vachana);
            }

        }
    }

}
