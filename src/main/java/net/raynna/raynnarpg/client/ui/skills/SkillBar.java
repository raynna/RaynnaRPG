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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkillBar {
    private static final int XP_BAR_WIDTH = 100;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int TEXT_Y_OFFSET = 12;
    private static final int XP_GAIN_Y_OFFSET = -8;
    private static final long XP_GAIN_DURATION_MS = 5000;

    private static final int BASE_BAR_COLOR = 0xFF00AA00;
    private static final int BACKGROUND_COLOR = 0xFF444444;

    private final SkillType type;
    private long XP_GAIN_TIMER = -1;
    private final List<XpGainMessage> xpGainMessages = new ArrayList<>();

    public SkillBar(SkillType type) {
        this.type = type;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        Skill skill = ClientSkills.getSkill(type);
        if (skill == null) return;

        Minecraft mc = Minecraft.getInstance();
        drawSkillText(guiGraphics, mc, x, y, skill);
        drawXpBar(guiGraphics, x, y + TEXT_Y_OFFSET, skill);
        drawXpGainMessages(guiGraphics, x, y + TEXT_Y_OFFSET + XP_GAIN_Y_OFFSET);
    }

    public void addXpGain(double amount) {
        this.xpGainMessages.add(new XpGainMessage(amount));
        XP_GAIN_TIMER = System.currentTimeMillis();
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
        int currentLevel = skill.getLevel();
        double currentXp = skill.getXp();

        // Calculate XP needed for next level
        double currentLevelXp = ClientSkills.getXpForLevel(currentLevel);
        double nextLevelXp = ClientSkills.getXpForLevel(currentLevel + 1);
        double xpNeededForLevel = nextLevelXp - currentLevelXp;

        // Calculate progress
        double progress = Math.min((currentXp - currentLevelXp) / xpNeededForLevel, 1.0);
        int barWidth = (int)(XP_BAR_WIDTH * progress);

        // Draw background
        guiGraphics.fill(x, y, x + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, BACKGROUND_COLOR);

        // Draw progress
        guiGraphics.fill(x, y, x + barWidth, y + XP_BAR_HEIGHT, BASE_BAR_COLOR);

        drawXpText(guiGraphics, x, y - 1, skill);
    }

    private void drawXpGainMessages(GuiGraphics guiGraphics, int x, int y) {
        long currentTime = System.currentTimeMillis();
        Iterator<XpGainMessage> iterator = xpGainMessages.iterator();
        Font font = Minecraft.getInstance().font;

        while (iterator.hasNext()) {
            XpGainMessage message = iterator.next();
            float age = (currentTime - message.startTime) / (float)XP_GAIN_DURATION_MS;

            if (age >= 1.0f) {
                iterator.remove();
                continue;
            }

            float fade = 1.0f - Math.max(0, (age - 0.6f) * 2.5f);
            int alpha = (int)(0xFF * fade);
            int color = (alpha << 24) | 0xFFFFFF;
            String text = String.format("+%.0f XP", message.amount);

            int textWidth = font.width(text);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.9f, 0.9f, 1f);

            int scaledX = (int)((x + XP_BAR_WIDTH - textWidth) / 0.9f);
            int scaledY = (int)(y / 0.9f);
            guiGraphics.drawString(
                    font,
                    Component.literal(text),
                    scaledX, scaledY,
                    color,
                    false
            );
            guiGraphics.pose().popPose();
        }

        if (xpGainMessages.isEmpty()) {
            XP_GAIN_TIMER = -1;
        }
    }

    private void drawXpText(GuiGraphics guiGraphics, int x, int y, Skill skill) {
        Font font = Minecraft.getInstance().font;
        boolean isMaxLevel = skill.getLevel() == 50;
        String xpText = isMaxLevel ? "Max Lvl" :
                String.format("%.0f / %.0f", skill.getXp(), ClientSkills.getXpForLevel(skill.getLevel() + 1));

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.6F, 1, 0.6F);
        int textWidth = (int)((double)font.width(xpText) * 0.75);
        int textX = x + (XP_BAR_WIDTH - textWidth);
        guiGraphics.drawString(
                font,
                Component.literal(xpText),
                textX, y,
                0xFFFFFF
        );
        guiGraphics.pose().popPose();
    }

    private static class XpGainMessage {
        final double amount;
        final long startTime;

        XpGainMessage(double amount) {
            this.amount = amount;
            this.startTime = System.currentTimeMillis();
        }
    }
}