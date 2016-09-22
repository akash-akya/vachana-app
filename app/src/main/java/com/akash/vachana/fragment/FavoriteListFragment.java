package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.VachanaMini;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FavoriteListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "VachanaListFragment";

    private ArrayList<VachanaMini> vachanaMinis = null;
    private MainActivity mainActivity;

    public FavoriteListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getContext();
    }

    private class VachanaFavoriteListTask extends AsyncTask {
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Object[] objects) {
            vachanaMinis = mainActivity.db.getFavoriteVachanaMinis();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            recyclerView.setAdapter(new MyFavoriteVachanaListAdapter(vachanaMinis,
                    (OnListFavoriteInteractionListener) getActivity()));
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (vachanaMinis == null)
            new VachanaFavoriteListTask().execute();
        else
            new VachanaFavoriteListTask().onPostExecute(null);

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        try {
            mainActivity.getSupportActionBar().setTitle("ನೆಚ್ಚಿನ ವಚನಗಳು");
        } catch (NullPointerException e){
            Log.d(  TAG, "onCreate: Actionbar not found");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vachana_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
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

    public interface OnListFavoriteInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ArrayList<VachanaMini> item, int position);
        void onFavoriteButton(int vachanaId, boolean checked);
    }
}
