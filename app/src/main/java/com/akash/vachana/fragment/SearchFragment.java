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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.akash.vachana.R;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.DatabaseReadAccess;
import com.akash.vachana.dbUtil.DbAccessTask;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.util.EditTextWatcher;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment implements Serializable {

    private static final String TAG = "SearchFragment";
    private KathruListTask kathruListTask;
    private OnSearchFragmentListener mListener;
    @BindView(R.id.auto_complete_kathru) AutoCompleteTextView autoTextView;
    @BindView(R.id.search_bar_text) EditText textSearchView;
    @BindView(R.id.radio_partial) RadioButton radioPartial;
    @BindView(R.id.reset_button) Button resetButton;
    @BindView(R.id.search_button) Button searchButton;

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
        ButterKnife.bind(this, view);

        textSearchView.addTextChangedListener(new EditTextWatcher(textSearchView));
        autoTextView.addTextChangedListener(new EditTextWatcher(autoTextView));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                final String query = textSearchView.getText().toString();
                final String kathruString = autoTextView.getText().toString();
                final Boolean isPartialSearch = radioPartial.isChecked();

                if (query.length() <= 1)
                    return;
                mListener.onSearchButtonClick(query, kathruString, isPartialSearch);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSearchView.setText("");
                autoTextView.setText("");
                radioPartial.setChecked(true);
            }
        });

        return view;
    }

    DbAccessTask.OnCompletion<List<KathruMini>> onCompletion = new DbAccessTask.OnCompletion<List<KathruMini>>() {
        @Override
        public void updateUI(List<KathruMini> kathruMinis) {
            ArrayAdapter<KathruMini> adapter = new ArrayAdapter<KathruMini>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                    kathruMinis);
            autoTextView.setAdapter(adapter);
            autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    KathruMini selected = (KathruMini) arg0.getAdapter().getItem(arg2);
                    autoTextView.setText(selected.getName());
                }
            });
        }
    };

    private static class KathruListTask extends DbAccessTask<Void, List<KathruMini>> {
        KathruListTask(OnCompletion<List<KathruMini>> onCompletion) {
            super(onCompletion);
        }

        @Override
        protected List<KathruMini> doInBackground(Void... voids) {
            DatabaseReadAccess db = MainActivity.getDatabaseReadAccess();
            return db.getAllKathruMinis();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        kathruListTask = new KathruListTask(onCompletion);
        kathruListTask.execute();
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
    public void onStop() {
        super.onStop();
        kathruListTask.cancel(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFragmentListener) {
            mListener = (OnSearchFragmentListener) context;
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

    public interface OnSearchFragmentListener {
        void onSearchButtonClick(String text, String kathru, boolean isPartial);
    }
}
