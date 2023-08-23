package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Service;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemCacheService extends Service {
    public ItemCacheService() {
        super(List.of());
    }

    public List<RimMappedItem> getCachedItems() {
        return lastCachedItems;
    }

    public void setCachedItems(List<RimMappedItem> lastCachedItems) {
        this.lastCachedItems = List.copyOf(lastCachedItems);
    }

    public boolean hasCache(){
        return lastCachedItems != null;
    }

    public void clearCache(){
        lastCachedItems = null;
    }

    private List<RimMappedItem> lastCachedItems;
}
