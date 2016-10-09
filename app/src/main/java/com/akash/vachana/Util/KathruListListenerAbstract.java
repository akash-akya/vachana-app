package com.akash.vachana.Util;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.KathruListFragment;

import java.util.ArrayList;

/**
 * Created by akash on 9/10/16.
 */
public abstract class KathruListListenerAbstract implements KathruListFragment.OnKathruListFragmentInteractionListener {
    FragmentActivity activity;

    public KathruListListenerAbstract(FragmentActivity activity){
        this.activity = activity;
    }

    @Override
    public void onListFragmentInteraction(KathruMini item) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(activity, MainActivity.fragments[1]);

        fragment.setArguments(getBundle(item));
        fragmentManager.popBackStack("kathru_list_vertical", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment, "kathru_list_vertical")
                .addToBackStack("kathru_list_vertical")
                .commit();
    }

    protected abstract Bundle getBundle(KathruMini kathruMini);

    @Override
    public void onFavoriteButton(int kathruId, boolean checked) {
        new UpdateKathruFavorite().execute(kathruId, checked, activity);
    }
}
