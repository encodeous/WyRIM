package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.RimServices;
import ca.encodeous.wyrim.models.item.ItemPredicate;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Service;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchService extends Service {
    public SearchService() {
        super(List.of());
    }
    private ItemPredicate predicate = ItemPredicate.ANY;
    boolean hasPredicate = false;

    public boolean isPredicateApplied(){
        return hasPredicate;
    }

    public void setPredicate(String query){
        hasPredicate = !query.trim().isEmpty();
        predicate = ItemPredicate.buildMatchQuery(query);
        RimServices.Core.predicatesUpdated();
    }

    public void destroy(){
        predicate = ItemPredicate.ANY;
        hasPredicate = false;
    }

    protected ArrayList<RimMappedItem> applyPredicates(List<RimMappedItem> items){
        var list = new ArrayList<RimMappedItem>();
        for(int i = 0; i < items.size(); i++){
            var item = items.get(i);
            if(predicate.satisfies(item.item)){
                list.add(new RimMappedItem(item.item, i));
            }
        }
        return list;
    }
}
