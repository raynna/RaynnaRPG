package net.raynna.raynnarpg.server.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CraftingTracker {

    private static final Map<ServerPlayer, CraftingTracker> craftingData = new HashMap<>();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void accumulateCraftingData(ServerPlayer player, String itemName, int amount, double experience, Runnable onFinish) {
        CraftingTracker tracker = craftingData.computeIfAbsent(player, key -> new CraftingTracker());

        tracker.setItemName(itemName);
        tracker.addCraftedAmount(amount);
        tracker.addExperience(experience);
        tracker.updateLastEventTime();
        int ping = player.connection.latency();
        int delay = Math.max(600, ping);
        scheduler.schedule(() -> {
            sendCraftingSummary(player);
            if (onFinish != null) {
                onFinish.run();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private static void sendCraftingSummary(ServerPlayer player) {
        CraftingTracker tracker = craftingData.get(player);
        if (tracker != null && tracker.shouldSendMessage()) {
            if (tracker.getCraftedAmount() > 1) {
                player.sendSystemMessage(Component.literal("You gained "
                        + tracker.getTotalExperience() + " experience for creating "
                        + tracker.getCraftedAmount() + " x " + tracker.getItemName() + " batches."));
            } else {
                player.sendSystemMessage(Component.literal("You gained "
                        + tracker.getTotalExperience() + " experience for creating one " + tracker.getItemName() + " batch."));
            }
            craftingData.remove(player);
        }
    }


    private String itemName = "";
    private int craftedAmount = 0;
    private double totalExperience = 0;
    private long lastEventTime = System.currentTimeMillis();

    private CraftingTracker() {}

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
        return System.currentTimeMillis() - lastEventTime >= 300; // 300 milliseconds of inactivity
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
