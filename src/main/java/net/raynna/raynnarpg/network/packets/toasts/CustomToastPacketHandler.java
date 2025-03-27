package net.raynna.raynnarpg.network.packets.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class CustomToastPacketHandler implements IPayloadHandler<CustomToastPacket> {

    @OnlyIn(Dist.CLIENT)
    private static SystemToast.SystemToastId getCustomToast() {
        return new SystemToast.SystemToastId();
    }



    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(CustomToastPacket packet, IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            Minecraft.getInstance().execute(() -> {
                Component title = Component.literal(packet.title());
                Component desc = Component.literal(packet.description());
                SystemToast toast = new SystemToast(getCustomToast(), title, desc);
                Minecraft.getInstance().getToasts().addToast(toast);
            });
        }
    }
}
