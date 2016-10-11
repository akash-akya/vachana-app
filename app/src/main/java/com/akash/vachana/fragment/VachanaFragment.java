package com.akash.vachana.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.Util.UpdateVachanaFavorite;
import com.akash.vachana.activity.ListType;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruDetails;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by akash on 9/2/16.
 */
public class VachanaFragment extends Fragment {

    private static final String TAG = "VachanaFragment";
    private static final String POSITION = "position";
    private static final String VACHANA_MINIS = "vachana_minis";

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Menu menu;
    private int textSize = 16;
    public  boolean needToUpdate = true;
    private int position;
    private ArrayList<VachanaMini> vachana_minis;


    public VachanaFragment() {}

    public static VachanaFragment newInstance(int position, ArrayList<VachanaMini> vachanaMinis) {
        VachanaFragment  fragment = new VachanaFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putSerializable(VACHANA_MINIS, vachanaMinis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            vachana_minis = (ArrayList<VachanaMini>) getArguments().getSerializable(VACHANA_MINIS);
        } else {
            Log.e(TAG, "onCreate: No arguments!!!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.vachana_pager_layout, container, false);

        Bundle extra = getArguments();
        myViewPagerAdapter = new MyViewPagerAdapter(vachana_minis);
        viewPager = (ViewPager) root.findViewById(R.id.vachana_view_pager);

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
                    updateActionBarFavorite(vachana);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {         }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.vachana, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final Vachana vachana = myViewPagerAdapter.vachanaHashMap.get(viewPager.getCurrentItem());
        if (vachana != null) {
            switch (id) {
                case R.id.action_share:
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody =  null;
                    try {
                        shareBody = vachana.getText();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                        Log.d(TAG, "onOptionsItemSelected: TextView not found") ;
                    }
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, vachana.getKathru());
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    getActivity().startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    return true;

                case R.id.action_favorite:
                    boolean new_state = !vachana.getFavorite();
                    vachana.setFavorite(new_state);
                    myViewPagerAdapter.vachanaHashMap.put(viewPager.getCurrentItem(), vachana);
                    if (new_state)
                        item.setIcon(R.drawable.ic_star_20dp);
                    else
                        item.setIcon(R.drawable.ic_star_outline_20dp);
                    new UpdateVachanaFavorite().execute(vachana.getId(), new_state, getActivity());
                    return true;

                case R.id.action_kathru_detail:
                    FragmentManager fragmentManager = ((FragmentActivity)getActivity()).getSupportFragmentManager();
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

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
    }

    private void updateActionBarFavorite(Vachana vachana) {
        MenuItem item = menu.findItem(R.id.action_favorite);
        if (item != null) {
            if (vachana.getFavorite()) {
                menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_20dp);
            } else {
                menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_outline_20dp);
            }
        }
    }

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

                if (newTextSize != textSize || needToUpdate) {
                    textSize = newTextSize;
                    if (myViewPagerAdapter != null){
                        myViewPagerAdapter.notifyDataSetChanged();
                        myViewPagerAdapter.cleanCacheMap();
                    }
                }
                needToUpdate = false;
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
    public class MyViewPagerAdapter extends PagerAdapter {
        private final ArrayList<VachanaMini> vachanaMinis;
        private LayoutInflater layoutInflater;
        public HashMap<Integer, Vachana> vachanaHashMap = new HashMap<>();

        public MyViewPagerAdapter(ArrayList<VachanaMini> vachanaMinis) {
            this.vachanaMinis = vachanaMinis;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.vachana_text_view, container, false);
            new GetVachanaFromDb(view, position).execute();
            container.addView(view);
            return view;
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

        public void cleanCacheMap(){
            notifyDataSetChanged();
            vachanaHashMap.clear();
        }

        private class GetVachanaFromDb extends AsyncTask {
            private final TextView vachanaNumber;
            private ProgressBar progressBar;
            private TextView vachanaTextView;
            private TextView kathruTextView;
            private int position;

            public GetVachanaFromDb(View view, int position) {
                vachanaTextView = (TextView) view.findViewById(R.id.vachana_text);
                kathruTextView = (TextView) view.findViewById(R.id.vachana_kathru_text);
                vachanaNumber = (TextView) view.findViewById(R.id.vachana_number);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                this.position = position;
            }

            @Override
            protected Vachana doInBackground(Object[] objects) {
                return ((MainActivity) getActivity()).db.getVachana(vachanaMinis.get(position).getId());
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                final Vachana vachana = (Vachana) o;
                if (viewPager.getCurrentItem() == position){
                    updateActionBarTitle(vachana);
                    updateActionBarFavorite(vachana);
                }
                progressBar.setVisibility(View.GONE);
                vachanaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                vachanaTextView.setText(vachana.getText());
                vachanaTextView.setVisibility(View.VISIBLE);
                vachanaTextView.setCustomSelectionActionModeCallback(new StyleCallback(vachanaTextView));
                kathruTextView.setText(vachana.getKathru());
                kathruTextView.setVisibility(View.VISIBLE);
                vachanaNumber.setText(String.format("%d/%d",position+1,vachanaMinis.size()));
                vachanaNumber.setVisibility(View.VISIBLE);
                final MainActivity mainActivity = (MainActivity) getActivity();
                final KathruMini kathruMini = MainActivity.db.getKathruMiniById(vachana.getKathruId());

                final int id = vachana.getKathruId();
                kathruTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    FragmentManager fragmentManager = (mainActivity).getSupportFragmentManager();
                    VachanaListFragment fragment = VachanaListFragment.newInstance(kathruMini, kathruMini.getName(),
                            ListType.NORMAL_LIST);

                    fragmentManager.popBackStack("vachana_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment, "vachana_list")
                            .addToBackStack( "vachana_list")
                            .commit();

                    }
                });
                vachanaHashMap.put(position, vachana);

            }

            class StyleCallback implements ActionMode.Callback, Serializable {
                private TextView bodyView;
                private static final String wikiLink = "https://kn.m.wiktionary.org/wiki/";

                public StyleCallback(TextView bodyView) {
                    this.bodyView = bodyView;
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.vachana_select, menu);
                    menu.removeItem(android.R.id.selectAll);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int startSelection = bodyView.getSelectionStart();
                    int endSelection = bodyView.getSelectionEnd();
                    String selectedText = bodyView.getText().toString().substring(startSelection, endSelection);

                    switch (item.getItemId()) {
                        case R.id.wiki:
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            boolean openable = sharedPreferences.getBoolean("dictionary", true);
                            if (openable){
                                showMeaningPopup(getContext(), wikiLink+selectedText);
                            } else {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wikiLink+selectedText));
                                startActivity(browserIntent);
                            }
                            bodyView.clearFocus();
                            return true;
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            }
        }
    }

    public void showMeaningPopup(Context context, String url){

        Dialog alert = new Dialog(context);
        alert.setContentView(R.layout.word_meaning_wv);

        WebView wv = (WebView) alert.findViewById(R.id.web);
        EditText edit = (EditText) alert.findViewById(R.id.edit);
        edit.setFocusable(true);
        edit.requestFocus();

        alert.setTitle("Meaning");

        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("kn.m.wiktionary.org/")){
                    view.loadUrl(url);
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                return true;
            }
        });

        alert.show();
    }
}