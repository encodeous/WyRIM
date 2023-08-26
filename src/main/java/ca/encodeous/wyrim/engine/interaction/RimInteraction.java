package ca.encodeous.wyrim.engine.interaction;

import net.minecraft.world.item.ItemStack;

public class RimInteraction {

    public RimItemOrigin src;

    public RimInteraction(RimItemOrigin src, ItemStack srcStack, RimItemOrigin dst, ItemStack dstStack, int amount) {
        this.src = src;
        this.srcStack = srcStack;
        this.dst = dst;
        this.dstStack = dstStack;
        this.amount = amount;
    }

    public ItemStack srcStack;
    public RimItemOrigin dst;
    public ItemStack dstStack;
    public int amount;
}
