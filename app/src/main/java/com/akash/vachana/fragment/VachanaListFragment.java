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
import android.os.Bundle;
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
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.DatabaseReadAccess;
import com.akash.vachana.dbUtil.DbAccessTask;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.util.KannadaTransliteration;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private VachanaListTask vachanaListTask;

    @BindView(R.id.vachana_list_progressBar) ProgressBar progressBar;
    @BindView(R.id.vachana_list_container) View vachanaListContainer;
    @BindView(R.id.no_data_vachana) View noDataTv;

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
        ButterKnife.bind(this, view);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        if (adapter == null){
            recyclerView.setAdapter(new MyVachanaListRecyclerViewAdapter(new ArrayList<VachanaMini>(), mListener, listType));
        } else {
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter == null) {
            title = getArguments().getString("title");

            try {
                progressBar.setVisibility(View.VISIBLE);
                vachanaListContainer.setVisibility(View.INVISIBLE);
                noDataTv.setVisibility(View.INVISIBLE);
                vachanaListTask =  new VachanaListTask(onCompletion, listType,
                        query_text, kathruString, isPartial);
                vachanaListTask.execute(kathruMini);
            } catch (NullPointerException e){
                FirebaseCrash.log(TAG+" VachanaListTask.onPreExecute(): display elements are null.");
            }

        }
    }

    DbAccessTask.OnCompletion<List<VachanaMini>> onCompletion = new DbAccessTask.OnCompletion<List<VachanaMini>>() {
        @Override
        public void updateUI(List<VachanaMini> vachanaMinis) {
            progressBar.setVisibility(View.INVISIBLE);
            if (vachanaMinis.size() > 0 && recyclerView != null && getActivity() != null) {
                adapter = new MyVachanaListRecyclerViewAdapter(vachanaMinis, mListener, listType);
                recyclerView.setAdapter(adapter);
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
    };


    private static class VachanaListTask extends DbAccessTask<KathruMini, List<VachanaMini>>{
        private final ListType listType;
        String queryString;
        String KathruName;
        boolean isPartial;

        VachanaListTask(OnCompletion<List<VachanaMini>> onCompletion,
                        ListType listType, String queryString, String KathruName, boolean isPartial) {
            super(onCompletion);
            this.listType = listType;
            this.queryString = queryString;
            this.KathruName = KathruName;
            this.isPartial = isPartial;
        }

        @Override
        protected List<VachanaMini> doInBackground(KathruMini... kathruMinis) {
            DatabaseReadAccess db = MainActivity.getDatabaseReadAccess();
            List<VachanaMini> vachanaMinis = new ArrayList<>();
            switch (listType){
                case SEARCH:
                    vachanaMinis = db.searchForVachana(queryString, KathruName, isPartial);
                    break;
                case NORMAL_LIST:
                    vachanaMinis = db.getVachanaMinisByKathruId(kathruMinis[0].getId());
                    break;
                case FAVORITE_LIST:
                    vachanaMinis = db.getFavoriteVachanaMinis();
                    break;
                default:
                    Log.d(TAG, "getVachanaMinis: Wrong listType");
            }
            return vachanaMinis;
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
    public void onStop() {
        super.onStop();
        vachanaListTask.cancel(true);
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        if (listType == ListType.NORMAL_LIST) {
            menu.findItem(R.id.action_kathru_favorite).setVisible(true);
            menu.findItem(R.id.action_kathru_detail).setVisible(true);
            updateActionBarFavorite(menu.findItem(R.id.action_kathru_favorite),
                    kathruMini.getFavorite()==1);
        } else {
            menu.findItem(R.id.action_kathru_favorite).setVisible(false);
            menu.findItem(R.id.action_kathru_detail).setVisible(false);
        }

        searchView.setQueryHint("ಮೊದಲ ಸಾಲಿನ ಪದ");
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
                    if (adapter != null && recyclerView != null) {
                        adapter.filter(newText);
                        recyclerView.invalidate();
                        mSearchQuery = newText;
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
        void onVachanaListItemClick(ArrayList<VachanaMini> vachanaMinis, int position);
    }
}
