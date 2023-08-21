package ca.encodeous.wyrim.services;

import ca.encodeous.wyrim.RimServices;
import ca.encodeous.wyrim.models.graph.ItemSnapshot;
import ca.encodeous.wyrim.models.graph.RimSlotPointer;
import com.wynntils.core.components.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ca.encodeous.wyrim.RimServices.Session;

public class ItemStorageService extends Service {
    public ItemStorageService() {
        super(List.of());
    }

    public ItemSnapshot carriedItemUi;
    public final ArrayList<ItemSnapshot> rimItems = new ArrayList<>();
    public void addItem(ItemSnapshot snapshot){
        rimItems.add(snapshot);
    }
    public List<ItemSnapshot> getItems(){
        return List.copyOf(rimItems);
    }
    public List<ItemSnapshot> getStoredItems(){
        return rimItems.stream().filter(x->x.backingSource == RimSlotPointer.ItemSource.BANK).toList();
    }
    public List<ItemSnapshot> getInventoryItems(){
        return rimItems.stream().filter(x->x.backingSource == RimSlotPointer.ItemSource.PLAYER).toList();
    }
    public void updateRim(){
        RimServices.Core.predicatesUpdated();
        Session.getFront().rimScreen.getMenu().refreshInv();
    }
    public ArrayList<ItemSnapshot> filteredItems = new ArrayList<>();
    public static int mapUiToPlayerId(int id){
        id -= 53;
        if(id >= 28){
            id = id - 28;
        }else{
            id += 8;
        }
        return id;
    }
    public Optional<ItemSnapshot> getItemFromContainer(int id){
        if(id <= 53){
            var column = id % 9;
            var row = id / 9;
            var screen = Session.getFront().rimScreen;
            var scrollIndex = screen.getMenu().getRowIndexForScroll(screen.scrollOffs);
            int index = column + (row + scrollIndex) * 9;
            if(index >= filteredItems.size()){
                return Optional.empty();
            }
            return Optional.ofNullable(filteredItems.get(index));
        }
        else{
            return Optional.of(rimItems.stream()
                    .filter(i -> i.backingSlotId == mapUiToPlayerId(id))
                    .findFirst().get());
        }
    }
    public ItemSnapshot getItem(RimSlotPointer pos){
        return rimItems.stream().filter(x->x.equals(pos)).findFirst().get();
    }
    public void destroyStorage(){
        rimItems.clear();
        filteredItems.clear();
    }
}
