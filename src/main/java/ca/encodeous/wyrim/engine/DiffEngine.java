package ca.encodeous.wyrim.engine;

import ca.encodeous.wyrim.RimServices;
import ca.encodeous.wyrim.engine.interaction.RimInteraction;
import ca.encodeous.wyrim.engine.interaction.RimItemOrigin;
import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.inventory.InvUtils;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Managers;
import net.minecraft.world.item.ItemStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import static ca.encodeous.wyrim.RimServices.Session;

public class DiffEngine {
    public InvSnapshot appliedSnapshot;

    public void initDiffApplicationEngine(){
        appliedSnapshot = InvSnapshot.makeSnapshot();
    }

    public CompletableFuture<Boolean> applySnapshot(){
        if(!Session.isActive()) CompletableFuture.completedFuture(false);
        var newSnapshot = InvSnapshot.makeSnapshot();
        var itxns = generateInteractions(newSnapshot);

        CompletableFuture<Boolean> cmpl = CompletableFuture.completedFuture(true);

        // apply interaction
        for(var itxn : itxns){
            cmpl = cmpl.thenCompose(x->{
                if(!x) return CompletableFuture.completedFuture(false);
                return applyInteraction(itxn);
            })
            .thenCompose(x->{
                var cCmpl = new CompletableFuture<Boolean>();
                Managers.TickScheduler.scheduleLater(()->{
                    cCmpl.complete(x);
                }, 2);
                return cCmpl;
            });
        }


        // replace snapshot
        appliedSnapshot = newSnapshot;
        return cmpl;
    }

    private CompletableFuture<Boolean> applyInteraction(RimInteraction itxn) {
        int srcId = InvUtils.mapToAppropriateId(itxn.src), dstId = InvUtils.mapToAppropriateId(itxn.dst);
        if (!InvUtils.isBank(itxn.src)
                || !InvUtils.isBank(itxn.dst)
                || InvUtils.getBankPage(itxn.src) == InvUtils.getBankPage(itxn.dst)
                && itxn.src.backingSource == itxn.dst.backingSource) {
            // single transfer
             CompletableFuture<Void> cmpl = CompletableFuture.completedFuture(null);
            if (InvUtils.isBank(itxn.src)) {
                cmpl = BankUtils.advanceToPage(InvUtils.getBankPage(itxn.src));
            } else if (InvUtils.isBank(itxn.dst)) {
                cmpl = BankUtils.advanceToPage(InvUtils.getBankPage(itxn.dst));
            }
            return cmpl.thenCompose(x -> InvUtils.transfer(srcId, dstId, itxn.amount, itxn.srcStack, itxn.dstStack));
        }

        // transfer to inv then transfer to slot
        var freeSlot = InvUtils.mapSlotToUiId(RimServices.Storage.findFreeInvSlot());
        return BankUtils.advanceToPage(InvUtils.getBankPage(itxn.src))
                .thenCompose(x -> InvUtils.transfer(srcId, freeSlot, itxn.amount, itxn.srcStack, itxn.dstStack))
                .thenCompose(x -> {
                    if (!x) return CompletableFuture.completedFuture(false);
                    return BankUtils.advanceToPage(InvUtils.getBankPage(itxn.dst))
                            .thenCompose(y -> InvUtils.transfer(freeSlot, dstId, itxn.amount, itxn.srcStack, itxn.dstStack));
                });
    }

    /**
     * Fulfills the movements within the page and the inventory first
     */
    private ArrayList<RimInteraction> generateInteractions(InvSnapshot newSnapshot){
        var differences = new ArrayList<SlotDiff>();
        var itxn = new ArrayList<RimInteraction>();
        for(int i = 0; i < appliedSnapshot.pages.size(); i++){
            differences.add(findDifference(appliedSnapshot.pages.get(i), newSnapshot.pages.get(i), i));
        }
        var invDiff = findDifference(appliedSnapshot.inv, newSnapshot.inv, -1);
        differences.add(invDiff);
        // fulfill bank additions
        for(int i = 0; i < appliedSnapshot.pages.size(); i++){
            var iDiff = differences.get(i);
            for(var added : iDiff.added){
                itxn.addAll(fulfill(added.getA(), added.getB(), differences, iDiff));
            }
        }
        // fulfill inv additions
        for(var added : invDiff.added){
            itxn.addAll(fulfill(added.getA(), added.getB(), differences, invDiff));
        }
        // TODO: assert that all diffs have been fulfilled
        return itxn;
    }

