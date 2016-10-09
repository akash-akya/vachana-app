package com.akash.vachana.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.VachanaListFragment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by akash on 7/10/16.
 */
public class VachanaKathruViewListener implements VachanaListFragment.OnListFragmentInteractionListener, Serializable {
    private final int kathruMiniId;
    final transient Context contetx;
    final transient FragmentManager fragmentManager;

    public VachanaKathruViewListener (int kathruMiniId, Context context) {
        this.kathruMiniId = kathruMiniId;
        this.contetx = context;
        fragmentManager =  ((FragmentActivity) context).getSupportFragmentManager();
    }

    @Override
    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {

        final Fragment fragment = Fragment.instantiate(contetx, MainActivity.fragments[0]);

        Intent intent = ((Activity)contetx).getIntent();
        intent.putExtra("vachanas", vachanaMinis);
        intent.putExtra("current_position", position);
        fragment.setArguments(intent.getExtras());

        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .add(R.id.main_content, fragment)
                .addToBackStack("vachana_list")
                .commit();
    }

    @Override
    public void onFavoriteButton(int vachanaId, boolean checked) {
        new MainActivity.UpdateVachanaFavorite().execute(vachanaId, checked);
    }

    @Override
    public ArrayList<VachanaMini> getVachanaMinis() {
        return MainActivity.db.getVachanaMinisByKathruId(kathruMiniId);
    }
}