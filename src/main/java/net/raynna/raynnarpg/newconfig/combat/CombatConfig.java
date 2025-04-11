package net.raynna.raynnarpg.newconfig.combat;

import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.ListStringEntry;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.compat.silentgear.SilentGearCompat;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.newconfig.RaynnaServerConfig;
import net.raynna.raynnarpg.utils.ItemUtils;
import net.silentchaos512.gear.setup.gear.GearProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CombatConfig extends AutoInitConfigContainer.AutoInitConfigCategoryBase {

    public static ConfigData getWeaponData(ItemStack stack) {
        return RaynnaServerConfig.getCombatConfig().getData(stack, false);
    }

    public static ConfigData getArmourData(ItemStack stack) {
        return RaynnaServerConfig.getCombatConfig().getData(stack, true);
    }

    public ListStringEntry VANILLA_WEAPONS = new ListStringEntry("vanilla_weapons",
            List.of(
                    "minecraft:wooden_sword,1",
                    "minecraft:stone_sword,5",
                    "minecraft:iron_sword,15",
                    "minecraft:diamond_sword,30",
                    "minecraft:netherite_sword,30"
            ));
    ;

    public ListStringEntry VANILLA_ARMOR = new ListStringEntry(
            "vanilla_armor", List.of("minecraft:leather_helmet,1", "minecraft:iron_chestplate,15")
    );

    public ListStringEntry SILENT_GEAR_WEAPONS = new ListStringEntry(
            "silentgear_weapons", List.of("wood,1", "iron,15")
    );

    public ListStringEntry SILENT_GEAR_ARMOURS = new ListStringEntry(
            "silentgear_armours", List.of("wood,1", "iron,15")
    );

    public CombatConfig() {
        super("combat", "config.raynna.common.combat");
        validateEntry(VANILLA_ARMOR);
        validateEntry(VANILLA_WEAPONS);
        if (SilentGearCompat.IS_LOADED) {
            SILENT_GEAR_WEAPONS.setValue(List.of("wood,1", "iron,15", "netherite,40"));
        }
    }

    private void validateEntry(ListStringEntry entry) {
        for (String s : entry.getValue()) {
            try {
                String[] parts = s.split(",");
                Integer.parseInt(parts[1]);
            } catch (Exception e) {
                RaynnaRPG.LOGGER.error("Invalid combat config format in entry: " + s);
            }
        }
    }

    private List<CombatEntry> getEntries(ListStringEntry entry) {
        return entry.getValue().stream()
                .map(CombatEntry::fromConfigString)
                .toList();
    }

    public ConfigData getData(ItemStack stack, boolean isArmor) {
        String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        RaynnaRPG.LOGGER.info("Checking item: {}", key);
        if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(stack)) {
            String tier = ItemUtils.getGearProperty(stack, GearProperties.HARVEST_TIER.get());
            RaynnaRPG.LOGGER.info("SilentGear item detected - Tier: {}", tier);
            ListStringEntry entry = isArmor ? SILENT_GEAR_ARMOURS : SILENT_GEAR_WEAPONS;
            RaynnaRPG.LOGGER.info("Using config entry: {}", entry.getValue());
            return findInList(entry, tier);
        }

        ListStringEntry entry = isArmor ? VANILLA_ARMOR : VANILLA_WEAPONS;
        RaynnaRPG.LOGGER.info("Using vanilla config entry: {}", entry.getValue());
        return findInList(entry, key);
    }

    private ConfigData findInList(ListStringEntry entry, String searchKey) {
        List<String> currentValues = entry.getValue();

        RaynnaRPG.LOGGER.info("Checking {} against current config values: {}", searchKey, currentValues);
        String path = "./config/raynna/common.json";
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            System.out.println(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String configString : currentValues) {
            try {
                String[] parts = configString.split(",");
                if (parts.length >= 1 && parts[0].equals(searchKey)) {
                    int level = Integer.parseInt(parts[1]);
                    RaynnaRPG.LOGGER.info("Found match - Key: {}, Level: {}", parts[0], level);
                    return new ConfigData(level, 0);
                }
            } catch (Exception e) {
                RaynnaRPG.LOGGER.error("Invalid config entry: " + configString, e);
            }
        }
        return null;
    }

    public record CombatEntry(String key, int level) {
        public static CombatEntry fromConfigString(String str) {
            String[] parts = str.split(",");
            return new CombatEntry(parts[0], Integer.parseInt(parts[1]));
        }

        public String toConfigString() {
            return String.join(",", key, String.valueOf(level));
        }
    }
}
