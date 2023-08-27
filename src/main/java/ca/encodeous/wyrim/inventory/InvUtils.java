package ca.encodeous.wyrim.inventory;

import ca.encodeous.wyrim.engine.RetryState;
import ca.encodeous.wyrim.engine.interaction.RimItemOrigin;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import ca.encodeous.wyrim.services.RimCoreService;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.components.Managers;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.item.ItemAnnotation;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GuiItem;
import com.wynntils.utils.mc.McUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.vidageek.mirror.dsl.Mirror;
import org.lwjgl.glfw.GLFW;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static ca.encodeous.wyrim.RimServices.Session;
import static ca.encodeous.wyrim.RimServices.Storage;

public class InvUtils {
    public static ArrayList<CompletableFuture<ItemStack>> itemSlotCallbacks = new ArrayList<>();
    public static ArrayList<CompletableFuture<Void>> pageLoadCallbacks = new ArrayList<>();
    private static HashSet<Integer> restrictedSlots = new HashSet<>(List.of(87, 88, 89, 54, 55, 56, 57, 58));
    public static CompletableFuture<ItemStack> waitForCarryUpdate(int expectedUpdates){
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            return new ItemStack(Items.STICK, 0);
//        });
        if(expectedUpdates == 0) return CompletableFuture.completedFuture(ItemStack.EMPTY);
        if(expectedUpdates == 1){
            var comp = new CompletableFuture<ItemStack>();
            itemSlotCallbacks.add(comp);
            return comp;
        }
        else{
            return waitForCarryUpdate(expectedUpdates - 1).thenCompose(x ->
                    waitForCarryUpdate(1)
            );
        }
    }

    public static CompletableFuture<Void> waitForPageLoad(int expectedUpdates){
        if(expectedUpdates == 0) return CompletableFuture.completedFuture(null);
        if(expectedUpdates == 1){
            var comp = new CompletableFuture<Void>();
            pageLoadCallbacks.add(comp);
            return comp;
        }
        else{
            return waitForPageLoad(expectedUpdates - 1).thenCompose(x ->
                    waitForPageLoad(1)
            );
        }
    }
    public static int mapUiToSlotId(int id){
        if(id <= 53){
            return id;
        }else{
            id -= 53;
            if(id >= 28){
                id = id - 28;
            }else{
                id += 8;
            }
        }
        return id;
    }

    public static int mapSlotToUiId(int id){
        var oid = id;
        if(id <= 8){
            id = id + 28;
        }else{
            id -= 8;
        }
        id += 53;
        assert oid == mapUiToSlotId(id);
        return id;
    }

    public static boolean isBank(int id){
        return id <= 53;
    }

    public static boolean isBank(RimItemOrigin origin){
        return origin.backingSource == RimItemOrigin.ItemSource.BANK;
    }

    public static int getBankPage(RimItemOrigin origin){
        // items.add(new RimMappedItem(itemStack, i + page * container.getContainerSize()));
        var contSize = Session.getBacking().getMenu().getContainer().getContainerSize();
        return origin.slotId / contSize;
    }

    public static int getBankSlotId(RimItemOrigin origin){
        // items.add(new RimMappedItem(itemStack, i + page * container.getContainerSize()));
        var contSize = Session.getBacking().getMenu().getContainer().getContainerSize();
        return origin.slotId % contSize;
    }

    public static int getBankSlotId(int mappedId){
        // items.add(new RimMappedItem(itemStack, i + page * container.getContainerSize()));
        var contSize = Session.getBacking().getMenu().getContainer().getContainerSize();
        return mappedId % contSize;
    }

    public static int mapToAppropriateId(RimItemOrigin origin){
        if(origin.backingSource == RimItemOrigin.ItemSource.BANK){
            return getBankSlotId(origin.slotId);
        }else{
            return mapSlotToUiId(origin.slotId);
        }
    }

