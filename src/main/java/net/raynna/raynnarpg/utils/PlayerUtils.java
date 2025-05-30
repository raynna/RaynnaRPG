package net.raynna.raynnarpg.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;

public class PlayerUtils {

    public static void removeItemStack(ServerPlayer player, ItemStack toRemove) {
        int count = toRemove.getCount();

        for (int i = 0; i < player.getInventory().getContainerSize() && count > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItem(stack, toRemove)) {
                int removeAmount = Math.min(count, stack.getCount());
                stack.shrink(removeAmount);
                count -= removeAmount;

                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }

        if (count > 0) {
            System.out.println("Warning: Failed to remove " + count + " items");
        }
    }

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