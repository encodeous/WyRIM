package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Service;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static ca.encodeous.wyrim.RimServices.*;

public class ItemStorageService extends Service {
    public static final int CONTAINER_ROWS = 6;
    public ItemStorageService() {
        super(List.of());
    }

    public ArrayList<RimMappedItem> bankItemPool;
    public ArrayList<RimMappedItem> invItemPool;
    public ArrayList<RimMappedItem> displayedItemPool;

    public SimpleContainer bank;

    public void initSession(){
        bank = new SimpleContainer(CONTAINER_ROWS * 9);
        bankItemPool = new ArrayList<>();
        displayedItemPool = new ArrayList<>();
        invItemPool = new ArrayList<>();
    }

    public void destroy(){
        invItemPool.clear();
        displayedItemPool.clear();
        bankItemPool.clear();
    }

    public void syncUiForChanges(){
        Core.predicatesUpdated();
        syncPlayerInventory();
    }

    public void syncPlayerInventory(){
        var inv = McUtils.player().getInventory();
        for(var item : Storage.invItemPool){
            inv.setItem(item.originSlot, item.item);
        }
    }

    public int findMostOptimalSlot(ItemStack item, ArrayList<RimMappedItem> pool){
        for(int i = 0; i < pool.size(); i++){
            var slotItem = pool.get(i).item;
            if(!ItemStack.isSameItemSameTags(slotItem, item)) continue;
            var sum = slotItem.count + item.count;

            var capacity = (slotItem.count > slotItem.getMaxStackSize() || // wynncraft does funny things with items, we can only predict what might happen
                    item.count > item.getMaxStackSize() ||
                    slotItem.getMaxStackSize() == 64)
                    ? 64 : slotItem.getMaxStackSize();

            if(sum <= capacity || slotItem.count != capacity){
                return i;
            }
        }
        return -1;
    }

    public int findFreeInvSlot(){
        var pool = Storage.invItemPool;
        for(int i = 0; i < pool.size(); i++){
            var slot = pool.get(i);
            var slotItem = McUtils.player().getInventory().getItem(slot.originSlot);
            if(slotItem.isEmpty()) return slot.originSlot;
        }
        return -1;
    }

    public ItemStack depositItemStack(ItemStack item, ArrayList<RimMappedItem> pool){
        int slot;
        while((slot = findMostOptimalSlot(item, pool)) != -1){
            var slotItem = pool.get(slot).item;
            var capacity = (slotItem.count > slotItem.getMaxStackSize() || // wynncraft does funny things with items, we can only predict what might happen
                    item.count > item.getMaxStackSize() ||
                    slotItem.getMaxStackSize() == 64)
                    ? 64 : slotItem.getMaxStackSize();
            int toAdd = Math.min(capacity - slotItem.count, item.count);
            slotItem.grow(toAdd);
            item.setCount(item.getCount() - toAdd);
            if(item.isEmpty()){
                item = ItemStack.EMPTY;
                break;
            }
        }
        for(int i = 0; i < pool.size() && !item.isEmpty(); i++){
            var slotItem = pool.get(i);
            if(slotItem.item.isEmpty()){
                slotItem.item = item;
                item = ItemStack.EMPTY;
                break;
            }
        }
        return item;
    }

    public void scanInventoriesForChanges(){
        int i = Session.getFront().getMenu().getRowIndexForScroll(Session.getFront().scrollOffs);
        boolean changeDetected = false;
        // scan bank
        for (int j = 0; j < 6; j++) {
            for (int k = 0; k < 9; k++) {
                int l = k + (j + i) * 9;
                var container = bank;
                var items = displayedItemPool;

                if (l >= 0 && l < items.size()) {
                    var contItem = container.getItem(k + j * 9);
                    var poolRimItem = items.get(l);
                    var poolItem = poolRimItem.item;
                    if (!ItemStack.isSameItemSameTags(poolItem, contItem) || contItem.count != poolItem.count) {
                        var matched = bankItemPool.get(poolRimItem.originSlot);
                        matched.item = contItem;
                        changeDetected = true;
                    }
                }else{
                    // scan new slots
                    var contItem = container.getItem(k + j * 9);
                    if(!contItem.isEmpty()){
                        container.setItem(k + j * 9, ItemStack.EMPTY);
                        var ret = depositItemStack(contItem, bankItemPool);
                        if(!ret.isEmpty()){
                            Session.getFront().getMenu().getCarried().grow(ret.count);
                        }
                        changeDetected = true;
                    }
                }
            }
        }
        // scan inventory
        var inv = McUtils.player().getInventory();
        for(var item : Storage.invItemPool){
            var contItem = inv.getItem(item.originSlot);
            var poolItem = item.item;
            if (!ItemStack.isSameItemSameTags(poolItem, contItem) || contItem.count != poolItem.count) {
                item.item = contItem;
                changeDetected = true;
            }
        }
        if(changeDetected){
            syncUiForChanges();
        }
    }
}
