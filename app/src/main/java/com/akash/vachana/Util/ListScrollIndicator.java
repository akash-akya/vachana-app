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

package com.akash.vachana.Util;

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