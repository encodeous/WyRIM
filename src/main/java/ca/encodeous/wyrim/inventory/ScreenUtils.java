package ca.encodeous.wyrim.inventory;

import com.mojang.blaze3d.vertex.BufferUploader;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ScreenUtils {
    public static void activateWithoutDestroy(AbstractContainerScreen<?> screen){
        var player = McUtils.player();
        player.containerMenu = screen.getMenu();
        var mc = McUtils.mc();
        mc.screen = screen;
        screen.added();

        BufferUploader.reset();
        mc.mouseHandler.releaseMouse();
        KeyMapping.releaseAll();
        screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        mc.noRender = false;
        mc.updateTitle();
    }

    public static void initWithoutChange(Screen screen){
        var mc = McUtils.mc();
        screen.added();

        BufferUploader.reset();
        mc.mouseHandler.releaseMouse();
        KeyMapping.releaseAll();
        screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        mc.noRender = false;
        mc.updateTitle();
    }

    public static void activate(AbstractContainerScreen<?> screen){
        var player = McUtils.player();
        player.containerMenu = screen.getMenu();
        var mc = McUtils.mc();
        mc.screen = screen;
        BufferUploader.reset();
        if(!screen.initialized){
            screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }
    }
}
