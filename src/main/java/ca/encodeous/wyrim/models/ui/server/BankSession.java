package ca.encodeous.wyrim.models.ui.server;

import ca.encodeous.wyrim.models.item.WyRimMappedItem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class BankSession {
    public ArrayList<WyRimMappedItem> allItems;

    public BankSession(AbstractContainerScreen<ChestMenu> bankScreen) {
        allItems = new ArrayList<>();
        this.bankScreen = bankScreen;
    }

    public AbstractContainerScreen<ChestMenu> bankScreen;
}
