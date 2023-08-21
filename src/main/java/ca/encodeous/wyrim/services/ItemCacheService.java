package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.models.graph.ItemSnapshot;
import ca.encodeous.wyrim.models.graph.RimItemPointer;
import com.wynntils.core.components.Service;

import java.util.Collection;
import java.util.List;

public class ItemCacheService extends Service {
    public ItemCacheService() {
        super(List.of());
    }

    public List<RimItemPointer> getCachedItems() {
        return lastCachedItems;
    }

    public void setCachedItems(Collection<RimItemPointer> lastCachedItems) {
        this.lastCachedItems = List.copyOf(lastCachedItems);
    }

    public boolean hasCache(){
        return lastCachedItems != null;
    }

    public void clearCache(){
        lastCachedItems = null;
    }

    private List<RimItemPointer> lastCachedItems;
}
