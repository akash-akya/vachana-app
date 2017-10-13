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
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.Util.EditTextWatcher;
import com.akash.vachana.Util.KannadaTransliteration;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruMini;
import com.google.firebase.crash.FirebaseCrash;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchFragment extends Fragment implements Serializable {

    private static final String TAG = "SearchFragment";
    private KathruListTask kathruListTask;
    private OnSearchFragmentListener mListener;

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
        final AutoCompleteTextView autoCompleteTextView= (AutoCompleteTextView) view.findViewById(R.id.auto_complete_kathru);
        final RadioButton radioPartial = (RadioButton) view.findViewById(R.id.radio_partial);
        final Button resetButton = (Button) view.findViewById(R.id.reset_button);
        final Button searchButton = (Button) view.findViewById(R.id.search_button);

        textSearchView.addTextChangedListener(new EditTextWatcher(textSearchView));
        autoCompleteTextView.addTextChangedListener(new EditTextWatcher(autoCompleteTextView));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                final String query = textSearchView.getText().toString();
                final String kathruString = autoCompleteTextView.getText().toString();
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
                autoCompleteTextView.setText("");
                radioPartial.setChecked(true);
            }
        });

        return view;
    }

    private class KathruListTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isCancelled()){
                return;
            }
        }

        @Override
        protected ArrayList<KathruMini> doInBackground(Object[] objects) {
            return ((MainActivity)getActivity()).getAllKathruMinis();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o != null) {
                ArrayList<KathruMini> kathruMinis = (ArrayList<KathruMini>) o;
                final AutoCompleteTextView autoTextView = (AutoCompleteTextView) getActivity().findViewById(R.id.auto_complete_kathru);
                ArrayAdapter<KathruMini> adapter = new ArrayAdapter<KathruMini>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                        kathruMinis);
                if (autoTextView != null) {
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
            } else {
                FirebaseCrash.log(TAG+"onPostExecute(): Task is cancelled or return value is null");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (kathruListTask == null || kathruListTask.isCancelled()) {
            kathruListTask = new KathruListTask();
            kathruListTask.execute();
        }

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
        try {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("ಹುಡುಕು");
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
