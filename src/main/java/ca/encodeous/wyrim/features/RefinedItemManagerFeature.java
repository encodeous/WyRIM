package ca.encodeous.wyrim.features;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.InvUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.mc.event.*;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onScreenInit(ScreenInitEvent e) {
        if (!(e.getScreen() instanceof AbstractContainerScreen<?> screen)) return;
        if (!(screen.getMenu() instanceof ChestMenu)) return;

        if(Models.Container.isBankScreen(screen)){
            handleScreen((AbstractContainerScreen<ChestMenu>) screen);
        }
    }

    @SubscribeEvent
    public void setScreen(ScreenOpenedEvent.Pre e) {
        if(!Session.isActive() || preserveDefaultBehaviour) return;
        var screen = e.getScreen();
        if(Models.Container.isBankScreen(screen)){
            handleScreen((AbstractContainerScreen<ChestMenu>) screen);
            if(!preserveDefaultBehaviour){
                e.setCanceled(true);
                ScreenUtils.initWithoutChange(screen);
                ScreenUtils.initWithoutChange(Session.getFront());
            }
        }
        if(e.getScreen() instanceof PauseScreen){
            preserveDefaultBehaviour = false;
            Core.destroySession();
            isSearching = false;
        }
    }

    private void handleScreen(AbstractContainerScreen<ChestMenu> screen) {
        if(McUtils.player().isCrouching() || preserveDefaultBehaviour){
            preserveDefaultBehaviour = true;
            Core.clearBankCache();
        }else{
            if(!Session.isActive()){
                if(Core.initBankSession(screen)){
                    isSearching = true;
                }
            }
            else{
                Session.setBacking(screen);
            }
        }
    }

    private void handlePageUpdate() {
        if(!Session.isActive() || preserveDefaultBehaviour) return;

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
            }, Models.Container.getCurrentBankPage(Session.bankScreen) - 1);
        }

        ScreenUtils.activate(Session.getFront());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSlotUpdate(ContainerSetSlotEvent.Post e){
        if(e.getSlot() == -1){
            if(e.getItemStack().isEmpty()) return;
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
        handlePageUpdate();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenClose(ScreenClosedEvent e) {
        preserveDefaultBehaviour = false;
        Core.destroySession();
        isSearching = false;
    }
}
