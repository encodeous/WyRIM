package ca.encodeous.wyrim.models.ui.client;

import ca.encodeous.wyrim.models.item.WyRimMappedItem;
import ca.encodeous.wyrim.ui.WyRimScreen;
import net.minecraft.core.NonNullList;

public class RimSession {
    public WyRimScreen rimScreen;
    public final NonNullList<WyRimMappedItem> items = NonNullList.create();

    public void setRimScreen(WyRimScreen rimScreen) {
        this.rimScreen = rimScreen;
    }
}
