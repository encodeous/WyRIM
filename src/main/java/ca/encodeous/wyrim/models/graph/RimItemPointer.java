package ca.encodeous.wyrim.models.graph;

import net.minecraft.world.item.ItemStack;

public class RimItemPointer extends RimSlotPointer {
    public ItemStack item;
    public RimItemPointer(int backingSlotId, ItemStack item, RimSlotPointer.ItemSource backingSource) {
        super(backingSlotId, backingSource);
        this.item = item;
    }
}
