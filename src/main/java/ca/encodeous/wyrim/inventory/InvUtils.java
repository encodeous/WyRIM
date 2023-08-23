package ca.encodeous.wyrim.inventory;

import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.item.ItemAnnotation;
import net.minecraft.world.item.ItemStack;
import net.vidageek.mirror.dsl.Mirror;

import static ca.encodeous.wyrim.RimServices.Session;
import static ca.encodeous.wyrim.RimServices.Storage;

public class InvUtils {
    public static int mapUiToSlotId(int id){
        if(id <= 53){
            return id;
        }else{
            id -= 53;
            if(id >= 28){
                id = id - 28;
            }else{
                id += 8;
            }
        }
        return id;
    }

    public static boolean isBank(int id){
        return id <= 53;
    }

    public static RimMappedItem getItemUniversal(int id){
        if(isBank(id)){
            int i = Session.getFront().getMenu().getRowIndexForScroll(Session.getFront().scrollOffs);
            int k = id % 9;
            int j = id / 9;
            int l = k + (j + i) * 9;
            return Storage.displayedItemPool.get(l);
        }else{
            id = mapUiToSlotId(id);
            int finalId = id;
            return Storage.invItemPool.stream().filter(x->x.originSlot == finalId).findFirst().get();
        }
    }

    public static ItemStack copyItemStack(ItemStack itemStack){
        var nItem = itemStack.copy();
        StyledText name = StyledText.fromComponent(itemStack.getHoverName()).getNormalized();
        ItemAnnotation annotation = (ItemAnnotation) new Mirror().on(Handlers.Item).invoke().method("calculateAnnotation").withArgs(itemStack, name);
        if (annotation == null) return nItem;

        Handlers.Item.updateItem(nItem, annotation, name);
        return nItem;
    }
}
