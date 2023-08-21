package ca.encodeous.wyrim.models.ui.server;

import ca.encodeous.wyrim.models.graph.RimItemPointer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ChestMenu;

public class BankSession {

    public BankSession(AbstractContainerScreen<ChestMenu> bankScreen) {
        this.bankScreen = bankScreen;
    }

    public AbstractContainerScreen<ChestMenu> bankScreen;
}
