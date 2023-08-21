package ca.encodeous.wyrim.models.ui;

import ca.encodeous.wyrim.models.graph.RimItemPointer;
import com.wynntils.core.components.Models;

import java.util.Comparator;

/**
 * Sorting modes
 * They are all ascending by default
 */
public enum SortMode {
    DEFAULT_MODE(Comparator.comparingInt(item -> item.backingSlotId)),
    NAME(Comparator.comparing(a -> a.item.getDisplayName().getString())),
    TYPE(Comparator.comparing(a -> Models.Item.getWynnItem(a.item).get().getClass().getTypeName()));
    private Comparator<RimItemPointer> sort;
    SortMode(Comparator<RimItemPointer> sort){
        this.sort = sort;
    }
    public Comparator<RimItemPointer> getComparator(){
        return sort;
    }
}
