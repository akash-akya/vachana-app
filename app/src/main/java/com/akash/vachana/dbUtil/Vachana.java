package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 8/28/16.
 */
public class Vachana {
    final public String className = "Vachana";
    private int id;
    private String text;
    private String kathru;

    public Vachana(int id, String text, String kathru)
    {
        this.id = id;
        this.text = text;
        this.kathru= kathru;
    }

/*    public Vachana(String src, String kathru) {
        this.kathru = kathru;
        JSONObject j = null;
        try {
            j = new JSONObject(src);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (j != null) {
                id = Integer.parseInt(j.getString("id"));
                text = j.getString("vachana");
            } else {
                Log.e(className, "JSON Object is null!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public String getKathru() {
        return kathru;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
