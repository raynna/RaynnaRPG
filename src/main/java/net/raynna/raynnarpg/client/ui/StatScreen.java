package net.raynna.raynnarpg.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class StatScreen extends Screen {

    private String selectedPlayer = null;

    public StatScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;
        List<AbstractClientPlayer> players = mc.level.players();
        int startY = 40;
        int lineHeight = 25;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String playerName = player.getName().getString();
            UUID playerUUID = player.getUUID();
            int y = startY + i * lineHeight;

            // Display player name as a label
            this.addRenderableWidget(Button.builder(Component.literal(playerName), b -> {})
                    .bounds(this.width / 2 - 100, y, 80, 20).build());

            // View button to select this player
            this.addRenderableWidget(Button.builder(Component.literal("View"), b -> {
                this.selectedPlayer = playerName;
                // You can set up a system to display the skills of the selected player
            }).bounds(this.width / 2, y, 60, 20).build());

            // If this player is selected, display their skills
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
