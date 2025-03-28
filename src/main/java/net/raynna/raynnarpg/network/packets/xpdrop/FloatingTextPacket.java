package net.raynna.raynnarpg.network.packets.xpdrop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.raynna.raynnarpg.server.player.skills.SkillType;

import javax.annotation.Nullable;

public record FloatingTextPacket(String message, Vec3 position, boolean screenSpace, boolean centered, @Nullable SkillType skillType) implements CustomPacketPayload {

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
            buf.writeBoolean(packet.skillType() != null);
            if (packet.skillType() != null) {
                buf.writeEnum(packet.skillType());
            }
        }

        @Override
        public FloatingTextPacket decode(FriendlyByteBuf buf) {
            String message = buf.readUtf(Short.MAX_VALUE);
            Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            boolean screenSpace = buf.readBoolean();
            boolean centered = buf.readBoolean();
            boolean hasSkillType = buf.readBoolean();
            SkillType skillType = hasSkillType ? buf.readEnum(SkillType.class) : null;
            return new FloatingTextPacket(message, position, screenSpace, centered, skillType);
        }
    };

    public static FloatingTextPacket atCenter(String message, @Nullable SkillType skillType) {
        return new FloatingTextPacket(message, Vec3.ZERO, true, true, skillType);
    }

    public static FloatingTextPacket onBlock(String message, BlockPos pos, @Nullable SkillType skillType) {
        return new FloatingTextPacket(message,
                new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                false, false, skillType);
    }

    public static FloatingTextPacket atEntity(String message, Entity entity, @Nullable SkillType skillType) {
        return new FloatingTextPacket(message, entity.position(), false, false, skillType);
    }

    public static FloatingTextPacket atPlayer(String message, ServerPlayer player, @Nullable SkillType skillType) {
        return new FloatingTextPacket(message, player.position(), false, false, skillType);
    }

    public static FloatingTextPacket atScreen(String message, double screenX, double screenY, @Nullable SkillType skillType) {
        return new FloatingTextPacket(message, new Vec3(screenX, screenY, 0), true, false, skillType);
    }
}
