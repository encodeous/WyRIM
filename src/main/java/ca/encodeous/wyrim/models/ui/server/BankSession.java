package ca.encodeous.wyrim.models.ui.server;

import ca.encodeous.wyrim.models.item.WyRimMappedItem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ChestMenu;

import java.util.ArrayList;

public class BankSession {
    public final NonNullList<WyRimMappedItem> items = NonNullList.create();

    public BankSession(AbstractContainerScreen<ChestMenu> bankScreen) {
        this.bankScreen = bankScreen;
    }

    public AbstractContainerScreen<ChestMenu> bankScreen;
}
