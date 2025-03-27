package net.raynna.raynnarpg.network.packets.xpdrop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.ui.floating_text.FloatingText;

public class FloatingTextHandler implements IPayloadHandler<FloatingTextPacket> {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(FloatingTextPacket packet, IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            handleClient(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(FloatingTextPacket packet) {
        Minecraft.getInstance().execute(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            FloatingText floatingText;
            if (packet.centered()) {
                int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                floatingText = FloatingText.createCentered(
                        packet.message(),
                        width / 2f,
                        height / 2f
                );
            } else if (packet.screenSpace()) {
                floatingText = FloatingText.createScreenSpace(
                        packet.message(),
                        packet.position().x,
                        packet.position().y
                );
            } else {
                floatingText = FloatingText.createWorldSpace(
                        packet.message(),
                        packet.position().x,
                        packet.position().y,
                        packet.position().z
                );
            }

            RaynnaRPG.getOverlayManager().addText(floatingText);
        });
    }
}