package ca.encodeous.wyrim.models.graph;

import ca.encodeous.wyrim.models.state.RimToken;
import ca.encodeous.wyrim.services.TokenService;

import java.util.*;
import java.util.function.Consumer;

import static ca.encodeous.wyrim.RimServices.*;
public class ItemTransaction {
    public final RimToken token;

    public final TransactionType type;
    public final TransactionImplementation impl;

    private ItemTransaction(RimToken token, TransactionType type, TransactionImplementation impl, RimToken... dependencies) {
        this.token = token;
        this.type = type;
        this.impl = impl;
        this.dependencies = Arrays.stream(dependencies).toList();
    }
    private ItemTransaction(TransactionType type, TransactionImplementation impl, RimToken... dependencies) {
        this.token = Token.checkout(this);
        this.type = type;
        this.impl = impl;
        this.dependencies = Arrays.stream(dependencies).toList();
    }

    public final List<RimToken> dependencies;
    public static ItemTransaction rootTransaction(){
        return new ItemTransaction(TokenService.ROOT, TransactionType.ROOT,
                (transac, completion) -> completion.accept(true));
    }

    public boolean canBeCompleted(){
        return dependencies.stream().allMatch(Token::isResolved);
    }

    public void complete(Consumer<Boolean> completion){
        if(!canBeCompleted()) completion.accept(false);
        impl.tryComplete(this, result ->{
            if(result) token.resolve();
            completion.accept(result);
        });
    }

    public enum TransactionType {
        MANY_TO_ONE, // merge items
        ONE_TO_MANY,
        ONE_TO_ONE, // swap items
        MANY_TO_MANY,
        ROOT
    }

    public static void swapSlot(ItemSnapshot currentItem, RimSlotPointer newSlot) {
        var newTsc = new ItemTransaction(
                TransactionType.ONE_TO_ONE,
                (transac, completion) -> {

                },
                currentItem.requiredTransaction.token
        );
        var movedItem = new ItemSnapshot(newSlot, newTsc, currentItem.item);
        var slotItem = Storage.rimItems.stream().filter(i->i.equals(newSlot)).findFirst().get();
        var movedItem1 = new ItemSnapshot(currentItem, newTsc, slotItem.item);
        Storage.rimItems.removeIf(x->x==currentItem || x == slotItem);
        Storage.rimItems.add(movedItem);
        Storage.rimItems.add(movedItem1);
        Storage.updateRim();
    }
    @FunctionalInterface
    public interface TransactionImplementation {
        void tryComplete(ItemTransaction transac, Consumer<Boolean> completion);
    }
}
