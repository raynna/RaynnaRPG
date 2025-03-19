package net.raynna.raynnarpg.client.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;


public class SkillTooltipComponent implements ClientTooltipComponent {


    private final ResourceLocation icon;
    private final int level;
    private final int xp;
    private static final int ICON_SIZE = 16;


    public SkillTooltipComponent(ResourceLocation icon, int level, int xp) {
        this.icon = icon;
        this.level = level;
        this.xp = xp;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        guiGraphics.blit(icon, x, y, 0, 0, ICON_SIZE, ICON_SIZE);
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth(Font font) {
        return 0;
    }
}
