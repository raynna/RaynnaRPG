package net.raynna.raynnarpg.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.ui.SkillOverlay;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientGuiEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();
        SkillOverlay.show(graphics);
        if (mc.player == null || mc.level == null) return;
        RaynnaRPG.getOverlayManager().renderAll(graphics, mc.gameRenderer.getMainCamera());
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ClientGuiEvents.class);
    }
}
