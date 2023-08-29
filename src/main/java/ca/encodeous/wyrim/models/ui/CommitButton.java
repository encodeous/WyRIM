package ca.encodeous.wyrim.models.ui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.Texture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static ca.encodeous.wyrim.RimServices.Core;
import static ca.encodeous.wyrim.RimServices.Session;

public class CommitButton extends AbstractButton {
    private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
    public CommitButton(
            int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }
    @Override
    public void onPress() {
        Core.commitSession();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(poseStack, mouseX, mouseY, partialTick);
        var scale = 0.7;
        var imgWidth = (int)(this.getWidth() * scale);
        var imgHeight = (int)(this.getHeight() * scale);
        var paddingWidth = (width - imgWidth) / 2;
        var paddingHeight = (height - imgHeight) / 2;
        RenderUtils.drawScalingTexturedRect(
                poseStack,
                CHECKMARK_TEXTURE,
                this.getX() + paddingWidth,
                this.getY() + paddingHeight,
                0,
                imgWidth,
                imgHeight,
                9,
                8);

        if (isHovered) {
            RenderUtils.drawTooltipAt(
                    poseStack,
                    mouseX,
                    mouseY,
                    100,
                    List.of(
                            Component.literal("Commit")
                                    .withStyle(ChatFormatting.YELLOW),
                            Component.literal("Apply all changes made to the inventory")
                                    .withStyle(ChatFormatting.GRAY)),
                    FontRenderer.getInstance().getFont(),
                    true);
        }
    }
}
