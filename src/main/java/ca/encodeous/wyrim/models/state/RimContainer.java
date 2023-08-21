package ca.encodeous.wyrim.models.state;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

public class RimContainer extends SimpleContainer {
    public RimContainer() {
        super(CONTAINER_ROWS * 9);
    }
    public static final int CONTAINER_ROWS = 6;
    public static final int SLOT_SIZE = 18;

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return super.getItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return super.removeItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return super.removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        super.setItem(i, itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {

    }
}
