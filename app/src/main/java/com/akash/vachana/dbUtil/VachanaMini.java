package com.akash.vachana.dbUtil;

/**
 * Created by akash on 8/28/16.
 */
public class VachanaMini {
    String id;
    String title;
    public VachanaMini(String id, String title){
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
