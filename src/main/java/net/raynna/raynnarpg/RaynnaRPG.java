package net.raynna.raynnarpg;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.minecraft.util.RandomSource;
import net.raynna.raynnarpg.client.ui.OverlayManager;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Random;

@Mod(RaynnaRPG.MOD_ID)
public class RaynnaRPG
{
    public static final String MOD_ID = "raynnarpg";
    public static final String MOD_NAME = "Raynna's RPG";

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();
    public static final Logger LOGGER = LogUtils.getLogger();

    public static RaynnaRPG INSTANCE;
    public static IProxy PROXY;

    @OnlyIn(Dist.CLIENT)
    private static OverlayManager clientOverlayManager;

    @OnlyIn(Dist.CLIENT)
    public static OverlayManager getOverlayManager() {
        if (clientOverlayManager == null) {
            clientOverlayManager = new OverlayManager();
        }
        return clientOverlayManager;
    }

    public RaynnaRPG(IEventBus modEventBus, ModContainer modContainer)
    {
        INSTANCE = this;
        PROXY = FMLEnvironment.dist == Dist.CLIENT
                ? new SideProxy.Client(modEventBus, modContainer)
                : new SideProxy.Server(modEventBus, modContainer);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.Server.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.Client.SPEC);
        NeoForge.EVENT_BUS.register(this);

    }

    public static ResourceLocation getId(String path) {
        if (path.contains(":")) {
            if (path.startsWith(RaynnaRPG.MOD_ID)) {
                return ResourceLocation.tryParse(path);
            } else {
                throw new IllegalArgumentException("path contains namespace other than " + RaynnaRPG.MOD_ID);
            }
        }
        return ResourceLocation.fromNamespaceAndPath(RaynnaRPG.MOD_ID, path);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("[RaynnaRPG] Mod loaded on dedicated server]");
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("[RaynnaRPG] Mod loaded on client]");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
