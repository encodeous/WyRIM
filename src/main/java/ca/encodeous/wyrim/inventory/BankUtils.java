package ca.encodeous.wyrim.inventory;

import ca.encodeous.wyrim.models.item.RimItemOrigin;
import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.containers.type.SearchableContainerType;
import com.wynntils.models.items.WynnItem;
import com.wynntils.utils.wynn.ContainerUtils;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Optional;

import static ca.encodeous.wyrim.RimServices.Session;

public class BankUtils {
    public static ArrayList<RimMappedItem> mapItems(){
        var screen = Session.getBacking();
        Container container = screen.getMenu().getContainer();
        ArrayList<RimMappedItem> items = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!SearchableContainerType.BANK.getBounds().getSlots().contains(i)) continue;

            ItemStack itemStack = container.getItem(i);

            Optional<WynnItem> wynnItemOpt = Models.Item.getWynnItem(itemStack);
            if (wynnItemOpt.isEmpty()) return new ArrayList<>();

            int page = Models.Container.getCurrentBankPage(Session.bankScreen);

            items.add(new RimMappedItem(itemStack, i + page * container.getContainerSize()));
        }
        return items;
    }

    public static int maxBankPage = 0;

    /**
     * Advances to the next page of the bank
     * @return
     */
    public static void advancePage(Runnable finished, int curPage){
        var screen = Session.getBacking();
        StyledText name = StyledText.fromComponent(screen
                .getMenu()
                .getItems()
                .get(SearchableContainerType.BANK.getNextItemSlot())
                .getHoverName());

        if (!name.matches(SearchableContainerType.BANK.getNextItemPattern())) {
            finished.run();
            return;
        }

        maxBankPage = Math.max(curPage, maxBankPage);

        ContainerUtils.clickOnSlot(
                SearchableContainerType.BANK.getNextItemSlot(),
                screen.getMenu().containerId,
                GLFW.GLFW_MOUSE_BUTTON_LEFT,
                screen.getMenu().getItems());
    }
}
