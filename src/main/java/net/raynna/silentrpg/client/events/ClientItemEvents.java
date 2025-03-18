package net.raynna.silentrpg.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.raynna.silentrpg.SilentRPG;

@EventBusSubscriber(modid = SilentRPG.MOD_ID, value = Dist.CLIENT)
public class ClientItemEvents {

    @SubscribeEvent
    public static void onItemHover(ItemTooltipEvent event) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.player != null;
        boolean isPlayerCreative = mc.player.isCreative();
        ItemStack stack = event.getItemStack();
        String itemId = stack.getItem().getDescriptionId();
            if (isPlayerCreative) {
                event.getToolTip().add(Component.literal("§6Description: §7" + itemId));
                event.getToolTip().add(Component.literal("§6Tags: "));
                for (TagKey<Item> tag : stack.getTags().toList()) {
                    event.getToolTip().add(Component.literal("§7- " + tag.location().toString()));
                }
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ClientItemEvents.class);
    }
}
