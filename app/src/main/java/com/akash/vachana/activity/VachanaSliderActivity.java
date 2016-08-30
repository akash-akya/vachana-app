package com.akash.vachana.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
//    private Vachana currentVachana;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    int paddingDp;
    private ArrayList<VachanaMini> vachanaIds;

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
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vachana_slider);

        currentKathru = getKathruById(104);
        vachanaIds = currentKathru.getVachanasId();

//        currentVachana = getFirstVachana(currentKathru.getId(),
//                currentKathru.getVachanasId().get(0).getId());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        float density =  getResources().getDisplayMetrics().density;
        int paddingPixel = 20;
        paddingDp = (int)(paddingPixel * density);
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

    private Vachana getVachana(int kathruId, int vachanaId) {
        Vachana vachana = null;
        try {
            InputStream inputStream = getAssets().open(kathruId+"/"+vachanaId+".json");
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
            InputStream inputStream = getAssets().open(kathruId+"/"+vachanaId+".json");
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
        public Object instantiateItem(ViewGroup container, int position)  {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            textView = new TextView(container.getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setIncludeFontPadding(true);
            textView.setVerticalScrollBarEnabled(true);
            textView.setTextIsSelectable(true);
            textView.setPadding(paddingDp, paddingDp,paddingDp,paddingDp);

            Vachana vachana= getFirstVachana(currentKathru.getId(),
                    vachanaIds.get(position).getId());

            String title =  "ವಚನಗಳು"; // vachanas.get(position).getId();
            String vachanaText = vachana.getText();
            String kathru = currentKathru.getName();

            textView.setText(Html.fromHtml(HtmlHelper.getHtmlString(title, kathru, vachanaText)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Log.d(TAG, "instantiateItem: "+position+" - "+ bgColors[position%bgColors.length]);
                textView.setBackgroundResource(bgColors[position%bgColors.length]);
            }
            container.addView(textView);
            return textView;
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
