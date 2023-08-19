package ca.encodeous.wyrim.models.item;

import net.minecraft.world.item.ItemStack;

public class WyRimMappedItem {
    public int bankSlotId;

    public WyRimMappedItem(int bankSlotId, ItemStack item) {
        this.bankSlotId = bankSlotId;
        this.item = item;
    }

    public ItemStack item;
}
