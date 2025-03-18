package net.raynna.silentrpg.server.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class MessageSender {

    public static void send(ServerPlayer player, String message) {
        send(player, message, null);
    }

    public static void send(ServerPlayer player, String message, Colour color) {
        player.sendSystemMessage(
                Component.literal(message).setStyle(Style.EMPTY.withColor(color.getTextColor()))
        );
    }
}