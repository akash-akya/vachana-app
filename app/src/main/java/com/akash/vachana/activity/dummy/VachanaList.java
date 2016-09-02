package com.akash.vachana.activity.dummy;

import android.util.Size;

import com.akash.vachana.dbUtil.Vachana;
import com.akash.vachana.dbUtil.VachanaMini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class VachanaList {

    private List<VachanaItem> ITEMS = new ArrayList<>();
//    private Map<String, VachanaItem> ITEM_MAP = new HashMap<String, VachanaItem>();
    private int COUNT = 25;

//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }
    public VachanaList(ArrayList<VachanaMini> vachanaMinis)
    {
        for (int i=0; i<vachanaMinis.size(); i++) {
            int id = vachanaMinis.get(i).getId();
            int kathruId = vachanaMinis.get(i).getKathru();
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

    /**
     * A dummy item representing a piece of content.
     */
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
