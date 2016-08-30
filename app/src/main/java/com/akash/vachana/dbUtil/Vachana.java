package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 8/28/16.
 */
public class Vachana {
    final public String className = "Vachana";
    int id;
    String text;

    public Vachana(String src) {
        JSONObject j = null;
        try {
            j = new JSONObject(src);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (j != null) {

                Log.d("test", "Vachana: "+j.getString("id"));


                id = Integer.parseInt(j.getString("id"));
                text = j.getString("vachana");
            } else {
                Log.e(className, "JSON Object is null!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
