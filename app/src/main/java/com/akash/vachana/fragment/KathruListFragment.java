package com.akash.vachana.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruMini;

import java.io.Serializable;
import java.util.ArrayList;

public class KathruListFragment extends Fragment {

    private static final String TAG = "KathruListFragment";
    private OnKathruListFragmentInteractionListener mListener;

    public KathruListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        try {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("ವಚನಕಾರರು");
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kathru_list, container, false);
        mListener = (KathruListFragment.OnKathruListFragmentInteractionListener) view.getContext();

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            ArrayList<KathruMini> kathruMinis = ((MainActivity) getActivity()).db.getAllKathruMinis();
            recyclerView.setAdapter(new MyKathruListRecyclerViewAdapter(kathruMinis, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKathruListFragmentInteractionListener ) {
            mListener = (OnKathruListFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnKathruListFragmentInteractionListener extends Serializable{
        // TODO: Update argument type and name
        void onListFragmentInteraction(KathruMini item);
    }
}
