package ca.encodeous.wyrim.mixin;

import ca.encodeous.wyrim.RimServices;
import ca.encodeous.wyrim.services.RimCoreService;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Shadow
    private ItemStack carried;
    @Inject(method = "clicked(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD"))
    private void injectMethod(CallbackInfo info) {
        if(RimCoreService.isInjectionMode)
            carried = ItemStack.EMPTY.copyWithCount(-1);
    }
}
