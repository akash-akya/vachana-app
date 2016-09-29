package com.akash.vachana.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchFragment extends Fragment implements Serializable{

    private OnSearchFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        final SearchView searchView = (SearchView) view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                final MainActivity mainActivity = (MainActivity) getActivity();
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                Fragment fragment = Fragment.instantiate(mainActivity, MainActivity.fragments[1]);
                bundle.putString("title", "ಹುಡುಕು");
                bundle.putSerializable("listener", new VachanaListFragment.OnListFragmentInteractionListener() {
                    public static final String TAG = "VachanaListFragment" ;

                    @Override
                    public void onListFragmentInteraction(ArrayList<VachanaMini> vachanaMinis, int position) {
                        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                        Fragment fragment = Fragment.instantiate(mainActivity, MainActivity.fragments[0]);

                        mainActivity.getIntent().putExtra("vachanas", vachanaMinis);
                        mainActivity.getIntent().putExtra("current_position", position);
                        fragment.setArguments(mainActivity.getIntent().getExtras());

                        fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content, fragment)
                                .addToBackStack( "vachana_list" )
                                .commit();
                    }

                    @Override
                    public void onFavoriteButton(int vachanaId, boolean checked) {
                        new UpdateVachanaFavorite().execute(vachanaId, checked);
                    }

                    @Override
                    public ArrayList<VachanaMini> getVachanaMinis() {
                        return ((MainActivity) getActivity()).db.query(
                                new String[] {MainDbHelper.KEY_VACHANA_ID,
                                        MainDbHelper.KEY_TITLE,
                                        MainDbHelper.FOREIGN_KEY_KATHRU_ID,
                                        MainDbHelper.KEY_FAVORITE},
                                MainDbHelper.KEY_TEXT + " LIKE ? ",
                                new String[] { "%"+String.valueOf(searchView.getQuery())+"%" }
                        );
                    }
                });

                fragment.setArguments(bundle);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack( "kathru_list" )
                        .commit();
                return true;
            }
        });

        return view;
    }

    public class UpdateVachanaFavorite extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            if ((boolean)objects[1])
                ((MainActivity) getActivity()).db.addVachanaToFavorite((int)objects[0]);
            else
                ((MainActivity) getActivity()).db.removeVachanaToFavorite((int)objects[0]);
            return null;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("ಹುಡುಕು");
        if (context instanceof OnSearchFragmentInteractionListener) {
            mListener = (OnSearchFragmentInteractionListener) context;
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

    public interface OnSearchFragmentInteractionListener extends Serializable{
        void onFragmentInteraction(Uri uri);
    }
}
