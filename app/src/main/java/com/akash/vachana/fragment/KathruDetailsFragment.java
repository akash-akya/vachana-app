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
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.Util.HtmlBuilder;
import com.akash.vachana.activity.ListType;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruDetails;
import com.google.firebase.crash.FirebaseCrash;

import java.io.Serializable;

public class KathruDetailsFragment extends Fragment {
    private static final String KATHRU_ID = "kathru_id";
    private static final String TITLE = "title";
    private static final String TAG = "KathruDetailsFragment";
    private OnKathruDetailsInteractionListener mListener;
    private String title;
    private int  kathru_id;
    private GetKathruDetailsTask kathruDetailsTask;

    public KathruDetailsFragment() {
        // Required empty public constructor
    }

    public static KathruDetailsFragment newInstance(int kathruId, String title) {
        KathruDetailsFragment fragment = new KathruDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(KATHRU_ID, kathruId);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            kathru_id = getArguments().getInt(KATHRU_ID);
        } else {
            Log.e(TAG, "onCreate: No arguments!!!");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kathru_details, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        try {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(title);
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        kathruDetailsTask = new GetKathruDetailsTask(kathru_id);
        kathruDetailsTask.execute();
    }

    private class GetKathruDetailsTask extends AsyncTask{
        private TextView kathruDetailsTextView;
        private Button vachanaLinkButton;
        private final int kathruId ;

        private GetKathruDetailsTask(int kathruId) {
            this.kathruId = kathruId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                if (!isCancelled()){
                    kathruDetailsTextView = (TextView) getActivity().findViewById(R.id.tv_kathru_details);
                    vachanaLinkButton = (Button) getActivity().findViewById(R.id.btn_vachanas_link);
                    vachanaLinkButton.setVisibility(View.INVISIBLE);
                }
            } catch (NullPointerException ex){
                FirebaseCrash.log(TAG+"onPreExecute: Display elements are null.");
                cancel(true);
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            return mListener.getKathruDetails(kathruId);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            final KathruDetails kathruDetails = (KathruDetails)o;
            kathruDetailsTextView.setText(getDetailsInFormat(kathruDetails));
            vachanaLinkButton.setText("ವಚನಗಳನ್ನು ತೆರೆಯಿರಿ");
            vachanaLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onVachanaButtonClick(kathruDetails.getId());
                }
            });
            vachanaLinkButton.setVisibility(View.VISIBLE);
        }
    }

    private Spanned getDetailsInFormat(KathruDetails kathruDetails) {
        return Html.fromHtml(HtmlBuilder.getFormattedString(kathruDetails));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setVisible(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKathruDetailsInteractionListener) {
            mListener = (OnKathruDetailsInteractionListener) context;
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

    @Override
    public void onPause() {
        super.onPause();
        kathruDetailsTask.cancel(true);
    }

    public interface OnKathruDetailsInteractionListener extends Serializable {
        KathruDetails getKathruDetails(int kathruId);
        void onVachanaButtonClick (int kathruId);
    }
}
