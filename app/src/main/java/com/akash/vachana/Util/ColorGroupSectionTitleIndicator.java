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
public class ColorGroupSectionTitleIndicator extends SectionTitleIndicator<KathruMini> {

    public ColorGroupSectionTitleIndicator(Context context) {
        super(context);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(KathruMini kathruMini) {
        setTitleText(kathruMini.getName().charAt(0) + "");
    }
}