package com.akash.vachana.dbUtil;

import java.io.Serializable;

/**
 * Created by akash on 8/28/16.
 */
public class VachanaMini implements Serializable{
    private int id;
    private int kathruId;
    private String kathruName;
    private String title;
    private int favorite;

    public VachanaMini(int id, int kathruId, String kathruName, String title, int favorite){
        this.id = id;
        this.kathruId = kathruId;
        this.title = title;
        this.kathruName = kathruName;
        this.favorite = favorite;
    }

    public String getKathruName() {
        return kathruName;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getKathruId() {
        return kathruId;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        if (favorite)
            this.favorite = 1;
        else
            this.favorite = 0;
    }
}
