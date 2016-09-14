package com.akash.vachana.dbUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private int id;
    private String name;
    private String ankitha;
    private int num;
//    private KathruDetails details;
//    private ArrayList<VachanaMini> vachanasId;

    public Kathru (int id, String name, String ankitha, int num, ArrayList<VachanaMini> vachanasId)
    {
        this.id = id;
        this.name= name;
        this.ankitha = ankitha;
        this.num = num;
//        this.vachanasId =
    }

    /*
    public Kathru (MainDbHelper mainDbHelper){
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
                details = new KathruDetails(j.getJSONObject("details"));
                vachanasId = getVachanas(j.getJSONObject("vachana"));
            } else {
                Log.e(className, "JSON Object is null!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*//*
        SQLiteDatabase db = mainDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "Id",
                "Name",
                "Ankitha",
                "Num"
        };

*//*      // Filter results WHERE "title" = 'My Title'
        String selection = "" + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";
*//*

        Cursor c = db.query(
                "Kathru",                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        }*/


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAnkitha() {
        return ankitha;
    }

    public int getNum () { return num; }

/*    public ArrayList<VachanaMini> getVachanasId() {
        return vachanasId;
    }*/
}
