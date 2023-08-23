package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.ui.RimScreen;
import com.wynntils.core.components.Service;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;

import java.util.List;

public class InventorySessionService extends Service {
    public InventorySessionService() {
        super(List.of());
    }

    public boolean isActive(){
        return active;
    }

    public void endSession(){
        rimScreen.removed();
        active = false;
    }

    public AbstractContainerScreen<ChestMenu> bankScreen;
    public RimScreen rimScreen;

    public AbstractContainerScreen<ChestMenu> getBacking() {
        return bankScreen;
    }

    public void setBacking(AbstractContainerScreen<ChestMenu> backing) {
        this.bankScreen = backing;
        active = true;
    }

    public RimScreen getFront() {
        return rimScreen;
    }

    public void setFront(RimScreen front) {
        this.rimScreen = front;
    }

    private boolean active = false;
}
