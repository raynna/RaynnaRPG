package net.raynna.raynnarpg.client.ui;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raynna.raynnarpg.client.ui.floating_text.FloatingText;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OverlayManager {

    private final List<FloatingText> floatingTexts = new ArrayList<>();

    public void addText(FloatingText text) {
        floatingTexts.add(text);
    }

    public void renderAll(GuiGraphics guiGraphics, Camera camera) {
        floatingTexts.removeIf(FloatingText::isExpired);
        floatingTexts.forEach(text -> text.render(guiGraphics, camera));
    }

}