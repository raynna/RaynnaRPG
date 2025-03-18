package net.raynna.silentrpg.network.packets.message;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record MessagePacket(String message) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final Type<MessagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("silentrpg", "player_message"));

    public static final StreamCodec<FriendlyByteBuf, MessagePacket> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MessagePacket packet) {
            buf.writeUtf(packet.message);
        }

        @Override
        public MessagePacket decode(FriendlyByteBuf buf) {
            String message = buf.readUtf(Short.MAX_VALUE);
            return new MessagePacket(message);
        }
    };
}