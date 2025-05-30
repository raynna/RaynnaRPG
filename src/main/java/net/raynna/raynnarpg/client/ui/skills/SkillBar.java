package net.raynna.raynnarpg.client.ui.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raynna.raynnarpg.Config;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.player.skills.Skills;
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.Utils;

@OnlyIn(Dist.CLIENT)
public class SkillBar {
    private static final int XP_BAR_WIDTH = 100;
    private static final int XP_BAR_HEIGHT = 5;
    private static final int TEXT_Y_OFFSET = 12;
    private static final int XP_GAIN_Y_OFFSET = -8;
    private static final long XP_GAIN_DURATION_MS = 5000;

    private static final int BASE_BAR_COLOR = 0xFF00AA00;
    private static final int BACKGROUND_COLOR = 0xFF444444;
    private static final int MAX_LEVEL_COLOR = 0xFFFFB500;

    private final SkillType type;
    private XpGainMessage currentXpGainMessage;

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
        long currentTime = System.currentTimeMillis();

        if (currentXpGainMessage == null || currentTime - currentXpGainMessage.startTime >= XP_GAIN_DURATION_MS) {
            currentXpGainMessage = new XpGainMessage(amount);
        } else {
            currentXpGainMessage.amount += amount;
            currentXpGainMessage.startTime = currentTime;
        }
    }

    private void drawSkillText(GuiGraphics guiGraphics, Minecraft mc, int x, int y, Skill skill) {
        boolean isMaxLevel = skill.getLevel() == Skills.MAX_LEVEL;
        StringBuilder builder = new StringBuilder();
        builder.append(" Lv. ");
        if (isMaxLevel) builder.append(Colour.GOLD);
        builder.append(skill.getLevel()).append("/").append(Skills.MAX_LEVEL);
        guiGraphics.drawString(
                mc.font,
                Component.literal(type.getName() + builder),
                x, y,
                0xFFFFFF
        );
    }

    private void drawXpBar(GuiGraphics guiGraphics, int x, int y, Skill skill) {
        int currentLevel = skill.getLevel();
        double currentXp = skill.getXp();

        double currentLevelXp = ClientSkills.getXpForLevel(currentLevel);
        double nextLevelXp = ClientSkills.getXpForLevel(currentLevel + 1);
        double xpNeededForLevel = nextLevelXp - currentLevelXp;

        double progress;
        int barColor;
        if (currentLevel == Skills.MAX_LEVEL) {
            barColor = MAX_LEVEL_COLOR;
            int xpInInt = (int) currentXp;
            progress = Math.min((double) xpInInt / Skills.MAX_XP, 1.0);
        } else {
            barColor = BASE_BAR_COLOR;
            progress = Math.min((currentXp - currentLevelXp) / xpNeededForLevel, 1.0);
        }
        int barWidth = (int) (XP_BAR_WIDTH * progress);
        guiGraphics.fill(x, y, x + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, BACKGROUND_COLOR);
        guiGraphics.fill(x, y, x + barWidth, y + XP_BAR_HEIGHT, barColor);
        drawXpText(guiGraphics, x, y - 1, skill);
    }

    private void drawXpGainMessages(GuiGraphics guiGraphics, int x, int y) {
        if (currentXpGainMessage == null) return;

        long currentTime = System.currentTimeMillis();
        Font font = Minecraft.getInstance().font;

        float age = (currentTime - currentXpGainMessage.startTime) / (float) XP_GAIN_DURATION_MS;

        if (age >= 0.9f) {
            currentXpGainMessage = null;
            return;
        }

        float fade = 1.0f - Math.max(0, (age - 0.6f) * 2.5f);
        int alpha = (int) (0xFF * fade);
        int color = (alpha << 24) | 0xFFFFFF;
        String text = String.format("+%.0f XP", currentXpGainMessage.amount);

        int textWidth = font.width(text);
        guiGraphics.pose().pushPose();

        float scale = 0.8f;
        guiGraphics.pose().scale(scale, scale, 1f);

        int scaledX = (int) ((x + XP_BAR_WIDTH + 4) / scale);
        int scaledY = (int) ((y + (XP_BAR_HEIGHT + 2)) / scale);
        guiGraphics.drawString(
                font,
                Component.literal(text),
                scaledX, scaledY,
                color,
                false
        );
        guiGraphics.pose().popPose();
    }

    private void drawXpText(GuiGraphics guiGraphics, int x, int y, Skill skill) {
        Font font = Minecraft.getInstance().font;
        boolean isMaxXp = (int) skill.getXp() >= Skills.MAX_XP;
        boolean isMaxLevel = skill.getLevel() >= Skills.MAX_LEVEL;

        String xpText;
        String mode = String.valueOf(Config.Client.XP_TEXT_MODE.get());
        double currentLevelXp = ClientSkills.getXpForLevel(skill.getLevel());
        double nextLevelXp = Skills.getXpForLevel(skill.getLevel() + 1);
        double currentXp = skill.getXp();
        double totalXpNeeded = (isMaxLevel ? Skills.MAX_XP : nextLevelXp);
        double xpNeeded = nextLevelXp - currentLevelXp;
        double xpInLevel = skill.getXp() - currentLevelXp;
        double progress = xpInLevel / xpNeeded;
        if (skill.getLevel() >= Skills.MAX_LEVEL) {
            progress = Math.min(currentXp / Skills.MAX_XP, 1.0);
        }
        xpText = switch (mode) {
            case "PERCENT" -> isMaxXp ? Colour.FORMAT_BOLD + "Max Experience" : Utils.formatPercentage(progress, false);
            case "BOTH" -> isMaxXp ? Colour.FORMAT_BOLD + "Max Experience" : String.format("%.0f/%.0f (%s)",
                    currentXp,
                    totalXpNeeded,
                    Utils.formatPercentage(progress, false)
            );
            default -> isMaxXp ? Colour.FORMAT_BOLD + "Max Experience" :
                    String.format("%.0f / %.0f", currentXp, totalXpNeeded);
        };

        guiGraphics.pose().pushPose();
        float scale = 0.6f;
        guiGraphics.pose().scale(scale, scale, 1.0f);

        int textWidth = font.width(xpText);
        int textX = (int) ((x + (XP_BAR_WIDTH / 2f) - (textWidth * scale / 2f)) / scale);
        int textY = (int) ((y + (XP_BAR_HEIGHT / 2f) - (font.lineHeight * scale / 2f) + 1.5) / scale);

        guiGraphics.drawString(
                font,
                Component.literal(xpText),
                textX, textY,
                0xFFFFFF,
                false
        );
        guiGraphics.pose().popPose();
    }

    private static class XpGainMessage {
        double amount;
        long startTime;

        XpGainMessage(double amount) {
            this.amount = amount;
            this.startTime = System.currentTimeMillis();
        }
    }
}