package com.akash.vachana.dbUtil;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by akash on 8/28/16.
 */
public class Kathru {
    public static final String className = "Katru";
    int id;
    String name;
    ArrayList<VachanaMini> vachanasId;

    public Kathru (String src){
        JSONObject j = null;
        try {
            j = new JSONObject(src);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (j != null) {
                id = j.getInt("id");
                name = j.getString("name");
                vachanasId = getVachanas(j.getJSONObject("vachana"));
            } else {
                Log.e(className, "JSON Object is null!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<VachanaMini> getVachanas(JSONObject vachanasObj) {
        Iterator<String> iter = vachanasObj.keys();
        ArrayList<VachanaMini> vachanas = new ArrayList<>();

        while (iter.hasNext()) {
            String key = iter.next();
            try {
                vachanas.add(new VachanaMini(key, vachanasObj.getString(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return vachanas;
    }

}
