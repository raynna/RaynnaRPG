package net.raynna.raynnarpg.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class MessageSender {

    public static void send(ServerPlayer player, String message) {
        send(player, message, null);
    }

    public static void send(ServerPlayer player, String message, Colour color) {
        if (color == null) {
            color = Colour.WHITE;
        }
        player.sendSystemMessage(
                Component.literal(message).setStyle(Style.EMPTY.withColor(color.getTextColor()))
        );
    }

    public static void sendAll(ServerPlayer player, String message) {
       sendAll(player, message, null);
    }

    public static void sendAll(ServerPlayer player, String message, Colour color) {
        if (color == null) {
            color = Colour.WHITE;
        }
        MinecraftServer server = player.getServer();
        if (server == null) return;

        for (ServerPlayer otherPlayer : server.getPlayerList().getPlayers()) {
            send(otherPlayer, message, color);
        }
    }

    public static void sendAllButSelf(ServerPlayer player, String message) {
        sendAllButSelf(player, message, null);
    }

    public static void sendAllButSelf(ServerPlayer player, String message, Colour color) {
        if (color == null) {
            color = Colour.WHITE;
        }
        MinecraftServer server = player.getServer();
        if (server == null) return;

        for (ServerPlayer otherPlayer : server.getPlayerList().getPlayers()) {
            if (otherPlayer == player) continue;
            send(otherPlayer, message, color);
        }
    }
}