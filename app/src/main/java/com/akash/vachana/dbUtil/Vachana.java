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
    private int kathruId;

    public Vachana(int id, String text, String kathru, boolean favorite, int kathruId)
    {
        this.id = id;
        this.text = text;
        this.kathru= kathru;
        this.favorite = favorite;
        this.kathruId = kathruId;
    }

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

    public int getKathruId() {
        return kathruId;
    }
}
