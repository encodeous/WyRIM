package ca.encodeous.wyrim.ui;

import ca.encodeous.wyrim.models.ui.WyRimSession;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Models;
import com.wynntils.utils.wynn.ItemUtils;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.vidageek.mirror.dsl.Mirror;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WyRimScreen extends AbstractContainerScreen<WyRimMenu> {
    private static final ResourceLocation SCROLL_BAR_BUTTON = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final ResourceLocation SCROLL_BAR = new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png");
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    private EditBox searchBox;
    private float scrollOffs;
    private boolean scrolling;
    private final WyRimSession session;
    private static final int scrollBarOffsetX = 170;
    private static final int scrollBarOffsetY = 18;
    private static final int scrollBarHeight = 112;
    private static final int scrollBarWidth = 14;

    public WyRimScreen(Player player, WyRimSession session) {
        super(new WyRimMenu(player), player.getInventory(), Component.literal("Your Bank"));

        this.session = session;

        this.imageHeight = 114 + WyRimMenu.CONTAINER_ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        // search box
        searchBox = new EditBox(this.font, this.leftPos + 88, this.topPos + 5, 80, 9, Component.translatable("itemGroup.search"));
        searchBox.setMaxLength(50);
        searchBox.setBordered(true);
        searchBox.setVisible(true);
        searchBox.setTextColor(16777215);
        searchBox.setHint(Component.literal("Search items"));
        addWidget(searchBox);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float mouseX, int mouseY, int delta) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int m = y + 112;

        // draw container
        blit(poseStack, x, y, 0, 0, this.imageWidth, WyRimMenu.CONTAINER_ROWS * 18 + 17);
        // draw player inventory
        blit(poseStack, x, y + WyRimMenu.CONTAINER_ROWS * 18 + 17, 0, 126, this.imageWidth, 96);

        this.searchBox.render(poseStack, mouseY, delta, mouseX);

        if (menu.canScroll()) {
            int ax = this.leftPos + scrollBarOffsetX;
            int ay = this.topPos + scrollBarOffsetY;
            var temp = scrollBarHeight - 17;

            RenderSystem.setShaderTexture(0, SCROLL_BAR);

            blit(poseStack,
                    ax + 2, ay - 18,
                    178, 0,
                    24, 213);
            RenderSystem.setShaderTexture(0, SCROLL_BAR_BUTTON);
            blit(poseStack, ax, ay + (int) (temp * this.scrollOffs), 232, 0, 12, 15);
        }
    }

    private void refreshSearchResults() {
        this.menu.items.clear();

        String string = this.searchBox.getValue();
        if (string.isEmpty()) {
            this.menu.items.addAll(session.serverSession.allItems.stream().map(x -> x.item).toList());
        } else {
            var filtered = session.serverSession.allItems.stream().map(x -> x.item).filter(x -> {

                if (x.getDisplayName().getString().toLowerCase().contains(string.toLowerCase())) return true;
                if (x.getTooltipLines(null, TooltipFlag.NORMAL).stream().anyMatch(y -> y.getString().toLowerCase().contains(string.toLowerCase())))
                    return true;
                var wynnItemOpt = Models.Item.getWynnItem(x);
                if (wynnItemOpt.isEmpty()) return false;
                var wynnItem = wynnItemOpt.get();
                return wynnItem.toString().toLowerCase().contains(string.toLowerCase());
            }).toList();
            this.menu.items.addAll(filtered);
        }

        this.scrollOffs = 0.0F;
        this.menu.scrollTo(0.0F);
    }

    public boolean charTyped(char c, int i) {
        String string = this.searchBox.getValue();
        if (this.searchBox.charTyped(c, i)) {
            if (!string.equals(this.searchBox.getValue())) {
                refreshSearchResults();
            }
        }
        return false;
    }

    public boolean keyPressed(int i, int j, int k) {
        String string = this.searchBox.getValue();
        if (this.searchBox.keyPressed(i, j, k)) {
            if (!string.equals(this.searchBox.getValue())) {
                refreshSearchResults();
            }
        }
        if(searchBox.isFocused()) return true;
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        renderBackground(poseStack);
        super.render(poseStack, i, j, f);
        renderTooltip(poseStack, i, j);
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double d, double e) {
        return super.getChildAt(d, e);
    }

    @Override
    public void mouseMoved(double d, double e) {
        super.mouseMoved(d, e);
    }


    public boolean mouseScrolled(double d, double e, double f) {
        if (!menu.canScroll()) {
            return false;
        }
        this.scrollOffs = this.menu.subtractInputFromScroll(this.scrollOffs, f);
        this.menu.scrollTo(this.scrollOffs);
        return true;
    }

    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.scrolling) {
            int j = this.topPos + 18;
            int k = j + 112;

            this.scrollOffs = ((float) e - j - 7.5F) / ((k - j) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs);

            return true;
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0) {
            if (insideScrollbar(d, e)) {
                this.scrolling = menu.canScroll();
                return true;
            }
        }

        return super.mouseClicked(d, e, i);
    }

    protected boolean insideScrollbar(double qx, double qy) {
        int cx = this.leftPos;
        int cy = this.topPos;

        int x = cx + scrollBarOffsetX;
        int y = cy + scrollBarOffsetY;
        int x1 = x + scrollBarWidth;
        int y1 = y + scrollBarHeight;
        return (qx >= x && qy >= y && qx < x1 && qy < y1);
    }


    public boolean mouseReleased(double d, double e, int i) {
        if (i == 0) {
            this.scrolling = false;
        }

        return super.mouseReleased(d, e, i);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        return super.keyReleased(i, j, k);
    }

    @Override
    public void setFocused(boolean bl) {
        super.setFocused(bl);
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return super.getCurrentFocusPath();
    }

    @Override
    public void magicalSpecialHackyFocus(@Nullable GuiEventListener guiEventListener) {
        super.magicalSpecialHackyFocus(guiEventListener);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        return super.nextFocusPath(focusNavigationEvent);
    }

    @Override
    public int getTabOrderGroup() {
        return super.getTabOrderGroup();
    }
}