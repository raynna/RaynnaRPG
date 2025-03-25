package net.raynna.raynnarpg.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.SgItems;

import java.util.List;

public class StarterItems {

    private static final List<String> STARTER_ITEM_IDS = List.of(
        SgItems.ROD_BLUEPRINT.getRegisteredName()
    );

    public static void giveItems(ServerPlayer player) {
        PlayerUtils.removeItem(player, SgItems.BLUEPRINT_PACKAGE.getRegisteredName(), 1);
         for (String id : STARTER_ITEM_IDS) {
            player.sendSystemMessage(Component.literal("[StarterItems.giveItems] id: " + id));
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
            ItemStack stack = new ItemStack(item);
            player.getInventory().add(stack);
        }
    }
}