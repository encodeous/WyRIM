package ca.encodeous.wyrim.models.item;

import ca.encodeous.wyrim.inventory.InvUtils;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.item.ItemAnnotation;
import net.minecraft.world.item.ItemStack;
import net.vidageek.mirror.dsl.Mirror;

public class RimMappedItem {
    public int originSlot;

    public RimMappedItem(ItemStack item, int originSlot) {
        this.originSlot = originSlot;
        this.item = item;
    }

    public ItemStack item;

    public RimMappedItem copy(){
        return new RimMappedItem(InvUtils.copyItemStack(item), originSlot);
    }
}
