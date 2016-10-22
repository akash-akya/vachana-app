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
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.akash.vachana.activity.ListType;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnVachanaFragmentListListener}
 * interface.
 */
public class VachanaListFragment extends Fragment {

    private static final String TAG = "VachanaListFragment";
    private static final String TITLE = "title";
    private static final String LIST_TYPE = "list_type";
    private static final String KATHRU_MINI = "kathru_mini";
    private static final String QUERY_TEXT = "query_text";
    private static final String KATHRU_TEXT = "kathru_text";
    private static final String IS_PARTIAL_SEARCH = "partial_search";
    private static final String SEARCH_QUERY = "search_query";

    private RecyclerView recyclerView;
    private MyVachanaListRecyclerViewAdapter adapter;
    private String title;
    private ListType listType;
    private OnVachanaFragmentListListener mListener;
    private KathruMini kathruMini;
    private String query_text;
    private String kathruString;
    private boolean isPartial;
    private String mSearchQuery;
    private Menu menu;

    public VachanaListFragment() {
    }

    public static VachanaListFragment newInstance(KathruMini kathruMini, String title, ListType listType) {
        VachanaListFragment fragment = new VachanaListFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putSerializable(LIST_TYPE, listType);
        args.putSerializable(KATHRU_MINI, kathruMini);
        fragment.setArguments(args);
        return fragment;
    }

    public static VachanaListFragment newInstance(String title, String text, String kathruString, boolean isPartial, ListType listType) {
        VachanaListFragment fragment = new VachanaListFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(QUERY_TEXT, text);
        args.putString(KATHRU_TEXT, kathruString);
        args.putBoolean(IS_PARTIAL_SEARCH, isPartial);
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
            if (listType == ListType.SEARCH){
                query_text = getArguments().getString(QUERY_TEXT);
                kathruString = getArguments().getString(KATHRU_TEXT);
                isPartial = getArguments().getBoolean(IS_PARTIAL_SEARCH);
            } else {
                kathruMini = (KathruMini) getArguments().getSerializable(KATHRU_MINI);
            }
        } else {
            Log.e(TAG, "onCreate: No arguments!!!");
        }

        if (savedInstanceState == null) {
            mSearchQuery = "";
        } else {
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_vachana_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller)
                view.findViewById(R.id.vachana_fast_scroller);
        SectionTitleIndicator sectionTitleIndicator = (SectionTitleIndicator)
                view.findViewById(R.id.vachan_fast_scroller_section_indicator);

        if (adapter == null){
            recyclerView.setAdapter(new MyVachanaListRecyclerViewAdapter(new ArrayList<VachanaMini>(), mListener, listType));
        } else {
            recyclerView.setAdapter(adapter);
        }

        fastScroller.setRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(sectionTitleIndicator);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (adapter == null) {
            title = getArguments().getString("title");
            new VachanaListTask(kathruMini).execute();
        }
    }


    private class VachanaListTask extends AsyncTask {

        private final KathruMini kathruMini;
        private ProgressBar progressBar;
        private View noDataTv;
        private View vachanaListContainer;

        public VachanaListTask(KathruMini kathruMini) {
            this.kathruMini = kathruMini;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            vachanaListContainer = getActivity().findViewById(R.id.vachana_list_container);
            noDataTv = getActivity().findViewById(R.id.no_data_vachana);

            progressBar.setVisibility(View.VISIBLE);
            vachanaListContainer.setVisibility(View.INVISIBLE);
            noDataTv.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<VachanaMini> doInBackground(Object[] objects) {
            if (mListener != null){
                if (listType == ListType.SEARCH){
                    return mListener.getVachanaMinis(query_text, kathruString, isPartial);
                } else {
                    return mListener.getVachanaMinis(kathruMini, listType);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o != null){
                ArrayList<VachanaMini> vachanaMinis = (ArrayList<VachanaMini>) o;
                progressBar.setVisibility(View.INVISIBLE);
                if (vachanaMinis.size() > 0 && recyclerView != null && getActivity() != null) {
                    adapter = new MyVachanaListRecyclerViewAdapter(vachanaMinis, mListener, listType);
                    VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller)
                            getActivity().findViewById(R.id.vachana_fast_scroller);
                    SectionTitleIndicator sectionTitleIndicator = (SectionTitleIndicator)
                            getActivity().findViewById(R.id.vachan_fast_scroller_section_indicator);

                    recyclerView.setAdapter(adapter);
                    fastScroller.setRecyclerView(recyclerView);
                    recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                    vachanaListContainer.setVisibility(View.VISIBLE);
                } else {
                    noDataTv.setVisibility(View.VISIBLE);
                }

                if (getActivity() != null) {
                    if (menu != null && listType == ListType.NORMAL_LIST){
                        updateActionBarFavorite(menu.findItem(R.id.kathru_favorite), kathruMini.getFavorite()==1);
                    }
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.vachana_list_menu, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        if (listType == ListType.NORMAL_LIST) {
            menu.findItem(R.id.action_kathru_favorite).setVisible(true);
            menu.findItem(R.id.action_kathru_detail).setVisible(true);
            updateActionBarFavorite(menu.findItem(R.id.action_kathru_favorite),
                    kathruMini.getFavorite()==1);
        } else {
            menu.findItem(R.id.action_kathru_favorite).setVisible(false);
            menu.findItem(R.id.action_kathru_detail).setVisible(false);
        }

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
                    mSearchQuery = newText;
                    return true;
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
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_kathru_favorite:
                boolean new_state = !(kathruMini.getFavorite() == 1);
                kathruMini.setFavorite(new_state);
                updateActionBarFavorite(item, new_state);
                EventBus.getDefault().post(kathruMini);
                return true;

            case R.id.action_kathru_detail:
                FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
                KathruDetailsFragment fragment = KathruDetailsFragment.newInstance(kathruMini.getId(),
                        kathruMini.getName());

                fragmentManager.popBackStack("kathru_details", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment, "kathru_details")
                        .addToBackStack( "kathru_details" )
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void doThis(KathruMini kathruMini){
        if (listType == ListType.NORMAL_LIST && kathruMini.getId() == this.kathruMini.getId()){
            updateActionBarFavorite(menu.findItem(R.id.kathru_favorite), kathruMini.getFavorite()==1);
        }
    }

    private void updateActionBarFavorite(MenuItem item, boolean state) {
        if (item != null) {
            item.setIcon(state? R.drawable.ic_star_20dp : R.drawable.ic_star_border_white_24dp);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        if (context instanceof OnVachanaFragmentListListener) {
            mListener = (OnVachanaFragmentListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public interface OnVachanaFragmentListListener extends Serializable {
        void OnVachanaListItemClick(ArrayList<VachanaMini> vachanaMinis, int position);
        ArrayList<VachanaMini> getVachanaMinis(KathruMini kathruMini, ListType listType);
        ArrayList<VachanaMini> getVachanaMinis(String text, String kathruString, boolean isPartialSearch);
    }
}
