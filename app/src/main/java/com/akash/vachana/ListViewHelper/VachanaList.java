package com.akash.vachana.ListViewHelper;

import com.akash.vachana.dbUtil.VachanaMini;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class VachanaList {

    private List<VachanaItem> ITEMS = new ArrayList<>();
    private int COUNT = 25;

    public VachanaList(ArrayList<VachanaMini> vachanaMinis)
    {
        for (int i=0; i<vachanaMinis.size(); i++) {
            int id = vachanaMinis.get(i).getId();
            int kathruId = vachanaMinis.get(i).getKathruId();
            String title = vachanaMinis.get(i).getTitle();
            addItem(new VachanaItem(id, kathruId, title, ""));
        }
        COUNT = vachanaMinis.size();
    }

    private void addItem(VachanaItem item) {
        ITEMS.add(item);
//        ITEM_MAP.put(item.id, item);
    }

//    private static VachanaItem createDummyItem(int position) {
//        return new VachanaItem(position, "Item " + position, makeDetails(position));
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public ArrayList<VachanaItem> getVachanaList() {
        return (ArrayList<VachanaItem>) ITEMS;
    }

    public static class VachanaItem {
        public final int id;
        public final int kathruId;
        public final String details;
        public final String content;

        public VachanaItem(int id, int kathruId, String content, String details) {
            this.id = id;
            this.kathruId = kathruId;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
