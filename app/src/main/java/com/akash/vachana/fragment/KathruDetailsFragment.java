package com.akash.vachana.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.Util.HtmlBuilder;
import com.akash.vachana.Util.VachanaListListenerAbstract;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruDetails;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;

public class KathruDetailsFragment extends Fragment {
    private OnKathruDetailsInteractionListener mListener;

    public KathruDetailsFragment() {
        // Required empty public constructor
    }

    public static KathruDetailsFragment newInstance(String param1, String param2) {
        return new KathruDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kathru_details, container, false);

        mListener = (OnKathruDetailsInteractionListener) getArguments().getSerializable("listener");
//        mListener.getKathruDetails((int) getArguments().getSerializable("kathru_id"));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetKathruDetailsTask((int) getArguments().getSerializable("kathru_id")).execute();
    }

    private class GetKathruDetailsTask extends AsyncTask{
//        private TextView kathruNameTextView;
        private TextView kathruDetailsTextView;
        private Button vachanaLinkButton;
        private final int kathruId ;

        private GetKathruDetailsTask(int kathruId) {
            this.kathruId = kathruId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            kathruNameTextView = (TextView) getActivity().findViewById(R.id.tv_kathru_name);
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
//            kathruNameTextView.setText(kathruDetails.getName());
            kathruDetailsTextView.setText(getDetailsInFormat(kathruDetails));
            vachanaLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", kathruDetails.getName());
                    bundle.putSerializable("listener", new VachanaListListenerAbstract(getActivity()) {
                        @Override
                        public ArrayList<VachanaMini> getVachanaMinis() {
                            return MainActivity.db.getVachanaMinisByKathruId(kathruDetails.getId());
                        }
                    });

                    Fragment fragment = Fragment.instantiate(getActivity(), MainActivity.fragments[1]);
                    assert fragment != null;
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment, "vachana_list")
                            .commit();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnKathruDetailsInteractionListener extends Serializable {
        KathruDetails getKathruDetails(int kathruId);
    }
}
