package net.raynna.raynnarpg.network.packets.keypress;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raynna.raynnarpg.server.player.skills.Skills;


public class ShiftClickPacketSender {
    @OnlyIn(Dist.CLIENT)
    public static void updateShiftState(boolean isDown) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.getConnection() != null) {
            mc.getConnection().send(new ShiftClickPacket(isDown));
        }
    }
}