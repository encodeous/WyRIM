/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 * This is a DUMMY file used for compilation ONLY
 */
package com.wynntils.services.hades;

import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.type.PoiLocation;
import com.wynntils.utils.type.CappedValue;
import java.util.UUID;

public class HadesUser {
    private final UUID uuid = null;
    private final String name = null;

    private boolean isPartyMember;
    private boolean isMutualFriend;
    private boolean isGuildMember;
    private float x, y, z;
    private PoiLocation poiLocation;
    private CappedValue health;
    private CappedValue mana;

    // Dummy constructor for previews
    public HadesUser(String name, CappedValue health, CappedValue mana) {

    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isPartyMember() {
        return isPartyMember;
    }

    public boolean isMutualFriend() {
        return isMutualFriend;
    }

    public boolean isGuildMember() {
        return isGuildMember;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public PoiLocation getMapLocation() {
        return poiLocation;
    }

    public CappedValue getHealth() {
        return health;
    }

    public CappedValue getMana() {
        return mana;
    }

    public CustomColor getRelationColor() {
        return null;
    }
}
