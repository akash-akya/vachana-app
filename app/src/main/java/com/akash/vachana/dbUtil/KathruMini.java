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

    public KathruMini(int id, String name, String ankitha, int count) {
        this.id = id;
        this.name = name;
        this.ankitha = ankitha;
        this.count = count;
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
}
