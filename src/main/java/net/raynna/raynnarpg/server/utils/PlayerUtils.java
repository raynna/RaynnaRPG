package net.raynna.raynnarpg.server.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlayerUtils {

    public static void removeItem(ServerPlayer player, String id, int amount) {
        Item targetItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));

        int remainingAmount = amount;
        boolean itemRemoved = false;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotStack = player.getInventory().getItem(i);

            if (slotStack.getItem() == targetItem) {
                int stackSize = slotStack.getCount();

                if (stackSize <= remainingAmount) {
                    remainingAmount -= stackSize;
                    player.getInventory().setItem(i, ItemStack.EMPTY);

                    if (remainingAmount == 0) {
                        itemRemoved = true;
                        break;
                    }
                } else {
                    slotStack.setCount(stackSize - remainingAmount);
                    player.getInventory().setItem(i, slotStack);
                    itemRemoved = true;
                    break;
                }
            }
        }

        if (itemRemoved) {
            player.sendSystemMessage(Component.literal(amount + " of " + targetItem.getDescription().getString() + " has been removed."));
        }
    }
}