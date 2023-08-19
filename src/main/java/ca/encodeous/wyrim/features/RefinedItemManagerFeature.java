package ca.encodeous.wyrim.features;

import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.mc.event.ScreenInitEvent;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RefinedItemManagerFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenInit(ScreenInitEvent e) {
        if (!(e.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

        if(Models.Container.isBankScreen(screen)){
            McUtils.sendMessageToClient(Component.literal("Bank gui caught!"));
            e.setCanceled(true);
        }
    }
}
