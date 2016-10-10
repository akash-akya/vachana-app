package com.akash.vachana.dbUtil;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * Created by akash on 9/5/16.
 */
public class KathruDetails implements Serializable{
    private static final String TAG = "KathruDetails";

    public static String textOf(KEYS key) {
        switch (key){
            case SIBLINGS: return "ಒಡಹುಟ್ಟಿದವರು";
            case BIRTH_PLACE: return "ಜನ್ಮ ಸ್ಥಳ";
            case MOTHER: return "ತಾಯಿ ಹೆಸರು";
            case PROVINCE: return "ಜಿಲ್ಲೆ";
            case DEATH_PLACE: return "ಕಾಲವಾದ ಸ್ಥಳ";
            case VILLAGE: return "ಗ್ರಾಮ";
            case FATHER: return "ತಂದೆ ಹೆಸರು";
            case TIME: return "ಕಾಲ";
            case OTHER_WORK: return "ವಚನಗಳಲ್ಲದೆ ಇತರ ಲಭ್ಯ ಕೃತಿಗಳು";
            case CHILDREN: return "ಮಕ್ಕಳು: ಅವರ ಹೆಸರು";
            case WORK: return "ಕಾಯಕ";
            case WIFE: return "ಪತಿ: ಪತ್ನಿಯ ಹೆಸರು";
            case AVAILABLE_VACHANA: return "ಲಭ್ಯ ವಚನಗಳ ಸಂಖ್ಯೆ";
            case ANKITHA: return "ಅಂಕಿತ";
            case TALUK: return "ತಾಲ್ಲೂಕು";
            case SPECIALITY: return "ಕೃತಿಯ ವೈಶಿಷ್ಟ್ಯ";
            case TOMB_PLACE: return "ಸಮಾಧಿ ಇರುವ ಸ್ಥಳ";
            case NAME: return "ವಚನಕಾರರ ಹೆಸರ";
        }
        return null;
    }

    public enum KEYS {SIBLINGS, BIRTH_PLACE, MOTHER, PROVINCE, DEATH_PLACE,
        VILLAGE, FATHER, TIME, OTHER_WORK, CHILDREN, WORK, WIFE, AVAILABLE_VACHANA,
        ANKITHA, TALUK, SPECIALITY, TOMB_PLACE, NAME;
    };

    private int id;
    private  EnumMap<KEYS,String> details;

    public KathruDetails(int id, EnumMap<KEYS,String> details) {
        this.id  = id;
        this.details = details;
    }

    public String getName() {
        return details.get(KEYS.NAME);
    }

    public String getSiblings() {
        return details.get(KEYS.SIBLINGS);
    }

    public String getBirthPlace() {
        return details.get(KEYS.BIRTH_PLACE);
    }

    public String getMother() {
        return details.get(KEYS.MOTHER);
    }

    public String getProvince() {
        return details.get(KEYS.PROVINCE);
    }

    public String getDeathPlace() {
        return details.get(KEYS.DEATH_PLACE);
    }

    public String getVillage() {
        return details.get(KEYS.VILLAGE);
    }

    public String getFather() {
        return details.get(KEYS.FATHER);
    }

    public String getTime() {
        return details.get(KEYS.TIME);
    }

    public String getOtherWork() {
        return details.get(KEYS.OTHER_WORK);
    }

    public String getChildren() {
        return details.get(KEYS.CHILDREN);
    }

    public String getWork() {
        return details.get(KEYS.WORK);
    }

    public String getWife() {
        return details.get(KEYS.WIFE);
    }

    public String getAvailableVachana() {
        return details.get(KEYS.AVAILABLE_VACHANA);
    }

    public String getAnkitha() {
        return details.get(KEYS.ANKITHA);
    }

    public String getTaluk() {
        return details.get(KEYS.TALUK);
    }

    public String getSpeciality() {
        return details.get(KEYS.SPECIALITY);
    }

    public String getTombPlace() {
        return details.get(KEYS.TOMB_PLACE);
    }

    public int getId() {
        return id;
    }
}
