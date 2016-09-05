package com.akash.vachana.ListViewHelper;

import com.akash.vachana.dbUtil.KathruMini;
import com.akash.vachana.dbUtil.VachanaMini;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class KathruList{

    private List<KathruMini> ITEMS = new ArrayList<>();

    public KathruList(ArrayList<KathruMini> kathruMinis)
    {
        ITEMS = kathruMinis;
/*        for (int i=0; i<vachanaMinis.size(); i++) {
            int id = vachanaMinis.get(i).getId();
            int kathruId = vachanaMinis.get(i).getKathru();
            String title = vachanaMinis.get(i).getTitle();
            addItem(new VachanaItem(id, kathruId, title, ""));
        }*/
    }

    private void addItem(KathruMini item) {
        ITEMS.add(item);
    }


    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public ArrayList<KathruMini> getKathruList() {
        return (ArrayList<KathruMini>) ITEMS;
    }
}
