package net.raynna.raynnarpg.network.packets.message;

import net.minecraft.server.level.ServerPlayer;


public class MessagePacketSender {

    public static void send(ServerPlayer player, String message) {
        MessagePacket packet = new MessagePacket(message);
        player.connection.send(packet);
    }
}