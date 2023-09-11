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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;

import com.akash.vachanas2.databinding.VachanaPagerLayoutBinding;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akash.vachanas2.R;
import com.akash.vachanas2.activity.ListType;
import com.akash.vachanas2.activity.MainActivity;
import com.akash.vachanas2.dbUtil.DbAccessTask;
import com.akash.vachanas2.dbUtil.KathruMini;
import com.akash.vachanas2.dbUtil.Vachana;
import com.akash.vachanas2.dbUtil.VachanaMini;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class VachanaFragment extends Fragment {

    private static final String TAG = "VachanaFragment";
    private static final String POSITION = "position";
    private static final String VACHANA_MINIS = "vachana_minis";

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int textSize = 16;
    private int position;
    private ArrayList<VachanaMini> vachana_minis;
    private MenuItem starMenuItem;
    private GetVachanaFromDb mDbTask;
    private VachanaPagerLayoutBinding binding;

    public VachanaFragment() {}

    public static VachanaFragment newInstance(int position, ArrayList<VachanaMini> vachanaMinis) {
        VachanaFragment  fragment = new VachanaFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putParcelableArrayList(VACHANA_MINIS,vachanaMinis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            vachana_minis = getArguments().getParcelableArrayList(VACHANA_MINIS);
        } else {
            Log.e(TAG, "onCreate: No arguments!!!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = VachanaPagerLayoutBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        myViewPagerAdapter = new MyViewPagerAdapter(vachana_minis);
        viewPager = binding.vachanaViewPager;

        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Vachana vachana = myViewPagerAdapter.vachanaHashMap.get(position);
                if (vachana != null) {
                    updateActionBarTitle(vachana);
                    updateActionBarFavorite(starMenuItem, vachana.getFavorite());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {         }
        });

        return view;
    }

    private void updateActionBarFavorite(MenuItem starMenuItem, boolean favorite) {
        if (starMenuItem != null){
            starMenuItem.setIcon(favorite? R.drawable.ic_star_20dp : R.drawable.ic_star_border_white_24dp);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.vachana, menu);
        starMenuItem = menu.findItem(R.id.action_favorite);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final int position = viewPager.getCurrentItem();
        final Vachana vachana = myViewPagerAdapter.vachanaHashMap.get(position);
        if (vachana != null) {
            switch (id) {
                case R.id.action_share:
                    onShareClick(vachana.getKathru(), vachana.getText());
                    return true;

                case R.id.action_favorite:
                    boolean new_state = !vachana.getFavorite();
                    vachana.setFavorite(new_state);
                    myViewPagerAdapter.vachanaHashMap.put(position, vachana);
                    updateActionBarFavorite(starMenuItem, new_state);
                    vachana_minis.get(position).setFavorite(new_state);
                    EventBus.getDefault().post(vachana_minis.get(position));
                    return true;

                case R.id.action_kathru_detail:
                    FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
                    KathruDetailsFragment fragment = KathruDetailsFragment.newInstance(vachana.getKathruId(),
                            vachana.getKathru());

                    fragmentManager.popBackStack("kathru_details", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment, "kathru_details")
                            .addToBackStack( "kathru_details" )
                            .commit();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setOnClickListener(onActionBarClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
//        mDbTask.cancel(true);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setOnClickListener(null);
    }

    View.OnClickListener onActionBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final VachanaMini vachanaMini = vachana_minis.get(viewPager.getCurrentItem());
            final KathruMini kathruMini = MainActivity.getDatabaseReadAccess().getKathruMiniById(vachanaMini.getKathruId());
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            VachanaListFragment fragment = VachanaListFragment.newInstance(kathruMini,
                    vachanaMini.getKathruName(),
                    ListType.NORMAL_LIST);

            fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment, "vachana_list")
                    .addToBackStack( "vachana_list")
                    .commit();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = getContext();
                int newTextSize = textSize;
                if (context != null) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    newTextSize = Integer.parseInt(sharedPreferences.getString("font_size", "16"));
                }

                if (newTextSize != textSize) {
                    textSize = newTextSize;
                    if (myViewPagerAdapter != null){
                        myViewPagerAdapter.notifyDataSetChanged();
                        myViewPagerAdapter.cleanCacheMap();
                    }
                }
            }
        }, 0);
    }

    void updateActionBarTitle(Vachana vachana) {
        if (vachana != null) {
            try {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(vachana.getKathru());
            } catch (NullPointerException e) {
                Log.d(TAG, "onCreate: Actionbar not found");
            }
        }
    }

    /**
     * View pager adapter
     */
    class MyViewPagerAdapter extends PagerAdapter {
        private final ArrayList<VachanaMini> vachanaMinis;
        SparseArray<Vachana> vachanaHashMap = new SparseArray<Vachana>();

        MyViewPagerAdapter(ArrayList<VachanaMini> vachanaMinis) {
            this.vachanaMinis = vachanaMinis;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.vachana_text_view, container, false);

            mDbTask = new GetVachanaFromDb(new DbAccessTask.OnCompletion<Vachana>() {
                TextView vachanaTextView = view.findViewById(R.id.vachana_text);
                TextView vachanaNumber = view.findViewById(R.id.vachana_number);
                ProgressBar progressBar = view.findViewById(R.id.progressBar);

                @Override
                public void updateUI(Vachana vachana) {
                    if (viewPager.getCurrentItem() == position){
                        updateActionBarTitle(vachana);
                        updateActionBarFavorite(starMenuItem, vachana.getFavorite());
                    }

                    updateView(progressBar, vachanaTextView, vachanaNumber, vachana, position);
                    vachanaHashMap.put(position, vachana);
                }
            });
            mDbTask.execute(vachanaMinis.get(position).getId());
            container.addView(view);
            return view;
        }

        private void updateView(ProgressBar progressBar, TextView vachanaTextView,
                                TextView vachanaNumber, Vachana vachana, int position){
            progressBar.setVisibility(View.GONE);
            vachanaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            vachanaTextView.setText(vachana.getText());
            vachanaTextView.setVisibility(View.VISIBLE);
            vachanaNumber.setText(String.format("%d/%d",position+1,vachanaMinis.size()));
            vachanaNumber.setVisibility(View.VISIBLE);
        }

        @Override
        public int getCount() {
            return vachanaMinis.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            vachanaHashMap.remove(position);
        }

        void cleanCacheMap(){
            notifyDataSetChanged();
            vachanaHashMap.clear();
        }
    }

    private static class GetVachanaFromDb extends DbAccessTask<Integer,Vachana> {
        GetVachanaFromDb(OnCompletion<Vachana> onCompletion) {
            super(onCompletion);
        }

        @Override
        protected Vachana doInBackground(Integer... id) {
            Log.d(TAG, "doInBackground: "+id[0]);
            return MainActivity.getDatabaseReadAccess().getVachana(id[0]);
        }
    }

    public void onShareClick(String subject, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}