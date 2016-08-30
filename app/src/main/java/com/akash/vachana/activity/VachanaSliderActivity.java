package com.akash.vachana.activity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.KathruMap;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.htmlUtil.HtmlHelper;
import com.akash.vachana.util.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VachanaSliderActivity extends AppCompatActivity {

    private static final String TAG = "VachanaSliderActivity";
    private Kathru currentKathru;
    private Vachana currentVachana;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private WebView webview;
    private ArrayList<VachanaMini> vachanaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vachana_slider);

        currentKathru = getKathruById(104);
        vachanaIds = currentKathru.getVachanasId();

        currentVachana = getFirstVachana(currentKathru.getId(),
                currentKathru.getVachanasId().get(0).getId());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private Kathru getKathruById(int id) {
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

    private Kathru getKathru(KathruMini kathruMini) {
        return getKathruById(kathruMini.getId());
    }

    private Vachana getVachana(int karthuId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = getAssets().open(karthuId+"/"+vachanaId+".json");
            vachana = new Vachana(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vachana;
    }

    private Vachana getFirstVachana(int karthuId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = getAssets().open(karthuId+"/"+vachanaId+".json");
            vachana = new Vachana(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vachana;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position <  vachanaIds.size()-1) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentVachana = getFirstVachana(currentKathru.getId(),
                                currentKathru.getVachanasId().get(position).getId());
                    }
                }).start();

//                Vachana currentVachana = getFirstVachana(currentKathru.getId(),
//                        currentKathru.getVachanasId().get(position).getId());
           }
        }

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
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            webview = new WebView(container.getContext());

            String title =  "ವಚನಗಳು"; // vachanas.get(position).getId();
            String vachanaText = currentVachana.getText();
            String kathru = currentKathru.getName();

            webview.loadData(HtmlHelper.getHtmlString(title, kathru, vachanaText),
                    "text/html; charset=utf-8","UTF-8");

            container.addView(webview);

            return webview;
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
