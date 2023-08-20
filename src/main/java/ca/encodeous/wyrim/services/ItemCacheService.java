package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.models.item.WyRimMappedItem;
import com.wynntils.core.components.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemCacheService extends Service {
    public ItemCacheService() {
        super(List.of());
    }

    public List<WyRimMappedItem> getCachedItems() {
        return lastCachedItems;
    }

    public void setCachedItems(List<WyRimMappedItem> lastCachedItems) {
        this.lastCachedItems = List.copyOf(lastCachedItems);
    }

    public boolean hasCache(){
        return lastCachedItems != null;
    }

    public void clearCache(){
        lastCachedItems = null;
    }

    private List<WyRimMappedItem> lastCachedItems;
}
