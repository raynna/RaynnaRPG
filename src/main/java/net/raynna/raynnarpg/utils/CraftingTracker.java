package net.raynna.raynnarpg.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.raynna.raynnarpg.network.packets.xpdrop.FloatingTextSender;
import net.raynna.raynnarpg.server.events.ServerPlayerEvents;
import net.raynna.raynnarpg.server.player.skills.SkillType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CraftingTracker {

    private static final Map<ServerPlayer, CraftingTracker> craftingData = new HashMap<>();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void accumulateCraftingData(ServerPlayer player, ItemStack craftedItem, ServerPlayerEvents.CraftingResult result, double experience, SkillType type, Runnable onFinish) {
        CraftingTracker tracker = craftingData.computeIfAbsent(player, key -> new CraftingTracker());

        tracker.setItemName(craftedItem.getHoverName().getString());
        tracker.addCraftedAmount(craftedItem.getCount());
        tracker.addExperience(experience);
        tracker.updateLastEventTime();
        tracker.setResult(result);

        if (result != null && result.materials != null) {
            for (Map.Entry<String, ServerPlayerEvents.CraftingResult.Materials> entry : result.materials.entrySet()) {
                String materialName = entry.getKey();
                ServerPlayerEvents.CraftingResult.Materials materialData = entry.getValue();
                tracker.accumulateMaterialData(materialName, materialData.getCount(), materialData.getXp(), materialData.isCapped());
            }
        }

        int ping = player.connection.latency();
        int delay = Math.max(600, ping);
        scheduler.schedule(() -> {
            sendCraftingSummary(player, type);
            if (onFinish != null) {
                onFinish.run();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private static void sendCraftingSummary(ServerPlayer player, SkillType type) {
        CraftingTracker tracker = craftingData.get(player);
        if (tracker != null && tracker.shouldSendMessage()) {

            double roundedXp = Math.round(tracker.getTotalExperience() * 100.0) / 100.0;

            ServerPlayerEvents.CraftingResult result = tracker.getCraftingResult();
            StringBuilder message = new StringBuilder();
            if (result != null) {
                message.append("XP Breakdown:\n");
                for (Map.Entry<String, ServerPlayerEvents.CraftingResult.Materials> entry : tracker.materials.entrySet()) {
                    String material = entry.getKey();
                    String name = RegistryUtils.getDisplayName(material);
                    double materialXp = entry.getValue().getXp();
                    int count = entry.getValue().getCount();
                    double xpPerMaterial = Math.round(materialXp / count * 100.0) / 100.0;
                    double xpForAll = Math.round(materialXp * 100.0) / 100.0;
                    boolean isCapped = entry.getValue().isCapped();
                    if (isCapped) {
                        message.append("You are too high level to gain XP from ").append(name);
                        continue;
                    }

                    if (count > 1) {
                        message.append(count).append(" x ");
                    }
                    message.append(name).append(": ").append(xpForAll).append(" XP");

                    if (count > 1) {
                        message.append(" (").append(xpPerMaterial).append(" each)");
                    }
                    message.append("\n");
                }
            }

            message.append("You gained ").append(roundedXp).append(" experience for crafting ");
            if (tracker.getCraftedAmount() > 1) message.append(tracker.getCraftedAmount()).append(" x "); else message.append("One ");
            message.append(tracker.getItemName());
            player.sendSystemMessage(Component.literal(message.toString()));
            FloatingTextSender.sendCenteredText(player, "+"+roundedXp+"xp", type);
            craftingData.remove(player);
        }
    }


    private String itemName = "";
    private int craftedAmount = 0;
    private double totalExperience = 0;
    private long lastEventTime = System.currentTimeMillis();
    private ServerPlayerEvents.CraftingResult result;

    private final Map<String, ServerPlayerEvents.CraftingResult.Materials> materials = new HashMap<>();

    public void accumulateMaterialData(String materialName, int count, double xp, boolean capped) {
        ServerPlayerEvents.CraftingResult.Materials materialData = materials.computeIfAbsent(materialName,
                key -> new ServerPlayerEvents.CraftingResult.Materials(key, 0, 0, false));

        materialData.increaseCount(count);
        materialData.trackXp(xp);
        materialData.setCapped(capped);

    }

    private CraftingTracker() {}

    public void setResult(ServerPlayerEvents.CraftingResult result) {
        this.result = result;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void addCraftedAmount(int amount) {
        this.craftedAmount += amount;
    }

    public void addExperience(double experience) {
        this.totalExperience += experience;
    }

    public void updateLastEventTime() {
        this.lastEventTime = System.currentTimeMillis();
    }

    public boolean shouldSendMessage() {
        return System.currentTimeMillis() - lastEventTime >= 300;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCraftedAmount() {
        return craftedAmount;
    }

    public double getTotalExperience() {
        return totalExperience;
    }

    public ServerPlayerEvents.CraftingResult getCraftingResult() {
        return result;
    }

    @Override
    public String toString() {
        return "CraftingTracker{" +
                "itemName='" + itemName + '\'' +
                ", craftedAmount=" + craftedAmount +
                ", totalExperience=" + totalExperience +
                ", lastEventTime=" + lastEventTime +
                '}';
    }
}
