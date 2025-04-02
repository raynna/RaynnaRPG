package net.raynna.raynnarpg.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raynna.raynnarpg.client.ui.skills.SkillBar;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SkillOverlay {
    private static final int LINE_SPACING = 26;
    private static final Map<SkillType, SkillBar> skillBars = new EnumMap<>(SkillType.class);

    static {
        initializeSkillBars();
    }

    private static void initializeSkillBars() {
        for (SkillType type : SkillType.values()) {
            skillBars.put(type, new SkillBar(type));
        }
    }

    public static void show(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        if (GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = 10;
        int y = screenHeight / 4;

        for (SkillBar bar : skillBars.values()) {
            bar.render(graphics, x, y);
            y += LINE_SPACING;
        }
    }

    public static void updateXpGain(SkillType type, double amount) {
        SkillBar bar = skillBars.get(type);
        if (bar != null) {
            bar.addXpGain(amount);
        }
    }

}