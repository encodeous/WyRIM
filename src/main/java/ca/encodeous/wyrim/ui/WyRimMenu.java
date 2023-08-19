package ca.encodeous.wyrim.ui;

import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WyRimMenu extends AbstractContainerMenu {
    protected final AbstractContainerMenu inventoryMenu;
    protected static final int CONTAINER_ROWS = 6;
    protected static final int SLOT_SIZE = 18;
    protected final SimpleContainer CONTAINER = new SimpleContainer(CONTAINER_ROWS * 9);
    public final NonNullList<ItemStack> items = NonNullList.create();

    protected WyRimMenu(Player player) {
        super(null, 0);

        Inventory inventory = player.getInventory();

        inventoryMenu = player.inventoryMenu;

        for (int i = 0; i < CONTAINER_ROWS; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(CONTAINER, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
        }

        int k = (CONTAINER_ROWS - 4) * SLOT_SIZE;

        for (int l = 0; l < 3; l++) {
            for (int m = 0; m < 9; m++) {
                addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
            }
        }
        for (int l = 0; l < 9; l++) {
            addSlot(new Slot(inventory, l, 8 + l * 18, 161 + k));
        }

        scrollTo(0.0F);
    }

    public void refresh() {
        scrollTo(0.0F);
    }

    protected int calculateRowCount() {
        return Mth.positiveCeilDiv(this.items.size(), 9) - 6;
    }

    protected int getRowIndexForScroll(float f) {
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
                if (l >= 0 && l < this.items.size()) {
                    CONTAINER.setItem(k + j * 9, this.items.get(l));
                } else {
                    CONTAINER.setItem(k + j * 9, ItemStack.EMPTY);
                }
            }
        }
    }

    public boolean canScroll() {
        return (this.items.size() > 45);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(final int id, final int dragType, final ClickType clickType, final Player player) {
//        final Slot slot = id >= 0 ? getSlot(id) : null;
//        if (isSwappingDisabledSlotWithNumberKeys(dragType, clickType)) {
//            return;
//        }
//        if (slot instanceof DisabledSlot) {
//            return;
//        }
        super.clicked(id, dragType, clickType, player);
    }
}