    private ArrayList<RimInteraction> fulfill(RimMappedItem added, ItemStack original, ArrayList<SlotDiff> differences, SlotDiff cur){
        int cnt = added.item.count;

        var itxn = new ArrayList<RimInteraction>();

        if(cnt != 0){
            var src = differences.stream()
                    .map(x-> new Pair<>(computeDistance(x.posId, cur.posId), x))
                    .sorted(Comparator.comparingInt(Pair::getA))
                    .map(Pair::getB)
                    .toList();
            for(var cSrc : src){
                if(cnt == 0) break;
                var match = cSrc.removed.stream().filter(x->ItemStack.isSameItemSameTags(x.getA().item, added.item)).toList();

                for(var mapped : match){
                    var item = mapped.getA();
                    var orig = mapped.getB();
                    int taken = Math.min(cnt, item.item.count);
                    if(taken != 0)
                        itxn.add(new RimInteraction(
                                new RimItemOrigin(
                                        item.originSlot, cSrc.posId == -1 ? RimItemOrigin.ItemSource.PLAYER : RimItemOrigin.ItemSource.BANK),
                                orig.copy(),
                                new RimItemOrigin(
                                        added.originSlot, cur.posId == -1 ? RimItemOrigin.ItemSource.PLAYER : RimItemOrigin.ItemSource.BANK),
                                original.copy(),
                                taken));
                    original.grow(taken);
                    item.item.shrink(taken);
                    orig.shrink(taken);
                    cnt -= taken;
                }
            }
        }

        added.item.setCount(cnt);

        return itxn;
    }

    /**
     * Computes the slot-wise difference in the items added or removed from the slots
     */
    private SlotDiff findDifference(ArrayList<RimMappedItem> original,
                                    ArrayList<RimMappedItem> current, int posId){
        var diff = new SlotDiff();
        diff.posId = posId;
        for(int i = 0; i < original.size(); i++){
            var o = original.get(i).item;
            var c = current.get(i).item;
            if(ItemStack.isSameItemSameTags(o, c)){
                var nis = InvUtils.copyItemStack(c);
                if(o.count < c.count) {
                    nis.setCount(c.count - o.count);
                    diff.added.add(
                            new Pair<>(
                                    new RimMappedItem(
                                            nis,
                                            original.get(i).originSlot
                                    ), o)
                    );
                }
                else if(o.count > c.count) {
                    nis.setCount(o.count - c.count);
                    diff.removed.add(
                            new Pair<>(
                                    new RimMappedItem(
                                            nis,
                                            original.get(i).originSlot
                                    ), o)
                    );
                }
            }else {
                if (!c.isEmpty())
                    diff.added.add(
                            new Pair<>(
                                    new RimMappedItem(
                                            InvUtils.copyItemStack(c),
                                            original.get(i).originSlot
                                    ), o)
                    );
                if (!o.isEmpty())
                    diff.removed.add(new Pair<>(
                            new RimMappedItem(
                                    InvUtils.copyItemStack(o),
                                    original.get(i).originSlot
                            ), o)
                    );
            }
        }
        return diff;
    }

    private int computeDistance(int posId1, int posId2){
        if(posId1 == posId2) return 1;
        if(posId1 == -1 || posId2 == -1) return 0;
        return Math.abs(posId1 - posId2) + 1;
    }

    private static class SlotDiff {
        public ArrayList<Pair<RimMappedItem, ItemStack>> added = new ArrayList<>();
        public ArrayList<Pair<RimMappedItem, ItemStack>> removed = new ArrayList<>();
        public int posId;
    }
}
