package net.raynna.raynnarpg.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.network.packets.keypress.ShiftClickPacketSender;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientKeyEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        boolean shiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        ShiftClickPacketSender.updateShiftState(shiftDown);
    }
}
