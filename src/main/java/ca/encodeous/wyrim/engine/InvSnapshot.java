package ca.encodeous.wyrim.engine;

import ca.encodeous.wyrim.inventory.BankUtils;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Models;
import com.wynntils.models.containers.type.SearchableContainerType;

import java.util.ArrayList;

import static ca.encodeous.wyrim.RimServices.Storage;

public class InvSnapshot {
    public ArrayList<ArrayList<RimMappedItem>> pages;
    public ArrayList<RimMappedItem> inv;
    private InvSnapshot(){

    }
    public static InvSnapshot makeSnapshot(){
        var snapshot = new InvSnapshot();
        snapshot.pages = new ArrayList<>();
        var bankIdx = 0;
        for(int i = 0; i < BankUtils.maxBankPage; i++){
            var curPage = new ArrayList<RimMappedItem>();
            for(var slot : SearchableContainerType.BANK.getBounds().getSlots()){
                var bankItem = Storage.bankItemPool.get(bankIdx);
                if(bankItem.originSlot != slot){
                    System.out.println("ERROR: Mismatch bank item index");
                    continue;
                }
                curPage.add(bankItem.copy());
            }
            snapshot.pages.add(curPage);
        }
        snapshot.inv.addAll(Storage.invItemPool.stream().map(RimMappedItem::copy).toList());
        return snapshot;
    }
}
