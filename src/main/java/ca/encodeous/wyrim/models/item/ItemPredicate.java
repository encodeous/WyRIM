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
    default ItemPredicate not(){
        return (item) -> !satisfies(item);
    }
    ItemPredicate ANY = (item) -> true;
    ItemPredicate NONE = (item) -> false;
    ItemPredicate ANY_NOT_EMPTY = (item) -> !item.item.isEmpty();
    static ItemPredicate buildMatchToken(String text){
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


    /**
     * Parses a simple boolean expression tree in the following form as an example:
     * A | B & !C & D | !E | F
     * @param query
     * @return
     */
    static ItemPredicate buildMatchQuery(String query){
        var pred = ItemPredicate.NONE;
        if (!query.isEmpty()) {
            var orOperations = query.split("[|]");
            for(var operation : orOperations){
                var predBlock = ItemPredicate.ANY_NOT_EMPTY;
                var andOperations = operation.split("&");
                for(var expression : andOperations){
                    var curExpr = expression.trim();
                    predBlock = predBlock.and(buildMatchUnaryExpression(curExpr));
                }
                pred = pred.or(predBlock);
            }
        }else{
            pred = ItemPredicate.ANY;
        }
        return pred;
    }

    static ItemPredicate buildMatchUnaryExpression(String expression){
        if(expression.startsWith("!")){
            return buildMatchUnaryExpression(expression.substring(1).trim()).not();
        }else{
            return ItemPredicate.buildMatchToken(expression);
        }
    }
}
