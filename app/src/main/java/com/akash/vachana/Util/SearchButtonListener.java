package com.akash.vachana.Util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.SupportActionModeWrapper;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.VachanaListFragment;

import java.util.ArrayList;

/**
 * Created by akash on 4/10/16.
 */
public class SearchButtonListener implements VachanaListFragment.OnListFragmentInteractionListener {
    public static final String TAG = "VachanaListFragment" ;
    private final Context context;
    private final Activity activity;
    private boolean isPartial;
    private  String query;
    private String kathruString;

    public SearchButtonListener(Context context, boolean isPartial, String query , String kathruString ){
        this.context = context;
        this.isPartial = isPartial;
        this.query = query;
        this.kathruString = kathruString;
        activity = ((Activity)context);
    }

    @Override
    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(context, MainActivity.fragments[0]);

        activity.getIntent().putExtra("vachanas", vachanaMinis);
        activity.getIntent().putExtra("current_position", position);
        fragment.setArguments(activity.getIntent().getExtras());

        fragmentManager.popBackStack("search_vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack( "search_vachana_list" )
                .commit();
    }

    @Override
    public void onFavoriteButton(int vachanaId, boolean checked) {
        new MainActivity.UpdateVachanaFavorite().execute(vachanaId, checked);
    }

    @Override
    public ArrayList<VachanaMini> getVachanaMinis() {
        String query_text = "SELECT " +
                MainDbHelper.KEY_VACHANA_ID + ", "+
                MainDbHelper.KEY_TITLE + ", "+
                MainDbHelper.FOREIGN_KEY_KATHRU_ID + ", "+
                MainDbHelper.KEY_FAVORITE;
        String[] parameters;

        query_text += " FROM " + MainDbHelper.TABLE_VACHANA;

        query_text += " WHERE " +
                MainDbHelper.KEY_TITLE + " LIKE ? "; // + "%"+query+"%";

        String query_text_parameter = isPartial? "%"+query+"%" : query;

        if (!kathruString.isEmpty()) {
            query_text += " AND " +
                    MainDbHelper.FOREIGN_KEY_KATHRU_ID + " IN " +
                    " ( SELECT " + MainDbHelper.KEY_KATHRU_ID +
                    " FROM " + MainDbHelper.TABLE_KATHRU +
                    " WHERE " + MainDbHelper.KEY_NAME + " LIKE ? )"; // + "%"+kathruString+"% ) ";
            parameters = new  String[]{query_text_parameter, "%"+kathruString+"%"};
        } else {
            parameters = new  String[]{query_text_parameter};
        }

        return MainActivity.db.query( query_text, parameters);
    }
}
