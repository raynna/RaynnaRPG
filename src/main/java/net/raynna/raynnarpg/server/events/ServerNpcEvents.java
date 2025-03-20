package net.raynna.raynnarpg.server.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.skills.SkillType;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.DEDICATED_SERVER)
public class ServerNpcEvents {

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            Entity source = event.getSource().getEntity();

            if (source instanceof ServerPlayer player) {
                double combatXP = getCombatXPForMob(mob);

                PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);

                if (progress != null) {
                    MessagePacketSender.send(player, "You gained " + combatXP + " Combat XP for killing " + mob.getName().getString() + ".");
                    progress.getSkills().addXp(SkillType.COMBAT, combatXP);

                }
            }
        }
    }

    private static double getCombatXPForMob(Mob mob) {
        // Hostile mobs
        if (mob instanceof Zombie) {
            return 75.0; // Zombies give 75 XP
        } else if (mob instanceof Skeleton) {
            return 150.0; // Skeletons give 150 XP
        } else if (mob instanceof EnderDragon) {
            return 25000.0; // Ender Dragon gives 25,000 XP
        } else if (mob instanceof Witch) {
            return 200.0; // Witches give 200 XP
        } else if (mob instanceof Creeper) {
            return 100.0; // Creepers give 100 XP
        } else if (mob instanceof Spider) {
            return 50.0; // Spiders give 50 XP
        } else if (mob instanceof Slime) {
            return 60.0; // Slimes give 60 XP
        } else if (mob instanceof EnderMan) {
            return 200.0; // Endermen give 200 XP
        } else if (mob instanceof Strider) {
            return 80.0; // strider give 80 XP
        } else if (mob instanceof Piglin) {
            return 125; // piglin give 125 XP
        } else if (mob instanceof Blaze) {
            return 150.0; // Blazes give 200 XP
        } else if (mob instanceof Ghast) {
            return 500.0; // Ghasts give 500 XP
        } else if (mob instanceof WitherSkeleton) {
            return 200.0; // Wither gives 5,000 XP
        } else if (mob instanceof Chicken) {
            return 10.0; // Chickens give 10 XP
        } else if (mob instanceof Sheep) {
            return 25.0; // Sheep give 25 XP
        } else if (mob instanceof Pig) {
            return 25.0; // Pigs give 25 XP
        } else if (mob instanceof Cow) {
            return 50.0; // Cows give 50 XP
        } else if (mob instanceof Horse) {
            return 75.0; // Horses give 75 XP
        } else if (mob instanceof Villager) {
            return 30.0; // Villagers give 30 XP (this is optional; you could disable this if you don't want villager kills to give XP)
        } else if (mob instanceof Wolf) {
            return 40.0; // Wolves give 40 XP
        } else if (mob instanceof Cat) {
            return 40.0; // Cats give 40 XP
        } else if (mob instanceof Ocelot) {
            return 40.0; // Ocelots give 40 XP
        } else if (mob instanceof Rabbit) {
            return 15.0; // Rabbits give 15 XP
        } else if (mob instanceof Llama) {
            return 60.0; // Llamas give 60 XP
        }
        return 25.0;
    }
}
