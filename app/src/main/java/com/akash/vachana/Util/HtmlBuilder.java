package com.akash.vachana.Util;

import com.akash.vachana.dbUtil.KathruDetails;

/**
 * Created by akash on 10/10/16.
 */
public class HtmlBuilder {

    private static String keyColor;

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
