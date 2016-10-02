package com.akash.vachana.dbUtil;

import java.io.Serializable;

/**
 * Created by akash on 8/28/16.
 */
public class KathruMini implements Serializable{
    private int id;
    private String name;
    private String ankitha;
    private int count;
    private int favorite;

    public KathruMini(int id, String name, String ankitha, int count, int favorite) {
        this.id = id;
        this.name = name;
        this.ankitha = ankitha;
        this.count = count;
        this.favorite = favorite;
    }

    public int getFavorite() {
        return favorite;
    }

    public String getAnkitha() {
        return ankitha;
    }

    public int getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setFavorite(boolean favorite) {
        if (favorite)
            this.favorite = 1;
        else
            this.favorite = 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
