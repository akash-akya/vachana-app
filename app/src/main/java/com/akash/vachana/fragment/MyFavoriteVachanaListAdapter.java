package com.akash.vachana.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.VachanaListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static android.view.View.OnClickListener;

public class MyFavoriteVachanaListAdapter extends RecyclerView.Adapter<MyFavoriteVachanaListAdapter.ViewHolder> {

    private final List<VachanaMini> mValues;
    private final FavoriteListFragment.OnListFavoriteInteractionListener mListener;

    public MyFavoriteVachanaListAdapter(List<VachanaMini> items, FavoriteListFragment.OnListFavoriteInteractionListener listener) {
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(holder.mItem.getTitle());
        holder.mKathru.setText(holder.mItem.getKathruName());

        if(holder.mItem.getFavorite() == 1)
            holder.mFavorite.setChecked(true);
        else
            holder.mFavorite.setChecked(false);

        if (!holder.mFavorite.hasOnClickListeners()) {
            holder.mFavorite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public static final String TAG = "onBindViewHolder";
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mListener.onFavoriteButton(holder.mItem.getId(), b);
                    holder.mItem.setFavorite(b);
                    Log.d(TAG, "onCheckedChanged: "+compoundButton.isChecked());
                }
            });
        }

        if (!holder.mView.hasOnClickListeners()) {
            holder.mView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction((ArrayList<VachanaMini>) mValues, position);
                }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mKathru;
        public final CheckBox mFavorite;
        public VachanaMini mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.tv_vachana_title);
            mFavorite = (CheckBox) view.findViewById(R.id.favorite);
            mKathru = (TextView) view.findViewById(R.id.tv_vachana_kathru);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
