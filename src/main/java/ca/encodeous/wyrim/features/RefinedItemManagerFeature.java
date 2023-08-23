package ca.encodeous.wyrim.features;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.ScreenUtils;
import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.mc.event.ScreenClosedEvent;
import com.wynntils.mc.event.ScreenInitEvent;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import static ca.encodeous.wyrim.RimServices.*;

public class RefinedItemManagerFeature extends Feature {
    private boolean isSearching = false;
    private boolean preserveDefaultBehaviour = false;

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

    @SubscribeEvent
    public void onContainerSetContent(ContainerSetContentEvent.Post event) {
        if(!Session.isActive() || preserveDefaultBehaviour) return;

        if(isSearching){
            Core.loadBankPage();
            BankUtils.advancePage(()->{
                Core.initRimSession();
                isSearching = false;
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenClose(ScreenClosedEvent e) {
        preserveDefaultBehaviour = false;
        Core.destroySession();
    }
}
