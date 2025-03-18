package net.raynna.silentrpg.server.events;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.raynna.silentrpg.data.DataRegistry;
import net.raynna.silentrpg.data.MaterialData;
import net.raynna.silentrpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.silentrpg.server.player.PlayerProgress;
import net.raynna.silentrpg.server.player.playerdata.PlayerDataStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.raynna.silentrpg.server.player.skills.SkillType;
import net.raynna.silentrpg.server.utils.CraftingTracker;

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
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos blockPos = event.getPos();
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();
        if (event.getEntity() instanceof ServerPlayer player) {
            if (block.equals(Blocks.DIRT)) {//TODO grab restrictions from database
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("You can't destroy block " + block.getName().toFlatList().getFirst()));
            }
        }
    }


    @SubscribeEvent
    public static void onCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            System.out.println("Crafting event triggered by player: " + serverPlayer.getName().getString());

            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            if (progress == null) {
                serverPlayer.sendSystemMessage(Component.literal("Error: Could not retrieve player progress."));
                System.out.println("Error: Player progress not found for player: " + serverPlayer.getName().getString());
                return;
            }

            int playerCraftingLevel = progress.getSkills().getSkill(SkillType.CRAFTING).getLevel();
            System.out.println("Player crafting level: " + playerCraftingLevel);

            if (event.getInventory() instanceof CraftingContainer craftingContainer) {
                boolean craftingBlocked = false;

                int baseExperience = 0;

                for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
                    ItemStack materialStack = craftingContainer.getItem(i);

                    if (materialStack.isEmpty()) {
                        System.out.println("Slot " + i + " is empty.");
                        continue;
                    }

                    String materialId = materialStack.getItem().getDescriptionId();
                    System.out.println("Material in slot " + i + ": " + materialId + ", count: " + materialStack.getCount());

                    MaterialData materialData = DataRegistry.getMaterial(materialId);

                    if (materialData == null) {
                        System.out.println("No MaterialData found for material: " + materialId);
                        continue;
                    }

                    int requiredLevel = materialData.getLevelRequirement();
                    System.out.println("Material " + materialId + " requires crafting level: " + requiredLevel);

                    if (playerCraftingLevel < requiredLevel) {
                        craftingBlocked = true;
                        serverPlayer.sendSystemMessage(Component.literal("You need crafting level " + requiredLevel + " to use " + materialStack.getHoverName().getString() + " in crafting."));
                        System.out.println("Crafting blocked: Required level for material (" + materialId + ") is not met. Player level: " + playerCraftingLevel + ", required level: " + requiredLevel);

                        for (int j = 0; j < craftingContainer.getContainerSize(); j++) {
                            ItemStack stack = craftingContainer.getItem(j);
                            if (!stack.isEmpty()) {
                                serverPlayer.getInventory().placeItemBackInInventory(stack);
                                craftingContainer.setItem(j, ItemStack.EMPTY);
                            }
                        }

                        event.getCrafting().setCount(0);
                        System.out.println("Crafting canceled. All materials returned to inventory.");
                        return;
                    }

                    baseExperience += materialData.getExperience();
                    System.out.println("Base experience updated to: " + baseExperience);
                }

                if (!craftingBlocked) {
                    int craftedAmount = event.getCrafting().getCount();
                    String itemName = event.getCrafting().getHoverName().getString();
                    int totalExperience = baseExperience * craftedAmount;

                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, craftedAmount, totalExperience);

                    progress.getSkills().addXp(SkillType.CRAFTING, totalExperience);
                } else {
                    System.out.println("Crafting blocked. All materials returned to inventory.");
                }
            } else {
                System.out.println("Event inventory is not a CraftingContainer.");
            }
        } else {
            System.out.println("Crafting event triggered by non-ServerPlayer entity.");
        }
    }





    public static void register() {
        NeoForge.EVENT_BUS.register(ServerPlayerEvents.class);
    }
}