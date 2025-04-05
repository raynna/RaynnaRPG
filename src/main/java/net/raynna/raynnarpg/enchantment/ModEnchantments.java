package net.raynna.raynnarpg.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raynna.raynnarpg.RaynnaRPG;
import net.neoforged.neoforge.registries.Registry
import net.raynna.raynnarpg.enchantment.custom.ChainLightningEnchantmentEffect;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, RaynnaRPG.MOD_ID);

    public static final DeferredHolder<Enchantment, Enchantment> CHAIN_LIGHTNING =
            ENCHANTMENTS.register("chain_lightning", () -> new ChainLightningEnchantmentEffect());

    public static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}