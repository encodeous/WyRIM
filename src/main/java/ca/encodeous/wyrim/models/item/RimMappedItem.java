package ca.encodeous.wyrim.models.item;

import net.minecraft.world.item.ItemStack;

public class RimMappedItem {
    public int bankSlotId;

    public RimMappedItem(int bankSlotId, ItemStack item) {
        this.bankSlotId = bankSlotId;
        this.item = item;
    }

    public ItemStack item;
}
