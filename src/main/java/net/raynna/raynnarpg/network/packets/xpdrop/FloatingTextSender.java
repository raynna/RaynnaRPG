package net.raynna.raynnarpg.network.packets.xpdrop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FloatingTextSender {
    public static void sendFloatText(ServerPlayer player, String message, Vec3 position, boolean screenSpace) {
        FloatingTextPacket packet = new FloatingTextPacket(message, position, screenSpace, false);
        player.connection.send(packet);
    }

    public static void sendOnEntity(ServerPlayer player, String message, Entity entity) {
        FloatingTextPacket packet = FloatingTextPacket.atEntity(message, entity);
        player.connection.send(packet);
    }

    public static void sendOnBlock(ServerPlayer player, String message, BlockPos blockPos) {
        FloatingTextPacket packet = FloatingTextPacket.onBlock(message, blockPos);
        player.connection.send(packet);
    }

    public static void sendOnPlayer(ServerPlayer player, String message, ServerPlayer otherPlayer) {
        FloatingTextPacket packet = FloatingTextPacket.atPlayer(message, otherPlayer);
        player.connection.send(packet);
    }

    public static void sendCenteredText(ServerPlayer player, String message) {
        FloatingTextPacket packet = FloatingTextPacket.atCenter(message);
        player.connection.send(packet);
    }

    public static void sendAtScreen(ServerPlayer player, String message, double x, double y) {
        FloatingTextPacket packet = FloatingTextPacket.atScreen(message, x, y);
        player.connection.send(packet);
    }
}