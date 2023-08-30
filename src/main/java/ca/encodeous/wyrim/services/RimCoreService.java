package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.engine.DiffEngine;
import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import ca.encodeous.wyrim.ui.RimScreen;
import com.wynntils.core.components.Service;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;

import java.util.List;
import static ca.encodeous.wyrim.RimServices.*;

public class RimCoreService extends Service {
    public RimCoreService() {
        super(List.of());
    }
    public DiffEngine diff;

    /**
     * Called when the search is updated with new predicates
     */
    protected void predicatesUpdated(){
        Storage.displayedItemPool.clear();
        Storage.displayedItemPool.addAll(
                Search.applyPredicates(Storage.bankItemPool)
        );
        Session.getFront().getMenu().refresh();
    }

    /**
     * Initializes the bank session with the Wynncraft backed inventory
     * @param screen
     * @return true if the inventory needs to be indexed
     */
    public boolean initBankSession(AbstractContainerScreen<ChestMenu> screen){
        McUtils.sendMessageToClient(Component.literal("Analyzing Bank...").withStyle(ChatFormatting.GRAY));
        Storage.initSession();
        Session.setBacking(screen);
        Session.setFront(new RimScreen(McUtils.player()));
        if(Cache.hasCache()){
            Storage.bankItemPool.addAll(Cache.getCachedItems());
            initRimSession();
            return false;
        }
        return true;
    }

    public void initRimSession(){
        predicatesUpdated(); // update with empty predicates
        diff = new DiffEngine();
        diff.initDiffApplicationEngine();
    }

    public void loadBankPage(){
        var newItems = BankUtils.mapItems();
        // remove duplicates if the server sends the packets more than once
        Storage.bankItemPool.removeIf(x -> newItems.stream().anyMatch(y->y.originSlot == x.originSlot));
        Storage.bankItemPool.addAll(newItems);
        predicatesUpdated();
    }

    public void destroySession(){
        if(Session.isActive()){
            Search.destroy();
            Storage.destroy();
            Session.endSession();
        }
    }

    public void commitSession(){
        McUtils.sendMessageToClient(Component.literal("Applying Changes...").withStyle(ChatFormatting.GRAY));
        Core.diff.applySnapshot().thenRun(()->{
            Cache.setCachedItems(Storage.bankItemPool);
            Core.destroySession();
            McUtils.player().closeContainer();
        });
    }

    public void clearBankCache(){
        Cache.clearCache();
    }

}
