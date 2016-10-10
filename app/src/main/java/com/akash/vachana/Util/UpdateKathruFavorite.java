package com.akash.vachana.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.akash.vachana.activity.MainActivity;

/**
 * Created by akash on 9/10/16.
 */
public class UpdateKathruFavorite  extends AsyncTask {
    private static final String TAG = "UpdateKathruFavorite";
    INotifyActivityChange listener;

    @Override
    protected Void doInBackground(Object[] objects) {
        MainActivity.kathruFavoriteChanged = true;

        Log.d(TAG, "doInBackground: kathruFavoriteChanged "+MainActivity.kathruFavoriteChanged);

        if (objects[2] instanceof INotifyActivityChange){
            listener = (INotifyActivityChange)objects[2];
            listener.notifyChange();
        }else {
            Log.e(TAG, "doInBackground: INotifyActivityChange is not passed!!!");
        }

        if ((boolean)objects[1])
            MainActivity.db.addKathruToFavorite((int)objects[0]);
        else
            MainActivity.db.removeKathruFromFavorite((int)objects[0]);
        return null;
    }

    public interface INotifyActivityChange {
        void notifyChange();
    }
}