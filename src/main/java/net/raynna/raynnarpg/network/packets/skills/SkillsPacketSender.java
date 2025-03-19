package net.raynna.raynnarpg.network.packets.skills;

import net.minecraft.server.level.ServerPlayer;
import net.raynna.raynnarpg.server.player.skills.Skills;


public class SkillsPacketSender {

    public static void send(ServerPlayer player, Skills skills) {
        SkillsPacket packet = new SkillsPacket(skills);
        player.connection.send(packet);
    }
}