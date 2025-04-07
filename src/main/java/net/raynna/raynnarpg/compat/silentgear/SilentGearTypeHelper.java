package net.raynna.raynnarpg.compat.silentgear;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.events.ClientTooltipEvent;
import net.raynna.raynnarpg.utils.Colour;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SilentGearTypeHelper {

    private static final Map<PartType, List<TraitInstance>> partTraitMap = new HashMap<>();

    public static boolean isGearItem(ItemStack stack) {
        return stack.getItem() instanceof GearItem;
    }

    public static PartInstance getPartInstance(ItemStack stack) {
        try {
            return PartInstance.from(stack);
        } catch (Exception e) {
            RaynnaRPG.LOGGER.error("Error getting part instance", e);
            return null;
        }
    }

    public static MaterialInstance getMaterialInstance(ItemStack stack) {
        try {
            return MaterialInstance.from(stack);
        } catch (Exception e)  {
            RaynnaRPG.LOGGER.error("Error getting material instance", e);
            return null;
        }
    }

    public static void computePartTraits(ItemStack stack) {
        PartInstance part = getPartInstance(stack);
        if (part != null) {
            Collection<TraitInstance> traits = part.getTraits(PartGearKey.ofAll(part.getType()));
            partTraitMap.computeIfAbsent(part.getType(), k -> new ArrayList<>()).addAll(traits);
        }
        MaterialInstance material = getMaterialInstance(stack);
        if (material != null) {
            for (PartType partType : material.getPartTypes()) {
                List<TraitInstance> matTraits = (List<TraitInstance>) material.getTraits(PartGearKey.ofAll(partType));
                partTraitMap.computeIfAbsent(partType, k -> new ArrayList<>()).addAll(matTraits);
            }
        }
    }

    public static List<TraitInstance> getTraits(ItemStack stack) {
        return GearData.getProperties(stack).getTraits();
    }

    public static Map<PartType, List<TraitInstance>> getPartTraitMap() {
        return partTraitMap;
    }

    public static void handleTraitTooltips(ClientTooltipEvent.TooltipContext context, AtomicInteger currentIndex) {
        ItemStack stack = context.stack;
        List<Component> tooltip = context.event.getToolTip();
        partTraitMap.clear();
        computePartTraits(stack);

        if (isGearItem(stack)) {
            List<TraitInstance> gearTraits = getTraits(stack);
            partTraitMap.computeIfAbsent(PartTypes.MAIN.get(), k -> new ArrayList<>()).addAll(gearTraits);
        }

        if (partTraitMap.isEmpty()) {
            return;
        }
        if (context.isAltDown) {
            addDetailedTraitsTooltip(stack, tooltip, currentIndex);
        } else {
            addCompactTraitsTooltip(stack, tooltip, currentIndex);
        }
    }

    private static void addDetailedTraitsTooltip(ItemStack stack, List<Component> tooltip, AtomicInteger index) {
        tooltip.add(index.getAndIncrement(), Component.literal(Colour.GOLD + "Traits:"));

        for (Map.Entry<PartType, List<TraitInstance>> entry : partTraitMap.entrySet()) {
            PartType partType = entry.getKey();
            for (TraitInstance trait : entry.getValue()) {
                String name = trait.getTrait().getDisplayName(trait.getLevel()).getString();
                String desc = trait.getTrait().getDescription(trait.getLevel()).getString();
                String part = isGearItem(stack) ? "" : " (" + partType.getDisplayName().getString() + ")";
                tooltip.add(index.getAndIncrement(), Component.literal(Colour.WHITE + " " + name + part));
                tooltip.add(index.getAndIncrement(), Component.literal(Colour.GRAY + "    " + desc));
            }
        }
    }

    private static void addCompactTraitsTooltip(ItemStack stack, List<Component> tooltip, AtomicInteger index) {
        StringBuilder compactLine = new StringBuilder(Colour.GOLD + "Traits: ");
        boolean first = true;

        for (Map.Entry<PartType, List<TraitInstance>> entry : partTraitMap.entrySet()) {
            PartType partType = entry.getKey();
            for (TraitInstance trait : entry.getValue()) {
                if (!first) {
                    compactLine.append(Colour.GRAY + ", ");
                }
                first = false;
                String name = trait.getTrait().getDisplayName(trait.getLevel()).getString();
                String part = isGearItem(stack) ? "" : " (" + partType.getDisplayName().getString() + ")";
                compactLine.append(Colour.WHITE).append(name).append(part);
            }
        }

        tooltip.add(index.getAndIncrement(), Component.literal(compactLine.toString()));
        tooltip.add(index.getAndIncrement(), Component.literal(Colour.YELLOW + "  Trait Details [Left Alt]"));
    }
}
