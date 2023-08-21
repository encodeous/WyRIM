package ca.encodeous.wyrim.models.ui.client;

import ca.encodeous.wyrim.models.graph.ItemTransaction;
import ca.encodeous.wyrim.models.graph.RimItemPointer;
import ca.encodeous.wyrim.ui.RimScreen;
import net.minecraft.core.NonNullList;

public class RimSession {
    public RimScreen rimScreen;
    public final NonNullList<RimItemPointer> items = NonNullList.create();
    public final NonNullList<RimItemPointer> playerItems = NonNullList.create();

    public void setRimScreen(RimScreen rimScreen) {
        this.rimScreen = rimScreen;
    }
}
