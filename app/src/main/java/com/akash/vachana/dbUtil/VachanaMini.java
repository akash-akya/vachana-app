package com.akash.vachana.dbUtil;

/**
 * Created by akash on 8/28/16.
 */
public class VachanaMini {
    private int id;
    private int kathruId;
    private String kathruName;
    private String title;

    public VachanaMini(int id, int kathruId, String kathruName, String title){
        this.id = id;
        this.kathruId = kathruId;
        this.title = title;
        this.kathruName = kathruName;
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
}
