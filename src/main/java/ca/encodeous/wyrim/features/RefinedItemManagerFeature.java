package ca.encodeous.wyrim.features;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import ca.encodeous.wyrim.models.ui.WyRimSession;
import ca.encodeous.wyrim.models.ui.client.RimSession;
import ca.encodeous.wyrim.models.ui.server.BankSession;
import ca.encodeous.wyrim.ui.WyRimScreen;
import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.mc.event.ScreenClosedEvent;
import com.wynntils.mc.event.ScreenInitEvent;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RefinedItemManagerFeature extends Feature {

    protected WyRimSession session = null;
    private boolean isSearching = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onScreenInit(ScreenInitEvent e) {
        if (!(e.getScreen() instanceof AbstractContainerScreen<?> screen)) return;
        if (!(screen.getMenu() instanceof ChestMenu)) return;

        if(Models.Container.isBankScreen(screen)){
            ensureSession((AbstractContainerScreen<ChestMenu>) screen);
        }
    }

    @SubscribeEvent
    public void onContainerSetContent(ContainerSetContentEvent.Post event) {
        if(session == null) return;
        refreshPage();

        if(isSearching){
            BankUtils.advancePage(session.serverSession, ()->{
                finalizeSearch();
                isSearching = false;
            });
        }
    }

    /**
     * Creates the session if it doesnt exist and reloads the data
     */
    private void ensureSession(AbstractContainerScreen<ChestMenu> bankScreen){
        if(session == null){
            McUtils.sendMessageToClient(Component.literal("Analyzing Bank...").withStyle(ChatFormatting.GRAY));
            session = new WyRimSession();
            session.serverSession = new BankSession(bankScreen);
            session.clientSession = new RimSession(new WyRimScreen(McUtils.player(), session));
            isSearching = true;
        }
        session.serverSession.bankScreen = bankScreen;
        refreshPage();
    }

    private void finalizeSearch(){
        session.clientSession.rimScreen.getMenu().items.clear();
        session.clientSession.rimScreen.getMenu().items.addAll(session.serverSession.allItems.stream().map(x->x.item).toList());
        session.clientSession.rimScreen.getMenu().refresh();
        ScreenUtils.activateWithoutDestroy(session.clientSession.rimScreen);
    }

    private void refreshPage(){
        var items = BankUtils.mapItems(session.serverSession);
        session.serverSession.allItems.removeIf(x->items.stream().anyMatch(y->x.bankSlotId == y.bankSlotId));
        session.serverSession.allItems.addAll(items);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenClose(ScreenClosedEvent e) {
        session = null;
//        if (!(e.getScreen() instanceof AbstractContainerScreen<?> screen)) return;
//
//        if(Models.Container.isBankScreen(screen)){
//            displayRim();
//        }
    }
}
