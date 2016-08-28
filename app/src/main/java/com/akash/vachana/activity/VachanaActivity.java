package com.akash.vachana.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.KathruMap;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.htmlUtil.HtmlHelper;
import com.akash.vachana.util.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VachanaActivity extends AppCompatActivity {
    private static final String className = "VachanaActivity";
    private ArrayList<KathruMini> kathruMap;
    private Kathru currentKathru;
    private int currentKathruIndex = 87;
    private Vachana currentVachana;
    private int currentVachanaIndex;
    private  WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vachana);
        try {
            getSupportActionBar().setTitle("ವಚನಗಳು");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        myWebView = (WebView) findViewById(R.id.webview);

        createKathruMap();

        currentKathruIndex = 0;
        currentKathru = getKathru(kathruMap.get(currentKathruIndex));
        currentVachanaIndex = 0;
        int id = currentKathru.getVachanasId().get(currentVachanaIndex).getId();
        currentVachana = getFirstVachana(currentKathru.getId(), id);

        String title = currentKathru.getVachanasId().get(currentVachanaIndex).getTitle();
        String vachanaText = currentVachana.getText();
        String kathru = currentKathru.getName();

        myWebView.loadData(HtmlHelper.getHtmlString(title, kathru, vachanaText),
                "text/html; charset=utf-8","UTF-8");
    }

    private Kathru getKathru(KathruMini kathruMini) {
        int id = kathruMini.getId();
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

    private void createKathruMap() {
        try {
            InputStream inputStream = getAssets().open("map.json");
            kathruMap = KathruMap.getKathruMap(FileHelper.getFileContent(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_prev_kathru:
                if (currentKathruIndex>0){
                    currentKathruIndex--;
                    currentKathru = getKathru(kathruMap.get(currentKathruIndex));
                    currentVachanaIndex = 0;
                }
                break;
            case R.id.btn_prev_vachana:
                if (currentVachanaIndex>0) {
                    currentVachanaIndex--;
                }
                break;
            case R.id.btn_next_vachana:
                if (currentVachanaIndex<currentKathru.getVachanasId().size()-1) {
                    currentVachanaIndex++;
                }
                break;
            case R.id.btn_next_kathru:
                if(currentKathruIndex < kathruMap.size()-1) {
                    currentKathruIndex++;
                    currentKathru = getKathru(kathruMap.get(currentKathruIndex));
                    currentVachanaIndex = 0;
                }
                break;
        }
        int vid = currentKathru.getVachanasId().get(currentVachanaIndex).getId();
        currentVachana = getFirstVachana(currentKathru.getId(), vid);
        String title = currentKathru.getVachanasId().get(currentVachanaIndex).getTitle();
        String vachanaText = currentVachana.getText();
        String kathru = currentKathru.getName();

        myWebView.loadData(HtmlHelper.getHtmlString(title, kathru, vachanaText),
                "text/html; charset=utf-8","UTF-8");
    }
}
