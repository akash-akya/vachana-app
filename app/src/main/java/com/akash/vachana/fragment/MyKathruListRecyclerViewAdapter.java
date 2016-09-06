package com.akash.vachana.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.fragment.KathruListFragment.OnKathruListFragmentInteractionListener;

import java.util.List;

public class MyKathruListRecyclerViewAdapter extends RecyclerView.Adapter<MyKathruListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MyKathruListRecyclerViewAdapter";
    private final List<KathruMini> mValues;
    private final OnKathruListFragmentInteractionListener mListener;

    public MyKathruListRecyclerViewAdapter(List<KathruMini> items, OnKathruListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_kathru_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAnkitha.setText(mValues.get(position).getAnkitha());
        holder.mName.setText(mValues.get(position).getName());
        holder.mVachanaCount.setText(String.format("%d", mValues.get(position).getCount()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAnkitha;
        public final TextView mName;
        public final TextView mVachanaCount;

        public KathruMini mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.tv_kathru_name);
            mAnkitha = (TextView) view.findViewById(R.id.tv_kathru_ankitha);
            mVachanaCount = (TextView) view.findViewById(R.id.tv_kathru_count);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
