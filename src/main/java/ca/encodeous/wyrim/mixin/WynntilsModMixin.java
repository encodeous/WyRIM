package ca.encodeous.wyrim.mixin;

import ca.encodeous.wyrim.WyRIM;
import com.wynntils.core.WynntilsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WynntilsMod.class)
public class WynntilsModMixin {
    @Inject(method = "Lcom/wynntils/core/WynntilsMod;onResourcesFinishedLoading()V", at = @At("TAIL"), remap = false)
    private static void onResourcesFinishedLoading(CallbackInfo info) {
        WyRIM.Instance.onWynntilsInitialize();
    }
}
