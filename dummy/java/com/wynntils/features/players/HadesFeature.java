/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 * This is a DUMMY file used for compilation ONLY
 */
package com.wynntils.features.players;

import com.wynntils.core.components.Models;
import com.wynntils.core.components.Services;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.persisted.Persisted;
import com.wynntils.core.persisted.config.Category;
import com.wynntils.core.persisted.config.Config;
import com.wynntils.core.persisted.config.ConfigCategory;

@ConfigCategory(Category.PLAYERS)
public class HadesFeature extends Feature {
    @Persisted
    public final Config<Boolean> getOtherPlayerInfo = new Config<>(true);

    @Persisted
    public final Config<Boolean> shareWithParty = new Config<>(true);

    @Persisted
    public final Config<Boolean> shareWithFriends = new Config<>(true);

    @Persisted
    public final Config<Boolean> shareWithGuild = new Config<>(true);

    @Override
    protected void onConfigUpdate(Config<?> config) {

    }
}
