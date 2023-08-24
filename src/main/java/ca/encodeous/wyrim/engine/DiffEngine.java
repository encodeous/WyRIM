package ca.encodeous.wyrim.engine;

import ca.encodeous.wyrim.inventory.InvUtils;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class DiffEngine {
    public InvSnapshot appliedSnapshot;
    public void markChanged(){

    }

    private void computeSnapshotDifference(InvSnapshot newSnapshot){

    }

    /**
     * Fulfills the movements between pages first
     */
    private void fulfillPagewiseDifference(){

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
                if(o.count < c.count){
                    nis.setCount(c.count - o.count);
                    diff.added.add(
                            new RimMappedItem(
                                    nis,
                                    i
                            )
                    );
                }
                else if(o.count > c.count){
                    nis.setCount(o.count - c.count);
                    diff.removed.add(
                            new RimMappedItem(
                                    nis,
                                    i
                            )
                    );
                }
            }
        }
        return diff;
    }

    private int computeDistance(int posId1, int posId2){
        if(posId1 == posId2) return 0;
        if(posId1 == -1 || posId2 == -1) return 0;
        return Math.abs(posId1 - posId2);
    }

    private static class SlotDiff {
        public ArrayList<RimMappedItem> added = new ArrayList<>();
        public ArrayList<RimMappedItem> removed = new ArrayList<>();
        public int posId;
    }
}
