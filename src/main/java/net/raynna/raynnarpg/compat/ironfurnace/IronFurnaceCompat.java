package net.raynna.raynnarpg.compat.ironfurnace;

import ironfurnaces.container.furnaces.BlockIronFurnaceContainerBase;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.raynna.raynnarpg.compat.silentgear.SilentGearTypeHelper;

public final class IronFurnaceCompat {

    public static final boolean IS_LOADED = ModList.get().isLoaded("ironfurnace");

    private IronFurnaceCompat() {}

    public static boolean isIronFurnace(AbstractContainerMenu menu) {
        if (!IS_LOADED) return false;
        return menu instanceof BlockIronFurnaceContainerBase;
    }
}
