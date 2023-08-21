package ca.encodeous.wyrim.ui;

import ca.encodeous.wyrim.models.graph.RimItemPointer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class RimSlot extends Slot {
    protected Slot baseSlot;
    public RimSlot(Slot baseSlot) {
        super(baseSlot.container, baseSlot.getContainerSlot(), baseSlot.x, baseSlot.y);
        this.baseSlot = baseSlot;
    }

    @Override
    public void onTake(Player player, ItemStack itemStack) {

    }

    public static class Bank extends RimSlot {

        public Bank(Slot bankSlot) {
            super(bankSlot);
        }

        @Override
        public void set(ItemStack itemStack) {
            super.set(itemStack);
        }

        @Override
        public void setByPlayer(ItemStack itemStack) {
            super.setByPlayer(itemStack);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return super.mayPlace(itemStack);
        }

        @Override
        public ItemStack safeInsert(ItemStack itemStack) {
            return super.safeInsert(itemStack);
        }
    }
    public static class PlayerInv extends RimSlot {

        public PlayerInv(Slot invSlot) {
            super(invSlot);
        }
    }
}
