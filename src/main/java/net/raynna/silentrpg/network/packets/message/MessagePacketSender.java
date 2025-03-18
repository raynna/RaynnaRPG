package net.raynna.silentrpg.network.packets.message;

import net.minecraft.server.level.ServerPlayer;
import net.raynna.silentrpg.network.packets.skills.SkillsPacket;
import net.raynna.silentrpg.server.player.skills.Skills;


public class MessagePacketSender {

    public static void send(ServerPlayer player, String message) {
        MessagePacket packet = new MessagePacket(message);
        player.connection.send(packet);
    }
}