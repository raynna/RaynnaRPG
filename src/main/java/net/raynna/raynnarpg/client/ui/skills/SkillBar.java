package net.raynna.raynnarpg.client.ui.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;

@OnlyIn(Dist.CLIENT)
public class SkillBar {

    private static final int XP_BAR_WIDTH = 100;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int TEXT_Y_OFFSET = 12;
    private static final long PULSE_DURATION_MS = 600;
    private static final int PULSE_COUNT = 2;

    private static final int BASE_BAR_COLOR = 0xFF00AA00; // Normal green
    private static final int PULSE_COLOR = 0xFF55FF55; // Brighter green for pulse
    private static final int BACKGROUND_COLOR = 0xFF444444; // Dark gray background

    private final SkillType type;
    private long pulseStartTime = -1;

    public SkillBar(SkillType type) {
        this.type = type;
    }

    public void triggerPulse() {
        this.pulseStartTime = System.currentTimeMillis();
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        Skill skill = ClientSkills.getSkill(type);
        if (skill == null) return;

        Minecraft mc = Minecraft.getInstance();

        drawSkillText(guiGraphics, mc, x, y, skill);
        drawXpBar(guiGraphics, x, y + TEXT_Y_OFFSET, skill);
    }

    private void drawSkillText(GuiGraphics guiGraphics, Minecraft mc, int x, int y, Skill skill) {
        guiGraphics.drawString(
                mc.font,
                Component.literal(type.getName() + " Lv. " + skill.getLevel()),
                x, y,
                0xFFFFFF
        );
    }

    private void drawXpBar(GuiGraphics guiGraphics, int x, int y, Skill skill) {
        double totalXpNeeded = ClientSkills.getXpForLevel(skill.getLevel() + 1);
        double currentLevelXp = ClientSkills.getXpForLevel(skill.getLevel());
        double currentXp = skill.getXp();

        double progress = Math.min(
                (currentXp - currentLevelXp) / (totalXpNeeded - currentLevelXp),
                1.0
        );

        int barWidth = (int) (XP_BAR_WIDTH * progress);

        guiGraphics.fill(x, y, x + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, BACKGROUND_COLOR);

        int barColor = getPulsingBarColor();

        guiGraphics.fill(x, y, x + barWidth, y + XP_BAR_HEIGHT, barColor);

        drawXpText(guiGraphics, x, y - 1, skill);
    }

    private int getPulsingBarColor() {
        if (pulseStartTime == -1) {
            return BASE_BAR_COLOR;
        }

        long elapsed = System.currentTimeMillis() - pulseStartTime;
        if (elapsed >= PULSE_DURATION_MS) {
            pulseStartTime = -1;
            return BASE_BAR_COLOR;
        }

        float pulseProgress = (elapsed % (PULSE_DURATION_MS / PULSE_COUNT)) / (float)(PULSE_DURATION_MS / PULSE_COUNT);

        float alpha = (float)(Math.sin(pulseProgress * Math.PI * 2) * 0.5 + 0.5);

        return interpolateColor(BASE_BAR_COLOR, PULSE_COLOR, alpha);
    }

    private int interpolateColor(int color1, int color2, float alpha) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int)(r1 + (r2 - r1) * alpha);
        int g = (int)(g1 + (g2 - g1) * alpha);
        int b = (int)(b1 + (b2 - b1) * alpha);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private void drawXpText(GuiGraphics guiGraphics, int x, int y, Skill skill) {
        Font font = Minecraft.getInstance().font;
        boolean isMaxLevel = skill.getLevel() == 50;
        String xpText = isMaxLevel ? "Max Lvl" :
                String.format("%.0f / %.0f", skill.getXp(), ClientSkills.getXpForLevel(skill.getLevel() + 1));

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.6F, 1, 0.6F);
        int textWidth = (int) ((double) font.width(xpText) * 0.75);
        int textX = x + (XP_BAR_WIDTH - textWidth);
        guiGraphics.drawString(
                font,
                Component.literal(xpText),
                textX, y,
                0xFFFFFF
        );
        guiGraphics.pose().popPose();
    }
}