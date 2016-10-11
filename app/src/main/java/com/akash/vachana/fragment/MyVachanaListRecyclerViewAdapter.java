package com.akash.vachana.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.dbUtil.VachanaMini;

import java.util.ArrayList;
import java.util.List;

import static android.widget.CompoundButton.*;

public class MyVachanaListRecyclerViewAdapter extends RecyclerView.Adapter<MyVachanaListRecyclerViewAdapter.ViewHolder>
        implements SectionIndexer {

    private String[] names;
    private List<VachanaMini> vachanaMinis;
    private ArrayList<VachanaMini> dupVachanaMinis = new ArrayList<>();
    private VachanaListFragment.OnVachanaFragmentListListener mListener;

    public MyVachanaListRecyclerViewAdapter(List<VachanaMini> items, VachanaListFragment.OnVachanaFragmentListListener listener) {
        vachanaMinis = items;
        mListener = listener;
        dupVachanaMinis.addAll(vachanaMinis);
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
                mListener.onVachanaFavoriteButton(holder.mItem.getId(), b);
                holder.mItem.setFavorite(b);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.OnVachanaListItemClick((ArrayList<VachanaMini>) vachanaMinis, position);
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
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vachanaMinis.size();
    }

    @Override
    public Object[] getSections() {
        if (names == null) {
            names = new String[vachanaMinis.size()];
            int i = 0;
            for (VachanaMini k : vachanaMinis) {
                names[i] = k.getTitle();
                i++;
            }
        }
        return names;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= vachanaMinis.size()) {
            position = vachanaMinis.size() - 1;
        }
        return position;
    }

    public void addVachanas(ArrayList<VachanaMini> vachanaMinis) {
        this.vachanaMinis = vachanaMinis;
        dupVachanaMinis.addAll(vachanaMinis);
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
