package net.raynna.raynnarpg.server.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.raynna.raynnarpg.network.packets.xpdrop.FloatingTextSender;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.utils.PlayerDamageTracker;

import java.util.List;


public class ServerNpcEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity target = event.getEntity();
            PlayerDamageTracker.recordDamage(player, target, event.getAmount());
            Vec3 vec = new Vec3(target.getX(), target.getY() + 1, target.getZ());
            FloatingTextSender.sendOnEntity(player,
                    String.format("§c%.1f", event.getAmount()),
                    vec,
                    null
            );
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        List<ServerPlayer> attackers = PlayerDamageTracker.getAttackers(entity);
        attackers.forEach(player -> {
            float damageDealt = PlayerDamageTracker.getDamageDealt(player, entity);
            float maxHealth = entity.getMaxHealth();
            float xp = calculateCombatXP(damageDealt, maxHealth, entity);
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
            if (progress != null) {
                progress.getSkills().addXp(SkillType.COMBAT, xp);
                Vec3 vec = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                FloatingTextSender.sendOnEntity(player,
                        String.format("+%.1fxp", xp),
                        vec,
                        SkillType.COMBAT
                );
            }
        });

        PlayerDamageTracker.clearRecords(entity);
    }

    private static float calculateCombatXP(float damageDealt, float maxHealth, LivingEntity entity) {
        float xp = damageDealt * getBaseXPForEntity(entity);
        float effectiveDamage = Math.min(damageDealt, maxHealth);
        return xp;
    }

    private static float getBaseXPForEntity(LivingEntity entity) {
        return 2.0f;
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ServerNpcEvents.class);
    }
}