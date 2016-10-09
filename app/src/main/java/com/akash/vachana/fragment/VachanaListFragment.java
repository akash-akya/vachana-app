package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class VachanaListFragment extends Fragment {

    private static final String TAG = "VachanaListFragment";
    private String title =null;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener listener;
    private MyVachanaListRecyclerViewAdapter adapter;
    private VerticalRecyclerViewFastScroller fastScroller;
    private SectionTitleIndicator sectionTitleIndicator;
    public boolean needToUpdate = false;

    public VachanaListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mainActivity = (MainActivity) getContext();
        title = getArguments().getString("title");
        listener = (OnListFragmentInteractionListener) getArguments().getSerializable("listener");
    }

    private class VachanaListTask extends AsyncTask {

        private ProgressBar progressBar;
        private View noDataTv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);
            fastScroller = (VerticalRecyclerViewFastScroller) getActivity().findViewById(R.id.vachana_fast_scroller);
            sectionTitleIndicator = (SectionTitleIndicator) getActivity().findViewById(R.id.vachan_fast_scroller_section_indicator);
            noDataTv = getActivity().findViewById(R.id.no_data_vachana);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            fastScroller.setVisibility(View.INVISIBLE);
            noDataTv.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<VachanaMini> doInBackground(Object[] objects) {
            return listener.getVachanaMinis();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ArrayList<VachanaMini> vachanaMinis = (ArrayList<VachanaMini>) o;
            if (progressBar != null &&   recyclerView != null) {
                progressBar.setVisibility(View.INVISIBLE);
                if (vachanaMinis.size() > 0) {
                    adapter = new MyVachanaListRecyclerViewAdapter(vachanaMinis, listener);
//                    adapter.addVachanas(vachanaMinis);
//                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    fastScroller.setRecyclerView(recyclerView);
                    recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                    recyclerView.setVisibility(View.VISIBLE);
                    fastScroller.setVisibility(View.VISIBLE);
                    sectionTitleIndicator.setVisibility(View.VISIBLE);
                } else {
                    noDataTv.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null)
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter == null){
            title = getArguments().getString("title");
            listener = (OnListFragmentInteractionListener) getArguments().getSerializable("listener");
            new VachanaListTask().execute();
            Log.d(TAG, "onResume: This is called");
        } else {
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            fastScroller.setRecyclerView(recyclerView);
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
            fastScroller.setSectionIndicator(sectionTitleIndicator);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            fastScroller.setVisibility(View.VISIBLE);
            sectionTitleIndicator.setVisibility(View.VISIBLE);
            if (getActivity() != null && title != null)
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
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
                if (adapter != null && recyclerView != null) {
                    adapter.filter(newText);
                    recyclerView.invalidate();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_vachana_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
//        adapter = new MyVachanaListRecyclerViewAdapter(new ArrayList<VachanaMini>(), listener);
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.vachana_fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.vachan_fast_scroller_section_indicator);
        recyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(sectionTitleIndicator);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(needToUpdate){
            new VachanaListTask().execute();
            Log.d(TAG, "onActivityCreated: needToReload");
            needToUpdate = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnListFragmentInteractionListener extends Serializable {
        void onListFragmentInteraction(ArrayList<VachanaMini> item, int position);
        void onFavoriteButton(int vachanaId, boolean checked);
        ArrayList<VachanaMini> getVachanaMinis();
    }
}
