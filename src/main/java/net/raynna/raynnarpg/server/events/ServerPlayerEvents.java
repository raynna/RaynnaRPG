package net.raynna.raynnarpg.server.events;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.crafting.CraftingConfig;
import net.raynna.raynnarpg.config.smelting.SmeltingConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;
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
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.utils.CraftingTracker;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearData;

import java.util.*;

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

                ItemStack itemInHand = interactingPlayer.getMainHandItem();
                FoodProperties food = itemInHand.getItem().getFoodProperties(itemInHand, targetPlayer);
                boolean isHandEmpty = itemInHand.isEmpty();
                if (!isHandEmpty && food != null) {
                    if (targetPlayer.getFoodData().getFoodLevel() < FoodConstants.MAX_FOOD) {
                        ItemStack result = targetPlayer.eat(interactingPlayer.level(), itemInHand);
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
            if (progress == null)
                return;
            int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
            ItemStack mainHand = player.getMainHandItem();
            if (ModList.get().isLoaded("silentgear")) {
                if (mainHand.getItem() instanceof GearItem silent) {
                    String toolName = silent.asItem().getName(mainHand).getString();

                    Map<String, String> properties = new HashMap<>();
                    GearPropertiesData propertiesData = GearData.getProperties(mainHand);
                    propertiesData.properties().forEach((key, value) -> {
                        properties.put(key.getDisplayName().getString(), value.toString());
                    });
                    String harvestTierByName = properties.get("Harvest Tier");
                    ConfigData data = ToolConfig.getSilentGearData(harvestTierByName);
                    if (data != null) {
                        if (miningLevel < data.getLevel()) {
                            event.setCanceled(true);
                            MessagePacketSender.send(player, "You need a mining level of " + data.getLevel() + " in order to use " + toolName + " as a tool.");
                            return;
                        }
                    }
                }
            }

            ConfigData data = ToolConfig.getToolData(mainHand);
            if (data != null) {
                int playerMiningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
                String toolName = player.getMainHandItem().getHoverName().getString();
                if (playerMiningLevel < data.getLevel()) {
                    event.setCanceled(true);
                    MessagePacketSender.send(player, "You need a mining level of " + data.getLevel() + " in order to use " + toolName + " as a tool.");
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
            Skill smithing = progress.getSkills().getSkill(SkillType.SMELTING);
            if (serverPlayer.containerMenu instanceof SmokerMenu furnaceMenu) {
                int smeltingLevel = progress.getSkills().getSkill(SkillType.SMELTING).getLevel();
                ItemStack inputItem = furnaceMenu.getSlot(INPUT_SLOT).getItem();
                ItemStack fuelItem = furnaceMenu.getSlot(FUEL_SLOT).getItem();
                String smeltingItemName = event.getSmelting().getHoverName().getString();
                ConfigData data = SmeltingConfig.getSmeltingData(event.getSmelting());
                if (data != null) {
                    int requiredLevel = data.getLevel();
                    if (smeltingLevel < requiredLevel) {
                        int outputCount = event.getSmelting().getCount();
                        event.getSmelting().setCount(0);
                        serverPlayer.sendSystemMessage(Component.literal("You need a smelting level of " + requiredLevel + " in order to create " + smeltingItemName + "s."));
                        Item raw = BuiltInRegistries.ITEM.get(ResourceLocation.parse(data.getRaw()));
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
                    baseExperience += data.getXp();
                    int smeltedAmount = event.getSmelting().getCount();
                    String itemName = event.getSmelting().getHoverName().getString();
                    double totalExperience = baseExperience * smeltedAmount;
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, smeltedAmount, totalExperience, smithing.getType(), () -> {
                        progress.getSkills().addXp(SkillType.SMELTING, totalExperience);
                    });
                }
            }
            if (serverPlayer.containerMenu instanceof BlastFurnaceMenu furnaceMenu) {
                int smeltingLevel = progress.getSkills().getSkill(SkillType.SMELTING).getLevel();
                ItemStack inputItem = furnaceMenu.getSlot(INPUT_SLOT).getItem();
                ItemStack fuelItem = furnaceMenu.getSlot(FUEL_SLOT).getItem();
                String smeltingItemName = event.getSmelting().getHoverName().getString();
                ConfigData data = SmeltingConfig.getSmeltingData(event.getSmelting());
                if (data != null) {
                    int requiredLevel = data.getLevel();
                    if (smeltingLevel < requiredLevel) {
                        int outputCount = event.getSmelting().getCount();
                        event.getSmelting().setCount(0);
                        serverPlayer.sendSystemMessage(Component.literal("You need a smelting level of " + requiredLevel + " in order to create " + smeltingItemName + "s."));
                        Item raw = BuiltInRegistries.ITEM.get(ResourceLocation.parse(data.getRaw()));
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
                    baseExperience += data.getXp();
                    int smeltedAmount = event.getSmelting().getCount();
                    String itemName = event.getSmelting().getHoverName().getString();
                    double totalExperience = baseExperience * smeltedAmount;
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, smeltedAmount, totalExperience, smithing.getType(), () -> {
                        progress.getSkills().addXp(SkillType.SMELTING, totalExperience);
                    });
                }
            }
            if (serverPlayer.containerMenu instanceof FurnaceMenu furnaceMenu) {
                int smeltingLevel = progress.getSkills().getSkill(SkillType.SMELTING).getLevel();
                ItemStack inputItem = furnaceMenu.getSlot(INPUT_SLOT).getItem();
                ItemStack fuelItem = furnaceMenu.getSlot(FUEL_SLOT).getItem();
                String smeltingItemName = event.getSmelting().getHoverName().getString();
                ConfigData data = SmeltingConfig.getSmeltingData(event.getSmelting());
                if (data != null) {
                    int requiredLevel = data.getLevel();
                    if (smeltingLevel < requiredLevel) {
                        int outputCount = event.getSmelting().getCount();
                        event.getSmelting().setCount(0);
                        serverPlayer.sendSystemMessage(Component.literal("You need a smelting level of " + requiredLevel + " in order to create " + smeltingItemName + "s."));
                        Item raw = BuiltInRegistries.ITEM.get(ResourceLocation.parse(data.getRaw()));
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
                    baseExperience += data.getXp();
                    int smeltedAmount = event.getSmelting().getCount();
                    String itemName = event.getSmelting().getHoverName().getString();
                    double totalExperience = baseExperience * smeltedAmount;
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, smeltedAmount, totalExperience, smithing.getType(), () -> {
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
            Skill crafting = progress.getSkills().getSkill(SkillType.CRAFTING);
            if (event.getInventory() instanceof CraftingContainer craftingContainer) {
                int playerCraftingLevel = crafting.getLevel();
                boolean craftingBlocked = false;
                double totalBaseExperience = 0.0;
                boolean craftingBenchFull = true;
                Set<String> uniqueMaterials = new HashSet<>();
                int totalSlotsUsed = 0;
                for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
                    ItemStack materialStack = craftingContainer.getItem(i);
                    if (materialStack.isEmpty()) {
                        craftingBenchFull = false;
                        continue;
                    }
                    totalSlotsUsed++;
                    String materialId = materialStack.getDescriptionId();
                    String materialName = materialStack.getHoverName().getString();
                    uniqueMaterials.add(materialName);
                    ConfigData data = CraftingConfig.getCraftingData(materialStack);
                    if (data == null) {
                        continue;
                    }
                    int requiredLevel = data.getLevel();
                    if (playerCraftingLevel < requiredLevel) {
                        craftingBlocked = true;
                        serverPlayer.sendSystemMessage(Component.literal("You need a " + crafting.getType().getName() + " level of " + requiredLevel + " in order to use " + materialStack.getHoverName().getString() + " in crafting."));
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
                    totalBaseExperience += data.getXp();
                }
                if (craftingBenchFull && uniqueMaterials.size() == 1 && totalSlotsUsed > 4) {
                    craftingBlocked = true;
                }
                if (!craftingBlocked) {
                    String itemName = event.getCrafting().getHoverName().getString();
                    double totalExperience = totalBaseExperience;
                    int itemsCreated = event.getCrafting().getCount();
                    CraftingTracker.accumulateCraftingData(serverPlayer, itemName, itemsCreated, totalExperience, crafting.getType(), () -> {
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