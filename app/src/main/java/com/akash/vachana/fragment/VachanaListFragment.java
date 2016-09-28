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
import java.io.Serializable;
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

//    private Kathru currentKathru;
    private String title = null;
    private ArrayList<VachanaMini> vachanaMinis = null;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener listener;

    public VachanaListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getContext();
        title = getArguments().getString("title");
        listener = (OnListFragmentInteractionListener) getArguments().getSerializable("listener");
    }

    private class VachanaListTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<VachanaMini> doInBackground(Object[] objects) {
            return listener.getVachanaMinis();
//            return ((MainActivity)getActivity()).getVachanaMinis((int) objects[0]);
//            Kathru kathru = mainActivity.db.getKathruById((int) objects[0]);
//            vachanaMinis = mainActivity.db.getVachanaMinisByKathruId(kathru.getId());
//            return kathru;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            vachanaMinis = (ArrayList<VachanaMini>) o;
//            Kathru kathru = (Kathru) o;
//            currentKathru = kathru;
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.vachana_list_progressBar);
            if (progressBar != null && recyclerView != null) {
                recyclerView.setAdapter(new MyVachanaListRecyclerViewAdapter(vachanaMinis,
                        listener));
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
            recyclerView.setAdapter(new MyVachanaListRecyclerViewAdapter(vachanaMinis,
                    listener));
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            if (title != null)
                mainActivity.getSupportActionBar().setTitle(title);
        }

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
