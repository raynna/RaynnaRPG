package net.raynna.raynnarpg.server.events;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.raynna.raynnarpg.Config;
import net.raynna.raynnarpg.compat.silentgear.SilentGearCompat;
import net.raynna.raynnarpg.config.*;
import net.raynna.raynnarpg.config.combat.CombatConfig;
import net.raynna.raynnarpg.config.crafting.CraftingConfig;
import net.raynna.raynnarpg.config.smelting.SmeltingConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.recipe.ReversibleCraftingRegistry;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataStorage;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.utils.*;

import java.util.*;

public class ServerPlayerEvents {

    private static final int OUTPUT_SLOT = 2;
    private static final int FUEL_SLOT = 1;
    private static final int INPUT_SLOT = 0;

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerDataProvider.getPlayerProgress(player).init(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerDataStorage.savePlayer(player);
        }
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppedEvent event) {
        event.getServer().getAllLevels().forEach(level -> {
            level.players().forEach(PlayerDataStorage::savePlayer);
        });
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
            if (progress == null) return;

            ItemStack mainHand = player.getMainHandItem();
            int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
            int combatLevel = progress.getSkills().getSkill(SkillType.COMBAT).getLevel();

            if (!validateToolUse(player, mainHand, miningLevel)) {
                event.setCanceled(true);
            }
            if (!canUseWeapon(player, mainHand, combatLevel)) {
                player.swing(event.getHand());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBowdraw(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        if (progress == null) return;
        ItemStack mainHand = player.getMainHandItem();
        if (ItemUtils.isBow(mainHand)) {
            int combatLevel = progress.getSkills().getSkill(SkillType.COMBAT).getLevel();
            if (!canUseWeapon(player, mainHand, combatLevel)) {
                player.releaseUsingItem();
                player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 0.5f, 0.5f);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
            if (progress == null) return;

            ItemStack mainHand = player.getMainHandItem();
        }
    }

    @SubscribeEvent
    public static void onEntityAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
            if (progress == null) return;

            ItemStack weapon = player.getMainHandItem();
            if (!canUseWeapon(player, weapon, progress.getSkills().getSkill(SkillType.COMBAT).getLevel())) {
                player.swing(InteractionHand.MAIN_HAND);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getTarget() instanceof Player target) {
                handlePlayerFeeding(player, target);
            }
        }
    }

    @SubscribeEvent
    public static void onEquip(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        EquipmentSlot slot = event.getSlot();
        ItemStack newItem = event.getTo();
        ItemStack oldItem = event.getFrom();

        if (newItem.isEmpty() || !isEquipmentSlot(slot)) {
            return;
        }

        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        if (progress == null) {
            return;
        }

        if (!canEquipItem(player, newItem, slot, progress)) {
            revertEquipmentChange(player, event);
            player.closeContainer();
            syncEquipmentState(player);
        }
    }

    private static void revertEquipmentChange(ServerPlayer player, LivingEquipmentChangeEvent event) {
        ItemStack attemptedItem = event.getTo().copy();
        EquipmentSlot slot = event.getSlot();

        if (!attemptedItem.isEmpty()) {
            player.getInventory().placeItemBackInInventory(attemptedItem);
        }
        player.setItemSlot(slot, ItemStack.EMPTY);
    }

    private static void syncEquipmentState(ServerPlayer player) {
        List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            equipmentList.add(new Pair<>(slot, player.getItemBySlot(slot)));
        }
        player.connection.send(new ClientboundSetEquipmentPacket(player.getId(), equipmentList));

        player.inventoryMenu.broadcastChanges();

        player.getServer().execute(() -> {
            player.containerMenu.slotsChanged(player.getInventory());
        });
    }

    private static boolean canEquipItem(ServerPlayer player, ItemStack item, EquipmentSlot slot, PlayerProgress progress) {
        int combatLevel = progress.getSkills().getSkill(SkillType.COMBAT).getLevel();
        boolean isArmor = isArmorSlot(slot);
        if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(item)) {
            if (!ItemUtils.checkCombatLevel(player, item, combatLevel, isArmor)) {
                return false;
            }
        }

        ConfigData data = CombatConfig.getData(item, isArmor);
        if (data != null && combatLevel < data.getLevel()) {
            MessagePacketSender.send(player,
                    "You need combat level " + data.getLevel() +
                            " to equip " + item.getHoverName().getString());
            return false;
        }
        return true;
    }

    private static boolean isEquipmentSlot(EquipmentSlot slot) {
        return isArmorSlot(slot) ||
                slot == EquipmentSlot.OFFHAND;
    }

    private static boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD ||
                slot == EquipmentSlot.CHEST ||
                slot == EquipmentSlot.LEGS ||
                slot == EquipmentSlot.FEET;
    }

    @SubscribeEvent
    public static void onFurnace(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getEntity().containerMenu instanceof AbstractFurnaceMenu menu)) return;
        handleSmeltingEvent(player, menu, event);
    }


    @SubscribeEvent
    public static void onAnvilTake(AnvilRepairEvent event) {
        if (event.getEntity().getPersistentData().contains("enchant_removed")) {
            CompoundTag newItemTag = event.getEntity().getPersistentData().getCompound("enchant_removed");
            event.getEntity().getPersistentData().remove("enchant_removed");
            if (event.getEntity().getServer() == null) {
                return;
            }
            HolderLookup.Provider provider = event.getEntity().getServer().registryAccess();
            Optional<ItemStack> optionalItem = ItemStack.parse(provider, newItemTag);
            optionalItem.ifPresent(item -> {
                event.getEntity().getInventory().placeItemBackInInventory(item);
            });
        }
    }

    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (event.getPlayer().getPersistentData().contains("enchant_removed")) {
            event.getPlayer().getPersistentData().remove("enchant_removed");
        }
        if (left.isEmpty() || !right.is(Items.BOOK) || !left.isEnchanted()) {
            return;
        }

        ItemEnchantments originalEnchants = left.getTagEnchantments();
        if (originalEnchants.isEmpty()) {
            return;
        }

        Holder<Enchantment> firstEnchantHolder = null;
        int firstLevel = 0;

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : originalEnchants.entrySet()) {
            firstEnchantHolder = entry.getKey();
            firstLevel = entry.getIntValue();
            break;
        }

        if (firstEnchantHolder == null) {
            return;
        }

        EnchantmentInstance instance = new EnchantmentInstance(firstEnchantHolder, firstLevel);
        ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(instance);

        Map<Holder<Enchantment>, Integer> mutableEnchants = new HashMap<>();
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : originalEnchants.entrySet()) {
            if (!entry.getKey().equals(firstEnchantHolder)) {
                mutableEnchants.put(entry.getKey(), entry.getIntValue());
            }
        }

        ItemStack newItem = left.copy();
        EnchantmentHelper.setEnchantments(newItem, ItemEnchantments.EMPTY);
        EnchantmentHelper.updateEnchantments(newItem, mutable -> {
            for (Map.Entry<Holder<Enchantment>, Integer> entry : mutableEnchants.entrySet()) {
                mutable.set(entry.getKey(), entry.getValue());
            }
        });
        if (event.getPlayer().getServer() == null) {
            return;
        }
        HolderLookup.Provider provider = event.getPlayer().getServer().registryAccess();
        CompoundTag compoundTag = new CompoundTag();
        event.getPlayer().getPersistentData().put("enchant_removed", newItem.save(provider, compoundTag));

        event.setOutput(enchantedBook);
        event.setCost(1);
        event.setMaterialCost(1);
    }


    @SubscribeEvent
    public static void onCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getInventory() instanceof CraftingContainer container) {
            handleCraftingEvent(player, container, event);
        }
    }

    private static boolean canUseWeapon(ServerPlayer player, ItemStack weapon, int combatLevel) {
        if (weapon.isEmpty()) return true;

        /*if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(weapon) && ItemUtils.isWeapon(weapon)) {
            if (!ItemUtils.checkCombatLevel(player, weapon, combatLevel, false)) {
                return false;
            }
        }*/

        /*ConfigData data = net.raynna.raynnarpg.newconfig.combat.CombatConfig.getWeaponData(weapon);
        if (data != null && combatLevel < data.getLevel()) {
            MessagePacketSender.send(player, "(Server) You need combat level " + data.getLevel() + " to use " + weapon.getHoverName().getString());
            return false;
        }*/
        return true;
    }

    private static boolean validateToolUse(ServerPlayer player, ItemStack tool, int miningLevel) {
        if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(tool) && ItemUtils.isTool(tool)) {
            if (!ItemUtils.checkMiningLevel(player, tool, miningLevel)) {
                return false;
            }
        }

        ConfigData data = ToolConfig.getToolData(tool);
        if (data != null && miningLevel < data.getLevel()) {
            MessagePacketSender.send(player, "You need mining level " + data.getLevel() + " to use " + tool.getHoverName().getString());
            return false;
        }
        return true;
    }

    private static void handlePlayerFeeding(ServerPlayer feeder, Player target) {
        ItemStack foodItem = feeder.getMainHandItem();
        FoodProperties foodProps = foodItem.getItem().getFoodProperties(foodItem, target);

        if (!foodItem.isEmpty() && foodProps != null) {
            if (target.getFoodData().getFoodLevel() < FoodConstants.MAX_FOOD) {
                ItemStack result = target.eat(feeder.level(), foodItem);
                if (result.isEmpty() || result.getCount() < foodItem.getCount()) {
                    foodItem.shrink(1);
                    sendFeedingMessages(feeder, target);
                }
            } else {
                feeder.sendSystemMessage(Component.literal(target.getName().getString() + " doesn't have any hunger."));
            }
        }
    }

    private static void sendFeedingMessages(Player feeder, Player target) {
        feeder.sendSystemMessage(Component.literal("You have fed " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal(feeder.getName().getString() + " has fed you."));
    }

    private static void handleSmeltingEvent(ServerPlayer player, AbstractFurnaceMenu menu, PlayerEvent.ItemSmeltedEvent event) {
        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        if (progress == null) return;

        ConfigData data = SmeltingConfig.getSmeltingData(event.getSmelting());
        if (data == null) return;
        if (progress.getSkills().getSkill(SkillType.SMELTING).getLevel() < data.getLevel()) {
            handleFailedSmelting(player, menu, event, data);
        } else {
            grantSmeltingExperience(player, progress, event, data);
        }
    }

    private static void handleFailedSmelting(ServerPlayer player, AbstractFurnaceMenu menu, PlayerEvent.ItemSmeltedEvent event, ConfigData data) {
        player.sendSystemMessage(Component.literal("You need smelting level " + data.getLevel() + " to smelt " + event.getSmelting().getHoverName().getString()));
        ItemStack outputCopy = event.getSmelting().copy();
        event.getSmelting().setCount(0);
        ItemStack rawMaterial = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(data.getRaw())), outputCopy.getCount());
        ItemStack input = menu.getSlot(INPUT_SLOT).getItem();
        if (input.isEmpty()) {
            menu.getSlot(INPUT_SLOT).set(rawMaterial);
        } else if (!ItemStack.isSameItem(input, rawMaterial)) {
            player.getInventory().placeItemBackInInventory(rawMaterial);
        } else {
            int maxStackSize = input.getMaxStackSize();
            int totalCount = input.getCount() + rawMaterial.getCount();

            if (totalCount > maxStackSize) {
                int fitCount = maxStackSize - input.getCount();
                input.grow(fitCount);

                int overflowCount = rawMaterial.getCount() - fitCount;
                if (overflowCount > 0) {
                    ItemStack overflow = rawMaterial.copy();
                    overflow.setCount(overflowCount);
                    player.getInventory().placeItemBackInInventory(overflow);
                }
            } else {
                input.grow(rawMaterial.getCount());
            }
            menu.getSlot(INPUT_SLOT).set(input);
        }
        boolean shifting = player.getPersistentData().getBoolean("isShifting");
        if (shifting) {//prevent dupe when shiftclicking
            PlayerUtils.removeItemStack(player, outputCopy);
        }
    }

    private static void grantSmeltingExperience(ServerPlayer player, PlayerProgress progress, PlayerEvent.ItemSmeltedEvent event, ConfigData data) {
        double xp = Math.round(data.getXp() * event.getSmelting().getCount() * 100) / 100.0;
        double xpRate = Config.Server.XP_RATE.get();
        if (xpRate != 1.0) {
            xp *= xpRate;
        }
        double finalXp = xp;
        CraftingTracker.accumulateCraftingData(player, event.getSmelting(), null, finalXp, SkillType.SMELTING, () -> {
            if (Utils.isXpCapped(progress.getSkills().getSkill(SkillType.SMELTING).getLevel(), data.getLevel())) {
                MessageSender.send(player, "You are to high of a level to gain experience from " + event.getSmelting().getHoverName().getString());
                return;
            }
            progress.getSkills().addXpNoBonus(SkillType.SMELTING, finalXp);
        });
    }

    private static void handleCraftingEvent(ServerPlayer player, CraftingContainer container, PlayerEvent.ItemCraftedEvent event) {
        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        if (progress == null) return;

        CraftingResult result = checkCraftingMaterials(player, event, container, progress);
        if (result.blocked) {
            event.getCrafting().setCount(0);
        } else if (shouldGrantExperience(event.getCrafting(), result)) {
            grantCraftingExperience(player, progress, event.getCrafting(), result);
        }
    }

    private static CraftingResult checkCraftingMaterials(ServerPlayer player, PlayerEvent.ItemCraftedEvent event, CraftingContainer container, PlayerProgress progress) {
        CraftingResult result = new CraftingResult();
        int playerLevel = progress.getSkills().getSkill(SkillType.CRAFTING).getLevel();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack material = container.getItem(i);
            if (material.isEmpty()) {
                result.hasEmptySlots = true;
                continue;
            }
            result.totalSlotsUsed++;
            String materialName = material.getItem().getDescriptionId();
            result.uniqueMaterials.add(materialName);

            CraftingResult.Materials materialData = result.materials.get(materialName);
            if (materialData == null) {
                materialData = new CraftingResult.Materials(materialName, 0, 0, false);
            }
            ConfigData data = CraftingConfig.getCraftingData(material);
            if (data == null) continue;
            materialData.increaseCount(1);
            double materialXp = data.getXp();
            if (Utils.isXpCapped(progress.getSkills().getSkill(SkillType.CRAFTING).getLevel(), data.getLevel())) {
                materialData.setCapped(true);
                result.materials.put(materialName, materialData);
                continue;
            }
            if (playerLevel < data.getLevel()) {
                handleInvalidCraftingMaterial(player, event, container, material, data.getLevel(), progress.getSkills().getSkill(SkillType.CRAFTING));
                result.blocked = true;
                break;
            }
            materialData.trackXp(materialXp);
            result.materials.put(materialName, materialData);
            result.totalExperience += materialXp;

            result.levelReq = data.getLevel();
        }
        return result;
    }

    private static void handleInvalidCraftingMaterial(ServerPlayer player, PlayerEvent.ItemCraftedEvent event, CraftingContainer container, ItemStack material, int requiredLevel, Skill skill) {
        MessageSender.send(player, "You need " + skill.getType().getName() + " level " + requiredLevel + " to use " + material.getHoverName().getString() + " in crafting.");

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
                container.setItem(i, ItemStack.EMPTY);
            }
        }
        boolean shifting = player.getPersistentData().getBoolean("isShifting");
        if (shifting) {//prevent dupe when shiftclicking
            PlayerUtils.removeItemStack(player, event.getCrafting().copy());
        }
    }

    private static boolean shouldGrantExperience(ItemStack craftedItem, CraftingResult result) {
        boolean isReversible = ReversibleCraftingRegistry.isReversible(craftedItem.getItem());
        boolean hasMultipleUniqueMaterials = result.uniqueMaterials.size() > 1;
        boolean invalidItem = isReversible && !hasMultipleUniqueMaterials;
        return !invalidItem;
    }

    private static void grantCraftingExperience(ServerPlayer player, PlayerProgress progress, ItemStack craftedItem, CraftingResult result) {
        double xp = Math.round(result.totalExperience * 100) / 100.0;
        double xpRate = Config.Server.XP_RATE.get();
        if (xpRate != 1.0) {
            xp *= xpRate;
        }
        double finalXp = xp;
        CraftingTracker.accumulateCraftingData(player, craftedItem, result, finalXp, SkillType.CRAFTING, () -> {
            progress.getSkills().addXpNoBonus(SkillType.CRAFTING, finalXp);

        });
    }

    public static class CraftingResult {
        public boolean blocked = false;
        public boolean hasEmptySlots = false;
        public int totalSlotsUsed = 0;
        public Set<String> uniqueMaterials = new HashSet<>();
        public double totalExperience = 0.0;
        public int levelReq = 0;
        public Map<String, Materials> materials = new HashMap<>();

        public static class Materials {

            private String name;
            private int count;
            private double xp;
            private boolean capped;


            public Materials(String name, int count, double xp, boolean capped) {
                this.name = name;
                this.count = count;
                this.xp = xp;
                this.capped = capped;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void trackXp(double amount) {
                this.xp += amount;
            }

            public void increaseCount(int count) {
                this.count += count;
            }

            public void setCapped(boolean capped) {
                this.capped = capped;
            }

            public double getXp() {
                return xp;
            }

            public boolean isCapped() {
                return capped;
            }

            public int getCount() {
                return count;
            }

            public String getName() {
                return name;
            }
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ServerPlayerEvents.class);
    }
}