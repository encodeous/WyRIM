package ca.encodeous.wyrim.ui;

import ca.encodeous.wyrim.models.graph.ItemSnapshot;
import ca.encodeous.wyrim.models.graph.ItemTransaction;
import ca.encodeous.wyrim.models.graph.RimItemPointer;
import ca.encodeous.wyrim.models.graph.RimSlotPointer;
import ca.encodeous.wyrim.models.state.RimContainer;
import ca.encodeous.wyrim.models.state.RimInventory;
import ca.encodeous.wyrim.services.ItemStorageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import static ca.encodeous.wyrim.models.state.RimContainer.*;

import static ca.encodeous.wyrim.RimServices.*;
public class RimMenu extends AbstractContainerMenu {

    public RimContainer rim;
    public RimInventory inv;
    protected RimMenu(Player player) {
        super(null, 0);

        rim = new RimContainer();
        inv = new RimInventory(McUtils.player());

        for (int i = 0; i < CONTAINER_ROWS; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(rim, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
        }

        int k = (CONTAINER_ROWS - 4) * SLOT_SIZE;

        var playerInv = player.getInventory();

        for (int l = 0; l < 3; l++) {
            for (int m = 0; m < 9; m++) {
                addSlot(new Slot(inv, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
                var item = playerInv.getItem(m + l * 9 + 9);
                inv.setItem(m + l * 9 + 9, playerInv.getItem(m + l * 9 + 9));
                Storage.addItem(ItemSnapshot.createRoot(new RimItemPointer(m + l * 9 + 9,
                        item, RimSlotPointer.ItemSource.PLAYER)));
            }
        }
        for (int l = 0; l < 9; l++) {
            addSlot(new Slot(inv, l, 8 + l * 18, 161 + k));
            var item = playerInv.getItem(l);
            inv.setItem(l, playerInv.getItem(l));
            Storage.addItem(ItemSnapshot.createRoot(new RimItemPointer(l,
                    item, RimSlotPointer.ItemSource.PLAYER)));
        }

        scrollTo(0.0F);
    }

    public void refresh() {
        scrollTo(Session.getFront().rimScreen.scrollOffs);
    }

    public void refreshInv(){
        for(var item : Storage.getInventoryItems()){
            inv.setItem(item.backingSlotId, item.item);
        }
        invUpdated.run();
        invUpdated = () -> { };
    }

    protected int calculateRowCount() {
        return Mth.positiveCeilDiv(Storage.filteredItems.size(), 9) - 6;
    }

    public int getRowIndexForScroll(float f) {
        return Math.max((int) ((f * calculateRowCount()) + 0.5D), 0);
    }

    protected float getScrollForRowIndex(int i) {
        return Mth.clamp(i / calculateRowCount(), 0.0F, 1.0F);
    }

    protected float subtractInputFromScroll(float f, double d) {
        return Mth.clamp(f - (float) (d / calculateRowCount()), 0.0F, 1.0F);
    }

    public void scrollTo(float f) {
        int scrollIndex = getRowIndexForScroll(f);
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                int index = column + (row + scrollIndex) * 9;
                var displayItems = Storage.filteredItems;
                if (index >= 0 && index < displayItems.size()) {
                    rim.setItem(column + row * 9, displayItems.get(index).item);
                } else {
                    rim.setItem(column + row * 9, ItemStack.EMPTY);
                }
            }
        }
    }

    public boolean canScroll() {
        return (Storage.filteredItems.size() > 45);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void setCarried(ItemSnapshot snapshot){
        Storage.carriedItemUi = snapshot;
        setCarried(snapshot.item);
    }

    private Runnable invUpdated = () -> { };
    @Override
    public void clicked(final int id, final int dragType, final ClickType clickType, final Player player) {
        if(id == -999) return;

        Container container;

        int relId = id;

        if(id <= 53){
            container = rim;
        }else{
            container = inv;
            // the inventory isnt in the right order, we must fix it...
            relId = ItemStorageService.mapUiToPlayerId(id);
        }

        McUtils.sendMessageToClient(Component.literal(
                clickType.toString() + relId)
        );

        if(relId < 0) return;

        var retrieved = Storage.getItemFromContainer(id);
        if(retrieved.isEmpty()) return;
        var itemInSlot = retrieved.get();
        var carriedRim = Storage.carriedItemUi;

        if(clickType == ClickType.PICKUP){
            var mouseType = dragType == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            var slotItem = container.getItem(relId);
            if(getCarried().isEmpty()){
                // pick up new stack
                if(mouseType == ClickAction.PRIMARY){
                    // whole stack - ONE_TO_ONE
                    setCarried(itemInSlot);
                    container.setItem(relId, ItemStack.EMPTY);
                }else{
                    var taken = (int) ((slotItem.count) / 2.0 + 0.5);
                    var rem = slotItem.count - taken;
                    setCarried(slotItem.split(taken)); // round up
                    if(rem == 0){
                        // move - ONE_TO_ONE
                        container.setItem(relId, ItemStack.EMPTY);
                    }else{
                        // half stack - ONE_TO_MANY
                        container.setItem(relId, slotItem.split(rem));
                    }
                }
            }
            else{
                var handStack = getCarried();
                if(slotItem.sameItem(handStack)){
                    // merge item
                    var sum = slotItem.count + handStack.count;

                    var capacity = (slotItem.count > slotItem.getMaxStackSize() || // wynncraft does funny things with items, we can only predict what might happen
                            handStack.count > handStack.getMaxStackSize() ||
                            slotItem.getMaxStackSize() == 64)
                            ? 64 : slotItem.getMaxStackSize();

                    if(sum <= capacity){
                        // merge stacks - MANY_TO_ONE
                        slotItem.setCount(sum);
                        setCarried(ItemStack.EMPTY);
                    } else if(slotItem.count != capacity) {
                        slotItem.setCount(capacity);
                        handStack.setCount(sum - capacity);
                    }
                }
                else if(slotItem.isEmpty()){
                    // move item - ONE_TO_EMPTY
                    container.setItem(relId, handStack);
                    setCarried(ItemStack.EMPTY);
                }else{
                    // swap item - ONE_TO_ONE
                    container.setItem(relId, handStack);
                    setCarried(slotItem);
                    ItemTransaction.swapSlot(carriedRim, itemInSlot);
                    invUpdated = () -> {
                        setCarried(Storage.getItem(carriedRim));
                        container.setItem(carriedRim.backingSlotId, ItemStack.EMPTY); // mask the original
                    };
                }
            }
        }

        if(true) return;

        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        NonNullList<Slot> nonNullList = abstractContainerMenu.slots;
        int l = nonNullList.size();
        List<ItemStack> list = new ArrayList<>(l);
        for (Slot slot : nonNullList) {
            list.add(slot.getItem().copy());
        }

//        Storage.carriedItemUi = this.getCarried();

        super.clicked(id, dragType, clickType, player);

        Int2ObjectOpenHashMap<ItemStack> int2ObjectOpenHashMap = new Int2ObjectOpenHashMap<>();
        for (int m = 0; m < l; m++) {
            ItemStack itemStack = list.get(m);
            ItemStack itemStack2 = ((Slot) nonNullList.get(m)).getItem();
            if (!ItemStack.matches(itemStack, itemStack2)) {
                int2ObjectOpenHashMap.put(m, itemStack2.copy());
            }
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeSpecialFloatingPointValues()
                .serializeNulls()
                .create();

        var menu = Session.getBacking().bankScreen.getMenu();

        var carried = abstractContainerMenu.getCarried().copy();
        carried.setCount(64);

        var packet = new ServerboundContainerClickPacket(menu.containerId, menu.getStateId(),
                id, dragType, clickType, carried,
                int2ObjectOpenHashMap);
        McUtils.sendMessageToClient(Component.literal(
                ToStringBuilder.reflectionToString(packet))
        );
        McUtils.sendPacket(packet);
    }

}