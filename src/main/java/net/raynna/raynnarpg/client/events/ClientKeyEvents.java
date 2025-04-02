package net.raynna.raynnarpg.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.network.packets.keypress.ShiftClickPacketSender;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientKeyEvents {

    private static boolean lastShiftState = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        boolean shiftDown = InputConstants.isKeyDown(
                Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT
        );

        if (shiftDown != lastShiftState) {
            lastShiftState = shiftDown;
            ShiftClickPacketSender.updateShiftState(shiftDown);
        }
    }

    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            boolean shiftDown = InputConstants.isKeyDown(
                    Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT
            );
            System.out.println("Client detected Shift press? " + shiftDown);
            ShiftClickPacketSender.updateShiftState(shiftDown);
        }
    }*/
}
