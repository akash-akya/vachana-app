package com.akash.vachana.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.VachanaListFragment.OnListFragmentInteractionListener;
import com.akash.vachana.ListViewHelper.VachanaList.VachanaItem;

import java.util.List;

public class MyVachanaListRecyclerViewAdapter extends RecyclerView.Adapter<MyVachanaListRecyclerViewAdapter.ViewHolder> {

    private final List<VachanaMini> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyVachanaListRecyclerViewAdapter(List<VachanaMini> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_vachana_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(holder.mItem.getTitle());
        holder.mKathru.setText(holder.mItem.getKathruName());

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
        public final TextView mTitle;
        public final TextView mKathru;
        public VachanaMini mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.tv_vachana_title);
            mKathru = (TextView) view.findViewById(R.id.tv_vachana_kathru);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
