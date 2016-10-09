package com.akash.vachana.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.akash.vachana.activity.MainActivity;

/**
 * Created by akash on 9/10/16.
 */
public class UpdateVachanaFavorite extends AsyncTask {
    private static final String TAG = "UpdateVachanaFavorite";

    @Override
    protected Void doInBackground(Object[] objects) {
        MainActivity.vachanaFavoriteChanged = true;

        if (objects[2] instanceof UpdateKathruFavorite.INotifyActivityChange){
            UpdateKathruFavorite.INotifyActivityChange listener = (UpdateKathruFavorite.INotifyActivityChange) objects[2];
            listener.notifyChange();
        }else {
            Log.e(TAG, "doInBackground: INotifyActivityChange is not passed!!!");
        }

        if ((boolean)objects[1])
            MainActivity.db.addVachanaToFavorite((int)objects[0]);
        else
            MainActivity.db.removeVachanaFromFavorite((int)objects[0]);
        return null;
    }
}