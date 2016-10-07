package com.akash.vachana.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.VachanaMini;
import com.akash.vachana.fragment.KathruListFragment.OnKathruListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyKathruListRecyclerViewAdapter extends RecyclerView.Adapter<MyKathruListRecyclerViewAdapter.ViewHolder>
        implements SectionIndexer {

    private static final String TAG = "MyKathruListRecyclerViewAdapter";
    private String[] names;
    private List<KathruMini> kathruMinis;
    private ArrayList<KathruMini> dupKathruMinis = new ArrayList<>();
    private final OnKathruListFragmentInteractionListener mListener;

    public MyKathruListRecyclerViewAdapter(List<KathruMini> items, OnKathruListFragmentInteractionListener listener) {
        kathruMinis = items;
        mListener = listener;
        dupKathruMinis.addAll(kathruMinis);
        names = new String[kathruMinis.size()];
        int i = 0;
        for (KathruMini k : kathruMinis) {
            names[i] = k.getName();
            i++;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_kathru_list_item, parent, false);
        return new ViewHolder(view);
    }

    public void filter(String charText) {
        kathruMinis.clear();
        if (charText.length() == 0) {
            kathruMinis.addAll(dupKathruMinis);

        } else {
            for (KathruMini kathruMini : dupKathruMinis) {
                if (charText.length() != 0 && (kathruMini.getName().contains(charText) ||
                        kathruMini.getAnkitha().contains(charText))) {
                    kathruMinis.add(kathruMini);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = kathruMinis.get(position);
        holder.mAnkitha.setText(kathruMinis.get(position).getAnkitha());
        holder.mName.setText(kathruMinis.get(position).getName());
        holder.mVachanaCount.setText(String.format("%d", kathruMinis.get(position).getCount()));

        if(holder.mItem.getFavorite() == 1)
            holder.mFavorite.setChecked(true);
        else
            holder.mFavorite.setChecked(false);

        holder.mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mListener.onFavoriteButton(holder.mItem.getId(), b);
                holder.mItem.setFavorite(b);
            }
        });

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
        return kathruMinis.size();
    }

    @Override
    public Object[] getSections() {
        return kathruMinis.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= kathruMinis.size()) {
            position = kathruMinis.size() - 1;
        }
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAnkitha;
        public final TextView mName;
        public final TextView mVachanaCount;
        private final CheckBox mFavorite;

        public KathruMini mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.tv_kathru_name);
            mAnkitha = (TextView) view.findViewById(R.id.tv_kathru_ankitha);
            mVachanaCount = (TextView) view.findViewById(R.id.tv_kathru_count);
            mFavorite = (CheckBox) view.findViewById(R.id.kathru_favorite);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
