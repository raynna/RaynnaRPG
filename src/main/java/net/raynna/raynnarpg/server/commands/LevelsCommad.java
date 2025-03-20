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
import net.raynna.raynnarpg.server.utils.Colour;
import net.raynna.raynnarpg.server.utils.MessageSender;
import net.raynna.raynnarpg.server.utils.Utils;

import static net.raynna.raynnarpg.RaynnaRPG.MOD_NAME;

public class LevelsCommad {

    private LevelsCommad() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("levels").executes(LevelsCommad::run)
        );
    }

    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        MessageSender.send(player, "["+ MOD_NAME + "] Your current skills: ", Colour.RED);
        for (SkillType type : SkillType.values()) {
            MessageSender.send(player, type.getName() + " - Level: " + progress.getSkills().getSkill(type).getLevel() + ", Xp: " + Utils.formatNumber(progress.getSkills().getSkill(type).getXp()), Colour.YELLOW);
        }
        return 1;
    }
}
