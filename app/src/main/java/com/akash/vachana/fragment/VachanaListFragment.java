package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.akash.vachana.R;
import com.akash.vachana.ListViewHelper.VachanaList;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.Kathru;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.util.FileHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class VachanaListFragment extends Fragment {

    private static final String TAG = "VachanaListFragment";

    private Kathru currentKathru;
    private ArrayList<VachanaMini> vachanaMinis = null;
    private MainActivity mainActivity;
    private int kathruId = 0;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public VachanaListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getContext();
        kathruId = 0;
    }

    private class VachanaListTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Object[] objects) {
            currentKathru = mainActivity.db.getKathruById((int)objects[0]);
            vachanaMinis = mainActivity.db.getVachanaMinisByKathruId(currentKathru.getId());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);

            if (progressBar != null && recyclerView != null) {
                recyclerView.setAdapter(new MyVachanaListRecyclerViewAdapter(vachanaMinis,
                        (OnListFragmentInteractionListener) getActivity()));
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mainActivity.getSupportActionBar().setTitle(currentKathru.getName());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (kathruId != getArguments().getInt("id")){
            kathruId = getArguments().getInt("id");
            new VachanaListTask().execute(kathruId);
        } else {
            if (progressBar == null)
                progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            recyclerView.setAdapter(new MyVachanaListRecyclerViewAdapter(vachanaMinis,
                    (OnListFragmentInteractionListener) getActivity()));
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            mainActivity.getSupportActionBar().setTitle(currentKathru.getName());
        }

        Log.d(TAG, "onResume: ");
        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vachana_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.vachana_list_progressBar);
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

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ArrayList<VachanaMini> item, int position);
        void onFavoriteButton(int vachanaId, boolean checked);
    }
}
