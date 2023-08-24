package ca.encodeous.wyrim.models.ui;

import ca.encodeous.wyrim.services.SearchService;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static ca.encodeous.wyrim.RimServices.Search;

public class RimSlot extends Slot {
    private int slot;
    public RimSlot(Container container, int i, int j, int k) {
        super(container, i, j, k);
        slot = i;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return true;
    }
}
