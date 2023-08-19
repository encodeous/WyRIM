package ca.encodeous.wyrim;

import ca.encodeous.wyrim.features.RefinedItemManagerFeature;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Managers;
import com.wynntils.core.consumers.features.FeatureManager;
import net.fabricmc.api.ModInitializer;
import net.vidageek.mirror.dsl.Mirror;

public class WyRIM implements ModInitializer {
    public static WyRIM Instance;
    @Override
    public void onInitialize() {
        Instance = this;

    }

    public void onWynntilsInitialize(){
        new Mirror().on(Managers.Feature).invoke()
                .method("registerFeature").withArgs(new RefinedItemManagerFeature());
    }
}
