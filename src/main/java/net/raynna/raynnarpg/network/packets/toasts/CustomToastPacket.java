package net.raynna.raynnarpg.network.packets.toasts;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CustomToastPacket(String title, String description) implements CustomPacketPayload {

    public static final Type<CustomToastPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("raynnarpg", "custom_toast"));

    public static final StreamCodec<FriendlyByteBuf, CustomToastPacket> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, CustomToastPacket packet) {
            buf.writeUtf(packet.title);
            buf.writeUtf(packet.description);
        }

        @Override
        public CustomToastPacket decode(FriendlyByteBuf buf) {
            String title = buf.readUtf(Short.MAX_VALUE);
            String description = buf.readUtf(Short.MAX_VALUE);
            return new CustomToastPacket(title, description);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
