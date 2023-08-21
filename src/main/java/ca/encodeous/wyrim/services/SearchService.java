package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.RimServices;
import ca.encodeous.wyrim.models.graph.ItemSnapshot;
import ca.encodeous.wyrim.models.item.ItemPredicate;
import ca.encodeous.wyrim.models.graph.RimItemPointer;
import ca.encodeous.wyrim.models.ui.SortMode;
import com.wynntils.core.components.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchService extends Service {
    public SearchService() {
        super(List.of());
    }
    private ItemPredicate predicate = ItemPredicate.ANY;

    public void setPredicate(ItemPredicate pred){
        predicate = pred;
        RimServices.Core.predicatesUpdated();
    }

    protected ArrayList<ItemSnapshot> applyPredicates(Collection<ItemSnapshot> items){
        var list = new ArrayList<ItemSnapshot>();
        for(var item : items) {
            if(predicate.satisfies(item)){
                list.add(item);
            }
        }
        list.sort(SortMode.DEFAULT_MODE.getComparator());
        return list;
    }
}
