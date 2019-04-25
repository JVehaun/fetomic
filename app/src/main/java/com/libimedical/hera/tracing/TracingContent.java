package com.libimedical.hera.tracing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TracingContent {

    /**
     * An array of TracingItems
     */
    public static List<TracingItem> items = new ArrayList<>();

    /**
     * A map of tracing items, by ID.
     */
    public static Map<String, TracingItem> itemMap = new HashMap<>();

    public static void addItem(TracingItem item) {
        items.add(item);
        itemMap.put(item.recordId, item);
    }

}
