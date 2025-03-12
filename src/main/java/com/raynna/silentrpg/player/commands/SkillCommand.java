package com.raynna.silentrpg.player.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.raynna.silentrpg.player.PlayerDataProvider;
import com.raynna.silentrpg.player.PlayerProgress;
import com.raynna.silentrpg.player.skills.SkillType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


public class SkillCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("skill")
                .requires((source) -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.literal("level")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .then(Commands.literal("get")
                                                .executes((command) -> getSkillLevel(command.getSource(), EntityArgument.getEntity(command, "target"), SkillType.valueOf(StringArgumentType.getString(command, "skill"))))
                                        )
                                )
                        )
                )
        );
    }

    private static int getSkillLevel(CommandSourceStack source, Entity target, SkillType skillType) {
        int level = getSkillLevelFromEntity(target, skillType);
        source.sendSuccess(() -> Component.literal("Skill: " + skillType + " Level: " + level), false);
        return level;
    }

    private static int getSkillLevelFromEntity(Entity entity, SkillType skillType) {
        Player player = (Player) entity;
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            return progress.getSkills().getSkill(skillType).getLevel();
        }
        return 1;
    }
}
