/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 * This is a DUMMY file used for compilation ONLY
 */
package com.wynntils.services.hades;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.core.components.Service;
import com.wynntils.core.components.Services;
import com.wynntils.features.players.HadesFeature;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.character.event.CharacterUpdateEvent;
import com.wynntils.models.players.event.HadesRelationsUpdateEvent;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import com.wynntils.services.athena.event.AthenaLoginEvent;
import com.wynntils.services.hades.event.HadesEvent;
import com.wynntils.services.hades.type.PlayerStatus;
import com.wynntils.utils.mc.McUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class HadesService extends Service {
    private static final int TICKS_PER_UPDATE = 2;
    private static final int MS_PER_PING = 1000;

    private final HadesUserRegistry userRegistry = new HadesUserRegistry();
    private int tickCountUntilUpdate = 0;
    private PlayerStatus lastSentStatus;
    private ScheduledExecutorService pingScheduler;

    public HadesService() {
        super(List.of());
    }

    public Stream<HadesUser> getHadesUsers() {
        return userRegistry.getHadesUserMap().values().stream();
    }

    private void login() {
    }

    public void tryDisconnect() {
    }

    @SubscribeEvent
    public void onAuth(HadesEvent.Authenticated event) {
    }

    @SubscribeEvent
    public void onDisconnect(HadesEvent.Disconnected event) {
    }

    @SubscribeEvent
    public void onWorldStateChange(WorldStateEvent event) {
    }

    @SubscribeEvent
    public void onAthenaLogin(AthenaLoginEvent event) {
        if (Models.WorldState.getCurrentState() != WorldState.NOT_CONNECTED && !isConnected()) {
            if (Services.WynntilsAccount.isLoggedIn()) {
                login();
            }
        }
    }

    @SubscribeEvent
    public void onClassChange(CharacterUpdateEvent event) {
        tryResendWorldData();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {

    }

    public void tryResendWorldData() {
    }
    public void resetHadesUsers() {
        userRegistry.getHadesUserMap().clear();
    }

    private boolean isConnected() {
        return false;
    }
}
