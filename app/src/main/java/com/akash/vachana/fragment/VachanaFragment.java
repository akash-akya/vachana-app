package com.akash.vachana.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.util.TypedValue;
import android.view.ActionMode;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akash.vachana.R;
import com.akash.vachana.Util.VachanaListListenerAbstract;
import com.akash.vachana.activity.MainActivity;
import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.MainDbHelper;
import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akash on 9/2/16.
 */
public class VachanaFragment extends Fragment {

    private static final String TAG = "VachanaFragment";

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Menu menu;
    private int textSize = 16;

    public VachanaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.vachana_pager_layout, container, false);

        Bundle extra = getArguments();
        myViewPagerAdapter = new MyViewPagerAdapter((ArrayList<VachanaMini>) extra.getSerializable("vachanas"));

        viewPager = (ViewPager) root.findViewById(R.id.vachana_view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem((int) extra.getSerializable("current_position"), true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Vachana vachana = myViewPagerAdapter.vachanaHashMap.get(position);
                if (vachana != null) {
                    try {
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(vachana.getKathru());
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onCreate: Actionbar not found");
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
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
        int id = item.getItemId();
        Vachana vachana = myViewPagerAdapter.vachanaHashMap.get(viewPager.getCurrentItem());
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
                    if (new_state)
                        item.setIcon(R.drawable.ic_star_20dp);
                    else
                        item.setIcon(R.drawable.ic_star_outline_20dp);
                    new MainActivity.UpdateVachanaFavorite().execute(vachana.getId(), new_state);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                int newTextSize = Integer.parseInt(sharedPreferences.getString("font_size", "16"));
                if (newTextSize != textSize){
                    textSize = newTextSize;
                    Bundle extra = getArguments();
                    int position = viewPager.getCurrentItem();
                    myViewPagerAdapter =
                            new MyViewPagerAdapter((ArrayList<VachanaMini>) extra.getSerializable("vachanas"));
                    viewPager.setAdapter(myViewPagerAdapter);
                    viewPager.setCurrentItem(position, true);
                    Log.d(TAG, "Text changed: "+textSize);
                }
            }
        }, 0);

        AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true, true);
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
                MenuItem item = menu.findItem(R.id.action_favorite);
                if (item != null) {
                    if (vachana.getFavorite()) {
                        menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_20dp);
                    } else {
                        menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_outline_20dp);
                    }
                }
                if (viewPager.getCurrentItem() == position)
                    updateActionBarTitle(vachana);
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
//                kathruTextView.setOnClickListener(mainActivity.getKathruOnClickListener(vachana.getKathruId()));
                final KathruMini kathruMini = MainActivity.db.getKathruMiniById(vachana.getKathruId());

                final int id = vachana.getKathruId();
                kathruTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("title", kathruMini.getName());
                        bundle.putSerializable("listener", new VachanaListListenerAbstract(getActivity()) {
                            @Override
                            public ArrayList<VachanaMini> getVachanaMinis() {
                                return MainActivity.db.getVachanaMinisByKathruId(id);
                            }
                        });

                        FragmentManager fragmentManager = (mainActivity).getSupportFragmentManager();
                        Fragment fragment = Fragment.instantiate(mainActivity, MainActivity.fragments[1]);
                        fragment.setArguments(bundle);
                        fragmentManager.popBackStack("vachana_list_vertical" , FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content, fragment)
                                .addToBackStack( "vachana_list_vertical" )
                                .commit();
                    }
                });
                vachanaHashMap.put(position, vachana);

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
                public void onDestroyActionMode(ActionMode mode) {
                }
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