package com.akash.vachana.dbUtil;

import java.util.ArrayList;

/**
 * Created by akash on 16/10/16.
 */
public interface DatabaseReadAccess {
    KathruMini getKathruMiniById(int id);
    ArrayList<KathruMini> getAllKathruMinis();
    ArrayList<VachanaMini> getVachanaMinisByKathruId (int kathruId);
    String getKathruNameById(int kathruId);
    Vachana getVachana(int id);
    ArrayList<VachanaMini> getFavoriteVachanaMinis();
    ArrayList<KathruMini> getFavoriteKathruMinis();
    KathruDetails getKathruDetails (int kathruId);
}
