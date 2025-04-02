package net.raynna.raynnarpg.network.packets.keypress;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ShiftClickPacket(boolean holdingShift) implements CustomPacketPayload {
    public static final Type<ShiftClickPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("raynnarpg", "shift_click")
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ShiftClickPacket> CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeBoolean(packet.holdingShift()),
            buf -> new ShiftClickPacket(buf.readBoolean())
    );
}