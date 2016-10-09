package com.akash.vachana.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.VachanaListFragment;

import java.util.ArrayList;

/**
 * Created by akash on 9/10/16.
 */
public abstract class VachanaListListenerAbstract implements VachanaListFragment.OnListFragmentInteractionListener {

    private FragmentActivity activity;

    public VachanaListListenerAbstract(FragmentActivity activity){
        this.activity = activity;
    }

    @Override
    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = Fragment.instantiate(activity, MainActivity.fragments[0]);

        Bundle bundle = new Bundle();
        bundle.putSerializable("vachanas", vachanaMinis);
        bundle.putSerializable("current_position", position);

//        fragment.setArguments(getBundle(vachanaMinis, position));
        fragment.setArguments(bundle);

        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment, "vachana_list")
                .addToBackStack("vachana_list")
                .commit();
    }

//    protected abstract Bundle getBundle(ArrayList<VachanaMini> vachanaMinis, int position);

    @Override
    public void onFavoriteButton(int vachanaId, boolean checked) {
        new UpdateVachanaFavorite().execute(vachanaId, checked, activity);
    }
}
