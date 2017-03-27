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

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.akash.vachana.R;

public class ThemeChangeUtil {
    private static final String TAG = "ThemeChangeUtil";

    public static void themeResetGard(final Activity activity)
    {
        // Ugly fix for 'Dark theme reset' defect. See: https://code.google.com/p/android/issues/detail?id=226208#c2
        if (Build.VERSION.SDK_INT >= 24) {
            new WebView(activity);
        }
    }

    public static void changeToTheme(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity, int themeColor) {
        switch (themeColor) {
            case 0x4fc3f7: activity.setTheme(R.style.Theme_theme1); break;
            case 0x42bd41: activity.setTheme(R.style.Theme_theme2); break;
            case 0xffb74d: activity.setTheme(R.style.Theme_theme3); break;
            case 0xff8a65: activity.setTheme(R.style.Theme_theme4); break;
            case 0x3F51B5: activity.setTheme(R.style.Theme_theme5); break;
            case 0x333333: activity.setTheme(R.style.Theme_theme6); break;
            default:
                activity.setTheme(R.style.Theme_theme5);
                Log.d(TAG, "Color: unknown theme");
                break;
        }
    }
}
