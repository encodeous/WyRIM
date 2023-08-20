package ca.encodeous.wyrim.models.ui.client;

import ca.encodeous.wyrim.models.item.RimMappedItem;
import ca.encodeous.wyrim.ui.RimScreen;
import net.minecraft.core.NonNullList;

public class RimSession {
    public RimScreen rimScreen;
    public final NonNullList<RimMappedItem> items = NonNullList.create();

    public void setRimScreen(RimScreen rimScreen) {
        this.rimScreen = rimScreen;
    }
}
