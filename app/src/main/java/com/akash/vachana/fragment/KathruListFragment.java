/*
 * vachana. An application for Android users, it contains kannada vachanas
 * Copyright (c) 2016. akash
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
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
import com.akash.vachana.util.KannadaTransliteration;
import com.akash.vachana.activity.ListType;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruMini;
import com.google.firebase.crash.FirebaseCrash;

import java.io.Serializable;
import java.util.ArrayList;

public class KathruListFragment extends Fragment {
    private static final String TAG = KathruListFragment.class.getSimpleName();
    private static final String TITLE = "title";
    private static final String LIST_TYPE = "list_type";
    private static final String SEARCH_QUERY = "search_query";
    private OnKathruListFragmentListener mListener;
    private MyKathruListRecyclerViewAdapter myAdapter;
    private RecyclerView mRecyclerView;
    private String title;
    private ListType listType;
    private String mSearchQuery;
    private KathruListTask katruRetreiveTask;

    public KathruListFragment() { }

    public static KathruListFragment newInstance(String title, ListType listType) {
        KathruListFragment  fragment = new KathruListFragment ();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putSerializable(LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            listType = (ListType) getArguments().getSerializable(LIST_TYPE);
        } else {
            throw new NullPointerException(TAG+" onCreate(): getArguments() is null!");
        }

        mSearchQuery = "";
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view =  inflater.inflate(R.layout.fragment_kathru_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.kathru_recycler_view);
        if (myAdapter != null){
            mRecyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (myAdapter == null){
            title = getArguments().getString("title");
            katruRetreiveTask = new KathruListTask();
            katruRetreiveTask.execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        try {
            ActionBar actionbar = ((MainActivity) getActivity()).getSupportActionBar();
            actionbar.setTitle(title);
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
        }
    }

    private class KathruListTask extends AsyncTask {

        private ProgressBar progressBar;
        private View noDataTv;
        private View kathruListContainer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isCancelled()){
                return;
            }

            progressBar = (ProgressBar) getActivity().findViewById(R.id.kathru_list_progressBar);
            kathruListContainer = getActivity().findViewById(R.id.kathru_list_container);
            noDataTv = getActivity().findViewById(R.id.no_data_kathru);

            try {
                progressBar.setVisibility(View.VISIBLE);
                kathruListContainer.setVisibility(View.INVISIBLE);
                noDataTv.setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                FirebaseCrash.log(TAG+".onPreExecute(): Display elements is null");
                cancel(true);
            }
        }

        @Override
        protected ArrayList<KathruMini> doInBackground(Object[] objects) {
            if (null != mListener) {
                return mListener.getKathruMinis(listType);
            } else {
                return new ArrayList<KathruMini>();
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ArrayList<KathruMini> kathruMinis = (ArrayList<KathruMini>) o;
            progressBar.setVisibility(View.INVISIBLE);

            if (kathruMinis.size() > 0){
                myAdapter = new MyKathruListRecyclerViewAdapter(kathruMinis, mListener, listType);
                mRecyclerView.setAdapter(myAdapter);

                kathruListContainer.setVisibility(View.VISIBLE);
            } else {
                noDataTv.findViewById(R.id.no_data_kathru).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        //***setOnQueryTextListener***
        searchView.setQueryHint("ಹೆಸರು ಅಥವಾ ಅಂಕಿತನಾಮ");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = KannadaTransliteration.getUnicodeString(newText);
                if (!newText.equals(text)){
                    searchView.setQuery(text, false);
                } else {
                    if (myAdapter != null && mRecyclerView != null) {
                        myAdapter.filter(text.trim());
                        mRecyclerView.invalidate();
                        mSearchQuery = text;
                        return true;
                    }
                }
                return false;
            }
        });

        if (mSearchQuery != null && !mSearchQuery.isEmpty()) {
            searchMenuItem.expandActionView();
            searchView.setQuery(mSearchQuery, true);
            searchView.setIconified(false);
            searchView.clearFocus();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onPause() {
        katruRetreiveTask.cancel(true);
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKathruListFragmentListener) {
            mListener = (OnKathruListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnKathruListFragmentListener extends Serializable {
        void onKathruListItemClick(KathruMini item);
        ArrayList<KathruMini> getKathruMinis(ListType listType);
    }
}
