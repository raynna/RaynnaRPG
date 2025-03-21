package net.raynna.raynnarpg.network.packets.message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class MessagePacketHandler implements IPayloadHandler<MessagePacket> {

    @Override
    public void handle(MessagePacket packet, IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
           handleClient(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessagePacket packet) {
        //System.out.println("[PlayerMessagePacket] read: " + packet.message());
        Minecraft.getInstance().execute(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            player.displayClientMessage(Component.literal(packet.message()), true);
        });
    }
}