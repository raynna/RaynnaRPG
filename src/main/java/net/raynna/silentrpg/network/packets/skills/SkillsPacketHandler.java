package net.raynna.silentrpg.network.packets.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.raynna.silentrpg.SilentRPG;
import net.raynna.silentrpg.client.player.ClientSkills;
import net.raynna.silentrpg.server.player.skills.SkillType;
import net.raynna.silentrpg.server.player.skills.Skills;

public class SkillsPacketHandler implements IPayloadHandler<SkillsPacket> {

    @Override
    public void handle(SkillsPacket packet, IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            Skills skills = packet.skills();
            for (SkillType type : SkillType.values()) {
                System.out.println("[PlayerSkillsPacket] read: Skill: " + type.getName() + " Level: " + skills.getSkill(type).getLevel());
            }
            handleClient(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(SkillsPacket packet) {
        Minecraft.getInstance().execute(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ClientSkills clientSkills = new ClientSkills(player);
            clientSkills.updateSkills(packet.skills());
        });
    }
}