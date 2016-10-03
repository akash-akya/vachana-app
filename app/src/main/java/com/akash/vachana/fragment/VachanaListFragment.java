package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
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


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class VachanaListFragment extends Fragment {

    private static final String TAG = "VachanaListFragment";
    private String title =null;
    private ArrayList<VachanaMini> vachanaMinis = null;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener listener;
    private MyVachanaListRecyclerViewAdapter adapter;

    public VachanaListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getContext();
        title = getArguments().getString("title");
        listener = (OnListFragmentInteractionListener) getArguments().getSerializable("listener");
    }

    private class VachanaListTask extends AsyncTask {

        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<VachanaMini> doInBackground(Object[] objects) {
            return listener.getVachanaMinis();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            vachanaMinis = (ArrayList<VachanaMini>) o;
            if (progressBar != null && recyclerView != null) {
                adapter = new MyVachanaListRecyclerViewAdapter(vachanaMinis, listener);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mainActivity.getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
        if (vachanaMinis == null){
            progressBar.setVisibility(View.VISIBLE);
            new VachanaListTask().execute(getArguments().getInt("id"));
        } else {
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            if (title != null)
                mainActivity.getSupportActionBar().setTitle(title);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
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
