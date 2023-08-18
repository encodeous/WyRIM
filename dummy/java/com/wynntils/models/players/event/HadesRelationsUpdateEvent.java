/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 * This is a DUMMY file used for compilation ONLY
 */
package com.wynntils.models.players.event;

import java.util.Set;
import net.minecraftforge.eventbus.api.Event;

public abstract class HadesRelationsUpdateEvent extends Event {
    private final Set<String> changedPlayers;
    private final ChangeType changeType;

    protected HadesRelationsUpdateEvent(Set<String> changedPlayers, ChangeType changeType) {

    }

    public Set<String> getChangedPlayers() {
        return changedPlayers;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public static class FriendList extends HadesRelationsUpdateEvent {
        public FriendList(Set<String> changedPlayers, ChangeType changeType) {
            super(changedPlayers, changeType);
        }
    }

    public static class PartyList extends HadesRelationsUpdateEvent {
        public PartyList(Set<String> changedPlayers, ChangeType changeType) {
            super(changedPlayers, changeType);
        }
    }

    public enum ChangeType {
        ADD,
        REMOVE,
        RELOAD; // This is used to indicate that we have a new fully parsed relations list
    }
}
