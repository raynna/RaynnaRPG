package net.raynna.raynnarpg.network.packets;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class NoOpPacketHandler<T extends CustomPacketPayload> implements IPayloadHandler<T> {
    @Override
    public void handle(T packet, IPayloadContext context) {
    }
}
