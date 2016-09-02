package com.akash.vachana.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.htmlUtil.HtmlHelper;
import com.akash.vachana.util.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by akash on 9/2/16.
 */
public class KathruFragment extends Fragment {

    private static final String TAG = "KathruFragment";

    private Kathru currentKathru;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ArrayList<VachanaMini> vachanaIds;
    private ActionBar actionBar;
    private static Context sContext;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sContext = getActivity();
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.vachana_pager_layout, null);

        int id = getArguments().getInt("id");
        currentKathru = getKathruById(id);
        vachanaIds = currentKathru.getVachanasId();

        viewPager = (ViewPager) root.findViewById(R.id.vachana_view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        return root;
    }

    private Kathru getKathruById(int id) {
        Kathru kathru = null;

        try {
            InputStream inputStream = sContext.getAssets().open(id+"/details.json");
            kathru = new Kathru(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kathru;
    }

    private Kathru getKathru(KathruMini kathruMini) {
        return getKathruById(kathruMini.getId());
    }

    private Vachana getVachana(int kathruId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = sContext.getAssets().open(kathruId+"/"+vachanaId+".json");
            vachana = new Vachana(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vachana;
    }

    private Vachana getFirstVachana(int kathruId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = sContext.getAssets().open(kathruId+"/"+vachanaId+".json");
            vachana = new Vachana(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vachana;
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

            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cc333333")));

            final TextView vachana_tv = (TextView) view.findViewById(R.id.vachana_text);

            Vachana vachana= getFirstVachana(currentKathru.getId(),
                    vachanaIds.get(position).getId());

            String vachanaText = vachana.getText();

            vachana_tv.setText(Html.fromHtml(HtmlHelper.getHtmlString(vachanaText)));

            vachana_tv.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector myG = new GestureDetector(sContext, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if(actionBar.isShowing())
                            actionBar.hide();
                        else
                            actionBar.show();
                        Log.d(TAG, "onSingleTapUp: is called");
                        return false;
                    }
                });

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return myG.onTouchEvent(motionEvent);
                }
            });
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