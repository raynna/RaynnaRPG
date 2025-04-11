package net.raynna.raynnarpg.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.raynna.raynnarpg.Config;

import java.awt.*;

public class ReloadConfigCommand {

    private ReloadConfigCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reloadconfig").executes(ReloadConfigCommand::run)
        );
    }

    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Config.Server.SPEC.save();
        Config.Server.SPEC.afterReload();
        context.getSource().sendSystemMessage(Component.literal("Reloaded server configs."));
        return 1;
    }
}
