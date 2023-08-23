package ca.encodeous.wyrim.ui;

import ca.encodeous.wyrim.inventory.InvUtils;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import ca.encodeous.wyrim.models.ui.RimSlot;
import com.google.common.collect.Sets;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static ca.encodeous.wyrim.RimServices.*;
import static ca.encodeous.wyrim.services.ItemStorageService.CONTAINER_ROWS;

public class RimMenu extends AbstractContainerMenu {
    protected final AbstractContainerMenu inventoryMenu;
    public static final int SLOT_SIZE = 18;

    protected RimMenu(Player player) {
        super(null, 0);

        Inventory inventory = player.getInventory();

        inventoryMenu = player.inventoryMenu;

        for (int i = 0; i < CONTAINER_ROWS; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new RimSlot(Storage.bank, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
        }

        int k = (CONTAINER_ROWS - 4) * SLOT_SIZE;

        for (int l = 0; l < 3; l++) {
            for (int m = 0; m < 9; m++) {
                Storage.invItemPool.add(new RimMappedItem(inventory.getItem(m + l * 9 + 9), m + l * 9 + 9));
                addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
            }
        }
        for (int l = 0; l < 9; l++) {
            Storage.invItemPool.add(new RimMappedItem(inventory.getItem(l), l));
            addSlot(new Slot(inventory, l, 8 + l * 18, 161 + k));
        }
        scrollTo(0.0F);
    }

    public void refresh() {
        scrollTo(Session.getFront().scrollOffs);
    }

    protected int calculateRowCount() {
        return Mth.positiveCeilDiv(Storage.displayedItemPool.size(), 9) - 6;
    }

    public int getRowIndexForScroll(float f) {
        return Math.max((int) ((f * calculateRowCount()) + 0.5D), 0);
    }

    protected float getScrollForRowIndex(int i) {
        return Mth.clamp(i / calculateRowCount(), 0.0F, 1.0F);
    }

    protected float subtractInputFromScroll(float f, double d) {
        return Mth.clamp(f - (float) (d / calculateRowCount()), 0.0F, 1.0F);
    }

    public void scrollTo(float f) {
        int i = getRowIndexForScroll(f);
        for (int j = 0; j < 6; j++) {
            for (int k = 0; k < 9; k++) {
                int l = k + (j + i) * 9;
                var container = Storage.bank;
                var items = Storage.displayedItemPool;
                if (l >= 0 && l < items.size()) {
                    container.setItem(k + j * 9, items.get(l).item);
                } else {
                    container.setItem(k + j * 9, ItemStack.EMPTY);
                }
            }
        }
    }

    public boolean canScroll() {
        return (Storage.displayedItemPool.size() > 45);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int id) {
        var item = InvUtils.getItemUniversal(id);
        var originalItem = item.item;
        if(!InvUtils.isBank(id)){
            // deposit into bank
            item.item = Storage.depositItemStack(item.item, Storage.bankItemPool);
            Storage.syncUiForChanges();
        }else{
            var rem = Storage.depositItemStack(item.item, Storage.invItemPool);
            setItem(id, 0, rem);
            Storage.syncPlayerInventory();
        }
        if(item.item == originalItem){
            return ItemStack.EMPTY;
        }
        return item.item;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(final int id, final int dragType, final ClickType clickType, final Player player) {
        if(id < 0 && (clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE)) return;
        var isCarried = !getCarried().isEmpty();
        super.clicked(id, dragType, clickType, player);
        Storage.scanInventoriesForChanges();
//        if(InvUtils.isBank(id) && Search.isPredicateApplied() && isCarried){
//            // find most optimal position
//            setCarried(Storage.depositItemStack(getCarried()));
//        }
    }
}