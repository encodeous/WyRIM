/*
 * Copyright © Wynntils 2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 * This is a DUMMY file used for compilation ONLY
 */
package com.wynntils.services.discord;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Service;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.utils.mc.McUtils;
import java.time.Instant;
import java.util.List;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DiscordService extends Service {
    private static final long DISCORD_APPLICATION_ID = 0;
    private static final int TICKS_PER_UPDATE = 5;

    private int ticksUntilUpdate = 0;

    public DiscordService() {
        super(List.of());
    }

    public boolean load() {
        return true;
    }

    public boolean isReady() {
        return true;
    }

    public void unload() {

    }

    private void createCore() {

    }

    public void setDetails(String details) {

    }

    public void setImage(String imageId) {

    }

    public void setImageText(String text) {

    }

    public void setWynncraftLogo() {

    }

    public void setState(String state) {

    }

    @SubscribeEvent
    public void onTick(TickEvent event) {

    }
}
