package ca.encodeous.wyrim.models.item;

import com.wynntils.core.components.Models;
import net.minecraft.world.item.TooltipFlag;

@FunctionalInterface
public interface ItemPredicate {
    boolean satisfies(WyRimMappedItem item);
    default ItemPredicate and(ItemPredicate other){
        return (item) -> satisfies(item) && other.satisfies(item);
    }
    default ItemPredicate or(ItemPredicate other){
        return (item) -> satisfies(item) || other.satisfies(item);
    }
    ItemPredicate ANY = (item) -> true;
    ItemPredicate EXCLUDE_EMPTY = (item) -> !item.item.isEmpty();
    static ItemPredicate buildMatchText(String text){
        return (item) -> {
            var x = item.item;
            var match = text.toLowerCase();
            if (x.getDisplayName().getString().toLowerCase().contains(match)) return true;
            if (x.getTooltipLines(null, TooltipFlag.NORMAL)
                    .stream()
                    .anyMatch(y ->
                            y.getString().toLowerCase().contains(match)
                    ))
                return true;
            var wynnItemOpt = Models.Item.getWynnItem(x);
            if (wynnItemOpt.isEmpty()) return false;
            var wynnItem = wynnItemOpt.get();
            return wynnItem.toString().toLowerCase().contains(match);
        };
    }
}
