package net.raynna.raynnarpg.network.packets.toasts;

import net.minecraft.server.level.ServerPlayer;

public class CustomToastPacketSender {
    public static void send(ServerPlayer player, String title, String description) {
        CustomToastPacket packet = new CustomToastPacket(title, description);
        player.connection.send(packet);
    }
}
