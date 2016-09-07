package com.akash.vachana.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.htmlUtil.HtmlHelper;

import java.util.ArrayList;

/**
 * Created by akash on 9/2/16.
 */
public class VachanaFragment extends Fragment {

    private static final String TAG = "VachanaFragment";

    private Kathru currentKathru;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ArrayList<Integer> vachanaIds;
    private static Context sContext;
    private MainActivity mainActivity;
    private Vachana currentVachana;

    public VachanaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        sContext = getActivity();
        mainActivity = (MainActivity) getActivity();
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.vachana_pager_layout, null);

        if (getArguments() != null) {
            vachanaIds = getArguments().getIntegerArrayList("ids");
            currentKathru = mainActivity.getKathruById(getArguments().getInt("kathru_id"));
        } else {
            Log.e(TAG, "onCreateView: No bundle!");
        }

        viewPager = (ViewPager) root.findViewById(R.id.vachana_view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.vachana, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody =  null;
                try {
                    shareBody = ((TextView) getView().findViewById(R.id.vachana_text)).getText().toString();
                } catch (NullPointerException e){
                    e.printStackTrace();
                    Log.d(TAG, "onOptionsItemSelected: TextView not found") ;
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,currentKathru.getName());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        try {
            mainActivity.getSupportActionBar().setTitle(currentKathru.getName());
        } catch (NullPointerException e){
            Log.d(  TAG, "onCreate: Actionbar not found");
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position)  {
            layoutInflater = (LayoutInflater) sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.vachana_text_view, container, false);
            final TextView vachana_tv = (TextView) view.findViewById(R.id.vachana_text);
            currentVachana = mainActivity.getFirstVachana(currentKathru.getId(),
                    vachanaIds.get(position));

            String vachanaText = currentVachana.getText();
            vachana_tv.setText(Html.fromHtml(HtmlHelper.getHtmlString(vachanaText)));

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return vachanaIds.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
