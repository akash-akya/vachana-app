package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 9/5/16.
 */
public class KathruDetails {
    private static final String TAG = "KathruDetails";
    private String ankithanama;
    private String kaala;
    private String kayaka;

    public KathruDetails(JSONObject j) {
        try {
            ankithanama = j.getString("ಅಂಕಿತ");
            kaala = j.getString("ಕಾಲ");
            kayaka = j.getString("ಕಾಯಕ");
        } catch (JSONException e){
            Log.d(TAG, "KathruDetails: JSON Exception");
        }
    }

    public String getAnkithanama() {
        return ankithanama;
    }

    public String getKaala() {
        return kaala;
    }

    public String getKayaka() {
        return kayaka;
    }
}
