package net.raynna.raynnarpg.network.packets.xpdrop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record FloatingTextPacket(String message, Vec3 position, boolean screenSpace, boolean centered) implements CustomPacketPayload {

    public static final Type<FloatingTextPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("raynnarpg", "floating_text"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, FloatingTextPacket> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, FloatingTextPacket packet) {
            buf.writeUtf(packet.message());
            buf.writeDouble(packet.position().x);
            buf.writeDouble(packet.position().y);
            buf.writeDouble(packet.position().z);
            buf.writeBoolean(packet.screenSpace());
            buf.writeBoolean(packet.centered());
        }

        @Override
        public FloatingTextPacket decode(FriendlyByteBuf buf) {
            return new FloatingTextPacket(
                    buf.readUtf(Short.MAX_VALUE),
                    new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    buf.readBoolean(),
                    buf.readBoolean()
            );
        }
    };

    public static FloatingTextPacket atCenter(String message) {
        return new FloatingTextPacket(message, Vec3.ZERO, true, true);
    }

    public static FloatingTextPacket onBlock(String message, BlockPos pos) {
        return new FloatingTextPacket(message,
                new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                false, false);
    }

    public static FloatingTextPacket atEntity(String message, Entity entity) {
        return new FloatingTextPacket(message, entity.position(), false, false);
    }

    public static FloatingTextPacket atPlayer(String message, ServerPlayer player) {
        return new FloatingTextPacket(message, player.position(), false, false);
    }

    public static FloatingTextPacket atScreen(String message, double screenX, double screenY) {
        return new FloatingTextPacket(message, new Vec3(screenX, screenY, 0), true, false);
    }
}
