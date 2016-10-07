package com.akash.vachana.Util;

/**
 * Created by akash on 7/10/16.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.akash.vachana.dbUtil.KathruMini;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Indicator for sections of type {@link com.akash.vachana.dbUtil.KathruMini}
 */
public class ListScrollIndicator extends SectionTitleIndicator<String> {

    public ListScrollIndicator(Context context) {
        super(context);
    }

    public ListScrollIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListScrollIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(String name) {
        setTitleText(name.charAt(0) + "");
    }
}