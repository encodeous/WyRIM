package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.RimServices;
import ca.encodeous.wyrim.models.item.ItemPredicate;
import ca.encodeous.wyrim.models.item.RimMappedItem;
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

    protected ArrayList<RimMappedItem> applyPredicates(Collection<RimMappedItem> items){
        var list = new ArrayList<RimMappedItem>();
        for(var item : items) {
            if(predicate.satisfies(item)){
                list.add(item);
            }
        }
        return list;
    }
}
