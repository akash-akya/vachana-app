package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
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
import com.akash.vachana.dbUtil.VachanaMini;

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
        recyclerView.setAdapter(adapter);
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
            if (listType == ListType.SEARCH){
                return mListener.getVachanaMinis(query_text, kathruString, isPartial);
            } else {
                return mListener.getVachanaMinis(kathruMini, listType);
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
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

            if (getActivity() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
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
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }

    public interface OnVachanaFragmentListListener extends Serializable {
        void OnVachanaListItemClick(ArrayList<VachanaMini> vachanaMinis, int position);
        ArrayList<VachanaMini> getVachanaMinis(KathruMini kathruMini, ListType listType);
        ArrayList<VachanaMini> getVachanaMinis(String text, String kathruString, boolean isPartialSearch);
    }
}
