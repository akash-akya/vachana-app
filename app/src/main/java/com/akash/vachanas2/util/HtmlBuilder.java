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

package com.akash.vachanas2.util;

import com.akash.vachanas2.dbUtil.KathruDetails;

public class HtmlBuilder {

    private static String setColor(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    private static String bold (String text){
        return "<b>"+text+"</b>";
    }

    private static String br() {
        return "<br />";
    }

    private static String getLine(String key, String content) {
        if (!content.isEmpty()){
            return bold(key)+" : "+content+br();
        } else {
            return "";
        }
    }

    public static String getFormattedString(KathruDetails kathruDetails) {
//        keyColor = color;
        String builder = getLine(KathruDetails.textOf(KathruDetails.KEYS.SIBLINGS), kathruDetails.getSiblings()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.BIRTH_PLACE), kathruDetails.getBirthPlace()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.MOTHER), kathruDetails.getMother()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.PROVINCE), kathruDetails.getProvince()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.DEATH_PLACE), kathruDetails.getDeathPlace()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.VILLAGE), kathruDetails.getVillage()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.FATHER), kathruDetails.getFather()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.TIME), kathruDetails.getTime()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.OTHER_WORK), kathruDetails.getOtherWork()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.CHILDREN), kathruDetails.getChildren()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.WORK), kathruDetails.getWork()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.WIFE), kathruDetails.getWife()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.AVAILABLE_VACHANA), kathruDetails.getAvailableVachana()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.ANKITHA), kathruDetails.getAnkitha()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.TALUK), kathruDetails.getTaluk()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.SPECIALITY), kathruDetails.getSpeciality()) +
                getLine(KathruDetails.textOf(KathruDetails.KEYS.TOMB_PLACE), kathruDetails.getTombPlace());
        //        builder.append(getLine(KathruDetails.textOf(KathruDetails.KEYS.NAME), kathruDetails.getName()));
        return builder;
    }
}
