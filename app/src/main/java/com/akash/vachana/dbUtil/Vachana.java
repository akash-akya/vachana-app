package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by akash on 8/28/16.
 */
public class Vachana implements Serializable {
    final public String className = "Vachana";
    private boolean favorite;
    private int id;
    private String text;
    private String kathru;

    public Vachana(int id, String text, String kathru, boolean favorite)
    {
        this.id = id;
        this.text = text;
        this.kathru= kathru;
        this.favorite = favorite;
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

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean v) {
        favorite = v;
    }

    public String getText() {
        return text;
    }
}
