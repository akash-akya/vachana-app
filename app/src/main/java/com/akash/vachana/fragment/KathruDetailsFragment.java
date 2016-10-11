package com.akash.vachana.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.Util.HtmlBuilder;
import com.akash.vachana.activity.ListType;
import com.akash.vachana.dbUtil.KathruDetails;

import java.io.Serializable;

public class KathruDetailsFragment extends Fragment {
    private static final String KATHRU_ID = "kathru_id";
    private static final String TITLE = "title";
    private static final String TAG = "KathruDetailsFragment";
    private OnKathruDetailsInteractionListener mListener;
    private String title;
    private int  kathru_id;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kathru_details, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetKathruDetailsTask(kathru_id).execute();
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
            kathruDetailsTextView = (TextView) getActivity().findViewById(R.id.tv_kathru_details);
            vachanaLinkButton = (Button) getActivity().findViewById(R.id.btn_vachanas_link);
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
            vachanaLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onVachanaButtonClick(kathruDetails.getId());
                }
            });
        }
    }

    private Spanned getDetailsInFormat(KathruDetails kathruDetails) {
        return Html.fromHtml(HtmlBuilder.getFormattedString(kathruDetails));
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

    public interface OnKathruDetailsInteractionListener extends Serializable {
        KathruDetails getKathruDetails(int kathruId);
        void onVachanaButtonClick (int kathruId);
    }
}
