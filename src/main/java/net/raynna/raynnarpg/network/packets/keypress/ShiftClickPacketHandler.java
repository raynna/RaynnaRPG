package net.raynna.raynnarpg.network.packets.keypress;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;


public class ShiftClickPacketHandler implements IPayloadHandler<ShiftClickPacket> {

    @Override
    public void handle(ShiftClickPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                player.getPersistentData().putBoolean("isShifting", packet.holdingShift());
            });
        }
    }
}