    public static Optional<RimMappedItem> getItemUniversal(int id){
        if(isBank(id)){
            int i = Session.getFront().getMenu().getRowIndexForScroll(Session.getFront().scrollOffs);
            int k = id % 9;
            int j = id / 9;
            int l = k + (j + i) * 9;
            if (l >= 0 && l < Storage.displayedItemPool.size()) {
                return Optional.ofNullable(Storage.displayedItemPool.get(l));
            }
            else{
                return Optional.empty();
            }
        }else{
            id = mapUiToSlotId(id);
            int finalId = id;
            return Storage.invItemPool.stream().filter(x -> x.originSlot == finalId).findFirst();
        }
    }

    public static ItemStack getItemBacking(int id){
        if(isBank(id)){
            return Storage.bank.getItem(id);
        }else{
            id = mapUiToSlotId(id);
            return McUtils.player().getInventory().getItem(id);
        }
    }

    public static ItemStack copyItemStack(ItemStack itemStack){
        var nItem = itemStack.copy();
        StyledText name = StyledText.fromComponent(itemStack.getHoverName()).getNormalized();
        ItemAnnotation annotation = (ItemAnnotation) new Mirror().on(Handlers.Item).invoke().method("calculateAnnotation").withArgs(itemStack, name);
        if (annotation == null) return nItem;

        Handlers.Item.updateItem(nItem, annotation, name);
        return nItem;
    }

    public static Optional<ItemAnnotation> getWynnAnnotation(ItemStack itemStack){
        var nItem = itemStack.copy();
        StyledText name = StyledText.fromComponent(itemStack.getHoverName()).getNormalized();
        ItemAnnotation annotation = (ItemAnnotation) new Mirror().on(Handlers.Item).invoke().method("calculateAnnotation").withArgs(itemStack, name);
        if (annotation == null) return Optional.empty();

        Handlers.Item.updateItem(nItem, annotation, name);
        return Optional.of(annotation);
    }

    public static boolean isRestrictedItem(ItemStack item, int slotId){
        if(item.isEmpty()) return false;
        var annotation = InvUtils.getWynnAnnotation(item);
        // block fallback annotator
        return (annotation.isEmpty() || annotation.get().getClass() == WynnItem.class || annotation.get() instanceof GuiItem) && restrictedSlots.contains(slotId);
    }



    private static CompletableFuture<Boolean> tryEquate(int updateCnt, ItemStack expected){
        return waitForCarryUpdate(updateCnt).thenCompose(pickedUpItem ->{
            if(pickedUpItem.count == expected.count && ItemStack.isSameItemSameTags(pickedUpItem, expected)
                    || expected.isEmpty() && pickedUpItem.isEmpty()){
                return CompletableFuture.completedFuture(true);
            }
            if(pickedUpItem.count == 0 && pickedUpItem.isEmpty()) return CompletableFuture.completedFuture(true);
            return CompletableFuture.completedFuture(false);
        });
    }

    public static CompletableFuture<Boolean> interact(int id, int button, ItemStack expected, int expectedUpdates){
        guiClick(id, button);
        return tryEquate(expectedUpdates, expected);
    }

    public static void guiClick(int id, int button){
        var player = McUtils.player();
//        RimCoreService.isInjectionMode = true;
        var menu = player.containerMenu;
        var before = menu.getCarried();
        player.containerMenu = Session.getBacking().getMenu();
        var slot = menu.getSlot(id);
        Session.getBacking().slotClicked(slot, id, button, ClickType.PICKUP);
        Session.getBacking().lastClickTime = 0;
        player.containerMenu = menu;
        RimCoreService.isInjectionMode = false;
        menu.setCarried(before);
    }

    public static CompletableFuture<Boolean> transfer(int idSrc, int idDst, int amount, ItemStack src, ItemStack dst){
        return transfer(src, dst, idSrc, idDst, amount)
                .thenCompose(x->{
                    if(!x){
                        return interact(idDst, GLFW.GLFW_MOUSE_BUTTON_LEFT, ItemStack.EMPTY, 0)
                                .thenCompose(y->{
                                    if(!y)
                                        throw new RuntimeException("Unable to restore item back into the original slot");
                                    return CompletableFuture.completedFuture(false);
                                });
                    }
                    return CompletableFuture.completedFuture(true);
                });
//        var player = McUtils.player();
//        var menu = player.containerMenu;
//        var slot = menu.getSlot(idSrc);
//        var slot2 = menu.getSlot(idDst);
//        return transfer(slot.getItem(), slot2.getItem(), idSrc, idDst, amount);
    }

