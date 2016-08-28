package com.akash.vachana.dbUtil;

/**
 * Created by akash on 8/28/16.
 */
public class VachanaMini {
    int id;
    String title;
    public VachanaMini(int id, String title){
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
