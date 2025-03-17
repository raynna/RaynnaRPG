package net.raynna.silentrpg.network.packets.skills;

import net.minecraft.server.level.ServerPlayer;
import net.raynna.silentrpg.server.player.skills.Skills;


public class SkillsPacketSender {

    public static void send(ServerPlayer player, Skills skills) {
        SkillsPacket packet = new SkillsPacket(skills);
        player.connection.send(packet);
    }
}