package net.raynna.raynnarpg.compat.silentgear;

import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class SilentGearCompat {

    public static final boolean IS_LOADED = ModList.get().isLoaded("silentgear");

    private SilentGearCompat() {}

    public static boolean isGearItem(ItemStack stack) {
        if (!IS_LOADED) return false;
        return SilentGearTypeHelper.isGearItem(stack);
    }



}