    private static CompletableFuture<Boolean> transfer(ItemStack src, ItemStack dst, int idSrc, int idDst, int amount){
        if(amount < 0) return transfer(dst, src, idDst, idSrc, -amount);
//        McUtils.sendMessageToClient(Component.literal("t-to " + idDst + " x " + amount));
        src = src.copy();
        dst = dst.copy();
        int srcAmt = src.count;
        int dstAmt = dst.count;
        if(dst.isEmpty()){
            dstAmt = 0;
            dst.setCount(0);
        }
        if(srcAmt < amount){
            return CompletableFuture.completedFuture(false);
        }

        ItemStack held = src.copy();

        int taken; // taken is always >= amount
        int btn = -1;

        var halfTransfer = (int) Math.ceil(srcAmt / 2.0);

        if(Math.log(Math.abs(srcAmt / 2 - amount)) < Math.abs(srcAmt - amount) && halfTransfer >= amount){
            taken = halfTransfer;
            held.setCount(taken);
            src.shrink(srcAmt - taken);
            btn = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        }else{
            taken = srcAmt;
            src.shrink(taken);
            btn = GLFW.GLFW_MOUSE_BUTTON_LEFT;
        }

        // leave items in the original slot
        int extra = taken - amount;

        // could be improved: improve heuristic on stack splitting
        int heurMoveStackSplitCost = Math.abs((dstAmt + taken) / 2 - (amount + dstAmt));

        CompletableFuture<Boolean> cComp = interact(idSrc, btn, held, 1);

        if(extra == 0){
            return cComp.thenCompose(r->{
                if(!r) return CompletableFuture.completedFuture(false);
                // store items into slot
                return interact(idDst, GLFW.GLFW_MOUSE_BUTTON_LEFT, ItemStack.EMPTY, 0);
            });
        }

        if(amount < Math.abs(extra) && amount <= heurMoveStackSplitCost || amount < 5){
            // manually add
            return massTransferAndStore(cComp, idDst, idSrc, held, amount);
        }

        if (Math.abs(extra) < heurMoveStackSplitCost || Math.abs(extra) < 5){
            // manually add items back
            return massTransferAndStore(cComp, idSrc, idDst, held, extra);
        }
        cComp = cComp.thenCompose(r->{
            if(!r) return CompletableFuture.completedFuture(false);
            // store items into slot
            return interact(idDst, GLFW.GLFW_MOUSE_BUTTON_LEFT, ItemStack.EMPTY, 0);
        });
        dst = held.copyWithCount(dstAmt);
        dst.grow(held.count);
        int remainder = amount - dst.count + dstAmt;
        if(dst.count != dstAmt + amount){
            // transfer item back from slot by transfer
            ItemStack finalDst = dst;
            ItemStack finalSrc1 = src;
            cComp = cComp.thenCompose(r->{
                if(!r) return CompletableFuture.completedFuture(false);
                var completion = new CompletableFuture<Boolean>();
                Managers.TickScheduler.scheduleLater(()->{
                    transfer(finalSrc1, finalDst, idSrc, idDst, remainder)
                            .thenAccept(completion::complete);
                }, 1);
                return completion;
            });
        }
        return cComp;
    }

    private static CompletableFuture<Boolean> massTransferAndStore(CompletableFuture<Boolean> runAfter, int dest, int store, ItemStack heldItem, int times){
//        McUtils.sendMessageToClient(Component.literal("mt-to " + dest + " x " + times));
        for(int i = 0; i < times; i++){
            runAfter = runAfter.thenCompose(r ->{
                if(!r) return CompletableFuture.completedFuture(false);
                heldItem.shrink(1);
                return interact(dest, GLFW.GLFW_MOUSE_BUTTON_RIGHT, heldItem.copy(), 2);
            });
        }
        runAfter = runAfter.thenCompose(r->{
            if(!r) return CompletableFuture.completedFuture(false);
            // store items back into slot
            return interact(store, GLFW.GLFW_MOUSE_BUTTON_LEFT, ItemStack.EMPTY, 0);
        });
        return runAfter;
    }
}
