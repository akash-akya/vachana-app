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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SearchView;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchFragment extends Fragment implements Serializable{

    private static final String TAG = "SearchFragment";

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
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        final EditText textSearchView = (EditText) view.findViewById(R.id.search_bar_text);
        final EditText kathruSearchView = (EditText) view.findViewById(R.id.search_bar_kathru);
        final RadioButton radioPartial = (RadioButton) view.findViewById(R.id.radio_partial);
        final Button resetButton = (Button) view.findViewById(R.id.reset_button);
        final Button searchButton = (Button) view.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                final MainActivity mainActivity = (MainActivity) getActivity();
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                Fragment fragment = Fragment.instantiate(mainActivity, MainActivity.fragments[1]);
                final String query = textSearchView.getText().toString();
                final String kathruString = kathruSearchView.getText().toString();
                final Boolean isPartialSearch = radioPartial.isChecked();

                if (query.length() < 3)
                    return;

                Log.d("Search Fragment", "onClick: "+query+" "+kathruString);
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

                        fragmentManager.popBackStack("search_vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content, fragment)
                                .addToBackStack( "search_vachana_list" )
                                .commit();
                    }

                    @Override
                    public void onFavoriteButton(int vachanaId, boolean checked) {
                        new UpdateVachanaFavorite().execute(vachanaId, checked);
                    }

                    @Override
                    public ArrayList<VachanaMini> getVachanaMinis() {
                        String query_text = "SELECT " +
                                MainDbHelper.KEY_VACHANA_ID + ", "+
                                MainDbHelper.KEY_TITLE + ", "+
                                MainDbHelper.FOREIGN_KEY_KATHRU_ID + ", "+
                                MainDbHelper.KEY_FAVORITE;
                        String[] parameters;

                        query_text += " FROM " + MainDbHelper.TABLE_VACHANA;

                        query_text += " WHERE " +
                                MainDbHelper.KEY_TITLE + " LIKE ? "; // + "%"+query+"%";

                        String query_text_parameter = isPartialSearch? "%"+query+"%" : query;

                        if (!kathruString.isEmpty()) {
                            query_text += " AND " +
                                    MainDbHelper.FOREIGN_KEY_KATHRU_ID + " IN " +
                                    " ( SELECT " + MainDbHelper.KEY_KATHRU_ID +
                                    " FROM " + MainDbHelper.TABLE_KATHRU +
                                    " WHERE " + MainDbHelper.KEY_NAME + " LIKE ? "; // + "%"+kathruString+"% ) ";
                            parameters = new  String[]{query_text_parameter, "%"+kathruString+"%"};
                        } else {
                            parameters = new  String[]{query_text_parameter};
                        }

                        return mainActivity.db.query( query_text, parameters);
                    }
                });

                fragment.setArguments(bundle);
                fragmentManager.popBackStack("search_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack( "search_fragment")
                        .commit();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSearchView.setText("");
                kathruSearchView.setText("");
                radioPartial.setChecked(true);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.simple_menu, menu);
    }

    public class UpdateVachanaFavorite extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            if ((boolean)objects[1])
                ((MainActivity) getActivity()).db.addVachanaToFavorite((int)objects[0]);
            else
                ((MainActivity) getActivity()).db.removeVachanaFromFavorite((int)objects[0]);
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
        try {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("ಹುಡುಕು");
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
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
}
