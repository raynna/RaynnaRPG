package net.raynna.raynnarpg.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.client.ui.skills.SkillBar;
import net.raynna.raynnarpg.server.player.skills.Skill;
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
        System.out.println("Initializing skill bars for all SkillTypes");
        for (SkillType type : SkillType.values()) {
            skillBars.put(type, new SkillBar(type));
            System.out.println("Created SkillBar for: " + type.getName());
        }
    }

    public static void show(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        // Check if shift is held to hide the overlay
        if (GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = 10;
        int y = screenHeight / 4;

        // Render all skill bars
        for (SkillBar bar : skillBars.values()) {
            bar.render(graphics, x, y);
            y += LINE_SPACING;
        }
    }

    public static void markSkillUpdated(SkillType type) {
        SkillBar bar = skillBars.get(type);
        if (bar != null) {
            bar.triggerPulse();
        }
    }
}