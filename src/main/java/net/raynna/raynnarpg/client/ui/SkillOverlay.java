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
        int overlayWidth = 110;
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

            int xPadding = Config.Client.X_PADDING.get();
            int yPadding = Config.Client.Y_PADDING.get();

            switch (position) {
                case TOP_LEFT -> {
                    x = xPadding;
                    y = yPadding;
                }
                case TOP_CENTER -> {
                    x = (screenWidth - overlayWidth) / 2 + xPadding;
                    y = yPadding;
                }
                case TOP_RIGHT -> {
                    x = screenWidth - overlayWidth - xPadding;
                    y = yPadding;
                }
                case CENTER_LEFT -> {
                    x = xPadding;
                    y = (screenHeight - overlayHeight) / 2 + yPadding;
                }
                case CENTER -> {
                    x = (screenWidth - overlayWidth) / 2 + xPadding;
                    y = (screenHeight - overlayHeight) / 2 + yPadding;
                }
                case CENTER_RIGHT -> {
                    x = screenWidth - overlayWidth - xPadding;
                    y = (screenHeight - overlayHeight) / 2 + yPadding;
                }
                case BOTTOM_LEFT -> {
                    x = xPadding;
                    y = screenHeight - overlayHeight - yPadding;
                }
                case BOTTOM_CENTER -> {
                    x = (screenWidth - overlayWidth) / 2 + xPadding;
                    y = screenHeight - overlayHeight - yPadding;
                }
                case BOTTOM_RIGHT -> {
                    x = screenWidth - overlayWidth - xPadding;
                    y = screenHeight - overlayHeight - yPadding;
                }
            }

            return new int[]{x, y};
        }
    }

}