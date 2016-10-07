package com.akash.vachana.Util;

/**
 * Created by akash on 7/10/16.
 */
import android.content.Context;
import android.util.AttributeSet;

import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.VachanaMini;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Indicator for sections of type {@link com.akash.vachana.dbUtil.VachanaMini}
 */
public class VachanaTitleIndicator extends SectionTitleIndicator<VachanaMini> {

    public VachanaTitleIndicator(Context context) {
        super(context);
    }

    public VachanaTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VachanaTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(VachanaMini vachanaMini) {
        setTitleText(vachanaMini.getTitle().charAt(0) + "");
    }
}