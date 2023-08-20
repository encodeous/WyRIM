package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import ca.encodeous.wyrim.models.ui.client.RimSession;
import ca.encodeous.wyrim.models.ui.server.BankSession;
import ca.encodeous.wyrim.ui.WyRimScreen;
import com.wynntils.core.components.Service;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;

import java.util.List;
import static ca.encodeous.wyrim.WyRimServices.*;

public class WyRimCoreService extends Service {
    public WyRimCoreService() {
        super(List.of());
    }

    /**
     * Called when the search is updated with new predicates
     */
    protected void predicatesUpdated(){
        Session.getFront().items.clear();
//        System.out.println(Session.getBacking().items);
        Session.getFront().items.addAll(
                Search.applyPredicates(Session.getBacking().items)
        );
        Session.getFront().rimScreen.getMenu().refresh();
    }

    /**
     * Initializes the bank session with the Wynncraft backed inventory
     * @param screen
     * @return true if the inventory needs to be indexed
     */
    public boolean initBankSession(AbstractContainerScreen<ChestMenu> screen){
        McUtils.sendMessageToClient(Component.literal("Analyzing Bank...").withStyle(ChatFormatting.GRAY));
        Session.setBacking(new BankSession(screen));
        Session.setFront(new RimSession());
        Session.getFront().setRimScreen(new WyRimScreen(McUtils.player()));
        if(Cache.hasCache()){
            Session.getBacking().items.addAll(Cache.getCachedItems());
            initRimSession();
            return false;
        }
        return true;
    }

    public void initRimSession(){
        Cache.clearCache();
        Cache.setCachedItems(Session.getBacking().items);
        var screen = Session.getFront().rimScreen;
        predicatesUpdated(); // update with empty predicates
        ScreenUtils.activateWithoutDestroy(screen);
    }

    public void loadBankPage(){
        var newItems = BankUtils.mapItems(Session.getBacking());
        var backingStore = Session.getBacking().items;
        // remove duplicates if the sends the packets more than once
        backingStore.removeIf(x->newItems.stream().anyMatch(y->x.bankSlotId == y.bankSlotId));
        backingStore.addAll(newItems);
    }

    public void destroySession(){
        Session.endSession();
    }

    public void clearBankCache(){
        Cache.clearCache();
    }
}
