package net.raynna.silentrpg.server.utils;

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

    public static void accumulateCraftingData(ServerPlayer player, String itemName, int amount, int experience) {
        CraftingTracker tracker = craftingData.computeIfAbsent(player, key -> new CraftingTracker());

        tracker.setItemName(itemName);
        tracker.addCraftedAmount(amount);
        tracker.addExperience(experience);
        tracker.updateLastEventTime();

        //System.out.println("Crafting data updated: " + tracker);

        scheduler.schedule(() -> sendCraftingSummary(player), 300, TimeUnit.MILLISECONDS);
    }

    private static void sendCraftingSummary(ServerPlayer player) {
        CraftingTracker tracker = craftingData.get(player);
        if (tracker != null && tracker.shouldSendMessage()) {
            player.sendSystemMessage(Component.literal("You gained "
                    + tracker.getTotalExperience() + " experience for creating "
                    + tracker.getCraftedAmount() + " x " + tracker.getItemName() + "."));

            //System.out.println("Crafting summary sent: " + tracker);

            craftingData.remove(player);
        }
    }


    private String itemName = "";
    private int craftedAmount = 0;
    private int totalExperience = 0;
    private long lastEventTime = System.currentTimeMillis();

    // Add a private constructor to ensure new instances are managed via `computeIfAbsent`
    private CraftingTracker() {}

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void addCraftedAmount(int amount) {
        this.craftedAmount += amount;
    }

    public void addExperience(int experience) {
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

    public int getTotalExperience() {
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
