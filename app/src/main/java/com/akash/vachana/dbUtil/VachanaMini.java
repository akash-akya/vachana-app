package com.akash.vachana.dbUtil;

/**
 * Created by akash on 8/28/16.
 */
public class VachanaMini {
    private int id;
    private int kathru;
    private String title;

    public VachanaMini(int id, int kathru, String title){
        this.id = id;
        this.kathru = kathru;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getKathru() {
        return kathru;
    }
}
