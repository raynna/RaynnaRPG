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

    private static final long XP_GAIN_DURATION_MS = 5000;
    private static final int XP_GAIN_Y_OFFSET = -8;

    private static final long PULSE_DURATION_MS = 1200;
    private static final int PULSE_COUNT = 4;

    private static final int BASE_BAR_COLOR = 0xFF00AA00;
    private static final int PULSE_COLOR = 0xFF55FF55;
    private static final int BACKGROUND_COLOR = 0xFF444444;

    private final SkillType type;
    private long xpGainStartTime = -1;
    private final List<XpGainMessage> xpGainMessages = new ArrayList<>();

    public SkillBar(SkillType type) {
        this.type = type;
    }


    private double xpAtPulseStart;
    private double xpGainDuringPulse;
    private long pulseStartTime = -1;

    public void triggerPulse(double amount) {
        Skill skill = ClientSkills.getSkill(type);
        if (skill != null) {
            this.xpAtPulseStart = skill.getXp() - amount;
            this.xpGainDuringPulse = amount;
        }
        this.pulseStartTime = System.currentTimeMillis();
    }

    public void addXpGain(double amount) {
        this.xpGainMessages.add(new XpGainMessage(amount));
        triggerPulse(amount);
        if (xpGainStartTime == -1) {
            xpGainStartTime = System.currentTimeMillis();
        }
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        Skill skill = ClientSkills.getSkill(type);
        if (skill == null) return;

        Minecraft mc = Minecraft.getInstance();

        drawSkillText(guiGraphics, mc, x, y, skill);
        drawXpBar(guiGraphics, x, y + TEXT_Y_OFFSET, skill);
        drawXpGainMessages(guiGraphics, x, y + TEXT_Y_OFFSET + XP_GAIN_Y_OFFSET);
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
        int totalWidth = (int)(XP_BAR_WIDTH * progress);

        guiGraphics.fill(x, y, x + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, BACKGROUND_COLOR);

        boolean isPulsing = pulseStartTime != -1 && System.currentTimeMillis() - pulseStartTime < PULSE_DURATION_MS;

        double baseProgress = Math.min(
                (xpAtPulseStart - currentLevelXp) / (totalXpNeeded - currentLevelXp),
                1.0
        );
        int baseWidth = (int)(XP_BAR_WIDTH * baseProgress);
        guiGraphics.fill(x, y, x + baseWidth, y + XP_BAR_HEIGHT, BASE_BAR_COLOR);

        if (isPulsing && xpGainDuringPulse > 0) {
            double gainedProgress = Math.min(
                    xpGainDuringPulse / (totalXpNeeded - currentLevelXp),
                    1.0
            );
            int gainedWidth = (int)(XP_BAR_WIDTH * gainedProgress);

            float alpha = 0.5f + 0.5f * getPulseAlphaFactor();
            int yellowWithAlpha = (int)(alpha * 255) << 24 | 0xFFFF00;

            int gainStart = baseWidth;
            int gainEnd = Math.min(baseWidth + gainedWidth, XP_BAR_WIDTH);

            if (gainEnd > gainStart) {
                guiGraphics.fill(x + gainStart, y, x + gainEnd, y + XP_BAR_HEIGHT, yellowWithAlpha);
            }
        }
        else {
            guiGraphics.fill(x, y, x + totalWidth, y + XP_BAR_HEIGHT, BASE_BAR_COLOR);
        }
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
            String text;
            if (message.amount % 1 == 0) {
                text = String.format("+%.0f XP", message.amount);
            } else {
                text = String.format("+%.1f XP", message.amount);
            }
            int textWidth = font.width(text);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.9f, 0.9f, 1f);

            int scaledX = (int)((x + XP_BAR_WIDTH - textWidth) / 0.9f);
            int scaledY = (int)(y / 0.9f);
            //guiGraphics.pose().translate(x + XP_BAR_WIDTH - textWidth, y + XP_GAIN_Y_OFFSET, 0);
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
            xpGainStartTime = -1;
        }
    }

    private float getPulseAlphaFactor() {
        if (pulseStartTime == -1) return 1.0f;

        long elapsed = System.currentTimeMillis() - pulseStartTime;
        if (elapsed >= PULSE_DURATION_MS) {
            pulseStartTime = -1;
            return 1.0f;
        }

        float pulsePhase = (elapsed % (PULSE_DURATION_MS / PULSE_COUNT)) / (float)(PULSE_DURATION_MS / PULSE_COUNT);
        return (float)(Math.sin(pulsePhase * Math.PI * 2) * 0.5 + 0.5);
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

    private static class XpGainMessage {
        final double amount;
        final long startTime;

        XpGainMessage(double amount) {
            this.amount = amount;
            this.startTime = System.currentTimeMillis();
        }
    }
}