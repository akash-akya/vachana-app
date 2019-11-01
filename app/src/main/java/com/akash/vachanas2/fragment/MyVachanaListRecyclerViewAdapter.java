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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.akash.vachanas2.R;
import com.akash.vachanas2.activity.ListType;
import com.akash.vachanas2.dbUtil.VachanaMini;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.widget.CompoundButton.*;

public class MyVachanaListRecyclerViewAdapter extends RecyclerView.Adapter<MyVachanaListRecyclerViewAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private final ListType listType;
    private char[] mScrollIndex;
    private List<VachanaMini> vachanaMinis;
    private ArrayList<VachanaMini> dupVachanaMinis = new ArrayList<>();
    private VachanaListFragment.OnVachanaFragmentListListener mListener;

    public MyVachanaListRecyclerViewAdapter(List<VachanaMini> items, VachanaListFragment.OnVachanaFragmentListListener listener, ListType listType) {
        vachanaMinis = items;
        mListener = listener;
        this.listType = listType;
        dupVachanaMinis.addAll(vachanaMinis);
        createScrollIndex();
    }

    void createScrollIndex(){
        mScrollIndex = new char[vachanaMinis.size()];
        for (int i=0; i< vachanaMinis.size(); i++){
            mScrollIndex[i] = vachanaMinis.get(i).getTitle().charAt(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_vachana_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = vachanaMinis.get(position);
        holder.mTitle.setText(holder.mItem.getTitle());
        holder.mKathru.setText(holder.mItem.getKathruName());

        holder.mFavorite.setOnCheckedChangeListener(null);

        if(holder.mItem.getFavorite() == 1)
            holder.mFavorite.setChecked(true);
        else
            holder.mFavorite.setChecked(false);

        holder.mFavorite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                holder.mItem.setFavorite(b);
                EventBus.getDefault().post(holder.mItem);
//                mListener.onVachanaFavoriteButton(holder.mItem.getId(), b);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onVachanaListItemClick((ArrayList<VachanaMini>) vachanaMinis, position);
                }
            }
        });
    }

    public void filter(String charText) {
        vachanaMinis.clear();
        if (charText.length() == 0) {
            vachanaMinis.addAll(dupVachanaMinis);
        } else {
            for (VachanaMini kathruMini : dupVachanaMinis) {
                if (charText.length() != 0 && (kathruMini.getTitle().contains(charText))) {
                    vachanaMinis.add(kathruMini);
                }
            }
        }
        createScrollIndex();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vachanaMinis.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void doThis(VachanaMini vachanaMini){
        for (int i=0; i<vachanaMinis.size(); i++){
            if (vachanaMinis.get(i).getId() == vachanaMini.getId()){
                if (listType == ListType.FAVORITE_LIST){
                    vachanaMinis.remove(vachanaMini);
                    notifyDataSetChanged();
                    createScrollIndex();
                }else {
                    vachanaMinis.set(i, vachanaMini);
                    notifyItemChanged(i);
                }
                break;
            }
        }
    }

    public void addVachanas(ArrayList<VachanaMini> vachanaMinis) {
        this.vachanaMinis = vachanaMinis;
        dupVachanaMinis.addAll(vachanaMinis);
        createScrollIndex();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(mScrollIndex[position]);
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
            mTitle = view.findViewById(R.id.tv_vachana_title);
            mFavorite = view.findViewById(R.id.favorite);
            mKathru = view.findViewById(R.id.tv_vachana_kathru);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
