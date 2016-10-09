package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.AbsRecyclerViewFastScroller;
import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class KathruListFragment extends Fragment {

    private static final String TAG = "KathruListFragment";
    private OnKathruListFragmentInteractionListener mListener;
    private MyKathruListRecyclerViewAdapter myAdapter;
    private RecyclerView recyclerView;
    private AbsRecyclerViewFastScroller fastScroller;
    private SectionTitleIndicator sectionTitleIndicator;
    private String title;
    public boolean needToUpdate = false;

    public KathruListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

//        mListener = (OnKathruListFragmentInteractionListener) getArguments().getSerializable("listener");

        if (myAdapter == null){
            title = getArguments().getString("title");
            mListener = (OnKathruListFragmentInteractionListener) getArguments().getSerializable("listener");
            new KathruListTask().execute();
        } else {
            recyclerView.setAdapter(myAdapter);
            fastScroller.setRecyclerView(recyclerView);
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
            fastScroller.setSectionIndicator(sectionTitleIndicator);
            recyclerView.setVisibility(View.VISIBLE);
            fastScroller.setVisibility(View.VISIBLE);
            sectionTitleIndicator.setVisibility(View.VISIBLE);
        }

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        try {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(title);
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view =  inflater.inflate(R.layout.fragment_kathru_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.kathru_recycler_view);
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(sectionTitleIndicator);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(needToUpdate){
            new KathruListTask().execute();
            needToUpdate = false;
        }
    }

    private class KathruListTask extends AsyncTask {

        private ProgressBar progressBar;
        private View noDataTv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) getActivity().findViewById(R.id.kathru_list_progressBar);
            recyclerView = (RecyclerView) getActivity().findViewById(R.id.kathru_recycler_view);
            fastScroller = (VerticalRecyclerViewFastScroller) getActivity().findViewById(R.id.fast_scroller);
            sectionTitleIndicator = (SectionTitleIndicator) getActivity().findViewById(R.id.fast_scroller_section_title_indicator);
            noDataTv = getActivity().findViewById(R.id.no_data_kathru);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            fastScroller.setVisibility(View.INVISIBLE);
            sectionTitleIndicator.setVisibility(View.INVISIBLE);
            noDataTv.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<KathruMini> doInBackground(Object[] objects) {
            return mListener.getKathruMinis();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ArrayList<KathruMini> kathruMinis = (ArrayList<KathruMini>) o;
            if (progressBar != null && recyclerView != null) {
                progressBar.setVisibility(View.INVISIBLE);

                if (kathruMinis.size() > 0){
                    myAdapter = new MyKathruListRecyclerViewAdapter(kathruMinis, mListener);
                    recyclerView.setAdapter(myAdapter);
                    fastScroller.setRecyclerView(recyclerView);
                    recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                    recyclerView.setVisibility(View.VISIBLE);
                    fastScroller.setVisibility(View.VISIBLE);
                    sectionTitleIndicator.setVisibility(View.VISIBLE);
                } else {
                    noDataTv.findViewById(R.id.no_data_kathru).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        //***setOnQueryTextListener***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (myAdapter != null && recyclerView != null) {
                    myAdapter.filter(newText.trim());
                    recyclerView.invalidate();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnKathruListFragmentInteractionListener extends Serializable {
        void onListFragmentInteraction(KathruMini item);
        void onFavoriteButton(int kathruId, boolean checked);
        ArrayList<KathruMini> getKathruMinis();
    }
}
