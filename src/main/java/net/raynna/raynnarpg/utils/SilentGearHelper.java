package net.raynna.raynnarpg.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.combat.CombatConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;

import java.util.HashMap;
import java.util.Map;

public class SilentGearHelper {

    /**
     * Checks if SilentGear is loaded
     */
    public static boolean isSilentGearLoaded() {
        return ModList.get().isLoaded("silentgear");
    }

    /**
     * Gets all properties from a SilentGear item as a map of strings
     */
    public static Map<String, String> getGearProperties(ItemStack gearStack) {
        Map<String, String> properties = new HashMap<>();
        if (!(gearStack.getItem() instanceof GearItem)) {
            return properties;
        }
        GearPropertiesData propertiesData = GearData.getProperties(gearStack);
        propertiesData.properties().forEach((key, value) -> {
            properties.put(key.getDisplayName().getString(), value.toString());
        });
        return properties;
    }

    /**
     * Gets a specific property value from a SilentGear item
     */
    /**
     * Improved property getter that uses SilentGear's enum properly
     */
    public static String getGearProperty(ItemStack gearStack, GearProperty property) {
        if (!(gearStack.getItem() instanceof GearItem)) {
            return "";
        }
        GearPropertiesData propertiesData = GearData.getProperties(gearStack);
        if (propertiesData == null) {
            return "";
        }
        Object value = propertiesData.properties().get(property);
        return value != null ? value.toString() : "";
    }

    public static boolean isWeapon(ItemStack item) {
        if (item.isEmpty()) return false;
        String itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        if (item.is(ItemTags.SWORDS) ||
                item.is(ItemTags.AXES)) {
            return true;
        }
        return itemId.matches(".*(sword|_axe|katana|blade|dagger|scythe|halberd|mace|warhammer|bow|crossbow|staff|wand|spear|trident|knife|slingshot|sickle|hammer|mattock|excavator|paxel).*");
    }

    public static boolean isTool(ItemStack item) {
        if (item.isEmpty()) return false;
        String itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        if (item.is(ItemTags.PICKAXES) ||
                item.is(ItemTags.SHOVELS) ||
                item.is(ItemTags.AXES) ||
                item.is(ItemTags.HOES)) {
            return true;
        }
        return itemId.matches(".*(tool|mattock|excavator|paxel|pickaxe|shovel|axe|hoe|hammer|wrench|saw|drill|multitool).*");
    }

    public static boolean isPickaxe(ItemStack item) {
        if (item.isEmpty()) return false;
        String itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        if (item.is(ItemTags.PICKAXES)) {
            return true;
        }
        return itemId.matches(".*(paxel|hammer|mattock|pickaxe|pick|drill|miner).*");
    }

    public static boolean isArmor(ItemStack item) {
        if (item.isEmpty()) return false;
        String itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        return itemId.matches(".*(helmet|chestplate|leggings|boots|armor|cuirass|greaves|pauldron|shield).*");
    }

    private static boolean isShovel(ItemStack item) {
        if (item.isEmpty()) return false;
        String itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        if (item.is(ItemTags.SHOVELS)) {
            return true;
        }
        return itemId.matches(".*(shovel|spade|excavator|digger).*");
    }

    /**
     * Checks if the player has the required combat level for a SilentGear weapons & armour
     */
    public static boolean checkCombatLevel(Player player, ItemStack item, int combatLevel, boolean armour) {
        if (!(item.getItem() instanceof GearItem)) {
            return true;
        }
        if (!isWeapon(item)) {
            return true;
        }
        String itemName = item.getItem().getName(item).getString();
        String tier = getGearProperty(item, GearProperties.HARVEST_TIER.get());
        ConfigData data = CombatConfig.getSilentGearData(tier, armour);

        if (data != null && combatLevel < data.getLevel()) {
            MessagePacketSender.send((ServerPlayer) player,
                    "You need a combat level of " + data.getLevel() +
                            " in order to " + (armour ? "equip" : "attack with") + " " + itemName + ".");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has the required mining level for a SilentGear tool
     */
    public static boolean checkMiningLevel(Player player, ItemStack tool, int miningLevel) {
        if (!(tool.getItem() instanceof GearItem)) {
            return true;
        }
        if (!tool.getItem().getDescriptionId().contains("pickaxe") &&
                !tool.getItem().getDescriptionId().contains("hammer")) {
            return true;
        }

        String toolName = tool.getItem().getName(tool).getString();
        String harvestTier = SilentGearHelper.getGearProperty(tool, GearProperties.HARVEST_TIER.get());
        ConfigData data = ToolConfig.getSilentGearData(harvestTier);

        if (data != null && miningLevel < data.getLevel()) {
            String message = "You need a mining level of " + data.getLevel() +
                    " to use " + toolName;
            if (player.level().isClientSide) {
                player.displayClientMessage(Component.literal(
                        message), true);
            } else if (player instanceof ServerPlayer serverPlayer) {
                MessagePacketSender.send(serverPlayer,
                        message);
            }
            return false;
        }
        return true;
    }
}