package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by akash on 8/28/16.
 */
public class KathruMap implements Serializable{

    private static final String TAG = "KathruMap";

    private KathruMap() {}
/*
    public static ArrayList<KathruMini> getKathruMap(String src) {
        JSONArray j = null;
        ArrayList<KathruMini> kathruMinis = new ArrayList<>();

        try {
            j = new JSONArray(src);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            assert j != null;
            Log.d(TAG, "getKathruMap: "+j.length());
            for (int i = 0; i<j.length(); i++){
                JSONObject obj = j.getJSONObject(i);
//                if(obj == null)
//                    Log.d(TAG, "getKathruMap: JOBJECT null");

                int id =Integer.parseInt(obj.getString("id"));

                kathruMinis.add(new KathruMini(id,
                        obj.getString("name"),
                        obj.getString("ankitha"),
                        obj.getInt("count")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kathruMinis;
    }*/

}
