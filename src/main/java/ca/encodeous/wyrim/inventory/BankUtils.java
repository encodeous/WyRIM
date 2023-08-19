package ca.encodeous.wyrim.inventory;

import ca.encodeous.wyrim.models.item.WyRimMappedItem;
import ca.encodeous.wyrim.models.ui.server.BankSession;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.containers.type.SearchableContainerType;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemCache;
import com.wynntils.utils.wynn.ContainerUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class BankUtils {
    public static ArrayList<WyRimMappedItem> mapItems(BankSession session){
        Container container = session.bankScreen.getMenu().getContainer();
        ArrayList<WyRimMappedItem> items = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!SearchableContainerType.BANK.getBounds().getSlots().contains(i)) continue;

            ItemStack itemStack = container.getItem(i);

            Optional<WynnItem> wynnItemOpt = Models.Item.getWynnItem(itemStack);
            if (wynnItemOpt.isEmpty()) return new ArrayList<>();

            int page = Models.Container.getCurrentBankPage(session.bankScreen);

            items.add(new WyRimMappedItem(i + page * container.getContainerSize(), itemStack));
        }
        return items;
    }

    /**
     * Advances to the next page of the bank
     * @param session
     * @return
     */
    public static void advancePage(BankSession session, Runnable finished){
        StyledText name = StyledText.fromComponent(session.bankScreen
                .getMenu()
                .getItems()
                .get(SearchableContainerType.BANK.getNextItemSlot())
                .getHoverName());

        if (!name.matches(SearchableContainerType.BANK.getNextItemPattern())) {
            finished.run();
            return;
        }

        ContainerUtils.clickOnSlot(
                SearchableContainerType.BANK.getNextItemSlot(),
                session.bankScreen.getMenu().containerId,
                GLFW.GLFW_MOUSE_BUTTON_LEFT,
                session.bankScreen.getMenu().getItems());
    }
}
