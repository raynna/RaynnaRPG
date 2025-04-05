package net.raynna.raynnarpg.enchantment.custom;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

public class ChainLightningEnchantment extends Enchantment {
    public ChainLightningEnchantment() {
            super(new EnchantmentDefinition(
                    // Supported Items
                    getHolderSet(ItemTags.WEAPON_ENCHANTABLE),
                    // Primary Items (Optional)
                    Optional.of(getHolderSet(ItemTags.SWORD_ENCHANTABLE)),
                    // Weight
                    2,
                    // Max Level
                    2,
                    // Min Cost
                    Enchantment.dynamicCost(5, 7),
                    // Max Cost
                    Enchantment.dynamicCost(25, 7),
                    // Anvil Cost
                    1,
                    // Equipment Slots
                    List.of(EquipmentSlotGroup.MAINHAND)
            );
        }

        @Override
        public int getMaxLevel() {
            return 2;
        }
    }

    private static HolderSet<Item> getHolderSet(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getOrCreateTag(tag);
    }

    // Add any custom behavior here
    @Override
    public int getMaxLevel() {
        return 2;
    }
}

