package net.raynna.raynnarpg.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raynna.raynnarpg.Config;
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
        int overlayWidth = 110; // approx, adjust as needed
        int overlayHeight = skillBars.size() * LINE_SPACING;

        int[] pos = GuiUtils.getPosition(Config.Client.GUI_POSITION.get(), mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), overlayWidth, overlayHeight);

        int x = pos[0];
        int y = pos[1];

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

    public static class GuiUtils {
        public static int[] getPosition(Config.Client.GuiPosition position, int screenWidth, int screenHeight, int overlayWidth, int overlayHeight) {
            int x = 0, y = 0;

            switch (position) {
                case TOP_LEFT -> {
                    x = Config.Client.X_PADDING.get();
                    y = Config.Client.Y_PADDING.get();
                }
                case TOP_CENTER -> {
                    x = (screenWidth - overlayWidth) / 2;
                    y = Config.Client.Y_PADDING.get();
                }
                case TOP_RIGHT -> {
                    x = screenWidth - overlayWidth - Config.Client.X_PADDING.get();
                    y = Config.Client.Y_PADDING.get();
                }
                case CENTER_LEFT -> {
                    x = Config.Client.X_PADDING.get();
                    y = (screenHeight - overlayHeight) / 2;
                }
                case CENTER -> {
                    x = (screenWidth - overlayWidth) / 2;
                    y = (screenHeight - overlayHeight) / 2;
                }
                case CENTER_RIGHT -> {
                    x = screenWidth - overlayWidth - Config.Client.X_PADDING.get();
                    y = (screenHeight - overlayHeight) / 2;
                }
                case BOTTOM_LEFT -> {
                    x = Config.Client.X_PADDING.get();
                    y = screenHeight - overlayHeight - Config.Client.Y_PADDING.get();
                }
                case BOTTOM_CENTER -> {
                    x = (screenWidth - overlayWidth) / 2;
                    y = screenHeight - overlayHeight - Config.Client.Y_PADDING.get();
                }
                case BOTTOM_RIGHT -> {
                    x = screenWidth - overlayWidth - Config.Client.X_PADDING.get();
                    y = screenHeight - overlayHeight - Config.Client.Y_PADDING.get();
                }
            }

            return new int[]{x, y};
        }
    }

}