package net.raynna.raynnarpg;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.raynna.raynnarpg.client.events.ClientBlockEvents;
import net.raynna.raynnarpg.client.events.ClientItemEvents;
import net.raynna.raynnarpg.client.ui.SkillsHud;
import net.raynna.raynnarpg.data.DataRegistry;
import net.raynna.raynnarpg.server.commands.Commands;
import net.raynna.raynnarpg.server.events.ServerBlockEvents;
import net.raynna.raynnarpg.server.events.ServerPlayerEvents;

import javax.annotation.Nullable;

class SideProxy implements IProxy {
    @Nullable
    private static MinecraftServer server;
    @Nullable
    private static CreativeModeTab creativeModeTab;

    SideProxy(IEventBus modEventBus) {
        modEventBus.addListener(SideProxy::commonSetup);


        NeoForge.EVENT_BUS.addListener(SideProxy::serverStarted);
        NeoForge.EVENT_BUS.addListener(SideProxy::serverStopping);
        NeoForge.EVENT_BUS.addListener(Commands::registerAll);
        ServerPlayerEvents.register();
        ServerBlockEvents.register();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        DataRegistry.loadData();
    }

    private static void serverStarted(ServerStartedEvent event) {
        server = event.getServer();
    }

    private static void serverStopping(ServerStoppingEvent event) {
        server = null;
    }

    @Nullable
    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Level getClientLevel() {
        return null;
    }

    @Override
    public boolean checkClientInstance() {
        return true;
    }

    @Override
    public boolean checkClientConnection() {
        return true;
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return server;
    }

    static class Client extends SideProxy {
        Client(IEventBus modEventBus, ModContainer container) {
            super(modEventBus);
            ClientBlockEvents.register();
            ClientItemEvents.register();
            SkillsHud.register();
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        @Nullable
        @Override
        public Player getClientPlayer() {
            return Minecraft.getInstance().player;
        }

        @Nullable
        @Override
        public Level getClientLevel() {
            Minecraft mc = Minecraft.getInstance();
            return mc != null ? mc.level : null;
        }

        @Override
        public boolean checkClientInstance() {
            return Minecraft.getInstance() != null;
        }

        @Override
        public boolean checkClientConnection() {
            Minecraft mc = Minecraft.getInstance();
            return mc != null && mc.getConnection() != null;
        }
    }

    static class Server extends SideProxy {
        Server(IEventBus modEventBus, ModContainer container) {
            super(modEventBus);
            modEventBus.addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {
        }
    }
}
