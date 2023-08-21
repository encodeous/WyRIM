package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import ca.encodeous.wyrim.models.graph.ItemSnapshot;
import ca.encodeous.wyrim.models.graph.RimItemPointer;
import ca.encodeous.wyrim.models.ui.client.RimSession;
import ca.encodeous.wyrim.models.ui.server.BankSession;
import ca.encodeous.wyrim.ui.RimScreen;
import ca.encodeous.wyrim.ui.RimSlot;
import com.wynntils.core.components.Service;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ca.encodeous.wyrim.RimServices.*;

public class RimCoreService extends Service {
    public RimCoreService() {
        super(List.of());
    }

    /**
     * Called when the search is updated with new predicates
     */
    protected void predicatesUpdated(){
        Session.getFront().items.clear();
//        System.out.println(Session.getBacking().items);
        Storage.filteredItems = Search.applyPredicates(Storage.getStoredItems());
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
        Session.getFront().setRimScreen(new RimScreen(McUtils.player()));
        if(Cache.hasCache()){
            Storage.rimItems.addAll(Cache.getCachedItems()
                    .stream().map(ItemSnapshot::createRoot)
                    .toList()
            );
            initRimSession();
            return false;
        }
        return true;
    }

    public void initRimSession(){
        Cache.clearCache();
        Cache.setCachedItems(new ArrayList<>(Storage.getStoredItems()));
        var screen = Session.getFront().rimScreen;
        predicatesUpdated(); // update with empty predicates
        ScreenUtils.activateWithoutDestroy(screen);
    }

    public void loadBankPage(){
        var newItems = BankUtils.mapItems(Session.getBacking());
        var backingStore = Storage.rimItems;
        // remove duplicates if the sends the packets more than once
        backingStore.removeIf(x->newItems.stream().anyMatch(y->x.backingSlotId == y.backingSlotId));
        backingStore.addAll(newItems
                .stream().map(ItemSnapshot::createRoot)
                .toList());
    }

    public void itemPickup(RimSlot slot){

    }

    public void destroySession(){
        Session.endSession();
        Storage.destroyStorage();
    }

    public void clearBankCache(){
        Cache.clearCache();
    }
}
