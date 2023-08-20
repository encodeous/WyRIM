package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.WyRimServices;
import ca.encodeous.wyrim.models.item.ItemPredicate;
import ca.encodeous.wyrim.models.item.WyRimMappedItem;
import com.wynntils.core.components.Service;
import com.wynntils.core.persisted.Persisted;
import com.wynntils.core.persisted.config.Category;
import com.wynntils.core.persisted.config.Config;
import com.wynntils.core.persisted.config.ConfigCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ConfigCategory(Category.INVENTORY)
public class SearchService extends Service {
    @Persisted
    public final Config<Boolean> showEmptySlots = new Config<>(true);
    public SearchService() {
        super(List.of());
    }
    private final ArrayList<ItemPredicate> predicates = new ArrayList<>();

    public void setPredicates(Collection<ItemPredicate> pred){
        predicates.clear();
        predicates.addAll(pred);
        WyRimServices.Core.predicatesUpdated();
    }

    public List<ItemPredicate> getPredicates(){
        return List.copyOf(predicates);
    }

    protected ArrayList<WyRimMappedItem> applyPredicates(Collection<WyRimMappedItem> items){
        var list = new ArrayList<WyRimMappedItem>();
        for(var item : items) {
            if (predicates.isEmpty()) {
                ItemPredicate predicate;
                if(!showEmptySlots.get()){
                    predicate = ItemPredicate.EXCLUDE_EMPTY;
                } else {
                    predicate = ItemPredicate.ANY;
                }
                if (predicate.satisfies(item)) {
                    list.add(item);
                }
            } else {
                if (predicates.stream().allMatch(x -> x.satisfies(item))) {
                    list.add(item);
                }
            }
        }
        return list;
    }
}
