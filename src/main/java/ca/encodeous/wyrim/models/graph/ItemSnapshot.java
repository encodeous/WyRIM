package ca.encodeous.wyrim.models.graph;

import ca.encodeous.wyrim.services.TokenService;
import net.minecraft.world.item.ItemStack;

import static ca.encodeous.wyrim.RimServices.Token;

public class ItemSnapshot extends RimItemPointer {
    public ItemSnapshot(RimSlotPointer location, ItemTransaction requiredTransaction, ItemStack item) {
        super(location.backingSlotId, item, location.backingSource);
        this.requiredTransaction = requiredTransaction;
        Token.addSnapshot(this);
    }

    public static ItemSnapshot createRoot(RimItemPointer backingLocation){
        return new ItemSnapshot(backingLocation, ItemTransaction.rootTransaction(), backingLocation.item.copy());
    }

    public final ItemTransaction requiredTransaction;
}
