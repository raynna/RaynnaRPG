package net.raynna.raynnarpg.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.raynna.raynnarpg.network.packets.skills.SkillsPacketSender;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.player.skills.Skills;
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.MessageSender;

public class SetLevelCommand {

    private SetLevelCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setlevel")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("skill", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (SkillType skill : SkillType.values()) {
                                builder.suggest(skill.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, Skills.MAX_LEVEL))
                                .suggests((context, builder) -> {
                                    builder.suggest("1-" + Skills.MAX_LEVEL);
                                    return builder.buildFuture();
                                })
                                .executes(context -> run(
                                        context,
                                        StringArgumentType.getString(context, "skill"),
                                        IntegerArgumentType.getInteger(context, "level")
                                ))
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSourceStack> context, String skillName, int level) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        SkillType skill = SkillType.getSkillByName(skillName);
        if (skill == null) {
            MessageSender.send(player, "That is not a valid skill.", Colour.Colours.RED);
            return 0;
        }
        if (level < 1 || level > Skills.MAX_LEVEL) {
            MessageSender.send(player, "You must set your level between 1 and " + Skills.MAX_LEVEL + ".", Colour.Colours.RED);
            return 0;
        }
        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        progress.getSkills().getSkill(skill).setLevel(level);
        double xp = Skills.getXpForLevel(level);
        if (level == 1)
            xp = 0;
        progress.getSkills().getSkill(skill).setXp(xp);
        MessageSender.send(player, "You have set your level to " + level + " with experience: " + xp + ".", Colour.Colours.GREEN);
        SkillsPacketSender.send(player, progress.getSkills());
        return 1;
    }
}
