package net.raynna.raynnarpg.server.events;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.raynna.raynnarpg.data.*;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.utils.CraftingTracker;

import java.util.UUID;

public class ServerPlayerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerProgress playerProgress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            playerProgress.init(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            UUID playerUUID = serverPlayer.getUUID();
            ServerLevel level = serverPlayer.serverLevel();
            PlayerDataStorage dataStorage = PlayerDataProvider.getData(level);
            dataStorage.markDirty();
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.EntityInteract event) {
        // Check if the entity being interacted with is a player
        if (event.getEntity() instanceof ServerPlayer interactingPlayer) {
            // Check if the target entity is another player
            if (event.getTarget() instanceof Player targetPlayer) {
                // Check if the target player has hunger (less than full saturation)

                // Check if the interacting player has food to give
                ItemStack itemInHand = interactingPlayer.getMainHandItem();
                FoodProperties food = itemInHand.getItem().getFoodProperties(itemInHand, targetPlayer);
                System.out.println(food);
                boolean isHandEmpty = itemInHand.isEmpty();
                if (!isHandEmpty && food != null) {
                    if (targetPlayer.getFoodData().getFoodLevel() < FoodConstants.MAX_FOOD) {
                        ItemStack result = targetPlayer.eat(interactingPlayer.level(), itemInHand);
                        System.out.println(result.getHoverName().getString());
                        if (result.isEmpty() || result.getCount() < itemInHand.getCount()) {
                            itemInHand.shrink(1);
                            interactingPlayer.sendSystemMessage(Component.literal("You have fed " + targetPlayer.getName().getString() + "."));
                            targetPlayer.sendSystemMessage(Component.literal(interactingPlayer.getName().getString() + " has fed you."));
                        }
                    } else {
                        interactingPlayer.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + " is already full."));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos blockPos = event.getPos();
        BlockState state = level.getBlockState(blockPos);
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
            ToolData toolData = DataRegistry.getTool(player.getMainHandItem().getDescriptionId());
            if (progress == null)
                return;
            if (toolData != null) {
                int playerMiningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
                int levelRequirement = toolData.getLevelRequirement();
                String toolName = player.getMainHandItem().getHoverName().getString();
                if (playerMiningLevel < levelRequirement) {
                    event.setCanceled(true);
                    MessagePacketSender.send(player, "You need a mining level of " + levelRequirement + " in order to use " + toolName + " as a tool.");
                }
            }
        }
    }

    private static final int FUEL_SLOT = 1, INPUT_SLOT = 0;

    @SubscribeEvent
    public static void onFurnace(PlayerEvent.ItemSmeltedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            if (progress == null) return;
            if (serverPlayer.containerMenu instanceof BlastFurnaceMenu furnaceMenu) {
                int smeltingLevel = progress.getSkills().getSkill(SkillType.SMELTING).getLevel();
                ItemStack inputItem = furnaceMenu.getSlot(INPUT_SLOT).getItem();
                ItemStack fuelItem = furnaceMenu.getSlot(FUEL_SLOT).getItem();
                String smeltingItemName = event.getSmelting().getHoverName().getString();
                SmeltingData smeltingData = DataRegistry.getDataFromItem(event.getSmelting(), SmeltingData.class);
                if (smeltingData != null) {
                    int requiredLevel = smeltingData.getLevelRequirement();
                    if (smeltingLevel < requiredLevel) {
                        int outputCount = event.getSmelting().getCount();
                        event.getSmelting().setCount(0);
                        serverPlayer.sendSystemMessage(Component.literal("You need a smelting level of " + requiredLevel + " in order to create " + smeltingItemName + "s."));
                        Item raw = BuiltInRegistries.ITEM.get(ResourceLocation.parse(smeltingData.getRawMaterial()));
                        ItemStack rawMaterial = new ItemStack(raw);
                        if (inputItem.isEmpty()) {
                            rawMaterial = new ItemStack(raw, outputCount);
                            furnaceMenu.getSlot(INPUT_SLOT).set(rawMaterial);
                            return;
                        }
                        boolean invalidInputItem = !furnaceMenu.getSlot(INPUT_SLOT).getItem().getDescriptionId().equals(rawMaterial.getDescriptionId());
                        if (invalidInputItem) {
                            rawMaterial = new ItemStack(raw, outputCount);
                            serverPlayer.getInventory().placeItemBackInInventory(rawMaterial);
                            return;
                        }
                        boolean fullInput = furnaceMenu.getSlot(INPUT_SLOT).getItem().getCount() + outputCount > furnaceMenu.getSlot(INPUT_SLOT).getItem().getMaxStackSize();
                        if (fullInput) {
                            ItemStack newInputItem = inputItem.copy();
                            newInputItem.setCount(outputCount);
                            serverPlayer.getInventory().placeItemBackInInventory(newInputItem);
                            return;
                        }
                        ItemStack newInputItem = inputItem.copy();
                        newInputItem.grow(outputCount);
                        furnaceMenu.getSlot(INPUT_SLOT).set(newInputItem);
                        return;
                    }
                    double baseExperience = 0;
                    baseExperience += smeltingData.getExperience();
                    int smeltedAmount = event.getSmelting().getCount();
                    String itemName = event.getSmelting().getHoverName().getString();
                    double totalExperience = baseExperience * smeltedAmount;
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, smeltedAmount, totalExperience, () -> {
                        progress.getSkills().addXp(SkillType.SMELTING, totalExperience);
                    });
                }

            }
            if (serverPlayer.containerMenu instanceof FurnaceMenu furnaceMenu) {
                int smeltingLevel = progress.getSkills().getSkill(SkillType.SMELTING).getLevel();
                ItemStack inputItem = furnaceMenu.getSlot(INPUT_SLOT).getItem();
                ItemStack fuelItem = furnaceMenu.getSlot(FUEL_SLOT).getItem();
                String smeltingItemName = event.getSmelting().getHoverName().getString();
                SmeltingData smeltingData = DataRegistry.getDataFromItem(event.getSmelting(), SmeltingData.class);
                if (smeltingData != null) {
                    int requiredLevel = smeltingData.getLevelRequirement();
                    if (smeltingLevel < requiredLevel) {
                        int outputCount = event.getSmelting().getCount();
                        event.getSmelting().setCount(0);
                        serverPlayer.sendSystemMessage(Component.literal("You need a smelting level of " + requiredLevel + " in order to create " + smeltingItemName + "s."));
                        Item raw = BuiltInRegistries.ITEM.get(ResourceLocation.parse(smeltingData.getRawMaterial()));
                        ItemStack rawMaterial = new ItemStack(raw);
                        if (inputItem.isEmpty()) {
                            rawMaterial = new ItemStack(raw, outputCount);
                            furnaceMenu.getSlot(INPUT_SLOT).set(rawMaterial);
                            return;
                        }
                        boolean invalidInputItem = !furnaceMenu.getSlot(INPUT_SLOT).getItem().getDescriptionId().equals(rawMaterial.getDescriptionId());
                        if (invalidInputItem) {
                            rawMaterial = new ItemStack(raw, outputCount);
                            serverPlayer.getInventory().placeItemBackInInventory(rawMaterial);
                            return;
                        }
                        boolean fullInput = furnaceMenu.getSlot(INPUT_SLOT).getItem().getCount() + outputCount > furnaceMenu.getSlot(INPUT_SLOT).getItem().getMaxStackSize();
                        if (fullInput) {
                            ItemStack newInputItem = inputItem.copy();
                            newInputItem.setCount(outputCount);
                            serverPlayer.getInventory().placeItemBackInInventory(newInputItem);
                            return;
                        }
                        ItemStack newInputItem = inputItem.copy();
                        newInputItem.grow(outputCount);
                        furnaceMenu.getSlot(INPUT_SLOT).set(newInputItem);
                        return;
                    }
                    double baseExperience = 0;
                    baseExperience += smeltingData.getExperience();
                    int smeltedAmount = event.getSmelting().getCount();
                    String itemName = event.getSmelting().getHoverName().getString();
                    double totalExperience = baseExperience * smeltedAmount;
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, smeltedAmount, totalExperience, () -> {
                        progress.getSkills().addXp(SkillType.SMELTING, totalExperience);
                    });
                }

            }
        }
    }

    @SubscribeEvent
    public static void onCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);

            if (progress == null) return;

            if (event.getInventory() instanceof CraftingContainer craftingContainer) {
                int playerCraftingLevel = progress.getSkills().getSkill(SkillType.CRAFTING).getLevel();
                boolean craftingBlocked = false;
                double totalBaseExperience = 0.0;
                int totalCraftedAmount = 0;
                for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
                    ItemStack materialStack = craftingContainer.getItem(i);
                    if (materialStack.isEmpty()) {
                        continue;
                    }
                    String materialId = materialStack.getItem().getDescriptionId();
                    CraftingData craftingData = DataRegistry.getDataFromItem(materialStack, CraftingData.class);
                    if (craftingData == null) {
                        continue;
                    }
                    int requiredLevel = craftingData.getLevelRequirement();
                    if (playerCraftingLevel < requiredLevel) {
                        craftingBlocked = true;
                        serverPlayer.sendSystemMessage(Component.literal("You need a crafting level of " + requiredLevel + " in order to use " + materialStack.getHoverName().getString() + " in crafting."));
                        for (int j = 0; j < craftingContainer.getContainerSize(); j++) {
                            ItemStack stack = craftingContainer.getItem(j);
                            if (!stack.isEmpty()) {
                                serverPlayer.getInventory().placeItemBackInInventory(stack);
                                craftingContainer.setItem(j, ItemStack.EMPTY);
                            }
                        }
                        event.getCrafting().setCount(0);
                        return;
                    }
                    totalBaseExperience += craftingData.getExperience();
                    totalCraftedAmount += event.getCrafting().getCount();
                }

                if (!craftingBlocked) {
                    String itemName = event.getCrafting().getHoverName().getString();
                    double totalExperience = totalBaseExperience * totalCraftedAmount;
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, totalCraftedAmount, totalExperience, () -> {
                        progress.getSkills().addXp(SkillType.CRAFTING, totalExperience);
                    });
                }
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ServerPlayerEvents.class);
    }
}