package ca.encodeous.wyrim.features;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.InvUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.mc.event.ContainerSetSlotEvent;
import com.wynntils.mc.event.ScreenClosedEvent;
import com.wynntils.mc.event.ScreenInitEvent;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static ca.encodeous.wyrim.RimServices.*;

public class RefinedItemManagerFeature extends Feature {
    private boolean isSearching = false;
    private boolean preserveDefaultBehaviour = false;

    private int curBankPage = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onScreenInit(ScreenInitEvent e) {
        if (!(e.getScreen() instanceof AbstractContainerScreen<?> screen)) return;
        if (!(screen.getMenu() instanceof ChestMenu)) return;

        if(Models.Container.isBankScreen(screen)){
            if(McUtils.player().isCrouching() || preserveDefaultBehaviour){
                preserveDefaultBehaviour = true;
                Core.clearBankCache();
            }else{
                if(!Session.isActive()){
                    if(Core.initBankSession((AbstractContainerScreen<ChestMenu>) screen)){
                        curBankPage = 0;
                        isSearching = true;
                    }
                }
                else{
                    Session.setBacking((AbstractContainerScreen<ChestMenu>) screen);
//                    ScreenUtils.activateWithoutDestroy(screen);
//                    Core.loadBankPage();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSlotUpdate(ContainerSetSlotEvent.Post e){
        if(e.getSlot() == -1){
            if(e.getItemStack().isEmpty()) return;
            McUtils.sendMessageToClient(Component.literal(e.getItemStack().toString()));
            if(!Session.isActive()){
                InvUtils.itemSlotCallbacks.clear();
                return;
            }
            var snap = new ArrayList<>(InvUtils.itemSlotCallbacks);
            InvUtils.itemSlotCallbacks.clear();
            for(var comp : snap){
                comp.complete(e.getItemStack());
            }
            snap.clear();
        }
    }

    @SubscribeEvent
    public void onContainerSetContent(ContainerSetContentEvent.Post event) {
        if(!Session.isActive() || preserveDefaultBehaviour) return;

        McUtils.sendMessageToClient(Component.literal("pg-load"));

        var snapAny = new ArrayList<>(InvUtils.pageLoadCallbacks);
        InvUtils.pageLoadCallbacks.clear();
        for(var comp : snapAny){
            comp.complete(null);
        }
        snapAny.clear();

        if(isSearching){
            Core.loadBankPage();
            BankUtils.advancePage(()->{
                Core.initRimSession();
                isSearching = false;
            }, curBankPage);
            curBankPage++;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenClose(ScreenClosedEvent e) {
        preserveDefaultBehaviour = false;
        Core.destroySession();
        isSearching = false;
        curBankPage = 0;
    }
}
