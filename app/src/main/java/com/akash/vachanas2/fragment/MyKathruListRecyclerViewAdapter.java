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
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.akash.vachanas2.R;
import com.akash.vachanas2.activity.ListType;
import com.akash.vachanas2.dbUtil.KathruMini;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

class MyKathruListRecyclerViewAdapter extends RecyclerView.Adapter<MyKathruListRecyclerViewAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private static final String TAG = "MyKathruListRecyclerViewAdapter";
    private List<KathruMini> kathruMinis;
    private ArrayList<KathruMini> dupKathruMinis = new ArrayList<>();
    private final KathruListFragment.OnKathruListFragmentListener mListener;
    private ListType listType;
    private char[] mScrollIndex;

    public MyKathruListRecyclerViewAdapter(List<KathruMini> items, KathruListFragment.OnKathruListFragmentListener listener, ListType listType) {
        kathruMinis = items;
        mListener = listener;
        this.listType = listType;
        dupKathruMinis.addAll(kathruMinis);
        createScrolIndex();
    }

    void createScrolIndex(){
        mScrollIndex = new char[kathruMinis.size()];
        for (int i=0; i< kathruMinis.size(); i++){
            mScrollIndex[i] = kathruMinis.get(i).getName().charAt(0);
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
        createScrolIndex();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = kathruMinis.get(position);
        holder.mAnkitha.setText(kathruMinis.get(position).getAnkitha());
        holder.mName.setText(kathruMinis.get(position).getName());
        holder.mVachanaCount.setText(String.format("%d", kathruMinis.get(position).getCount()));

        holder.mFavorite.setOnCheckedChangeListener (null);
        if(holder.mItem.getFavorite() == 1) {
            holder.mFavorite.setChecked(true);
        } else {
            holder.mFavorite.setChecked(false);
        }

        holder.mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                holder.mItem.setFavorite(b);
                EventBus.getDefault().post(holder.mItem);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onKathruListItemClick(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showKathruDetailsPopup(holder.mView.getContext(), holder.mItem);
                return true;
            }
        });
    }

    private void showKathruDetailsPopup(final Context context, final KathruMini kathruMini){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1);
        arrayAdapter.add(kathruMini.getName()+" ಬಗ್ಗೆ ಹೆಚ್ಚಿನ ಮಾಹಿತಿ");

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                        KathruDetailsFragment fragment = KathruDetailsFragment.newInstance(kathruMini.getId(),
                                kathruMini.getName());

                        fragmentManager.popBackStack("kathru_details", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content, fragment, "kathru_details")
                                .addToBackStack( "kathru_details" )
                                .commit();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void doThis(KathruMini kathruMini){
        for (int i=0; i<kathruMinis.size(); i++){
            if (kathruMinis.get(i).getId() == kathruMini.getId()){
                if (listType == ListType.FAVORITE_LIST){
                    kathruMinis.remove(i);
                    notifyDataSetChanged();
                    createScrolIndex();
                }else {
                    kathruMinis.set(i, kathruMini);
                    notifyItemChanged(i);
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return kathruMinis.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(mScrollIndex[position]);
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
            mName = view.findViewById(R.id.tv_kathru_name);
            mAnkitha = view.findViewById(R.id.tv_kathru_ankitha);
            mVachanaCount = view.findViewById(R.id.tv_kathru_count);
            mFavorite = view.findViewById(R.id.kathru_favorite);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
