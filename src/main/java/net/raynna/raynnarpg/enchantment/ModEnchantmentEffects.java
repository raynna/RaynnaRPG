package net.raynna.raynnarpg.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.enchantment.custom.ChainLightningEnchantmentEffect;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ModEnchantmentEffects {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, RaynnaRPG.MOD_ID);

    public static final DeferredHolder<Enchantment, Enchantment> CHAIN_LIGHTNING =
            ENCHANTMENTS.register("chain_lightning", () -> new ChainLightningEnchantment());

    private static HolderSet<Item> getHolderSet(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getOrCreateTag(tag);
    }

    public static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }

    public static class ChainLightningEnchantment extends Enchantment {
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
}
