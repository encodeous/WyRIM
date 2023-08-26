package ca.encodeous.wyrim.inventory;

import ca.encodeous.wyrim.models.item.RimMappedItem;
import com.wynntils.core.components.Manager;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.containers.type.SearchableContainerType;
import com.wynntils.models.items.WynnItem;
import com.wynntils.utils.wynn.ContainerUtils;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static ca.encodeous.wyrim.RimServices.Session;

public class BankUtils {
    private static final int[] jumpSlots = new int[]{
            7, 16, 25, 34, 43, 52
    };
    private static final int[] jumpDestinations = new int[]{
            1, 5, 9, 13, 17, 21
    };
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
     */
    public static void advancePage(Runnable finished, int curPage){
        var screen = Session.getBacking();
        StyledText name = StyledText.fromComponent(screen
                .getMenu()
                .getItems()
                .get(SearchableContainerType.BANK.getNextItemSlot())
                .getHoverName());

        maxBankPage = Math.max(curPage, maxBankPage);

        if (!name.matches(SearchableContainerType.BANK.getNextItemPattern())) {
            finished.run();
            return;
        }

        ContainerUtils.clickOnSlot(
                SearchableContainerType.BANK.getNextItemSlot(),
                screen.getMenu().containerId,
                GLFW.GLFW_MOUSE_BUTTON_LEFT,
                screen.getMenu().getItems());
    }

    public static CompletableFuture<Void> advanceToPage(int dst){
        var screen = Session.getBacking();
        var curPage = Models.Container.getCurrentBankPage(screen);
        var delta = dst - curPage;
        if(delta == 0) return CompletableFuture.completedFuture(null);
        CompletableFuture<Void> cf;
        if(Math.abs(delta) <= 1){
            if(delta > 0){
                // go forward
                ContainerUtils.clickOnSlot(
                        SearchableContainerType.BANK.getNextItemSlot(),
                        screen.getMenu().containerId,
                        GLFW.GLFW_MOUSE_BUTTON_LEFT,
                        screen.getMenu().getItems());
            }else{
                // go backward
                ContainerUtils.clickOnSlot(
                        17, // prev item slot
                        screen.getMenu().containerId,
                        GLFW.GLFW_MOUSE_BUTTON_LEFT,
                        screen.getMenu().getItems());
            }
            cf = InvUtils.waitForPageLoad(2);
        }else{
            int clickSlot = -1;
            for(int i = 0; i < jumpDestinations.length; i++){
                if(Math.abs(jumpDestinations[i] - curPage) < Math.abs(delta)){
                    delta = jumpDestinations[i] - curPage;
                    clickSlot = jumpSlots[i];
                }
            }
            ContainerUtils.clickOnSlot(
                    clickSlot,
                    screen.getMenu().containerId,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    screen.getMenu().getItems());
            int finalDelta = delta;
            cf = InvUtils.waitForPageLoad(2)
                    .thenCompose((x)->advanceToPage(dst + finalDelta));
        }
        return cf;
    }
}
