package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.WyRimServices;
import ca.encodeous.wyrim.models.item.ItemPredicate;
import ca.encodeous.wyrim.models.item.WyRimMappedItem;
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
        WyRimServices.Core.predicatesUpdated();
    }

    protected ArrayList<WyRimMappedItem> applyPredicates(Collection<WyRimMappedItem> items){
        var list = new ArrayList<WyRimMappedItem>();
        for(var item : items) {
            if(predicate.satisfies(item)){
                list.add(item);
            }
        }
        return list;
    }
}
