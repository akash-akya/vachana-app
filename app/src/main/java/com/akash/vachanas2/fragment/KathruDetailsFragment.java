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

package com.akash.vachanas2.fragment;

import android.content.Context;
import android.os.Bundle;
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

import com.akash.vachanas2.R;
import com.akash.vachanas2.activity.MainActivity;
import com.akash.vachanas2.dbUtil.DatabaseReadAccess;
import com.akash.vachanas2.dbUtil.DbAccessTask;
import com.akash.vachanas2.dbUtil.KathruDetails;
import com.akash.vachanas2.util.HtmlBuilder;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KathruDetailsFragment extends Fragment {
    private static final String KATHRU_ID = "kathru_id";
    private static final String TITLE = "title";
    private static final String TAG = "KathruDetailsFragment";
    private OnKathruDetailsListener mListener;
    private String title;
    private int  kathru_id;
    private GetKathruDetailsTask kathruDetailsTask;
    @BindView(R.id.tv_kathru_details) TextView detailsTextView;
    @BindView(R.id.btn_vachanas_link) Button allVachanasButton;

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
        View view = inflater.inflate(R.layout.fragment_kathru_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        try {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(title);
        } catch (NullPointerException e){
            Log.d(TAG, "onCreate: Actionbar not found");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        kathruDetailsTask = new GetKathruDetailsTask(mOnGetDetailsCompletion);
        kathruDetailsTask.execute(kathru_id);
    }

    @Override
    public void onStop() {
        super.onStop();
        kathruDetailsTask.cancel(true);
    }

    private final DbAccessTask.OnCompletion<KathruDetails> mOnGetDetailsCompletion =
        new DbAccessTask.OnCompletion<KathruDetails>()
        {
            @Override
            public void updateUI(final KathruDetails kathruDetails) {
                detailsTextView.setText(getDetailsInFormat(kathruDetails));
                allVachanasButton.setText("ವಚನಗಳನ್ನು ತೆರೆಯಿರಿ");
                allVachanasButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onVachanaButtonClick(kathruDetails.getId());
                    }
                });
                allVachanasButton.setVisibility(View.VISIBLE);
            }
        };

    private static class GetKathruDetailsTask extends DbAccessTask<Integer,KathruDetails> {
        GetKathruDetailsTask(DbAccessTask.OnCompletion<KathruDetails> onCompletion) {
            super(onCompletion);
        }

        @Override
        protected KathruDetails doInBackground(Integer... kathruIds) {
            DatabaseReadAccess db = MainActivity.getDatabaseReadAccess();
            return db.getKathruDetails(kathruIds[0]);
        }
    }

    private static Spanned getDetailsInFormat(KathruDetails kathruDetails) {
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
        if (context instanceof OnKathruDetailsListener) {
            mListener = (OnKathruDetailsListener) context;
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

    public interface OnKathruDetailsListener extends Serializable {
        void onVachanaButtonClick (int kathruId);
    }
}
