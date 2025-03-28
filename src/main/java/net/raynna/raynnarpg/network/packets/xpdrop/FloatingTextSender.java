package net.raynna.raynnarpg.network.packets.xpdrop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.raynna.raynnarpg.server.player.skills.SkillType;

public class FloatingTextSender {
    public static void sendFloatText(ServerPlayer player, String message, Vec3 position, boolean screenSpace, SkillType skill) {
        FloatingTextPacket packet = new FloatingTextPacket(message, position, screenSpace, false, skill);
        player.connection.send(packet);
    }

    public static void sendOnEntity(ServerPlayer player, String message, Entity entity, SkillType skill) {
        FloatingTextPacket packet = FloatingTextPacket.atEntity(message, entity, skill);
        player.connection.send(packet);
    }

    public static void sendOnBlock(ServerPlayer player, String message, BlockPos blockPos, SkillType skill) {
        FloatingTextPacket packet = FloatingTextPacket.onBlock(message, blockPos, skill);
        player.connection.send(packet);
    }

    public static void sendOnPlayer(ServerPlayer player, String message, ServerPlayer otherPlayer, SkillType skill) {
        FloatingTextPacket packet = FloatingTextPacket.atPlayer(message, otherPlayer, skill);
        player.connection.send(packet);
    }

    public static void sendCenteredText(ServerPlayer player, String message, SkillType skill) {
        FloatingTextPacket packet = FloatingTextPacket.atCenter(message, skill);
        player.connection.send(packet);
    }

    public static void sendAtScreen(ServerPlayer player, String message, double x, double y, SkillType skill) {
        FloatingTextPacket packet = FloatingTextPacket.atScreen(message, x, y, skill);
        player.connection.send(packet);
    }
}